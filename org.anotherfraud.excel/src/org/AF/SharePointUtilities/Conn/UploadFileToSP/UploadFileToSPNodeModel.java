package org.AF.SharePointUtilities.Conn.UploadFileToSP;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Optional;

import org.AF.Connections.ConnectionInformation;
import org.AF.Connections.ConnectionInformationPortObject;
import org.AF.SharePointUtilities.GetRestAccessToken.GetRestAccessTokenNodeModel;
import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * This is an example implementation of the node model of the
 * "UploadFileToSP" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class UploadFileToSPNodeModel extends NodeModel {


	private static final NodeLogger LOGGER = NodeLogger.getLogger(GetRestAccessTokenNodeModel.class);
	private Optional<FSConnection> m_fs = Optional.empty();


	private static final int defaulttimeoutInSeconds = 5;
	
    static final String SharePointName = "SharePointName";
    static final String uploadfilePath = "uploadFile";
    static final String spFolderPath = "spFolderPath";
    
	
    
 
	static SettingsModelFileChooser2 createUploadFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(uploadfilePath);
	}
	
	static SettingsModelString createSpFolderPathSettingsModel() {
		SettingsModelString coof = new SettingsModelString(spFolderPath, "Shared%20Documents/<FolderName>");
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createSharePointNameSettingsModel() {
		SettingsModelString coof = new SettingsModelString(SharePointName, "<yourSharepointSiteName>");
		coof.setEnabled(true);
		return coof;				
	}	
	


	private final SettingsModelString m_sharePointName = createSharePointNameSettingsModel();
	private final SettingsModelFileChooser2 m_uploadFilePath2 = createUploadFilePath2SettingsModel();
	private final SettingsModelString m_SPFolderPath = createSpFolderPathSettingsModel();

	
	

	/**
	 * Constructor for the node model.
	 */
	protected UploadFileToSPNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[]{ConnectionInformationPortObject.TYPE}, new PortType[] {FlowVariablePortObject.TYPE});
		
	}


	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec)
			throws Exception {
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

		
	    /* Token variable declaration */

	    String folderpath = m_SPFolderPath.getStringValue();
	    String sharePointName = m_sharePointName.getStringValue();
		FileChooserHelper fileHelperTemplate = new FileChooserHelper(m_fs, m_uploadFilePath2, defaulttimeoutInSeconds * 1000);
		Path pathTemplate = fileHelperTemplate.getPathFromSettings();
        
         
	
		File file = new File(pathTemplate.toAbsolutePath().toString());
		String fileuri = URLEncoder.encode(file.getName().replaceAll(" ","%20"), "UTF-8");
		
		ConnectionInformationPortObject spConn = (ConnectionInformationPortObject)inObjects[0];
		ConnectionInformation connInfo = spConn.getConnectionInformation();
		
	    String token = connInfo.getToken();    
	    String sharePointSite = connInfo.getSharePointOnlineSiteURL();
 	    String proxyUser = connInfo.getUser();
 	    String proxyPass = connInfo.getPassword();	
    	String proxyHost = connInfo.getHost();
		int proyPort = connInfo.getPort();    
	    boolean proxyEnabled = connInfo.getUseProxy(); 
	    
	    
	    
	    /* Null or fail check */
	    if (!token.isEmpty())
	    { 
	        /* Upload path and file name declaration */
	        /*
	         * https://<your_domain>.sharepoint.com/_api/web/
	         * GetFolderByServerRelativeUrl('/Shared%20Documents/<FolderName>')/
	         * Files/
	         */
	  
	    	
		    	
	    	String Url_parameter = "Add(url='" + fileuri + "',overwrite=true)";
	        String url = "https://"
	        		+ sharePointSite
	        		+ "/sites/"
	        		+ sharePointName
	        		+ "/_api/Web/GetFolderByServerRelativeUrl('"
	        		+ SharePointHelper.buildCompleteSPPath(folderpath, sharePointName)
	        		+ "')/Files/" 
	        		+ Url_parameter;
	        


		
	        HttpClientBuilder clientbuilder = HttpClients.custom();
	        SharePointHelper.setProxyCredentials(clientbuilder, proxyEnabled, proxyHost, proyPort, proxyUser, proxyPass);
		          

	        HttpClient client = clientbuilder.build();
	        
	        //HttpHost target = new HttpHost("google.de", 80, "http");
	        

 
	        HttpPost post = new HttpPost(url);   
	        SharePointHelper.createProxyRequestConfig(post, proxyEnabled, proxyHost, proyPort);
	        


	        post.setHeader("Authorization", "Bearer " + token);
	        post.setHeader("accept", "application/json;odata=verbose");
	        //post.addHeader("Proxy-Authorization", "Basic " + authenticationEncoded );
	        
	        /* Declaring File Entity */
	        post.setEntity(new FileEntity(file, null));

	        /* Executing the post request */
	        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post);
	       
	        
	        String responseBody = EntityUtils.toString(response.getEntity());
	        

	        pushFlowVariableString("UploadStatus", String.valueOf(response.getCode()));
	        pushFlowVariableString("UploadResponse", responseBody);
	        
		    if(response.getCode()==200)
		    {	
		    	
		    	
		    try {
	    	Gson gson = new Gson();
	    	JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
	    	JsonObject responseJson = jsonObj.getAsJsonObject("d");
        
	        pushFlowVariableString("CheckInComment", responseJson.get("CheckInComment").getAsString());
	        pushFlowVariableInt("CheckOutType", responseJson.get("CheckOutType").getAsInt());
	        pushFlowVariableString("ContentTag", responseJson.get("ContentTag").getAsString());
	        pushFlowVariableString("ETag", responseJson.get("ETag").getAsString());
	        pushFlowVariableString("Length", responseJson.get("Length").getAsString());
	        pushFlowVariableString("LinkingUri", responseJson.get("LinkingUri").getAsString());
	        pushFlowVariableString("LinkingUrl", responseJson.get("LinkingUrl").getAsString());
	        pushFlowVariableInt("MajorVersion", responseJson.get("MajorVersion").getAsInt());
	        pushFlowVariableInt("MinorVersion", responseJson.get("MinorVersion").getAsInt());
	        pushFlowVariableString("Name", responseJson.get("Name").getAsString());
	        pushFlowVariableString("ServerRelativeUrl", responseJson.get("ServerRelativeUrl").getAsString());
	        pushFlowVariableString("TimeCreated", responseJson.get("TimeCreated").getAsString());
	        pushFlowVariableString("TimeLastModified", responseJson.get("TimeLastModified").getAsString());
	        pushFlowVariableString("Title", responseJson.get("Title").getAsString());
	        pushFlowVariableString("UniqueId", responseJson.get("UniqueId").getAsString());
		    } catch (Exception e)
		    {
		    	LOGGER.info("Error response - could not generate flow variables");
		    }
		    }
	    }

		return new FlowVariablePortObject[]{FlowVariablePortObject.INSTANCE};
		
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
		//DataTableSpec inputTableSpec = inSpecs[0];
		//return new DataTableSpec[] { createOutputSpec(inputTableSpec) };
		return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		/*
		 * Save user settings to the NodeSettings object. SettingsModels already know how to
		 * save them self to a NodeSettings object by calling the below method. In general,
		 * the NodeSettings object is just a key-value store and has methods to write
		 * all common data types. Hence, you can easily write your settings manually.
		 * See the methods of the NodeSettingsWO.
		 */

		m_sharePointName.saveSettingsTo(settings);
		m_uploadFilePath2.saveSettingsTo(settings);
		m_SPFolderPath.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * Load (valid) settings from the NodeSettings object. It can be safely assumed that
		 * the settings are validated by the method below.
		 * 
		 * The SettingsModel will handle the loading. After this call, the current value
		 * (from the view) can be retrieved from the settings model.
		 */
		m_uploadFilePath2.loadSettingsFrom(settings);
		m_SPFolderPath.loadSettingsFrom(settings);
		m_sharePointName.loadSettingsFrom(settings);
		
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
		m_uploadFilePath2.validateSettings(settings);
		m_SPFolderPath.validateSettings(settings);
		m_sharePointName.validateSettings(settings);

	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything that is
		 * handed to the output ports is loaded automatically (data returned by the execute
		 * method, models loaded in loadModelContent, and user settings set through
		 * loadSettingsFrom - is all taken care of). Only load the internals
		 * that need to be restored (e.g. data used by the views).
		 */
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything
		 * written to the output ports is saved automatically (data returned by the execute
		 * method, models saved in the saveModelContent, and user settings saved through
		 * saveSettingsTo - is all taken care of). Save only the internals
		 * that need to be preserved (e.g. data used by the views).
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
