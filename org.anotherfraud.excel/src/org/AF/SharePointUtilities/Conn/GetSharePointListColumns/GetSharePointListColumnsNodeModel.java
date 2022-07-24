package org.AF.SharePointUtilities.Conn.GetSharePointListColumns;

import java.io.File;
import java.io.IOException;

import org.AF.Connections.ConnectionInformation;
import org.AF.Connections.ConnectionInformationPortObject;
import org.AF.SharePointUtilities.Conn.GetSPListItems.GetSPListItemsNodeModel;
import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell.StringCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
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


/**
 * This is an example implementation of the node model of the
 * "GetSharePointListColumns" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class GetSharePointListColumnsNodeModel extends NodeModel {
    
	private static final NodeLogger LOGGER = NodeLogger.getLogger(GetSPListItemsNodeModel.class);


    static final String SharePointName = "SharePointName";
    static final String spListName = "spListName";
	

	static SettingsModelString createSpListNameSettingsModel() {
		SettingsModelString coof = new SettingsModelString(spListName, "<List-Name>");
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createSharePointNameSettingsModel() {
		SettingsModelString coof = new SettingsModelString(SharePointName, "<yourSharepointSiteName>");
		coof.setEnabled(true);
		return coof;				
	}	

	private final SettingsModelString m_sharePointName = createSharePointNameSettingsModel();
	private final SettingsModelString m_SPListName = createSpListNameSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected GetSharePointListColumnsNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[]{ConnectionInformationPortObject.TYPE}, new PortType[] { BufferedDataTable.TYPE});
	}



	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final PortObject[] inObjects, final ExecutionContext exec)
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
		LOGGER.info("Starting SP List Load");

	    String listName = m_SPListName.getStringValue();
	    String sharePointName = m_sharePointName.getStringValue();

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

	
	    	 String url = "https://"
	        		+ sharePointSite
	        		+ "/sites/"
	        		+ SharePointHelper.formatStringForUrl(sharePointName)
	        		+ "/_api/web/lists/getbytitle('"
	        		+ SharePointHelper.formatStringForUrl(listName)
	        		+ SharePointHelper.formatStringForUrl("')/fields")
	        		
	        		//+ SharePointHelper.formatStringForUrl("')/fields?$filter=Hidden eq false and ReadOnlyField eq false")
	        		
	        		+ SharePointHelper.formatStringForUrl("')/fields?$select=Title,TypeAsString,TypeDisplayName,InternalName&$filter=Hidden eq false and ReadOnlyField eq false and TypeAsString ne 'Computed'")
	        		;
	    	 
	    	 
		
	        HttpClientBuilder clientbuilder = HttpClients.custom();
	        SharePointHelper.setProxyCredentials(clientbuilder, proxyEnabled, proxyHost, proyPort, proxyUser, proxyPass);
	         

	        HttpClient client = clientbuilder.build();
	        
	        HttpGet get = new HttpGet(url);
	        SharePointHelper.createProxyRequestConfig(get, proxyEnabled, proxyHost, proyPort);
	        
		    get.setHeader("Authorization", "Bearer " + token);
		    get.setHeader("accept", "application/json;odata=verbose");

	        /* Executing the post request */
		    ClassicHttpResponse response = (ClassicHttpResponse) client.execute(get);
  
	        String responseBody = EntityUtils.toString(response.getEntity());

	        pushFlowVariableString("ResponseStatus", String.valueOf(response.getCode()));
	        pushFlowVariableString("ResponseString", responseBody);
	        
	        
	        BufferedDataContainer container = exec.createDataContainer(getSpec());
	    

		     if(response.getCode()==200)
		     {
		    	
		    	 
		    	 parseJsonResult(responseBody, container);
		    	 container.close();
		
		    	 return new BufferedDataTable[] { container.getTable() };
		    }
		    else
		    {
		    	container.close();
		    	return new BufferedDataTable[] { container.getTable() };
		    }
		}
	    else
	    {
	    	return new BufferedDataTable[] {};
	    }	    
	    
		
	}
	
	
	private void parseJsonResult(String responseBody, BufferedDataContainer container) {
		JSONObject jsonObj = new JSONObject(responseBody); 
		    // "I want to iterate though the objects in the array..."

		    JSONObject innerObject = jsonObj.getJSONObject("d");
		    
		    JSONArray jsonArray = innerObject.getJSONArray("results");
		    int rowCnt = 0;
		    
		    
		    for (int i = 0, size = jsonArray.length(); i < size; i++)
		    {
		      JSONObject objectInArray = jsonArray.getJSONObject(i);
		            
		      
		      addRow(
		    		  container
		    		  ,"Row_"+String.valueOf(rowCnt)
		    		  ,SharePointHelper.getJsonString(objectInArray, "Title")
		    		  ,SharePointHelper.getJsonString(objectInArray, "TypeAsString")
		    		  ,SharePointHelper.getJsonString(objectInArray, "TypeDisplayName")
		    		  ,SharePointHelper.getJsonString(objectInArray, "InternalName")
		    		  
		    		  

		       );		      
		      
		      
		      rowCnt++;
		    }
		    
	
			
	
	}


	private void addRow(
			BufferedDataContainer container
			,String key
			,String Title
			,String TypeAsString
			,String TypeDisplayName
			,String InternalName
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(key), new DataCell[] { 
						StringCellFactory.create(Title)
						,StringCellFactory.create(TypeAsString)
						,StringCellFactory.create(TypeDisplayName)
						,StringCellFactory.create(InternalName)
						
				}));
	}
	

	
	private DataTableSpec getSpec()
	{
		
		
		DataTableSpecCreator crator = new DataTableSpecCreator();

		crator.addColumns(new DataColumnSpecCreator("Title", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("TypeAsString", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("TypeDisplayName", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("InternalName", StringCellFactory.TYPE).createSpec());
		return crator.createSpec();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { getSpec() };		
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
		m_SPListName.saveSettingsTo(settings);

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

		m_SPListName.loadSettingsFrom(settings);
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
		m_SPListName.validateSettings(settings);
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

