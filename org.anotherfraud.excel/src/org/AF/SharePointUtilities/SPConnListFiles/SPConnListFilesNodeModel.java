package org.AF.SharePointUtilities.SPConnListFiles;

import java.io.File;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import org.AF.Connections.ConnectionInformation;
import org.AF.Connections.ConnectionInformationPortObjectSpec;
import org.AF.Connections.SPConnectionInformationPortObject;
import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.IntCell.IntCellFactory;
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
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;


/**
 * This is an example implementation of the node model of the
 * "SPConnListFiles" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author AnotherFraud
 */
public class SPConnListFilesNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(SPConnListFilesNodeModel.class);


	
    static final String SharePointName = "SharePointName";
    static final String spFolderPath = "spFolderPath";
    
	
    
 

	
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
	private final SettingsModelString m_SPFolderPath = createSpFolderPathSettingsModel();

	
	
	/**
	 * Constructor for the node model.
	 */	
	protected SPConnListFilesNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[]{SPConnectionInformationPortObject.TYPE}, new PortType[] { BufferedDataTable.TYPE});
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
		LOGGER.info("Starting SP List Files");

		SPConnectionInformationPortObject spConn = (SPConnectionInformationPortObject)inObjects[0];
		ConnectionInformation connInfo = spConn.getConnectionInformation();
		
		
		
		
	    /* Token variable declaration */
	    String token = connInfo.getToken();
	    
	    
	    String folderpath = m_SPFolderPath.getStringValue();
	    String sharePointSite = connInfo.getSharePointOnlineSiteURL();
	    String sharePointName = m_sharePointName.getStringValue();

	    
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
	        		+ sharePointName
	        		+ "/_api/Web/GetFolderByServerRelativeUrl('"
	        		+ SharePointHelper.buildCompleteSPPath(folderpath, sharePointName)
	        		+ "')?$expand=Folders,Files&select=FileLeafRef,FileRef,Id";
	        

	     	
	
        	
        		
	        HttpClientBuilder clientbuilder = HttpClients.custom();
	        SharePointHelper.setProxyCredentials(clientbuilder, proxyEnabled, proxyHost, proyPort, proxyUser, proxyPass);
	   	 
	        HttpClient client = clientbuilder.build();
	        

	        
 
	        HttpPost post = new HttpPost(url);
	        SharePointHelper.createProxyRequestConfig(post, proxyEnabled, proxyHost, proyPort);
	        

		    
	        
		    post.setHeader("Authorization", "Bearer " + token);
		    post.setHeader("accept", "application/json;odata=verbose");

	        

	        /* Executing the post request */
	        HttpResponse response = client.execute(post);
	       
	        
	        String responseBody = EntityUtils.toString(response.getEntity());
	        

	        pushFlowVariableString("ResponseStatus", response.getStatusLine().toString());
	        pushFlowVariableString("ResponseString", responseBody);
	        

	        BufferedDataContainer container = exec.createDataContainer(getSpec());
	        
	     if(response.getStatusLine().getStatusCode()==200)
	     {
	    	
	    	 
	    	 //parseJsonResult(responseBody, container);
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
	        BufferedDataContainer container = exec.createDataContainer(getSpec());
	        container.close();
	        return new BufferedDataTable[] { container.getTable() };
	    }
	    
	    
	    
        
        

	}


	private void parseJsonResult(String responseBody, BufferedDataContainer container) {
		JSONObject jsonObj = new JSONObject(responseBody); 
		    // "I want to iterate though the objects in the array..."

		    JSONObject innerObject = jsonObj.getJSONObject("d").getJSONObject("Files");
		    
		    JSONArray jsonArray = innerObject.getJSONArray("results");
		    int rowCnt = 0;
		    
		    
		    for (int i = 0, size = jsonArray.length(); i < size; i++)
		    {
		      JSONObject objectInArray = jsonArray.getJSONObject(i);
		      addRow(
		    		  container
		    		  ,"Row:"+String.valueOf(rowCnt)
		    		  ,objectInArray.getString("CheckInComment")
		    		  ,objectInArray.getInt("CheckOutType")
		    		  ,objectInArray.getString("ContentTag")
		    		  ,objectInArray.getString("ETag")
		    		  ,objectInArray.getBoolean("Exists")
		    		  ,objectInArray.getBoolean("IrmEnabled")
		    		  ,objectInArray.getString("Length")
		    		  ,objectInArray.getInt("Level")
		    		  ,objectInArray.getString("LinkingUri")
		    		  ,objectInArray.getString("LinkingUrl")
		    		  ,objectInArray.getInt("MajorVersion")
		    		  ,objectInArray.getInt("MinorVersion")
		    		  ,objectInArray.getString("Name")
		    		  ,objectInArray.getString("ServerRelativeUrl")
		    		  ,objectInArray.getString("TimeCreated")
		    		  ,objectInArray.getString("TimeLastModified")
		    		  ,objectInArray.getString("Title")
		    		  ,objectInArray.getString("UniqueId")
		    		  ,"SP.File"
		    		  ,-1
		       );
		      rowCnt++;
		    }
		    
		    
		    
		    innerObject = jsonObj.getJSONObject("d").getJSONObject("Folders"); 
		    jsonArray = innerObject.getJSONArray("results");
		    
		    for (int i = 0, size = jsonArray.length(); i < size; i++)
		    {
		      JSONObject objectInArray = jsonArray.getJSONObject(i);
		      addRow(
		    		  container
		    		  ,"Row:"+String.valueOf(rowCnt)
		    		  ,""
		    		  ,-1
		    		  ,""
		    		  ,""
		    		  ,objectInArray.getBoolean("Exists")
		    		  ,false
		    		  ,""
		    		  ,-1
		    		  ,""
		    		  ,""
		    		  ,-1
		    		  ,-1
		    		  ,objectInArray.getString("Name")
		    		  ,objectInArray.getString("ServerRelativeUrl")
		    		  ,objectInArray.getString("TimeCreated")
		    		  ,objectInArray.getString("TimeLastModified")
		    		  ,""
		    		  ,objectInArray.getString("UniqueId")
		    		  ,"SP.Folder"
		    		  ,objectInArray.getInt("ItemCount")
		       );
		      rowCnt++;
		    }		    
			
	
	}

	
	
	
	
	
	private void addRow(
			BufferedDataContainer container
			,String key
			,String CheckInComment
			,int CheckOutType
			,String ContentTag
			,String ETag
			,boolean Exists
			,boolean IrmEnabled
			,String Length
			,int Level
			,String LinkingUri
			,String LinkingUrl
			,int MajorVersion
			,int MinorVersion
			,String Name
			,String ServerRelativeUrl
			,String TimeCreated
			,String TimeLastModified
			,String Title
			,String UniqueId
			,String Type
			,int ItemCount
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(key), new DataCell[] { 
					
						StringCellFactory.create(Name)
						,StringCellFactory.create(Type)
						,StringCellFactory.create(TimeCreated)
						,StringCellFactory.create(TimeLastModified)
						,StringCellFactory.create(Title)
						,StringCellFactory.create(UniqueId)
						
					
						,StringCellFactory.create(LinkingUri)
						,StringCellFactory.create(LinkingUrl)
						,StringCellFactory.create(ServerRelativeUrl)
						
						,IntCellFactory.create(MajorVersion)
						,IntCellFactory.create(MinorVersion)
								
						
						,StringCellFactory.create(CheckInComment)
						,IntCellFactory.create(CheckOutType)
						,StringCellFactory.create(ContentTag)
						,StringCellFactory.create(ETag)
						,BooleanCellFactory.create(Exists)
						,BooleanCellFactory.create(IrmEnabled)
						,StringCellFactory.create(Length)
						,IntCellFactory.create(Level)
						,IntCellFactory.create(ItemCount)
						
						
	
				
				}));
	}
	
	
	private DataTableSpec getSpec()
	{
		
		
		DataTableSpecCreator crator = new DataTableSpecCreator();
		
		
		
		crator.addColumns(new DataColumnSpecCreator("Name", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Type", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("TimeCreated", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("TimeLastModified", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Title", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("UniqueId", StringCellFactory.TYPE).createSpec());

		crator.addColumns(new DataColumnSpecCreator("LinkingUri", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("LinkingUrl", StringCellFactory.TYPE).createSpec());
		
		crator.addColumns(new DataColumnSpecCreator("ServerRelativeUrl", StringCellFactory.TYPE).createSpec());
						
		crator.addColumns(new DataColumnSpecCreator("MajorVersion", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("MinorVersion", IntCellFactory.TYPE).createSpec());
		
		
		crator.addColumns(new DataColumnSpecCreator("CheckInComment", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("CheckOutType", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("ContentTag", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("ETag", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Exists", BooleanCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("IrmEnabled", BooleanCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Length", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Level", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("ItemCount", IntCellFactory.TYPE).createSpec());	


		

        
        
        

        	
        	
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

