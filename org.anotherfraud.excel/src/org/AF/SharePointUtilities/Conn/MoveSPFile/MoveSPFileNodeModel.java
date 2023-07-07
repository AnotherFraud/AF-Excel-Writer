package org.AF.SharePointUtilities.Conn.MoveSPFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import org.AF.Connections.ConnectionInformation;
import org.AF.Connections.ConnectionInformationPortObject;
import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;

/**
 * This is an example implementation of the node model of the "MoveSPFile" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class MoveSPFileNodeModel extends NodeModel {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(MoveSPFileNodeModel.class);

	static final String SharePointName = "SharePointName";
	static final String spFolderPathFrom = "spFolderPathFrom";
	static final String spFolderPathTo = "spFolderPathTo";
	static final String copyFile = "copyFile";

	static SettingsModelString createSpFolderPathToSettingsModel() {
		SettingsModelString coof = new SettingsModelString(spFolderPathTo,
				"Shared%20Documents/<FolderName>/<FileName>");
		coof.setEnabled(true);
		return coof;
	}

	static SettingsModelString createSpFolderPathFromSettingsModel() {
		SettingsModelString coof = new SettingsModelString(spFolderPathFrom,
				"Shared%20Documents/<FolderName>/<FileName>");
		coof.setEnabled(true);
		return coof;
	}

	static SettingsModelString createSharePointNameSettingsModel() {
		SettingsModelString coof = new SettingsModelString(SharePointName, "<yourSharepointSiteName>");
		coof.setEnabled(true);
		return coof;
	}

	static SettingsModelBoolean createCopyFileSettingsModel() {
		SettingsModelBoolean wlr = new SettingsModelBoolean(copyFile, true);
		return wlr;
	}

	private final SettingsModelString m_sharePointName = createSharePointNameSettingsModel();
	private final SettingsModelString m_SPFolderPathTo = createSpFolderPathToSettingsModel();
	private final SettingsModelString m_SPFolderPathFrom = createSpFolderPathFromSettingsModel();
	private final SettingsModelBoolean m_copyFile = createCopyFileSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected MoveSPFileNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[] { ConnectionInformationPortObject.TYPE }, new PortType[] { FlowVariablePortObject.TYPE });

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
		/*
		 * The functionality of the node is implemented in the execute method. This
		 * implementation will format each double column of the input table using a user
		 * provided format String. The output will be one String column for each double
		 * column of the input containing the formatted number from the input table. For
		 * simplicity, all other columns are ignored in this example.
		 * 
		 * Some example log output. This will be printed to the KNIME console and KNIME
		 * log.
		 */
		LOGGER.info("Starting SP Upload");

		String folderpathFrom = m_SPFolderPathFrom.getStringValue();
		String folderpathTo = m_SPFolderPathTo.getStringValue();
		String sharePointName = m_sharePointName.getStringValue();

		ConnectionInformationPortObject spConn = (ConnectionInformationPortObject) inObjects[0];
		ConnectionInformation connInfo = spConn.getConnectionInformation();

		String token = connInfo.getToken();
		String sharePointSite = connInfo.getSharePointOnlineSiteURL();
		String proxyUser = connInfo.getUser();
		String proxyPass = connInfo.getPassword();
		String proxyHost = connInfo.getHost();
		int proxyPort = connInfo.getPort();
		boolean proxyEnabled = connInfo.getUseProxy();

		boolean copyFile = m_copyFile.getBooleanValue();
		String parameter = "";

		if (!copyFile) {
			parameter = "/moveto(newurl='" + SharePointHelper.buildCompleteSPPath(folderpathTo, sharePointName)
					+ "',flags=1)";
		} else {
			parameter = "/copyto(strnewurl='" + SharePointHelper.buildCompleteSPPath(folderpathTo, sharePointName)
					+ "',boverwrite=false)";
		}

		/* Null or fail check */
		if (!token.isEmpty()) {
			/* Upload path and file name declaration */
			/*
			 * https://<your_domain>.sharepoint.com/_api/web/
			 * GetFolderByServerRelativeUrl('/Shared%20Documents/<FolderName>')/ Files/
			 */

			String url = "https://" + sharePointSite + "/sites/" + sharePointName
					+ "/_api/Web/GetFileByServerRelativeUrl('"
					+ SharePointHelper.buildCompleteSPPath(folderpathFrom, sharePointName) + "')" + parameter;
			
			
			
			URL sourceUrl = new URL(url);
            HttpURLConnection sourceConn;
            
            
            
            
			if (proxyEnabled) {
                // Set proxy details
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new java.net.InetSocketAddress(proxyHost, proxyPort));

                // Set proxy authentication
                Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (getRequestorType() == RequestorType.PROXY) {
                            return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                        }
                        return null;
                    }
                };
                Authenticator.setDefault(authenticator);

                // Create connection for the source file with proxy
                sourceConn = (HttpURLConnection) sourceUrl.openConnection(proxy);
            } else {
                // Create connection for the source file without proxy
                sourceConn = (HttpURLConnection) sourceUrl.openConnection();
            }
			

			
			sourceConn.setRequestMethod("POST");
			
            sourceConn.setDoOutput(true);
           
   

            // Set credentials for the source file request
            sourceConn.setRequestProperty("Authorization", "Bearer " + token);
            sourceConn.setRequestProperty("accept", "application/json;odata=verbose");
            sourceConn.setRequestProperty("IF-MATCH", "*");
            sourceConn.setChunkedStreamingMode(0);
            
            
            int responseCode = sourceConn.getResponseCode();
            //System.out.println("Response Code: " + responseCode);

            // Read the response body
            String responseBody = readResponseBody(sourceConn);
            //System.out.println("Response Body: " + responseBody);
            
            
            sourceConn.disconnect();

            
			pushFlowVariableString("ResponseStatus", String.valueOf(responseCode));
			pushFlowVariableString("ResponseString", responseBody);

		}

		return new FlowVariablePortObject[] { FlowVariablePortObject.INSTANCE };

	}
	
	
	
	 private static String readResponseBody(HttpURLConnection connection) throws IOException {
	        InputStream inputStream;
	        if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 299) {
	            inputStream = connection.getInputStream();
	        } else {
	            inputStream = connection.getErrorStream();
	        }

	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        StringBuilder response = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            response.append(line);
	        }
	        reader.close();

	        return response.toString();
	    }
	 

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		/*
		 * Similar to the return type of the execute method, we need to return an array
		 * of DataTableSpecs with the length of the number of outputs ports of the node
		 * (as specified in the constructor). The resulting table created in the execute
		 * methods must match the spec created in this method. As we will need to
		 * calculate the output table spec again in the execute method in order to
		 * create a new data container, we create a new method to do that.
		 */
		// DataTableSpec inputTableSpec = inSpecs[0];
		// return new DataTableSpec[] { createOutputSpec(inputTableSpec) };
		return new PortObjectSpec[] { FlowVariablePortObjectSpec.INSTANCE };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		/*
		 * Save user settings to the NodeSettings object. SettingsModels already know
		 * how to save them self to a NodeSettings object by calling the below method.
		 * In general, the NodeSettings object is just a key-value store and has methods
		 * to write all common data types. Hence, you can easily write your settings
		 * manually. See the methods of the NodeSettingsWO.
		 */
		m_sharePointName.saveSettingsTo(settings);
		m_copyFile.saveSettingsTo(settings);
		m_SPFolderPathTo.saveSettingsTo(settings);
		m_SPFolderPathFrom.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * Load (valid) settings from the NodeSettings object. It can be safely assumed
		 * that the settings are validated by the method below.
		 * 
		 * The SettingsModel will handle the loading. After this call, the current value
		 * (from the view) can be retrieved from the settings model.
		 */

		m_sharePointName.loadSettingsFrom(settings);
		m_copyFile.loadSettingsFrom(settings);
		m_SPFolderPathTo.loadSettingsFrom(settings);
		m_SPFolderPathFrom.loadSettingsFrom(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * Check if the settings could be applied to our model e.g. if the user provided
		 * format String is empty. In this case we do not need to check as this is
		 * already handled in the dialog. Do not actually set any values of any member
		 * variables.
		 */
		m_sharePointName.validateSettings(settings);
		m_copyFile.validateSettings(settings);
		m_SPFolderPathTo.validateSettings(settings);
		m_SPFolderPathFrom.validateSettings(settings);

	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything that is handed to the output
		 * ports is loaded automatically (data returned by the execute method, models
		 * loaded in loadModelContent, and user settings set through loadSettingsFrom -
		 * is all taken care of). Only load the internals that need to be restored (e.g.
		 * data used by the views).
		 */
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything written to the output ports
		 * is saved automatically (data returned by the execute method, models saved in
		 * the saveModelContent, and user settings saved through saveSettingsTo - is all
		 * taken care of). Save only the internals that need to be preserved (e.g. data
		 * used by the views).
		 */
	}

	@Override
	protected void reset() {
		/*
		 * Code executed on a reset of the node. Models built during execute are cleared
		 * and the data handled in loadInternals/saveInternals will be erased.
		 */
	}
}
