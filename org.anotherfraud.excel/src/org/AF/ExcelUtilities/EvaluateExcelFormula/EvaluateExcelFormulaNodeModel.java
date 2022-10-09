package org.AF.ExcelUtilities.EvaluateExcelFormula;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.StringCell.StringCellFactory;
import org.knime.core.data.def.TimestampCell;
import org.knime.core.data.image.png.PNGImageCell;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.data.image.png.PNGImageValue;
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
 * "EvaluateExcelFormula" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class EvaluateExcelFormulaNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(EvaluateExcelFormulaNodeModel.class);
	
	static final String valColName = "formulaColName";


	static SettingsModelString createValColNameStringSettingsModel() {
		SettingsModelString coof = new SettingsModelString(valColName,null);
		coof.setEnabled(true);
		return coof;				
	}	

	
	private final SettingsModelString m_valColName = createValColNameStringSettingsModel();



	/**
	 * Constructor for the node model.
	 */
	protected EvaluateExcelFormulaNodeModel() {
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
		LOGGER.info("Starting eval of excel formula cell");

		Workbook workbook = new XSSFWorkbook();
		Sheet tmpSheet = workbook.createSheet("tempSheet");
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				
		DataFormat df = workbook.createDataFormat();
		CellStyle textStyle = workbook.createCellStyle();
		textStyle.setDataFormat(df.getFormat("text"));	
		/*
		 * The input data table to work with. The "inData" array will contain as many
		 * input tables as specified in the constructor. In this case it can only be one
		 * (see constructor).
		 */
		BufferedDataTable inputTable = inData[0];

		DataTableSpec outputSpec = createOutputSpec(inputTable.getDataTableSpec());
		BufferedDataContainer container = exec.createDataContainer(outputSpec);

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
				
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);	

				writeXlsSSCell(workbook, tmpSheet, textStyle, currentRowCounter, i,  cell);	
			}
			
			currentRowCounter++;
			exec.checkCanceled();		
			exec.setProgress(currentRowCounter / ((double) inputTable.size() * 2), "parsing row " + currentRowCounter);
		}
		
		
		currentRowCounter = 0;
		rowIterator = inputTable.iterator();
		while (rowIterator.hasNext()) {
			
			DataRow currentRow = rowIterator.next();
			List<DataCell> cells = new ArrayList<>();
			int numberOfCells = currentRow.getNumCells();
			
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				cells.add(cell);
			}
		
			
			Row XlsRow = tmpSheet.getRow(currentRowCounter);

			
			Cell XlsCell = XlsRow.getCell(valColIndex); 
			

			CellValue cellValue = evaluator.evaluate(XlsCell);
					

		
			switch (cellValue.getCellType()) {
			    case BOOLEAN:
			    	cells.add(StringCellFactory.create(String.valueOf(cellValue.getBooleanValue())));
			        break;
			    case NUMERIC:
			    		cells.add(StringCellFactory.create(String.valueOf(cellValue.getNumberValue())));	
			        break;
			    case STRING:
			        cells.add(StringCellFactory.create(cellValue.getStringValue()));
			        break;
			    case BLANK:
			    	cells.add(DataType.getMissingCell());
			        break;
			    case ERROR:	
			    	cells.add(StringCellFactory.create(cellValue.formatAsString()));
			        break;
			default:
				cells.add(DataType.getMissingCell());
				break;
			}	
			
			

	
			
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
			exec.setProgress(currentRowCounter / ((double) inputTable.size() * 2), "Parsing formulas " + currentRowCounter);
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
	 * writes cell to excel and set current format if row/column have to be created
 * @param textStyle 
	 */		
	private void writeXlsSSCell(Workbook workbook, Sheet sheet, CellStyle textStyle, int rowIndex, int colIndex,  DataCell cell)	
	{
		CreationHelper createHelper = workbook.getCreationHelper();
		FormulaEvaluator evaluator = createHelper.createFormulaEvaluator();
		
	
		Row row = sheet.getRow(rowIndex);
		

		if(row == null)
		{
			sheet.createRow(rowIndex);
			row = sheet.getRow(rowIndex);	
		}
		
		
		
		//shift xls col by offset
		Cell xlsxCell = row.getCell(colIndex); 

			
		if(xlsxCell == null)
		{
			
			xlsxCell = row.createCell(colIndex);
		}
			
		
		
		if(cell.isMissing())
		{
		//handle all null values
			
		}
		else if(cell.getType().getCellClass().equals((IntCell.class))) 
		{
			
			IntCell intCell = (IntCell) cell;
			xlsxCell.setCellValue(
					intCell.getIntValue());
		}
		else if(cell.getType().getCellClass().equals((DoubleCell.class))) 
		{
			DoubleCell doubleCell = (DoubleCell) cell;
			xlsxCell.setCellValue(
					doubleCell.getDoubleValue());
		}
		
		else if(cell.getType().getCellClass().equals((LongCell.class))) 
		{
			LongCell longCell = (LongCell) cell;
			xlsxCell.setCellValue(
					longCell.getLongValue());
		}
		
		

		else if(cell.getType().getCellClass().equals((DateAndTimeCell.class))) 
		{
			
			DateAndTimeCell timeDateCell = (DateAndTimeCell) cell;
			xlsxCell.setCellValue(
					timeDateCell.getStringValue());			

			
		}
		else if(cell.getType().getCellClass().equals((TimestampCell.class))) 
		{
			
			TimestampCell timeCell = (TimestampCell) cell;
			xlsxCell.setCellValue(
					timeCell.getDate());			
		}					
		else if(cell.getType().getCellClass().equals((BooleanCell.class))) 
		{
			BooleanCell boolCell = (BooleanCell) cell;
			xlsxCell.setCellValue(
					boolCell.getBooleanValue());	
			
		}
		
		
		
		
		else if(cell.getType().getCellClass().equals((StringCell.class))) 
		{
			StringCell stringCell = (StringCell) cell;
			
			if (!stringCell.getStringValue().startsWith("="))
			{
				
				xlsxCell.setCellStyle(textStyle);
				//escape formula 
				xlsxCell.setCellValue(createHelper.createRichTextString(stringCell.getStringValue()));
				
				
			} else if (stringCell.getStringValue().startsWith("="))
			{
				
				xlsxCell.setCellFormula(stringCell.getStringValue().substring(1));
				evaluator.evaluateFormulaCell(xlsxCell);	
			}
			
			
			
			else
			{
				xlsxCell.setCellValue(createHelper.createRichTextString(stringCell.getStringValue()));
			}	
		}
		

		else if(cell.getType().getCellClass().equals((PNGImageCell.class))) 
		{
			
			
			PNGImageContent image = ((PNGImageValue) cell).getImageContent();
			
			byte[] bytes = image.getByteArray();

			
			int my_picture_id = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			Drawing<?> drawing = (Drawing<?>) sheet.createDrawingPatriarch();
			
			ClientAnchor my_anchor = createHelper.createClientAnchor();
			
			//upper right
			my_anchor.setCol1(colIndex);
			my_anchor.setRow1(rowIndex); 
			
			//lower left
			my_anchor.setCol2(colIndex+1);
			my_anchor.setRow2(rowIndex+1); 
		

			@SuppressWarnings("unused")
			Picture my_picture = drawing.createPicture(my_anchor, my_picture_id);
			//my_picture.resize();
			   
		}
		

		
		else
		{
			xlsxCell.setCellValue(cell.toString());
		}
		
		
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
	 * @param dataType 
	 * @return
	 */
	private DataTableSpec createOutputSpec(DataTableSpec inputTableSpec) {
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			newColumnSpecs.add(inputTableSpec.getColumnSpec(i));
		}
		
		
		String newName =  m_valColName.getStringValue() + "_result";
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
		m_valColName.saveSettingsTo(settings);
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

