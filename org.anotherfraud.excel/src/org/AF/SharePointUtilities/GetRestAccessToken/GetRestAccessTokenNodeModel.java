package org.AF.SharePointUtilities.GetRestAccessToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.openpgp.PGPException;
import org.json.JSONObject;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.Node;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.workflow.CredentialsStore;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.filehandling.core.connections.FSConnection;


/**
 * This is an example implementation of the node model of the
 * "GetRestAccessToken" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author AnotherFraud
 */
public class GetRestAccessTokenNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(GetRestAccessTokenNodeModel.class);
	private Optional<FSConnection> m_fs = Optional.empty();


	
    static final String proxyAuth = "proxyAuth";
    static final String ClientID = "ClientID";
    static final String SharePointOnlineSiteURL = "SharePointOnlineSiteURL";
    static final String ClientSecret = "ClientSecret";
    static final String TennantID = "TennantID";
    static final String UseProxy = "UseProxy";
    static final String ProxyHost = "ProxyHost";
    static final String ProxyPort = "ProxyPort";

    
    
	static SettingsModelAuthentication createProxySettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(proxyAuth, AuthenticationType.USER_PWD);
		cps.setEnabled(false);
		return cps;
	}
	
	static SettingsModelString createSharePointUrlSettingsModel() {
		SettingsModelString coof = new SettingsModelString(SharePointOnlineSiteURL, null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createClientIDSettingsModel() {
		SettingsModelString coof = new SettingsModelString(ClientID, null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelAuthentication createClientSecretSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(ClientSecret, AuthenticationType.PWD);
		cps.setEnabled(true);
		return cps;				
	}	
	
	static SettingsModelString createTennantIDSettingsModel() {
		SettingsModelString coof = new SettingsModelString(TennantID, null);
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
	private final SettingsModelString m_clientID = createClientIDSettingsModel();
	private final SettingsModelAuthentication m_clientSecret = createClientSecretSettingsModel();
	private final SettingsModelString m_tennantID = createTennantIDSettingsModel();
	private final SettingsModelString m_useProxy = createUseProxySettingsModel();
	private final SettingsModelIntegerBounded m_proxyPort = createProxyPortIndexModel();
	private final SettingsModelString m_proxyHost = createProxyHostSettingsModel();
	private final SettingsModelString m_sharePointUrl = createSharePointUrlSettingsModel();
	
	
	/**
	 * Constructor for the node model.
	 */
	protected GetRestAccessTokenNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[] {FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[] {FlowVariablePortObject.TYPE});
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
		LOGGER.info("Starting get SP access token");

		
		 /*
	     * see http://www.ktskumar.com/2017/01/access-sharepoint-online-using-postman/
	     */


					
			
		String tennant = m_tennantID.getStringValue();
		String clientId = m_clientID.getStringValue();
		String proxyHost = m_proxyHost.getStringValue();
		int proyPort = m_proxyPort.getIntValue();
		
		
		String resource = "00000003-0000-0ff1-ce00-000000000000/"
				+m_sharePointUrl.getStringValue()
				+"@" + tennant;	
		
	
	    String url = "https://accounts.accesscontrol.windows.net/"
	    		+tennant
	    		+"/tokens/OAuth/2";
	    
	    
	    
		    
	    	
	        CredentialsProvider credentialsPovider = new BasicCredentialsProvider();
        	credentialsPovider.setCredentials(new AuthScope(proxyHost, proyPort), new
        		   UsernamePasswordCredentials(m_proxy.getUsername(), m_proxy.getPassword()));
        		
        		
	        HttpClientBuilder clientbuilder = HttpClients.custom();
	        clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);
	        
	        

	        HttpClient client = clientbuilder.build();
	        
	        //HttpHost target = new HttpHost("google.de", 80, "http");
	        
	        HttpHost proxy = new HttpHost(proxyHost, proyPort, "http");
	        
	        RequestConfig.Builder reqconfigconbuilder= RequestConfig.custom();
	        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxy);
	        RequestConfig config = reqconfigconbuilder.build();

	        

		    HttpPost post = new HttpPost(url);
		    post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		    
		    if (m_useProxy.getStringValue().equals("Use Proxy"))
		    {
		        post.setConfig(config);    	
		    }



		    /* Adding URL Parameters */
		    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		    urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
		    urlParameters.add(new BasicNameValuePair("client_id", clientId));
		    urlParameters.add(new BasicNameValuePair("client_secret", m_clientSecret.getPassword()));
		    urlParameters.add(new BasicNameValuePair("resource", resource));
		    
		    post.setEntity(new UrlEncodedFormEntity(urlParameters));

		    /* Executing the post request */
		    HttpResponse response = client.execute(post);


		    System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		    
		    
		    String json_string = EntityUtils.toString(response.getEntity());
		    
		    JSONObject temp1 = new JSONObject(json_string);  
		    System.out.println("Json : " + temp1.get("access_token").toString());
		    
		    String accessToken = temp1.get("access_token").toString();
		    
		    
		    
		    if (accessToken != null)
		    {
				//create credentials varaiable
				FlowVariable flowVar =   CredentialsStore.newCredentialsFlowVariable("SP_AccessToken", clientId, accessToken, false, false);
				Node.invokePushFlowVariable(this, flowVar);
				//pushFlowVariableString("Token", accessToken);
		    }
		    else
		    {
				 throw new InvalidSettingsException(
							"No token response for given settings.\n"
						 			+ "HttpStatus:" 
									+ response.getStatusLine().getStatusCode()
									+ "\n"
									+ json_string
						 );
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
		
		m_proxy.saveSettingsTo(settings);
		m_clientID.saveSettingsTo(settings);
		m_clientSecret.saveSettingsTo(settings);
		m_tennantID.saveSettingsTo(settings);
		m_useProxy.saveSettingsTo(settings);
		m_proxyPort.saveSettingsTo(settings);
		m_proxyHost.saveSettingsTo(settings);
		m_sharePointUrl.saveSettingsTo(settings);
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
		m_clientID.loadSettingsFrom(settings);
		m_clientSecret.loadSettingsFrom(settings);
		m_tennantID.loadSettingsFrom(settings);
		m_useProxy.loadSettingsFrom(settings);
		m_proxyPort.loadSettingsFrom(settings);
		m_proxyHost.loadSettingsFrom(settings);
		m_sharePointUrl.loadSettingsFrom(settings);
		
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
		m_clientID.validateSettings(settings);
		m_clientSecret.validateSettings(settings);
		m_tennantID.validateSettings(settings);
		m_useProxy.validateSettings(settings);
		m_proxyPort.validateSettings(settings);
		m_proxyHost.validateSettings(settings);
		m_sharePointUrl.validateSettings(settings);
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

