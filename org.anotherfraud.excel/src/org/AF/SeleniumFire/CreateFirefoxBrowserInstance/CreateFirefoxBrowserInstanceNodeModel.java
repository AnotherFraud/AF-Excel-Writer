package org.AF.SeleniumFire.CreateFirefoxBrowserInstance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

import org.AF.Selenium.Port.SeleniumConnectionInformation;
import org.AF.Selenium.Port.SeleniumConnectionInformationPortObject;
import org.AF.Selenium.Port.SeleniumConnectionInformationPortObjectSpec;
import org.AF.Selenium.Port.WebdriverHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.util.FileUtil;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


/**
 * This is an example implementation of the node model of the
 * "CreateFirefoxBrowserInstance" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class CreateFirefoxBrowserInstanceNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(CreateFirefoxBrowserInstanceNodeModel.class);

	
    static final String proxyAuth = "proxyAuth";
    static final String UseProxy = "UseProxy";
    static final String ProxyHost = "ProxyHost";
    static final String ProxyPort = "ProxyPort";
    static final String DriverPath = "DriverPath";
    static final String downloadPath = "downloadPath";
    static final String screenshotPath = "screenshotPath";
    static final String firefoxPath = "firefoxPath";
	private Optional<FSConnection> m_fs = Optional.empty();
	private int defaulttimeoutInSeconds = 5;
    

    
	static SettingsModelFileChooser2 createDownloadPathSettingsModel() {
		SettingsModelFileChooser2 ofp = new SettingsModelFileChooser2(downloadPath);
		return ofp;
	}
	
	static SettingsModelFileChooser2 createScreenshotPathSettingsModel() {
		SettingsModelFileChooser2 ofp = new SettingsModelFileChooser2(screenshotPath);
		return ofp;
	}

	static SettingsModelFileChooser2 createFirefoxPathSettingsModel() {
		SettingsModelFileChooser2 ofp = new SettingsModelFileChooser2(firefoxPath, new String[] { ".exe" });
		return ofp;
	}
	
	static SettingsModelString createDriverPathSettingsModel() {
		SettingsModelString coof = new SettingsModelString(DriverPath, null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelAuthentication createProxySettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(proxyAuth, AuthenticationType.USER_PWD);
		cps.setEnabled(false);
		return cps;
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

	private final SettingsModelString m_useProxy = createUseProxySettingsModel();
	private final SettingsModelIntegerBounded m_proxyPort = createProxyPortIndexModel();
	private final SettingsModelString m_proxyHost = createProxyHostSettingsModel();

	private final SettingsModelString m_driverPath = createDriverPathSettingsModel();	
    private final SettingsModelFileChooser2 m_downloadPath = createDownloadPathSettingsModel();	
    private final SettingsModelFileChooser2 m_screenshotPath = createScreenshotPathSettingsModel();	
    private final SettingsModelFileChooser2 m_firefoxPath = createFirefoxPathSettingsModel();	

	private String m_connectionKey;
    /** Settings containing information about the SP connection. */

    /** Method to create a new settings object containing information about the AWS connection. */
    static final SeleniumConnectionInformationPortObject createSeleniumModel() {
    	SeleniumConnectionInformationPortObject newSPConn = new SeleniumConnectionInformationPortObject();
    	return newSPConn;
    }	

	/**
	 * Constructor for the node model.
	 */
	protected CreateFirefoxBrowserInstanceNodeModel() {

		super(new PortType[]{FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[]{SeleniumConnectionInformationPortObject.TYPE});
		//super(new PortType[]{FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[] {PortTypeRegistry.getInstance().getPortType(ConnectionInformationPortObject.class)});
	}



    /**
     * Create the spec
     *
     */
    private SeleniumConnectionInformationPortObjectSpec createSpec(String key, String screenshotpath, String downloadPath) {
    	SeleniumConnectionInformation connInfo = new SeleniumConnectionInformation();
    	
    	
    	connInfo.setWebdriverHandlerKey(key);
    	connInfo.setHost(m_proxyHost.getStringValue());
    	connInfo.setPort(m_proxyPort.getIntValue());
    	connInfo.setUseProxy(m_useProxy.getStringValue().equals("Use Proxy"));
    	connInfo.setDownloadPath(downloadPath);  	
    	connInfo.setScreenShotPath(screenshotpath);
    	connInfo.setDownloadWaitSeconds(1);
    	connInfo.setPageWaitSeconds(1);
    	connInfo.setUser(m_proxy.getUserName(getCredentialsProvider()));
    	connInfo.setPassword(m_proxy.getPassword(getCredentialsProvider())); 

    

 
        return new SeleniumConnectionInformationPortObjectSpec(connInfo);
    }
    
	private String createNewKey() {

	    byte[] array = new byte[10];
	    new Random().nextBytes(array);
	    String generatedString = new String(array, Charset.forName("UTF-8"));
	    
		String ConnectionKey = 
				String.valueOf(NodeContext.getContext().getWorkflowManager().getID().getIndex())
				+ NodeContext.getContext().getWorkflowManager().getName()
				+ generatedString
				;
		return ConnectionKey;
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
		LOGGER.info("Starting Firefox Instance");
	


		String screenShotPath = getPathFromModel(m_screenshotPath);
		String downloadPath = getPathFromModel(m_downloadPath);
		String firefoxPath = getPathFromModel(m_firefoxPath);


		
		m_connectionKey = createNewKey();
		getDriverForOS(m_connectionKey);		
		

		//System.setProperty("webdriver.gecko.driver", "/transfer/sftp/fraud/data/WorkflowTempFiles/CargoImport/geckodriver26");
		//System.setProperty("webdriver.gecko.driver", "C:\\Temp\\Selenium\\geckodriver.exe");
		System.setProperty("webdriver.gecko.driver", m_driverPath.getStringValue());

		
		
		File pathBinary = new File(firefoxPath);
		FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary); 
		
		FirefoxOptions options = new FirefoxOptions();
		options.setBinary(firefoxBinary);
		options.addArguments("--sandbox");
		options.addPreference("browser.download.dir",downloadPath);
		options.addPreference("browser.download.folderList", 2);
		options.addPreference("browser.link.open_newwindow", 3);
		options.addPreference("browser.link.open_newwindow.restriction", 0);
		options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/msexcel");
		options.addPreference("security.fileuri.strict_origin_policy", "false");
		options.addPreference("network.proxy.autoconfig_url", "http://inetprox.inet.cns.fra.dlh.de/lhpproxy.pac");
		options.addPreference("network.proxy.type", 2);
		options.addPreference("browser.tabs.remote.autostart", false);
		options.addPreference("browser.tabs.remote.autostart.2", false);
	 	options.addPreference("browser.tabs.remote.warmup.maxTabs",2);
	 	options.setCapability("acceptInsecureCerts", true);
	 	

		options.setHeadless(false);
		FirefoxDriver driver = new FirefoxDriver(options); 
		

		
	    
		WebdriverHandler handle = WebdriverHandler.getInstance(m_connectionKey);
		handle.setDriver(driver);
		

		return new PortObject[]{new SeleniumConnectionInformationPortObject(createSpec(m_connectionKey, screenShotPath, downloadPath))};
	}

	
	
	
	private String getPathFromModel(SettingsModelFileChooser2 fileChooserModel) throws InvalidSettingsException, IOException {		
		FileChooserHelper fileHelper = new FileChooserHelper(m_fs, fileChooserModel, defaulttimeoutInSeconds * 1000);
		Path pathOutput = fileHelper.getPathFromSettings();
		return pathOutput.toAbsolutePath().toString();
	}


	
	private String getDriverOSName() throws IOException{
		
		String driverByOS;
		
		if (SystemUtils.IS_OS_WINDOWS)
		{
			
			driverByOS = "geckodriver_win64.exe";	
		}	
		else if (SystemUtils.IS_OS_LINUX)
		{
			driverByOS = "geckodriver_linux64";
		}
		else if (SystemUtils.IS_OS_MAC)
		{
			driverByOS = "geckodriver_macos";
		}
		else
		{
			throw new IOException("OS not supported"); 
		}

		return driverByOS;
		
	}
	
	
	

	private void getDriverForOS(String connectionID) throws IOException{

		
		File tempDriverPath = new File(m_driverPath.getStringValue());
		InputStream driverStream;
		File tempDriver;
		
		if (!tempDriverPath.canExecute() || m_driverPath.getStringValue() != "" )
		{	
			
		tempDriver = FileUtil.createTempDir("connectionID"+"webDriver", new File(KNIMEConstants.getKNIMETempDir()), true);
		driverStream = this.getClass().getClassLoader().getResourceAsStream(getDriverOSName());
		
		FileUtils.copyInputStreamToFile(driverStream, tempDriver);
		m_driverPath.setStringValue(tempDriver.getAbsolutePath());
		
		
		}
		else
		{
		}	
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
		m_downloadPath.saveSettingsTo(settings);
		m_screenshotPath.saveSettingsTo(settings);
		m_firefoxPath.saveSettingsTo(settings);
		m_useProxy.saveSettingsTo(settings);
		m_proxyPort.saveSettingsTo(settings);
		m_proxyHost.saveSettingsTo(settings);
		m_driverPath.saveSettingsTo(settings);
		
		


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
		m_screenshotPath.loadSettingsFrom(settings);
		m_downloadPath.loadSettingsFrom(settings);
		m_firefoxPath.loadSettingsFrom(settings);
		m_useProxy.loadSettingsFrom(settings);
		m_proxyPort.loadSettingsFrom(settings);
		m_proxyHost.loadSettingsFrom(settings);
		m_driverPath.loadSettingsFrom(settings);

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
		m_screenshotPath.validateSettings(settings);
		m_downloadPath.validateSettings(settings);
		m_firefoxPath.validateSettings(settings);
		m_useProxy.validateSettings(settings);
		m_proxyPort.validateSettings(settings);
		m_proxyHost.validateSettings(settings);
		m_driverPath.validateSettings(settings);
		

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
		
		if (!m_driverPath.getStringValue().contains(nodeInternDir.getAbsolutePath()) && m_driverPath.getStringValue() != "")
		{
			File tempDriverPath = new File(m_driverPath.getStringValue());
			File nodeDirDriverPath = new File(nodeInternDir.getAbsolutePath() + File.separator + getDriverOSName());		
			FileUtils.moveFile(tempDriverPath, nodeDirDriverPath);
			m_driverPath.setStringValue(nodeDirDriverPath.getAbsolutePath());
				
		}
		
		/*
		 * Advanced method, usually left empty. Everything
		 * written to the output ports is saved automatically (data returned by the execute
		 * method, models saved in the saveModelContent, and user settings saved through
		 * saveSettingsTo - is all taken care of). Save only the internals
		 * that need to be preserved (e.g. data used by the views).
		 */
	}

	
	@Override
	protected void reset() 
	{
	WebdriverHandler.getInstance(m_connectionKey).removeInstance();	
	
	File tempDriverPath = new File(m_driverPath.getStringValue());
		if (tempDriverPath.exists())
		{
			tempDriverPath.delete();
		}	
	}
	
	  
	  
	 protected void onDispose() {
	 WebdriverHandler.getInstance(m_connectionKey).removeInstance();	
	 
	 File tempDriverPath = new File(m_driverPath.getStringValue());
		if (tempDriverPath.exists())
		{
			tempDriverPath.delete();
		}
	 }
	  
}

