package org.AF.PGPUtilities.HmacHashing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node model of the
 * "HmacHashing" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class HmacHashingNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(HmacHashingNodeModel.class);

	static final String keyColName = "keyColName";
	static final String messageColName = "messageColName";
	static final String hashMode = "hashMode";
	


	static SettingsModelString createKeyColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(keyColName,null);
		coof.setEnabled(true);
		return coof;				
	}	

	static SettingsModelString createMessageColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(messageColName,null);
		coof.setEnabled(true);
		return coof;				
	}	


	static SettingsModelString createHashModeSettingsModel() {
		SettingsModelString coof = new SettingsModelString(hashMode,"HmacSHA256");
		coof.setEnabled(true);
		return coof;				
	}		
	
	private final SettingsModelString m_keyColName = createKeyColNameStringSettingsModel();
	private final SettingsModelString m_messageColName = createMessageColNameStringSettingsModel();
	private final SettingsModelString m_hashMode = createHashModeSettingsModel();
	
	/**
	 * Constructor for the node model.
	 */
	protected HmacHashingNodeModel() {
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
		LOGGER.info("Start Hashing column");

		/*
		 * The input data table to work with. The "inData" array will contain as many
		 * input tables as specified in the constructor. In this case it can only be one
		 * (see constructor).
		 */
		BufferedDataTable inputTable = inData[0];

		/*
		 * Create the spec of the output table, for each double column of the input
		 * table we will create one formatted String column in the output. See the
		 * javadoc of the "createOutputSpec(...)" for more information.
		 */
		DataTableSpec outputSpec = createOutputSpec(inputTable.getDataTableSpec());
		BufferedDataContainer container = exec.createDataContainer(outputSpec);


		
		int valMessageIndex = inData[0].getDataTableSpec().findColumnIndex(m_messageColName.getStringValue());
		int valKeyIndex = inData[0].getDataTableSpec().findColumnIndex(m_keyColName.getStringValue());
		

		

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
			/*
			 * A list to collect the cells to output for the current row. The type and
			 * amount of cells must match the DataTableSpec we used when creating the
			 * DataContainer. 
			 */
			List<DataCell> cells = new ArrayList<>();
			
			DataCell messageCell = currentRow.getCell(valMessageIndex);
			DataCell keyCell = currentRow.getCell(valKeyIndex);
			

			// Iterate over the cells of the current row.
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				cells.add(cell);

			}
			
			
			
			
			String message = messageCell.toString();
	        String key = keyCell.toString();
	        
	        String algorithm = m_hashMode.getStringValue();  // OPTIONS= HmacSHA512, HmacSHA256, HmacSHA1, HmacMD5



            // 1. Get an algorithm instance.
            Mac sha256_hmac = Mac.getInstance(algorithm);

            // 2. Create secret key.
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);

            // 3. Assign secret key algorithm.
            sha256_hmac.init(secret_key);

            // 4. Generate Base64 encoded cipher string.
            String hash = Base64.toBase64String(sha256_hmac.doFinal(message.getBytes("UTF-8")));
	            

			DataCell outputCell = StringCellFactory.create(hash);
			cells.add(outputCell);
			
			
			// Add the new row to the output data container
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
			exec.setProgress(currentRowCounter / (double) inputTable.size(), "Hashing row " + currentRowCounter);
		}

		/*
		 * Once we are done, we close the container and return its table. Here we need
		 * to return as many tables as we specified in the constructor. This node has
		 * one output, hence return one table (wrapped in an array of tables).
		 */
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { null};
	}

	/**
	 * Creates the output table spec from the input spec. For each double column in
	 * the input, one String column will be created containing the formatted double
	 * value as String.
	 * 
	 * @param inputTableSpec
	 * @return
	 */
	private DataTableSpec createOutputSpec(DataTableSpec inputTableSpec) {
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			newColumnSpecs.add(inputTableSpec.getColumnSpec(i));
		}
		
		
		String newName =  m_messageColName.getStringValue() + "_HMAC";
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
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		/*
		 * Save user settings to the NodeSettings object. SettingsModels already know how to
		 * save them self to a NodeSettings object by calling the below method. In general,
		 * the NodeSettings object is just a key-value store and has methods to write
		 * all common data types. Hence, you can easily write your settings manually.
		 * See the methods of the NodeSettingsWO.
		 */
		m_keyColName.saveSettingsTo(settings);
		m_messageColName.saveSettingsTo(settings);
		m_hashMode.saveSettingsTo(settings);
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
		
		m_keyColName.loadSettingsFrom(settings);
		m_messageColName.loadSettingsFrom(settings);
		m_hashMode.loadSettingsFrom(settings);
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
		
		m_keyColName.validateSettings(settings);
		m_messageColName.validateSettings(settings);
		m_hashMode.validateSettings(settings);
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

