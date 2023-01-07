package org.AF.ExecuteShellScript;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
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


/**
 * This is an example implementation of the node model of the
 * "ExecuteShellScript" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class ExecuteShellScriptNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ExecuteShellScriptNodeModel.class);



	
    static final String scriptString = "scriptString";
    static final String executionDir = "executionDir";
    static final String environment = "environment";
    
    

	static SettingsModelString createShellScriptSettingsModel() {
		SettingsModelString coof = new SettingsModelString(scriptString, "");
		coof.setEnabled(true);
		return coof;				
	}	
	
	
	static SettingsModelString createExecutionDirSettingsModel() {
		SettingsModelString coof = new SettingsModelString(executionDir, "");
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelScriptEnvironmentSettings createEnviromentSettingsModel() {

        List<ScriptEnvironmentPreferences> initialPrefs = new ArrayList<ScriptEnvironmentPreferences>();
        
        ProcessBuilder builder = new ProcessBuilder();
        
        Map<String, String> env = builder.environment();
        
        for (Entry<String,String> pair : env.entrySet()){
        	initialPrefs.add(new ScriptEnvironmentPreferences(pair.getKey(), pair.getValue()));

        }
 

        SettingsModelScriptEnvironmentSettings coof = new SettingsModelScriptEnvironmentSettings(environment, initialPrefs);
		
		return coof;				
	}	
	
	


	private final SettingsModelString m_scriptString = createShellScriptSettingsModel();
	private final SettingsModelString m_executionDir = createExecutionDirSettingsModel();
	private final SettingsModelScriptEnvironmentSettings m_environment = createEnviromentSettingsModel();
   
    
	/**
	 * Constructor for the node model.
	 */
	protected ExecuteShellScriptNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(0, 2);
	}



	

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final PortObject[] inObjects, final ExecutionContext exec)
			throws Exception {


		LOGGER.info("Start script execution");

	
	    BufferedDataContainer scriptOutput = exec.createDataContainer(getSpec("StdOut"));
	    BufferedDataContainer scriptError = exec.createDataContainer(getSpec("StdOut"));
	      
	     
	    String script = m_scriptString.getStringValue();

	    String executionDir = m_executionDir.getStringValue();
	      
	    ProcessBuilder builder = new ProcessBuilder();
	    
	    
	    LOGGER.info("Script to execute:" + script);
	    LOGGER.info("executionDir:" + executionDir);
	    
	    
	    
	    if (executionDir.length()>0)
	    {
	    builder.directory(new File(executionDir));
	    }
	    
	    
	    Map<String, String> envs = builder.environment();
	    
	    for (ScriptEnvironmentPreferences pref : m_environment.getSettings()) {
	    	
	    	envs.put(pref.getColumnAt(0), pref.getColumnAt(1));  	
    	}
	    
	    
	    

	    
	    if (isWindows()) {
	    	builder.command(script.split(" "));
	    } else {
	    	builder.command( ArrayUtils.addAll(new String[]{"sh", "-c"}, script.split(" ")));
	    } 
	    

	    
	    
	    Process process = null;

	      try {
	    	  process = builder.start();
	      } catch (IOException e) {
	         throw new InvalidSettingsException("Error while executing script");
	      }
	      
	      
	      
	      
	    
	    try
	    {

	    	
	    	putOutputToTable(process.getInputStream(), scriptOutput);
	    	putOutputToTable(process.getErrorStream(), scriptError);
	    	


        } catch (IOException e) {
            e.printStackTrace();
         

        }
	
	    scriptOutput.close();
	    scriptError.close();
	    
		return new BufferedDataTable[]{scriptOutput.getTable(), scriptError.getTable()};
	}
	
	

private void putOutputToTable(InputStream inputStream,BufferedDataContainer container ) throws IOException {
	
    int i = 0;
    
    BufferedReader br = null;
    try {
        br = new BufferedReader(new InputStreamReader(inputStream,"CP437"));
        String line = null;
        while ((line = br.readLine()) != null) {

        	addRow(container,"Row"+i,line);
        	i++;
       
        }
    } finally {
        br.close();
    }
    
   
}





private void addRow(
		BufferedDataContainer container
		,String key
		,String output
		)
		{
			
			
			container.addRowToTable(
					new DefaultRow(new RowKey(key), new DataCell[] { 
							StringCellFactory.create(output)
					}));
		}




	
	
	
    public static boolean isWindows() {
    	String OS = System.getProperty("os.name").toLowerCase();
        return OS.contains("win");
    }
    
    
	private DataTableSpec getSpec(String colName)
	{

		
		DataTableSpecCreator crator = new DataTableSpecCreator();
		crator.addColumns(new DataColumnSpecCreator(colName, StringCellFactory.TYPE).createSpec());
	

		return crator.createSpec();
	}
	
	

	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		/*
		 * Check if the node is executable, e.g. all required user settings are
		 * available and valid, or the incoming types are feasible for the node to
		 * execute. In case the node can execute in its current configuration with the
		 * current input, calculate and return the table spec that would result of the
		 * execution of this node. I.e. this method precalculates the table spec of the
		 * output table.
		 * 
		 * Here we perform a sanity check on the entered number format String. In this
		 * case we just try to apply it to some dummy double number. If there is a
		 * problem, an IllegalFormatException will be thrown. We catch the exception and
		 * wrap it in a InvalidSettingsException with an informative message for the
		 * user. The message should make clear what the problem is and how it can be
		 * fixed if this information is available. This will be displayed in the KNIME
		 * console and printed to the KNIME log. The log will also contain the stack
		 * trace.
		 */

		//validateUserInput();


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
		return null;
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

		m_scriptString.saveSettingsTo(settings);
		m_executionDir.saveSettingsTo(settings);
		m_environment.saveSettingsTo(settings);
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
		m_scriptString.loadSettingsFrom(settings);
		m_executionDir.loadSettingsFrom(settings);
		m_environment.loadSettingsFrom(settings);
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
		m_scriptString.validateSettings(settings);
		m_executionDir.validateSettings(settings);
		m_environment.validateSettings(settings);
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

