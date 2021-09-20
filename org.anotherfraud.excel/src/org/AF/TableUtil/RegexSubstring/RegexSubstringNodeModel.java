package org.AF.TableUtil.RegexSubstring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
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
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is an example implementation of the node model of the
 * "RegexSubstring" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class RegexSubstringNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(RegexSubstringNodeModel.class);

	static final String valColName = "regexColName";
	static final String regexString = "regexString";
	static final String regexMode = "regexMode";
	static final String returnNumber = "returnNumber";
	static final String delimiterChar = "delimiterChar";

	static SettingsModelString createValColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(valColName,null);
		coof.setEnabled(true);
		return coof;				
	}	

	static SettingsModelString createRegexStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(regexString,null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createDelimiterCharSettingsModel() {
		SettingsModelString coof = new SettingsModelString(delimiterChar,null);
		coof.setEnabled(false);
		return coof;				
	}	
	
	static SettingsModelString createRegexModeSettingsModel() {
		SettingsModelString epw = new SettingsModelString(regexMode, "First match");
		return epw;				
	}	
	
	static SettingsModelIntegerBounded createReturnNumberModel() {
		SettingsModelIntegerBounded si = new SettingsModelIntegerBounded(returnNumber, 1, 1,2147483646);
		si.setEnabled(false);
        return si;
    }
	

	
	private final SettingsModelIntegerBounded m_returnNumber = createReturnNumberModel();
	private final SettingsModelString m_regexMode = createRegexModeSettingsModel();
	private final SettingsModelString m_delimiterChar = createDelimiterCharSettingsModel();
	private final SettingsModelString m_valColName = createValColNameStringSettingsModel();
	private final SettingsModelString m_regexString = createRegexStringSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected RegexSubstringNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(1, 1);
	}


	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
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
		LOGGER.info("Starting regex substring node");
		/*
		 * The input data table to work with. The "inData" array will contain as many
		 * input tables as specified in the constructor. In this case it can only be one
		 * (see constructor).
		 */
		BufferedDataTable inputTable = inData[0];

		DataTableSpec outputSpec = createOutputSpec(inputTable.getDataTableSpec());
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		
		int valColIndex = inData[0].getDataTableSpec().findColumnIndex(m_valColName.getStringValue());
		String regexMode = m_regexMode.getStringValue();
		String returnString = "";
		
		/*
		 * Get the row iterator over the input table which returns each row one-by-one
		 * from the input table.
		 */
		
		CloseableRowIterator rowIterator = inputTable.iterator();



		/*
		 * A counter for how many rows have already been processed. This is used to
		 * calculate the progress of the node, which is displayed as a loading bar under
		 * the node icon.
		 */
		int currentRowCounter = 0;
		// Iterate over the rows of the input table.
		while (rowIterator.hasNext()) {
			DataRow currentRow = rowIterator.next();
			int numberOfCells = currentRow.getNumCells();
			
			List<DataCell> cells = new ArrayList<>();
			
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				cells.add(cell);
			}
			
			DataCell formCell = currentRow.getCell(valColIndex);	
			List<String> matchesArray = new ArrayList<>();

			
			Pattern pattern = Pattern.compile(m_regexString.getStringValue());
			Matcher matcher = pattern.matcher(formCell.toString());
			


			while (matcher.find()) {				   
				matchesArray.add(matcher.group());
			}
			
		
			if (matchesArray.isEmpty())
			{
				returnString = "";
			}
			else if (regexMode.equals("First match"))
			{
				returnString = matchesArray.get(0);
			}
			else if (regexMode.equals("Last match"))
			{
				returnString = matchesArray.get(matchesArray.size() - 1);
			}
			else if (regexMode.equals("Return match by number") && matchesArray.size()>= m_returnNumber.getIntValue())
			{
				returnString = matchesArray.get(m_returnNumber.getIntValue()-1);
			}
			else
			{
				returnString = StringUtils.join(matchesArray, m_delimiterChar.getStringValue());
			}
			
				

			DataCell outputCell = StringCellFactory.create(returnString);
			cells.add(outputCell);
	
			
			DataRow row = new DefaultRow(currentRow.getKey(), cells);
			container.addRowToTable(row);		

			// We finished processing one row, hence increase the counter
			currentRowCounter++;

			/*
			 * Here we check if a user triggered a cancel of the node. If so, this call will
			 * throw an exception and the execution will stop. This should be done
			 * frequently during execution, e.g. after the processing of one row if
			 * possible.
			 */
			exec.checkCanceled();
			
			

			/*
			 * Calculate the percentage of execution progress and inform the
			 * ExecutionMonitor. Additionally, we can set a message what the node is
			 * currently doing (the message will be displayed as a tooltip when hovering
			 * over the progress bar of the node). This is especially useful to inform the
			 * user about the execution status for long running nodes.
			 */
			exec.setProgress(currentRowCounter / (double) inputTable.size(), "computing row " + currentRowCounter);
		}
		
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	
	
	
	private DataTableSpec createOutputSpec(DataTableSpec inputTableSpec) {
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			newColumnSpecs.add(inputTableSpec.getColumnSpec(i));
		}
		
		
		String newName =  m_valColName.getStringValue() + "_regexSubstring";
		DataColumnSpecCreator specCreator = new DataColumnSpecCreator(newName, StringCellFactory.TYPE);	 
		newColumnSpecs.add(specCreator.createSpec());
		

		// Create and return a new DataTableSpec from the list of DataColumnSpecs.
		DataColumnSpec[] newColumnSpecsArray = newColumnSpecs.toArray(new DataColumnSpec[newColumnSpecs.size()]);
		return new DataTableSpec(newColumnSpecsArray);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { null};
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
		m_valColName.saveSettingsTo(settings);
		m_regexString.saveSettingsTo(settings);
		m_returnNumber.saveSettingsTo(settings);
		m_regexMode.saveSettingsTo(settings);
		m_delimiterChar.saveSettingsTo(settings);
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
		m_valColName.loadSettingsFrom(settings);
		m_regexString.loadSettingsFrom(settings);
		m_returnNumber.loadSettingsFrom(settings);
		m_regexMode.loadSettingsFrom(settings);
		m_delimiterChar.loadSettingsFrom(settings);
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
		m_valColName.validateSettings(settings);
		m_regexString.validateSettings(settings);
		m_returnNumber.validateSettings(settings);
		m_regexMode.validateSettings(settings);
		m_delimiterChar.validateSettings(settings);
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