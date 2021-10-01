package org.AF.SharePointUtilities.Conn.GetSPListItems;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.AF.Connections.ConnectionInformation;
import org.AF.Connections.ConnectionInformationPortObject;
import org.AF.SharePointUtilities.SharePointHelper.SharePointHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;


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


	    static final String SharePointName = "SharePointName";
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
		
		static SettingsModelString createSharePointNameSettingsModel() {
			SettingsModelString coof = new SettingsModelString(SharePointName, "<yourSharepointSiteName>");
			coof.setEnabled(true);
			return coof;				
		}	

		private final SettingsModelString m_sharePointName = createSharePointNameSettingsModel();
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
				HttpResponse responseColumnInfo = client.execute(get);
		  
			    String responseBodyColumnInfo = EntityUtils.toString(responseColumnInfo.getEntity());
					        

			        
			        
				    if(responseColumnInfo.getStatusLine().getStatusCode()==200)
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
					        		+ SharePointHelper.formatStringForUrl("')/items?$skiptoken=Paged=TRUE&$orderby=Created " + loadOrder);
				    	 
				    	 clientbuilder = HttpClients.custom();
					     SharePointHelper.setProxyCredentials(clientbuilder, proxyEnabled, proxyHost, proyPort, proxyUser, proxyPass);

					     client = clientbuilder.build();

					     get = new HttpGet(url);
					     SharePointHelper.createProxyRequestConfig(get, proxyEnabled, proxyHost, proyPort);
						 get.setHeader("Authorization", "Bearer " + token);
						 get.setHeader("accept", "application/json;odata=verbose");
						 
						 int rowCnt = 0;
						 int responseCnt = 0;
						 JSONObject responseJson = new JSONObject();
						
				    	 while(getNext)
				    	 {
					         get.setURI(URI.create(url));
					    	 HttpResponse response = client.execute(get);
							 String responseBody = EntityUtils.toString(response.getEntity());
							 
							 rowCnt = parseJsonResult(responseBody, container, columnHeaders,rowCnt);
					    		 
	
							 JSONObject jsonObj = new JSONObject(responseBody);
							 
							 responseJson.put("response"+String.valueOf(responseCnt), jsonObj);
							 
							 JSONObject innerObject = jsonObj.getJSONObject("d");
							
							 if (innerObject.has("__next") 
									 && (m_loadall.getBooleanValue() || itemlimt > rowCnt)
							 )
							 {				
							 url = (String) innerObject.get("__next");
							 responseCnt++;
							 }
							 else
							 {
							 	getNext = false;
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
			
			JSONObject jsonObj = new JSONObject(tableConfig);
			
		    JSONObject innerObject = jsonObj.getJSONObject("d");	    
		    JSONArray jsonArray = innerObject.getJSONArray("results");		
		    
		    String[][] columnHeader = new String[jsonArray.length()][3];
		    		    

		    for (int i = 0, size = jsonArray.length(); i < size; i++)
		    {
		      JSONObject objectInArray = jsonArray.getJSONObject(i);
		            
		      columnHeader[i][0] = SharePointHelper.getJsonString(objectInArray, "Title");
		      columnHeader[i][1] = SharePointHelper.getJsonString(objectInArray, "TypeAsString");
		      columnHeader[i][2] = SharePointHelper.getJsonString(objectInArray, "InternalName");
		    }		
		    
			
			return columnHeader;
					
		}
		
		
		private int parseJsonResult(String responseBody, BufferedDataContainer container, String[][] columnHeaders, int rowCnt) {
			JSONObject jsonObj = new JSONObject(responseBody); 


			    JSONObject innerObject = jsonObj.getJSONObject("d");	    
			    JSONArray jsonArray = innerObject.getJSONArray("results");
	    
			    DataCell[] cells = new DataCell[columnHeaders.length];
			    
			    
			    
			 
			    
			    for (int i = 0, size = jsonArray.length(); i < size; i++)
			    {
			      JSONObject objectInArray = jsonArray.getJSONObject(i);
			            
			      

			    	
			      
			      
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
			
			m_sharePointName.saveSettingsTo(settings);
			m_SPListName.saveSettingsTo(settings);
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

			m_SPListName.loadSettingsFrom(settings);
			m_sharePointName.loadSettingsFrom(settings);
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
			m_SPListName.validateSettings(settings);
			m_sharePointName.validateSettings(settings);
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

