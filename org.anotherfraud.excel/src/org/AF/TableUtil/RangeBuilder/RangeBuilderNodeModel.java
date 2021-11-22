package org.AF.TableUtil.RangeBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
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
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleRange;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is an example implementation of the node model of the
 * "RangeBuilder" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author AnotherFraud
 */
public class RangeBuilderNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(RangeBuilderNodeModel.class);

	static final String counterColName = "counterColName";
	static final String totalColName = "totalColName";
	static final String valColName = "valColName";
	static final String catColName = "catColName";
	static final String percRange = "percRange";

	static final String minTotalPerGroup = "minTotalPerGroup";
	static final String minCounterPerGroup = "minCounterPerGroup";
	static final String maxCounterPerGroup = "maxCounterPerGroup";
	
	
	
	static SettingsModelString createCounterColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(counterColName,null);
		coof.setEnabled(true);
		return coof;				
	}	
	
	static SettingsModelString createTotalColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(totalColName,null);
		coof.setEnabled(true);
		return coof;				
	}		

	static SettingsModelString createValColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(valColName,null);
		coof.setEnabled(true);
		return coof;				
	}		
	
	static SettingsModelString createCatColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(catColName,null);
		coof.setEnabled(true);
		return coof;				
	}	
	

	static SettingsModelIntegerBounded createMinTotalSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(minTotalPerGroup, 0, 0, 1048575);
		return roff;				
	}
	
	
	static SettingsModelDoubleRange createPercRangeSettingsModel() {
		SettingsModelDoubleRange roff = new SettingsModelDoubleRange(percRange,0,100);
		return roff;				
	}	
	
	static SettingsModelIntegerBounded createMinCounterSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(minCounterPerGroup, 0, 0, 1048575);
		return roff;				
	}	
	
	static SettingsModelIntegerBounded createMaxCounterSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(maxCounterPerGroup, 0, 0, 1048575);
		return roff;				
	}	
	
	
	
	private final SettingsModelString m_valColName = createValColNameStringSettingsModel();
	private final SettingsModelString m_catColName = createCatColNameStringSettingsModel();
	private final SettingsModelString m_totalColName = createTotalColNameStringSettingsModel();
	private final SettingsModelString m_counterColName = createCounterColNameStringSettingsModel();
	
	private final SettingsModelDoubleRange m_percRange = createPercRangeSettingsModel();
	
	private final SettingsModelIntegerBounded m_minTotal = createMinTotalSettingsModel();
	private final SettingsModelIntegerBounded m_minCounter = createMinCounterSettingsModel();
	private final SettingsModelIntegerBounded m_maxCounter = createMaxCounterSettingsModel();
	

	
	/**
	 * Constructor for the node model.
	 */
	protected RangeBuilderNodeModel() {
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
		LOGGER.info("Starting Range Builder.");

		BufferedDataTable inputTable = inData[0];
		
		
		


		/*
		 * The execution context provides storage capacity, in this case a
		 * data container to which we will add rows sequentially. Note, this container
		 * can handle arbitrary big data tables, it will buffer to disc if necessary.
		 * The execution context is provided as an argument to the execute method by the
		 * framework. Have a look at the methods of the "exec". There is a lot of
		 * functionality to create and change data tables.
		 */
		
		

		int valColIndex = inData[0].getDataTableSpec().findColumnIndex(m_valColName.getStringValue());
		int catColIndex = inData[0].getDataTableSpec().findColumnIndex(m_catColName.getStringValue());
		int totalIndex = inData[0].getDataTableSpec().findColumnIndex(m_totalColName.getStringValue());
		int counterColIndex = inData[0].getDataTableSpec().findColumnIndex(m_counterColName.getStringValue());
		
		BufferedDataContainer container = exec.createDataContainer(getSpec(inData[0].getDataTableSpec().getColumnSpec(valColIndex)));
		
		String currentCategory = "";		
		ArrayList<List<Object>> countList = new ArrayList<>();

		
		
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
		int rowCnt = 0;
		// Iterate over the rows of the input table.
		while (rowIterator.hasNext()) {
			DataRow currentRow = rowIterator.next();
			
		
			int counterVal = ((IntCell) currentRow.getCell(counterColIndex)).getIntValue();
			int totalVal = ((IntCell) currentRow.getCell(totalIndex)).getIntValue();
			String catVal = ((StringCell) currentRow.getCell(catColIndex)).getStringValue();
			
			DataCell valueCell = ((DataCell) currentRow.getCell(valColIndex));
			
			
			if( !currentCategory.equals(catVal))
			{
				currentCategory = catVal;
				countList = new ArrayList<>();
			}
			

	
			if (counterVal == 0)
			{
				if(countList.size()>0)
				{
					List<Object> list = countList.get(countList.size()-1);
					
					int tmpTotal = (int) list.get(2) + totalVal;
					int tmpCounter = (int) list.get(3) + counterVal;
					
				    list.set(2, tmpTotal);
				    list.set(3, tmpCounter);

				}
				
			}
			else
			{
			
				for(List<Object> list : countList)
				{
					
					int tmpTotal = (int) list.get(2) + totalVal;
					int tmpCounter = (int) list.get(3) + counterVal;
					
		
				    list.set(2, tmpTotal);
				    list.set(3, tmpCounter);
				    double tmpPerc = ((double) tmpCounter) / tmpTotal * 100;
				    
		
				    
				    if(
				    	tmpPerc >= m_percRange.getMinRange() 
				    	&& tmpPerc <= m_percRange.getMaxRange()
				    	&& tmpTotal >= m_minTotal.getIntValue()
				    	&& tmpCounter >= m_minCounter.getIntValue()
				    	&& tmpCounter <= m_maxCounter.getIntValue()	
				    )
				    {
						addRow
						(
							container
							,"Row_" + rowCnt		
							,(String) list.get(0)
							,(DataCell) list.get(1)
							,valueCell
							,tmpTotal
							,tmpCounter
							,tmpPerc	
						);
						rowCnt++;
		
				    }  	
				}
				
				
				countList.add(Arrays.asList(catVal, valueCell,totalVal,counterVal));
				
				double perc = ((double) counterVal) / totalVal * 100;
				//System.out.println("Perc " + perc);
				
			    if(
			    		perc >= m_percRange.getMinRange() 
				    	&& perc <= m_percRange.getMaxRange()
				    	&& totalVal >= m_minTotal.getIntValue()
				    	&& counterVal >= m_minCounter.getIntValue()
				    	&& counterVal <= m_maxCounter.getIntValue()	
				    )
				    {
						addRow
						(
							container
							,"Row_" + rowCnt		
							,catVal
							,valueCell
							,valueCell
							,totalVal
							,counterVal
							,perc	
						);
						rowCnt++;
	
				    } 			
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
		
	
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	

	
	private void addRow(
			BufferedDataContainer container
			,String rowKey
			,String Category
			,DataCell FromValue
			,DataCell ToValue
			,int total
			,int counter
			,Double perc
			)
	{
		container.addRowToTable(
				new DefaultRow(new RowKey(rowKey), new DataCell[] { 
						StringCellFactory.create(Category)
						,FromValue
						,ToValue
						
						,IntCellFactory.create(total)
						,IntCellFactory.create(counter)
						,DoubleCellFactory.create(perc)
				}));
	}	
	
	private DataTableSpec getSpec(DataColumnSpec valColdataColumnSpec)
	{

		
		DataTableSpecCreator crator = new DataTableSpecCreator();
		crator.addColumns(new DataColumnSpecCreator("Category", StringCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("FromValue", valColdataColumnSpec.getType()).createSpec());
		crator.addColumns(new DataColumnSpecCreator("ToValue", valColdataColumnSpec.getType()).createSpec());
		
		
		crator.addColumns(new DataColumnSpecCreator("total", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("counter", IntCellFactory.TYPE).createSpec());
		crator.addColumns(new DataColumnSpecCreator("Perc", DoubleCellFactory.TYPE).createSpec());

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
		m_catColName.saveSettingsTo(settings);
		m_totalColName.saveSettingsTo(settings);
		m_counterColName.saveSettingsTo(settings);
		
		m_percRange.saveSettingsTo(settings);
		m_minTotal.saveSettingsTo(settings);
		m_minCounter.saveSettingsTo(settings);
		m_maxCounter.saveSettingsTo(settings);
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
		m_catColName.loadSettingsFrom(settings);
		m_totalColName.loadSettingsFrom(settings);
		m_counterColName.loadSettingsFrom(settings);
		
		m_percRange.loadSettingsFrom(settings);
		m_minTotal.loadSettingsFrom(settings);
		m_minCounter.loadSettingsFrom(settings);
		m_maxCounter.loadSettingsFrom(settings);
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
		m_catColName.validateSettings(settings);
		m_totalColName.validateSettings(settings);
		m_counterColName.validateSettings(settings);
		
		m_percRange.validateSettings(settings);
		m_minTotal.validateSettings(settings);
		m_minCounter.validateSettings(settings);
		m_maxCounter.validateSettings(settings);
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
