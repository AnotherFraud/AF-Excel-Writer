package org.AF.ExcelUtilities.TableToExcelCellUpdater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellReference;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
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

/**
 * This is an example implementation of the node model of the
 * "TableToExcelCellUpdater" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class TableToExcelCellUpdaterNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(TableToExcelCellUpdaterNodeModel.class);

    static final String rowOffset = "rowOffset";
    static final String colOffset = "colOff";
    static final String cellAddress = "cellAddress";   
    static final String ignoreMissing = "ignoreMissing";  
    

	static SettingsModelIntegerBounded createRowOffsetSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(rowOffset, 0, 0, 1048575);
		return roff;				
	}	
	
	static SettingsModelIntegerBounded createColOffsetSettingsModel() {
		SettingsModelIntegerBounded coff = new SettingsModelIntegerBounded(colOffset, 0, 0, 16384);
		return coff;				
	}	
	
	
	static SettingsModelString createCellAddressSettingsModel() {
		return new SettingsModelString(cellAddress, "excelAddr");
	}	
    
	static SettingsModelBoolean createIgnoreMissingSettingsModel() {
		SettingsModelBoolean wlr = new SettingsModelBoolean(ignoreMissing, false);
		return wlr;				
	}	

	
	private final SettingsModelIntegerBounded m_rowOffset = createRowOffsetSettingsModel();
    private final SettingsModelIntegerBounded m_colOffset = createColOffsetSettingsModel();
	private final SettingsModelString m_cellAddress = createCellAddressSettingsModel();
	private final SettingsModelBoolean m_ignoreMissing = createIgnoreMissingSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected TableToExcelCellUpdaterNodeModel() {
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
		LOGGER.info("Table to Excel Cell Updater Format");

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

		/*
		 * The execution context provides storage capacity, in this case a
		 * data container to which we will add rows sequentially. Note, this container
		 * can handle arbitrary big data tables, it will buffer to disc if necessary.
		 * The execution context is provided as an argument to the execute method by the
		 * framework. Have a look at the methods of the "exec". There is a lot of
		 * functionality to create and change data tables.
		 */
		BufferedDataContainer container = exec.createDataContainer(outputSpec);

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
		int rowNumber = 0;
		int rowId = 0;
		
		
		// Iterate over the rows of the input table.
		while (rowIterator.hasNext()) {
			DataRow currentRow = rowIterator.next();
			int numberOfCells = currentRow.getNumCells();
			/*
			 * A list to collect the cells to output for the current row. The type and
			 * amount of cells must match the DataTableSpec we used when creating the
			 * DataContainer. 
			 */

			// Iterate over the cells of the current row.
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				

				
				if (!cell.isMissing() || !m_ignoreMissing.getBooleanValue())

				addRow
				(
					container
					,outputSpec
					,"Row_" + rowId		
					,rowNumber
					,i
					,cell	
				);
				
				rowId++;
				
				
				

			}

			// We finished processing one row, hence increase the counter
			rowNumber++;

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
			exec.setProgress(rowNumber / (double) inputTable.size(), "Addings cells from row " + rowNumber);


			
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

	private void addRow(BufferedDataContainer container, DataTableSpec outputSpec, String rowId, int rowNumber, int colNumber, DataCell cell) {
		

		List<DataCell> dataCells = new ArrayList<DataCell>();
		

		

		dataCells.add(StringCellFactory.create(getAddress(rowNumber,colNumber)));
		
		
		
		// Iterate over the input column specs
		for (int i = 1; i < outputSpec.getNumColumns(); i++) {
			
			DataColumnSpec columnSpec = outputSpec.getColumnSpec(i);
						
			
			if (columnSpec.getType().equals(cell.getType()))
			{
				dataCells.add(cell);
				
			}
			else
			{

				dataCells.add(DataType.getMissingCell()); 
				
			}

		}
		
		DataCell[] cells = new DataCell[dataCells.size()];
		dataCells.toArray(cells);
	    
	    
		container.addRowToTable(
					new DefaultRow(new RowKey(rowId)
					,cells
					));	
		
		

		
		
		
	}

	private String getAddress(int rowNumber, int colNumber) {
		

		
		String cellAddress = "";
		rowNumber = rowNumber + m_rowOffset.getIntValue();
		colNumber = colNumber + m_colOffset.getIntValue();
		
		if (m_cellAddress.getStringValue().equals("excelAddr"))
		{
			cellAddress = CellReference.convertNumToColString(colNumber);
			cellAddress = cellAddress + (rowNumber+1);
			
		}
		else
		{
			cellAddress = (colNumber+1) + ":" + (rowNumber+1);
			
		}

		
		
		return cellAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] {null};
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
	
		//ensure that each datatyp is only used once
		Map<String, DataColumnSpec > uniqueSpecs = new LinkedHashMap<String, DataColumnSpec >();

	    
	    
	    
	    
		DataColumnSpecCreator specCreator = new DataColumnSpecCreator("ExcelAddress", StringCell.TYPE);

		uniqueSpecs.put("ExcelAddress", specCreator.createSpec());
		
		
		
		
		// Iterate over the input column specs
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

			specCreator = new DataColumnSpecCreator(columnSpec.getType().toPrettyString(), columnSpec.getType());
			uniqueSpecs.putIfAbsent(columnSpec.getType().toPrettyString(), specCreator.createSpec());			
		}

		
		

		
		// Create and return a new DataTableSpec from the list of DataColumnSpecs.
		DataColumnSpec[] newColumnSpecsArray = new DataColumnSpec[uniqueSpecs.size()];
		uniqueSpecs.values().toArray(newColumnSpecsArray);
		
		
		
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
		m_rowOffset.saveSettingsTo(settings);
		m_colOffset.saveSettingsTo(settings);
		m_cellAddress.saveSettingsTo(settings);
		m_ignoreMissing.saveSettingsTo(settings);
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

		m_rowOffset.loadSettingsFrom(settings);
		m_colOffset.loadSettingsFrom(settings);
		m_cellAddress.loadSettingsFrom(settings);
		m_ignoreMissing.loadSettingsFrom(settings);
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

		m_rowOffset.validateSettings(settings);
		m_colOffset.validateSettings(settings);
		m_cellAddress.validateSettings(settings);
		m_ignoreMissing.validateSettings(settings);
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

