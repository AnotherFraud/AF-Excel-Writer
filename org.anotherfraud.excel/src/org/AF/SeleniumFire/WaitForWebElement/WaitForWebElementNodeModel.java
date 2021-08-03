package org.AF.SeleniumFire.WaitForWebElement;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import org.AF.Selenium.Port.SeleniumConnectionInformation;
import org.AF.Selenium.Port.SeleniumConnectionInformationPortObject;
import org.AF.Selenium.Port.WebdriverHandler;
import org.AF.SeleniumFire.FireHelper.FireHelper;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * This is an example implementation of the node model of the
 * "WaitForWebElement" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class WaitForWebElementNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(WaitForWebElementNodeModel.class);


	
	static final String waitCond = "waitCond";
    static final String locatorString = "locatorString";
    static final String searchIn = "searchIn";
    static final String findBy = "findBy";
    static final String defaultWait = "defaultWait";

    static SettingsModelIntegerBounded createDefaultWaitSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(defaultWait, 15, 0, 1048575);
		return roff;				
	}	
    
	static SettingsModelString createWaitConditionSettingsModel() {
		SettingsModelString coof = new SettingsModelString(waitCond, "isClickable");
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createlocatorStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(locatorString, null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createSearchInSettingsModel() {
		SettingsModelString coof = new SettingsModelString(searchIn, "with locator");
		coof.setEnabled(true);
		return coof;				
	}	
    
	static SettingsModelString createFindBySettingsModel() {
		SettingsModelString coof = new SettingsModelString(findBy, "ById");
		coof.setEnabled(true);
		return coof;				
	}	    
	

	private final SettingsModelIntegerBounded m_wait = createDefaultWaitSettingsModel();
	private final SettingsModelString m_cond = createWaitConditionSettingsModel();
	private final SettingsModelString m_locatorString = createlocatorStringSettingsModel();
	private final SettingsModelString m_searchIn = createSearchInSettingsModel();
	private final SettingsModelString m_findBy = createFindBySettingsModel();
    
	/**
	 * Constructor for the node model.
	 */
	protected WaitForWebElementNodeModel() {
		super(new PortType[]{SeleniumConnectionInformationPortObject.TYPE}, new PortType[]{SeleniumConnectionInformationPortObject.TYPE});
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
		LOGGER.info("Start executing wait for webelement");
		
		
		SeleniumConnectionInformationPortObject spConn = (SeleniumConnectionInformationPortObject)inObjects[0];
		SeleniumConnectionInformation connInfo = spConn.getConnectionInformation();
		
		WebdriverHandler handle = WebdriverHandler.getInstance(connInfo.getWebdriverHandlerKey());
		

		FirefoxDriver driver = handle.getDriver();
		
		String locatorString = m_locatorString.getStringValue();	
		
		By by = FireHelper.locatorSwitch(locatorString,m_findBy.getStringValue()); 
		
		WebDriverWait wait = new WebDriverWait(driver,m_wait.getIntValue());

		
        switch(m_cond.getStringValue()){
        case "isPresent":
        	wait.until((Function<WebDriver, WebElement>)ExpectedConditions.presenceOfElementLocated(by)); 
            break;
        case "isClickable":
        	wait.until((Function<WebDriver, WebElement>)ExpectedConditions.elementToBeClickable(by)); 
            break;
        case "isVisible":
        	wait.until((Function<WebDriver, WebElement>)ExpectedConditions.visibilityOfElementLocated(by)); 
            break;
        case "isInvisivle":
        	wait.until((Function<WebDriver, Boolean>)ExpectedConditions.invisibilityOfElementLocated(by)); 
            break;
        case "isSelected":
        	wait.until((Function<WebDriver, Boolean>)ExpectedConditions.elementToBeSelected(by)); 
            break;
        default:
        	throw new IOException("Unknown waitfor type"); 
        }
		
        

		return new PortObject[]{spConn};
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
		
	
		m_locatorString.saveSettingsTo(settings);
		m_searchIn.saveSettingsTo(settings);
		m_findBy.saveSettingsTo(settings);
		m_wait.saveSettingsTo(settings);
		m_cond.saveSettingsTo(settings);
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

		m_locatorString.loadSettingsFrom(settings);
		m_findBy.loadSettingsFrom(settings);
		m_searchIn.loadSettingsFrom(settings);
		m_wait.loadSettingsFrom(settings);
		m_cond.loadSettingsFrom(settings);
	
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
		m_findBy.validateSettings(settings);
		m_locatorString.validateSettings(settings);
		m_searchIn.validateSettings(settings);
		m_wait.validateSettings(settings);
		m_cond.validateSettings(settings);

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

