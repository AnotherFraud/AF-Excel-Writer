package org.AF.SharePointUtilities.GetRestAccessTokenAsString;


import java.util.Arrays;
import java.util.HashMap;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.Pair;

/**
 * This is an example implementation of the node dialog of the
 * "GetRestAccessToken" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author AnotherFraud
 */
public class GetRestAccessTokenAsStringNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
	
	
	private final DialogComponentAuthentication m_authenticationPanel;
	private final SettingsModelAuthentication proxyAuth;
	
	
	private final DialogComponentAuthentication m_authenticationSecretPanel;
	private final SettingsModelAuthentication clientSecretModel;
	
    protected GetRestAccessTokenAsStringNodeDialog() {
        super();
        
        proxyAuth = GetRestAccessTokenAsStringNodeModel.createProxySettingsModel();
                
       	final SettingsModelString clientIDModel = GetRestAccessTokenAsStringNodeModel.createClientIDSettingsModel();
       	clientSecretModel = GetRestAccessTokenAsStringNodeModel.createClientSecretSettingsModel();
       	final SettingsModelString tennantIDModel = GetRestAccessTokenAsStringNodeModel.createTennantIDSettingsModel();
       	final SettingsModelString useProxyModel = GetRestAccessTokenAsStringNodeModel.createUseProxySettingsModel();
       	final SettingsModelIntegerBounded proxyPort = GetRestAccessTokenAsStringNodeModel.createProxyPortIndexModel();
       	final SettingsModelString proxyHost = GetRestAccessTokenAsStringNodeModel.createProxyHostSettingsModel();
       	final SettingsModelString sharePointOnlineSiteURL = GetRestAccessTokenAsStringNodeModel.createSharePointUrlSettingsModel();
       	
       	
       	
       	
       	
        //listener check selection for password usage
       	useProxyModel.addChangeListener(e -> {
            if (useProxyModel.getStringValue().equals("Use Proxy")) {
            	proxyAuth.setEnabled(true);
            	proxyPort.setEnabled(true);
            	proxyHost.setEnabled(true);
            	
            }
            else  {
            	proxyAuth.setEnabled(false);
            	proxyPort.setEnabled(false);
            	proxyHost.setEnabled(false);
            }
        });  
       	
       	//Map<AuthenticationType, Pair<String, String>> map;
       	HashMap<AuthenticationType, Pair<String, String>> mapPass = new HashMap<AuthenticationType, Pair<String, String>>()
       	{
		private static final long serialVersionUID = -3883031017710953380L;

		{
       	     put(AuthenticationType.CREDENTIALS, new Pair<String, String>("Proxy Credential","Proxy Credential"));
       	     put(AuthenticationType.USER_PWD, new Pair<String, String>("Proxy User/Password","Proxy User/Password"));

       	}}; 
       	
       	//Map<AuthenticationType, Pair<String, String>> map;
       	HashMap<AuthenticationType, Pair<String, String>> mapSecret = new HashMap<AuthenticationType, Pair<String, String>>()
       	{
		private static final long serialVersionUID = -3883030017710953380L;

		{
       	     put(AuthenticationType.CREDENTIALS, new Pair<String, String>("Client Secret Credential","Client Secret Credential"));
       	     put(AuthenticationType.PWD, new Pair<String, String>("Client Secret","Client Secret"));

       	}};    
       	
       	
       	
       	addDialogComponent(new DialogComponentString(clientIDModel, "Client ID", true, 30));
       	addDialogComponent(new DialogComponentString(tennantIDModel, "Tennant ID", true, 30));
       	addDialogComponent(new DialogComponentString(sharePointOnlineSiteURL, "SharePoint Online Site URL", true, 60));
       	
       	m_authenticationSecretPanel = new  DialogComponentAuthentication(clientSecretModel, "Client Secret", Arrays.asList(AuthenticationType.CREDENTIALS, AuthenticationType.PWD), mapSecret);
        addDialogComponent(m_authenticationSecretPanel);	
        

		
        
        
        
		
        createNewTab("Proxy Options");
        createNewGroup("Proxy Options"); 
        
        addDialogComponent(
        new DialogComponentStringSelection(useProxyModel, "Proxy Config",
        		Arrays.asList( "no Proxy","Use Proxy"),false));
        
        
        addDialogComponent(new DialogComponentString(proxyHost, "Proxy Host", true, 30));
        addDialogComponent(new DialogComponentNumber(proxyPort, "Proxy Port", 1));
        
        m_authenticationPanel = new  DialogComponentAuthentication(proxyAuth, "Proxy User/Password", Arrays.asList(AuthenticationType.CREDENTIALS, AuthenticationType.USER_PWD), mapPass);
        addDialogComponent(m_authenticationPanel);

        closeCurrentGroup();
        
        
    }
    @Override
    public void saveAdditionalSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
    	proxyAuth.saveSettingsTo(settings); 
    	clientSecretModel.saveSettingsTo(settings);
 
    	
    }

    @Override
    public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
    	try {
    		proxyAuth.loadSettingsFrom(settings);
    		m_authenticationPanel.loadSettingsFrom(settings, specs, getCredentialsProvider());
    		
    		clientSecretModel.loadSettingsFrom(settings);
    		m_authenticationSecretPanel.loadSettingsFrom(settings, specs, getCredentialsProvider());

    		
    	} catch (InvalidSettingsException e) {
    		throw new NotConfigurableException(e.getMessage(), e);
    	}
    }
}

