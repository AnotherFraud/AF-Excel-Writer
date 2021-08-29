package org.AF.TableUtil.GroupAll;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.DoubleCell.DoubleCellFactory;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.IntCell.IntCellFactory;
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
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is an example implementation of the node model of the
 * "GroupAll" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class GroupAllNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(GroupAllNodeModel.class);


	static final String valColName = "valColName";
	static final String groupMode = "groupMode";
	static final String minTotalPerGroup = "minTotalPerGroup";
	static final String minCounterPerGroup = "minCounterPerGroup";

	static SettingsModelString createValColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(valColName,null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	
	static SettingsModelString createGroupModeSettingsModel() {
		SettingsModelString coof = new SettingsModelString(groupMode, "all columns");
		coof.setEnabled(true);
		return coof;				
	}	
    
	static SettingsModelIntegerBounded createMinTotalSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(minTotalPerGroup, 0, 0, 1048575);
		return roff;				
	}	
	
	static SettingsModelIntegerBounded createMinCounterSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(minCounterPerGroup, 0, 0, 1048575);
		return roff;				
	}	
	
	private final SettingsModelString m_valColName = createValColNameStringSettingsModel();
	private final SettingsModelString m_groupMode = createGroupModeSettingsModel();
	private final SettingsModelIntegerBounded m_minTotal = createMinTotalSettingsModel();
	private final SettingsModelIntegerBounded m_minCounter = createMinCounterSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected GroupAllNodeModel() {
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
		LOGGER.info("Start grouping columns");

		/*
		 * The input data table to work with. The "inData" array will contain as many
		 * input tables as specified in the constructor. In this case it can only be one
		 * (see constructor).
		 */
		BufferedDataTable inputTable = inData[0];
		GroupingMapper mapper = new GroupingMapper();
		


		/*
		 * The execution context provides storage capacity, in this case a
		 * data container to which we will add rows sequentially. Note, this container
		 * can handle arbitrary big data tables, it will buffer to disc if necessary.
		 * The execution context is provided as an argument to the execute method by the
		 * framework. Have a look at the methods of the "exec". There is a lot of
		 * functionality to create and change data tables.
		 */
		
		
		String groupMode = m_groupMode.getStringValue();
		BufferedDataContainer container = exec.createDataContainer(getSpec(groupMode));
		
		String[] colNames = inData[0].getDataTableSpec().getColumnNames();
		int valColIndex = inData[0].getDataTableSpec().findColumnIndex(m_valColName.getStringValue());

		
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

			int counterVal = ((IntCell) currentRow.getCell(valColIndex)).getIntValue();
			
			// Iterate over the cells of the current row.
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
					
		
				
				if (i != valColIndex) {
					
					if(groupMode.equals("all columns"))
					{
					//mapper.add(colNames[i] + "col:|" + cell.toString(), counterVal);
					mapper.add(Arrays.asList(colNames[i], cell.toString(), cell.getType().toPrettyString()), counterVal);
					}
					else if (groupMode.equals("only numbers") && cell.getType().getCellClass().equals((DoubleCell.class)))
					{
					mapper.add(Arrays.asList(colNames[i], ((DoubleCell) cell).getDoubleValue()), counterVal);	
					}
					else if (groupMode.equals("only string") && cell.getType().getCellClass().equals((StringCell.class)))
					{
					mapper.add(Arrays.asList(colNames[i], cell.toString()), counterVal);	
					}						
					
				}
				/*
				 * In this example we do not check for missing cells. If there are missing cells
				 * in a row, the node will throw an Exception because we try to create a row
				 * with less cells than specified in the table specification we used to create
				 * the data container above. Hence, for your node implementation keep in mind to
				 * check for missing cells in the input table. Then create missing cells with an
				 * appropriate message or throw an Exception with a nice error message in case
				 * missing cells are not allowed at all. Here, this could be done in an 'else
				 * if' clause checking 'cell.isMissing()'. Then, add a new MissingCell to the
				 * list of cells.
				 */
			}
			

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
		
		
		int rowNum = 0;

		exec.setProgress(currentRowCounter / (double) inputTable.size(), "prepare result table");
		
		
		

		
		
		
		
		for (Entry<List<Object>, int[]> entry : mapper.getData().entrySet()) {
			
			if (entry.getValue()[0] >= m_minTotal.getIntValue()  && entry.getValue()[1] >= m_minCounter.getIntValue())
			{
				if(groupMode.equals("all columns"))
				{
					addRow
					(
						container
						,"Row_" + rowNum		
						,(String) entry.getKey().get(0)
						,(String) entry.getKey().get(1)
						,(String) entry.getKey().get(2)
						,entry.getValue()[0]
						,entry.getValue()[1]		
					);
					
				}
				else if (groupMode.equals("only numbers"))
				{
					addRow
					(
						container
						,"Row_" + rowNum		
						,(String) entry.getKey().get(0)
						,(Double) entry.getKey().get(1)
						,entry.getValue()[0]
						,entry.getValue()[1]		
					);
				}
				else if (groupMode.equals("only string"))
				{
					addRow
					(
						container
						,"Row_" + rowNum		
						,(String) entry.getKey().get(0)
						,(String) entry.getKey().get(1)
						,entry.getValue()[0]
						,entry.getValue()[1]		
					);
				}	
				
				
				rowNum++;
			}

		}
		
		
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	
	

	private void addRow(
			BufferedDataContainer container
			,String rowKey
			,String colName
			,String key
			,String type
			,int total
			,int counter
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(rowKey), new DataCell[] { 
						StringCellFactory.create(colName)
						,StringCellFactory.create(key)
						,StringCellFactory.create(type)
						,IntCellFactory.create(total)
						,IntCellFactory.create(counter)
						
				}));
	}
	
	private void addRow(
			BufferedDataContainer container
			,String rowKey
			,String colName
			,Double key
			,int total
			,int counter
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(rowKey), new DataCell[] { 
						StringCellFactory.create(colName)
						,DoubleCellFactory.create(key)
						,IntCellFactory.create(total)
						,IntCellFactory.create(counter)
				}));
	}
	private void addRow(
			BufferedDataContainer container
			,String rowKey
			,String colName
			,String key
			,int total
			,int counter
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(rowKey), new DataCell[] { 
						StringCellFactory.create(colName)
						,StringCellFactory.create(key)
						,IntCellFactory.create(total)
						,IntCellFactory.create(counter)
				}));
	}	
	
	private DataTableSpec getSpec(String groupMode)
	{
		
		
		DataTableSpecCreator crator = new DataTableSpecCreator();
		crator.addColumns(new DataColumnSpecCreator("ColName", StringCellFactory.TYPE).createSpec());
		
		if(groupMode.equals("only numbers"))
		{
		crator.addColumns(new DataColumnSpecCreator("Key", DoubleCellFactory.TYPE).createSpec());
		}
		else
		{
		crator.addColumns(new DataColumnSpecCreator("Key", StringCellFactory.TYPE).createSpec());
		}
		
		
		if(groupMode.equals("all columns"))
		{
		crator.addColumns(new DataColumnSpecCreator("type", StringCellFactory.TYPE).createSpec());
		}
		crator.addColumns(new DataColumnSpecCreator("total", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("counter", IntCellFactory.TYPE).createSpec());
		return crator.createSpec();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		//return new DataTableSpec[] { getSpec() };
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
		m_valColName.saveSettingsTo(settings);
		m_groupMode.saveSettingsTo(settings);
		m_minTotal.saveSettingsTo(settings);
		m_minCounter.saveSettingsTo(settings);
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
		m_groupMode.loadSettingsFrom(settings);
		m_minTotal.loadSettingsFrom(settings);
		m_minCounter.loadSettingsFrom(settings);
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
		m_groupMode.validateSettings(settings);
		m_minTotal.validateSettings(settings);
		m_minCounter.validateSettings(settings);
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

