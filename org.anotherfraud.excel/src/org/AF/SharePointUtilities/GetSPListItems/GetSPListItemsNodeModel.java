package org.AF.SharePointUtilities.GetSPListItems;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell.DoubleCellFactory;
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
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



/**
 * This is an example implementation of the node model of the
 * "GetSPListItems" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class GetSPListItemsNodeModel extends NodeModel {
    
	   

		private static final NodeLogger LOGGER = NodeLogger.getLogger(GetSPListItemsNodeModel.class);



	    static final String proxyAuth = "proxyAuth";
	    static final String SharePointOnlineSiteURL = "SharePointOnlineSiteURL";
	    static final String ClientToken = "ClientToken";
	    static final String SharePointName = "SharePointName";
	    static final String UseProxy = "UseProxy";
	    static final String ProxyHost = "ProxyHost";
	    static final String ProxyPort = "ProxyPort";
	    static final String spListName = "spListName";
	    static final String loadingOrder = "loadingOrder";
	    static final String itemLimit = "itemLimit";
	    static final String loadAll = "loadAll";

	    static SettingsModelBoolean createLoadAllSettingsModel() {
			SettingsModelBoolean wlr = new SettingsModelBoolean(loadAll, true);
			return wlr;				
		}	
	    
	    static SettingsModelIntegerBounded createItemLimitSettingsModel() {
			SettingsModelIntegerBounded si = new SettingsModelIntegerBounded(itemLimit, 5000, 100, 2147483647);
			si.setEnabled(false);
	        return si;
	    }	
	    
	    static SettingsModelString createLoadingOrderSettingsModel() {
			SettingsModelString coof = new SettingsModelString(loadingOrder, "Ascending Creation Date");
			coof.setEnabled(true);
			return coof;				
		}	
	    
	 

		
		static SettingsModelString createSpListNameSettingsModel() {
			SettingsModelString coof = new SettingsModelString(spListName, "Shared%20Documents/<FolderName>/<FileName>");
			coof.setEnabled(true);
			return coof;				
		}	
		
		
		static SettingsModelAuthentication createProxySettingsModel() {
			SettingsModelAuthentication cps = new SettingsModelAuthentication(proxyAuth, AuthenticationType.CREDENTIALS);
			cps.setEnabled(false);
			return cps;
		}
		
		static SettingsModelString createSharePointUrlSettingsModel() {
			SettingsModelString coof = new SettingsModelString(SharePointOnlineSiteURL, "<yourSharepoint>.sharepoint.com");
			coof.setEnabled(true);
			return coof;				
		}	
		

		static SettingsModelAuthentication createClientTokenSettingsModel() {
			SettingsModelAuthentication cps = new SettingsModelAuthentication(ClientToken, AuthenticationType.CREDENTIALS);
			cps.setEnabled(true);
			return cps;				
		}	
		
		static SettingsModelString createSharePointNameSettingsModel() {
			SettingsModelString coof = new SettingsModelString(SharePointName, "<yourSharepointSiteName>");
			coof.setEnabled(true);
			return coof;				
		}	


		
		
		
		static SettingsModelString createUseProxySettingsModel() {
			SettingsModelString coof = new SettingsModelString(UseProxy, "no proxy");
			coof.setEnabled(true);
			return coof;				
		}	
	    
	    
		static SettingsModelIntegerBounded createProxyPortIndexModel() {
			SettingsModelIntegerBounded si = new SettingsModelIntegerBounded(ProxyPort, 8080, 0, 65535);
			si.setEnabled(false);
	        return si;
	    }	
		
		static SettingsModelString createProxyHostSettingsModel() {
			SettingsModelString coof = new SettingsModelString(ProxyHost, null);
			coof.setEnabled(false);
			return coof;				
		}		
		


		
		
		
		
		private final SettingsModelAuthentication m_proxy = createProxySettingsModel();
		private final SettingsModelAuthentication m_clientToken = createClientTokenSettingsModel();
		private final SettingsModelString m_sharePointName = createSharePointNameSettingsModel();
		private final SettingsModelString m_useProxy = createUseProxySettingsModel();
		private final SettingsModelIntegerBounded m_proxyPort = createProxyPortIndexModel();
		private final SettingsModelString m_proxyHost = createProxyHostSettingsModel();
		private final SettingsModelString m_sharePointUrl = createSharePointUrlSettingsModel();
		private final SettingsModelString m_SPListName = createSpListNameSettingsModel();
		private final SettingsModelString m_loadOrder = createLoadingOrderSettingsModel();
		private final SettingsModelIntegerBounded m_itemlimit = createItemLimitSettingsModel();
		private final SettingsModelBoolean m_loadall = createLoadAllSettingsModel();
		

		/**
		 * Constructor for the node model.
		 */
		protected GetSPListItemsNodeModel() {
			/**
			 * Here we specify how many data input and output tables the node should have.
			 * In this case its one input and one output table.
			 */
			super(new PortType[] {FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[] { BufferedDataTable.TYPE});
			
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

			
		    /* Token variable declaration */
		    String token = m_clientToken.getPassword(getCredentialsProvider());
		    String listName = m_SPListName.getStringValue();
		    String sharePointSite = m_sharePointUrl.getStringValue();
		    String sharePointName = m_sharePointName.getStringValue();

		    
	 	    String proxyUser = m_proxy.getUserName(getCredentialsProvider());
	 	    String proxyPass = m_proxy.getPassword(getCredentialsProvider());
	 	    		
	    	String proxyHost = m_proxyHost.getStringValue();
			int proyPort = m_proxyPort.getIntValue();    
		    boolean proxyEnabled = m_useProxy.getStringValue().equals("Use Proxy");
		    	
		    
		    String loadOrder = (m_loadOrder.getStringValue().equals("Ascending Creation Date")) ? "asc" :  "desc";
		    int itemlimt = m_itemlimit.getIntValue();
		    
			
		
		    
		    /* Null or fail check */
		    if (!token.isEmpty())
		    { 
		        /* Upload path and file name declaration */
		        /*
		         * https://<your_domain>.sharepoint.com/_api/web/
		         * GetFolderByServerRelativeUrl('/Shared%20Documents/<FolderName>')/
		         * Files/
		         */

		        BufferedDataContainer container;

		        //get column types for table parsing
		        String url = "https://"
			        		+ sharePointSite
			        		+ "/sites/"
			        		+ SharePointHelper.formatStringForUrl(sharePointName)
			        		+ "/_api/web/lists/getbytitle('"
			        		+ SharePointHelper.formatStringForUrl(listName)
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
				ClassicHttpResponse responseColumnInfo = (ClassicHttpResponse) client.execute(get);
		  
			    String responseBodyColumnInfo = EntityUtils.toString(responseColumnInfo.getEntity());
					        

			        
			        
				    if(responseColumnInfo.getCode()==200)
					     {   
				    	
				    	String[][] columnHeaders = parseColumnHeaderInfo(responseBodyColumnInfo);
				    	container = exec.createDataContainer(createSpec(columnHeaders));
				    	 
				    	 Boolean getNext = true;
				    	 url = "https://"
					        		+ sharePointSite
					        		+ "/sites/"
					        		+ sharePointName
					        		+ "/_api/web/lists/GetByTitle('"
					        		+ SharePointHelper.formatStringForUrl(listName)
					        		+ SharePointHelper.formatStringForUrl("')/items?&$orderby=Id " + loadOrder + "&$skiptoken=Paged=TRUE&$top=100");
					        		//+ SharePointHelper.formatStringForUrl("')/items?$skiptoken=Paged=TRUE&$orderby=Created " + loadOrder);
				    	 
				    	 clientbuilder = HttpClients.custom();
					     SharePointHelper.setProxyCredentials(clientbuilder, proxyEnabled, proxyHost, proyPort, proxyUser, proxyPass);

					     client = clientbuilder.build();

					     get = new HttpGet(url);
					     SharePointHelper.createProxyRequestConfig(get, proxyEnabled, proxyHost, proyPort);
						 get.setHeader("Authorization", "Bearer " + token);
						 get.setHeader("accept", "application/json;odata=verbose");
						 
						 int rowCnt = 0;
						 int responseCnt = 0;
				

						 
						 Gson gson = new Gson();
						 JsonObject responseJson = new JsonObject();
	 
						 while(getNext)
				    	 {
					         get.setUri(URI.create(url));
					    	 ClassicHttpResponse response = (ClassicHttpResponse) client.execute(get);
							 String responseBody = EntityUtils.toString(response.getEntity());
							 
							 JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
							 if (jsonObj.has("d"))
							 {
								 
								 rowCnt = parseJsonResult(responseBody, container, columnHeaders,rowCnt);					 
								 responseJson.add("response" + String.valueOf(responseCnt), jsonObj);
								 
								 JsonObject innerObject = jsonObj.getAsJsonObject("d");
								
								 if (innerObject.has("__next") 
										 && (m_loadall.getBooleanValue() || itemlimt > rowCnt)
								 )
								 {				
								 url = innerObject.get("__next").getAsString();
								 responseCnt++;
								 }
								 else
								 {
								 	getNext = false;
								 }
							 
							 }
							 else
							 {
								 pushFlowVariableString("ErrorResponseString", responseBody);
								 pushFlowVariableString("ErrorResponseStatus", Integer.toString(response.getCode()));
								 break;
							 }
							 

				    	 }
				    	 
				    	 
				    		

				    	 container.close();

					     pushFlowVariableString("ResponseString", responseJson.toString());
				    	 
				    	 return new BufferedDataTable[] { container.getTable() };
				    	 
				    	 
				    }
				    else
				    {
				    	return new BufferedDataTable[] { };
				    }
				    
				    
				    
				    
			     		        
		    }

		    return new BufferedDataTable[] { };
			
		}
		
		
		
		private String[][] parseColumnHeaderInfo(String tableConfig)
		{
			
			Gson gson = new Gson();
			JsonObject jsonObj = gson.fromJson(tableConfig, JsonObject.class);
			JsonObject innerObject = jsonObj.getAsJsonObject("d");
			JsonArray jsonArray = innerObject.getAsJsonArray("results");	
		    
		    String[][] columnHeader = new String[jsonArray.size()][3];
		    		    

		    for (int i = 0, size = jsonArray.size(); i < size; i++)
		    {
		      JsonObject objectInArray = jsonArray.get(i).getAsJsonObject();
		            
		      columnHeader[i][0] = SharePointHelper.getJsonString(objectInArray, "Title");
		      columnHeader[i][1] = SharePointHelper.getJsonString(objectInArray, "TypeAsString");
		      columnHeader[i][2] = SharePointHelper.getJsonString(objectInArray, "InternalName");
		    }		
		    
			
			return columnHeader;
					
		}
		
		
		private int parseJsonResult(String responseBody, BufferedDataContainer container, String[][] columnHeaders, int rowCnt) {
				Gson gson = new Gson();
				JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
				JsonObject innerObject = jsonObj.getAsJsonObject("d");
				JsonArray jsonArray = innerObject.getAsJsonArray("results");
			     

			    
			    DataCell[] cells = new DataCell[columnHeaders.length];
			    
			    
			    
			 
			    
			    for (int i = 0, size = jsonArray.size(); i < size; i++)
			    {
			     JsonObject objectInArray = jsonArray.get(i).getAsJsonObject();
			            
			      

			    	
			      
			      
			      for(int c = 0, sizeHead = columnHeaders.length; c < sizeHead; c++)
				    {
				    

					    if (columnHeaders[c][1].equals("Text") 
					    		|| columnHeaders[c][1].equals("Attachments") 
					    		|| columnHeaders[c][1].equals("Note")
					    		|| columnHeaders[c][1].equals("Choice")
					    		|| columnHeaders[c][1].equals("UserAA")
					    		|| columnHeaders[c][1].equals("Thumbnail")
					    )
					    {
					    	cells[c] = StringCellFactory.create(SharePointHelper.getJsonString(objectInArray, columnHeaders[c][2]));
					    	
					    }
					    else if (columnHeaders[c][1].equals("Number") 
					    		|| columnHeaders[c][1].equals("Currency"))
					    {
					    	cells[c] = nullableDoubleCell(SharePointHelper.getJsonDouble(objectInArray, columnHeaders[c][2]));
					    }
					    else if (columnHeaders[c][1].equals("Boolean"))
					    {
					    	cells[c] = nullableBoolCell(SharePointHelper.getJsonBoolean(objectInArray, columnHeaders[c][2]));
					    }
					    else if (columnHeaders[c][1].equals("DateTime"))
					    {
					    	cells[c] = StringCellFactory.create(SharePointHelper.getJsonString(objectInArray, columnHeaders[c][2]));
					    }	
					    else if (columnHeaders[c][1].equals("URL"))
					    {
			
					    	cells[c] = StringCellFactory.create(SharePointHelper.getStringFromNullableJsonObject(objectInArray, columnHeaders[c][2],"Url"));
					    	//cells[c] = StringCellFactory.create(SharePointHelper.getJsonString(subOjectInArray, "Url"));
					    }	
					    else if (columnHeaders[c][1].equals("User"))
					    {
			
					    	cells[c] = StringCellFactory.create(SharePointHelper.getStringFromNullableJsonObject(objectInArray, columnHeaders[c][2] + "Id","Url"));
					    	//cells[c] = StringCellFactory.create(SharePointHelper.getJsonString(subOjectInArray, "Url"));
					    }	
					    else
					    {
					    	cells[c] = StringCellFactory.create(SharePointHelper.getJsonString(objectInArray, columnHeaders[c][2]));
					    }
				    }

	
			      container.addRowToTable(
							new DefaultRow(new RowKey("Row_"+String.valueOf(rowCnt)), cells));
			      
      
			      
			      
			      rowCnt++;
			    }
			    return rowCnt;
			 		
				
		
		}




		
		private DataCell nullableDoubleCell(Double i){
	        if (i == null) {
	            return DataType.getMissingCell();
	        }
	        return DoubleCellFactory.create(i);	
			
		}
		
		private DataCell nullableBoolCell(Boolean b){
	        if (b == null) {
	            return DataType.getMissingCell();
	        }
	        return BooleanCellFactory.create(b);	
			
		}
		
		


		
		
		
		
		
		private DataTableSpec createSpec(String[][] columnHeaders)
		{
			
			
			DataTableSpecCreator crator = new DataTableSpecCreator();

			
			
			
		    for(int c = 0, sizeHead = columnHeaders.length; c < sizeHead; c++)
		    {
		    
			    if (columnHeaders[c][1].equals("Text") 
			    		|| columnHeaders[c][1].equals("Attachments") 
			    		|| columnHeaders[c][1].equals("Note")
			    		|| columnHeaders[c][1].equals("Choice")
			    		|| columnHeaders[c][1].equals("URL")
			    		|| columnHeaders[c][1].equals("Thumbnail")
			    )
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0], StringCellFactory.TYPE).createSpec());
			    }
			    else if (columnHeaders[c][1].equals("Number") 
			    		|| columnHeaders[c][1].equals("Currency"))
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0], DoubleCellFactory.TYPE).createSpec());
			    }
			    else if (columnHeaders[c][1].equals("Boolean"))
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0], BooleanCellFactory.TYPE).createSpec());
			    }
			    else if (columnHeaders[c][1].equals("DateTime"))
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0], StringCellFactory.TYPE).createSpec());
			    }	
			    else if (columnHeaders[c][1].equals("User"))
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0] + " (ID)", StringCellFactory.TYPE).createSpec());
			    }
			    else
			    {
			    	crator.addColumns(new DataColumnSpecCreator(columnHeaders[c][0], StringCellFactory.TYPE).createSpec());
			    }
 	  
		    }
	 	
			return crator.createSpec();
		}
		
		
		

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected DataTableSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
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
			
			
			return new DataTableSpec[] {null};

			
			
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
			
			m_proxy.saveSettingsTo(settings);

			m_sharePointName.saveSettingsTo(settings);
			m_useProxy.saveSettingsTo(settings);
			m_proxyPort.saveSettingsTo(settings);
			m_proxyHost.saveSettingsTo(settings);
			m_sharePointUrl.saveSettingsTo(settings);
			m_SPListName.saveSettingsTo(settings);
			m_clientToken.saveSettingsTo(settings);
			m_loadOrder.saveSettingsTo(settings);
			m_itemlimit.saveSettingsTo(settings);
			m_loadall.saveSettingsTo(settings);
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

			m_proxy.loadSettingsFrom(settings);
			m_SPListName.loadSettingsFrom(settings);
			m_sharePointName.loadSettingsFrom(settings);
			m_useProxy.loadSettingsFrom(settings);
			m_proxyPort.loadSettingsFrom(settings);
			m_proxyHost.loadSettingsFrom(settings);
			m_sharePointUrl.loadSettingsFrom(settings);
			m_clientToken.loadSettingsFrom(settings);
			m_loadOrder.loadSettingsFrom(settings);
			m_itemlimit.loadSettingsFrom(settings);
			m_loadall.loadSettingsFrom(settings);		
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
			m_proxy.validateSettings(settings);
			m_clientToken.validateSettings(settings);
			m_SPListName.validateSettings(settings);
			m_sharePointName.validateSettings(settings);
			m_useProxy.validateSettings(settings);
			m_proxyPort.validateSettings(settings);
			m_proxyHost.validateSettings(settings);
			m_sharePointUrl.validateSettings(settings);
			m_loadOrder.validateSettings(settings);
			m_itemlimit.validateSettings(settings);
			m_loadall.validateSettings(settings);
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

