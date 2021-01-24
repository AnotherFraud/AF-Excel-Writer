package org.AnotherFraudExcel;


/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * @author Another Fraud
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;
import org.knime.core.data.image.png.PNGImageCell;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.data.image.png.PNGImageValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.data.location.cell.FSLocationCell;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;





@SuppressWarnings("deprecation")
public class WriteToExcelTemplateXLSXNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(WriteToExcelTemplateXLSXNodeModel.class);
	private Optional<FSConnection> m_fs = Optional.empty();
	private int defaulttimeoutInSeconds = 5;
    static final String templatefilePath2 = "templateFile2";
    static final String templatefilePath = "templateFile";
    static final String colOffset = "colOff";
    static final String rowOffset = "rowOff";
    static final String outputfilePath2 = "outputFile2";
    static final String outputfilePath = "outputFile";
    static final String copyOrWrite = "copywrite";
    static final String overrideOrFail = "overridefail";
    static final String sheetName = "sheet";
    static final String sheetIndex = "sheetIndex";
    static final String sheetNameOrIndex = "sheetOrIndex";
    static final String writeHeaderOption = "writeHeader";
    static final String writeFormulaOption = "writeFormula";
    static final String writeLastRowOption = "lastRowOption";
    static final String password = "pwd";
    static final String outPassword = "outpwd";
    static final String enablePassOption = "enablePwd";
        
	static SettingsModelString createTemplateFilePathSettingsModel() {
		return new SettingsModelString(templatefilePath, null);
	}
	
	
	
	static SettingsModelFileChooser2 createTemplateFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(templatefilePath2, new String[] { ".xlsx", ".xls", ".xlsm" });
	}
	
	

	
	static SettingsModelAuthentication createPassSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(password, AuthenticationType.PWD);
		cps.setEnabled(false);
		return cps;
	}
	
	static SettingsModelAuthentication createOutPassSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(outPassword, AuthenticationType.PWD);
		cps.setEnabled(false);
		return cps;
	}

	
	static SettingsModelString createOutputFilePathSettingsModel() {
		SettingsModelString ofp = new SettingsModelString(outputfilePath, null);
		ofp.setEnabled(false);	
		return ofp;
	}
	


	static SettingsModelFileChooser2 createOutputFilePath2SettingsModel() {
		SettingsModelFileChooser2 ofp = new SettingsModelFileChooser2(outputfilePath2, new String[] { ".xlsx", ".xls", ".xlsm" });
		ofp.setEnabled(false);	
		return ofp;
	}
	

	
	
	static SettingsModelString createCopyOrWriteSettingsModel() {
		return new SettingsModelString(copyOrWrite, "WriteInto");
	}	
	
	
	
	static SettingsModelString createSheetNamesModel() {
		SettingsModelString sn = new SettingsModelString(sheetName, "");
		sn.setEnabled(false);	
        return sn;
    }

	
	
	static SettingsModelIntegerBounded createSheetIndexModel() {
		SettingsModelIntegerBounded si = new SettingsModelIntegerBounded(sheetIndex, 0, 0, 255);
		si.setEnabled(true);
        return si;
    }
	
	static SettingsModelString createSheetNameOrIndexSettingsModel() {
		return new SettingsModelString(sheetNameOrIndex, "index");
	}	
	
	
	
	static SettingsModelString createOverrideOrFailModelSettingsModel() {
		SettingsModelString coof = new SettingsModelString(overrideOrFail, "Fail");
		coof.setEnabled(false);
		return coof;				
	}	
	

	static SettingsModelIntegerBounded createRowOffsetSettingsModel() {
		SettingsModelIntegerBounded roff = new SettingsModelIntegerBounded(rowOffset, 2, 1, 1048575);
		return roff;				
	}	
	
	static SettingsModelIntegerBounded createColOffsetSettingsModel() {
		SettingsModelIntegerBounded coff = new SettingsModelIntegerBounded(colOffset, 1, 1, 16384);
		return coff;				
	}	
	
	
	static SettingsModelBoolean createWriteLastRowSettingsModel() {
		SettingsModelBoolean wlr = new SettingsModelBoolean(writeLastRowOption, false);
		return wlr;				
	}	

	static SettingsModelString enablePasswordSettingsModel() {
		SettingsModelString epw = new SettingsModelString(enablePassOption, "No PWD needed");
		return epw;				
	}	    
    

	
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();
	private final SettingsModelAuthentication m_outPwd = createOutPassSettingsModel();
	private final SettingsModelString m_sheetName = createSheetNamesModel();
	private final SettingsModelFileChooser2 m_templatefilePath2 = createTemplateFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_outputfilePath2 = createOutputFilePath2SettingsModel();

	private final SettingsModelString m_copyOrWrite = createCopyOrWriteSettingsModel();
	private final SettingsModelString m_sheetOrIndex = createSheetNameOrIndexSettingsModel();
	private final SettingsModelIntegerBounded m_sheetIndex = createSheetIndexModel();
	
	private final SettingsModelIntegerBounded m_rowOffset = createRowOffsetSettingsModel();
    private final SettingsModelIntegerBounded m_colOffset = createColOffsetSettingsModel();
    private final SettingsModelBoolean m_writeLastRowOption = createWriteLastRowSettingsModel();      
    private final SettingsModelString m_enablePassOption = enablePasswordSettingsModel();   
    private final SettingsModelString m_overrideOrFail = createOverrideOrFailModelSettingsModel();

    private final SettingsModelBoolean m_writeHeaderOption =
            new SettingsModelBoolean(writeHeaderOption, false);
    
    private final SettingsModelBoolean m_writeFormulaOption =
            new SettingsModelBoolean(writeFormulaOption, false);
    
    
    
	/**
	 * The settings model to manage the shared settings. This model will hold the
	 * value entered by the user in the dialog and will update once the user changes
	 * the value. Furthermore, it provides methods to easily load and save the value
	 * to and from the shared settings (see:
	 * <br>
	 * {@link #loadValidatedSettingsFrom(NodeSettingsRO)},
	 * {@link #saveSettingsTo(NodeSettingsWO)}). 
	 * <br>
	 * Here, we use a SettingsModelString as the number format is a String. 
	 * There are models for all common data types. Also have a look at the comments 
	 * in the constructor of the {@link WriteToExcelTemplateXLSXNodeDialog} as the settings 
	 * models are also used to create simple dialogs.
	 */


	/**
	 * Constructor for the node model.
	 */
	protected WriteToExcelTemplateXLSXNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(1,0);
	}


	/**
	 * 
	 * {@inheritDoc}
	 * @return 
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec)
			throws Exception {
		/*
		 * The functionality of the node is implemented in the execute method. This
		 * implementation will write in input buffered table to the selected Excel sheet
		 * output
		 */
		LOGGER.info("Start execution of excel file to template");

	

		
		BufferedDataTable inputTable =  (BufferedDataTable)inObjects[0];
		
		
		/*
		 * A counter for how many rows have already been processed. This is used to
		 * calculate the progress of the node, which is displayed as a loading bar under
		 * the node icon.
		 */
		CloseableRowIterator rowIterator = inputTable.iterator();

	
	try 
	{
			
		
		FileChooserHelper fileHelperTemplate = new FileChooserHelper(m_fs, m_templatefilePath2, defaulttimeoutInSeconds * 1000);
		Path pathTemplate = fileHelperTemplate.getPathFromSettings();
			
		String templatefilePath = pathTemplate.toAbsolutePath().toString();
		String outputPath;

		
		Workbook workbook = openWorkBook(Files.newInputStream(pathTemplate));
	
	
		
		
		if (m_copyOrWrite.getStringValue().equals("CopyFrom"))
		{	
			
			FileChooserHelper fileHelperOutput;
			fileHelperOutput = new FileChooserHelper(m_fs, m_outputfilePath2, defaulttimeoutInSeconds * 1000);
			Path pathOutput = fileHelperOutput.getPathFromSettings();
			outputPath = pathOutput.toAbsolutePath().toString();
			
		}
		else
		{
			outputPath = templatefilePath;
		}
		
		pushFlowVariableString("templatefilePath", templatefilePath);
		pushFlowVariableString("outputFilePath", outputPath);
		
		
		
		//fail if file exists and fail on exists was selected
		if (
			isFileReachable(outputPath) != null 
			&& m_overrideOrFail.getStringValue().equals("Fail") 
			&& m_copyOrWrite.getStringValue().equals("CopyFrom")
		)
		{
			throw new IOException("Output file exists and fail overwrite option was selected");
		}
		
		Sheet sheet;
		
		int xlsxRowOffset;
		int xlsxColOffset =  m_colOffset.getIntValue() - 1;
		int currentRowCounter = 0;
		
		

		//use sheet name if available
		if (m_sheetName.getStringValue().length() > 0 && m_sheetOrIndex.getStringValue().equals("name"))
		{
			sheet = workbook.getSheet(m_sheetName.getStringValue());
			
		} else
		{
			sheet = workbook.getSheetAt(m_sheetIndex.getIntValue());
		}
		

		
		
		//get last row in Excel if write to last row option is selected
		if(m_writeLastRowOption.getBooleanValue())
		{

			xlsxRowOffset = sheet.getPhysicalNumberOfRows();
		}
		else
		{
			xlsxRowOffset =	m_rowOffset.getIntValue() - 1;

		};
		
		
		
		//writer column headers if enabled
		if (m_writeHeaderOption.getBooleanValue())
		{
		int colHeaderIndex = 0;
			for (String headerName: inputTable.getSpec().getColumnNames()) {           
				writeXlsSSCell(workbook,sheet,currentRowCounter + xlsxRowOffset, colHeaderIndex + xlsxColOffset, new StringCell(headerName));
				colHeaderIndex++;
				
			}
			xlsxRowOffset = xlsxRowOffset + 1;

		}
		
		
		
		try
		{
			

			// Iterate over the rows of the input table.
			while (rowIterator.hasNext()) {
				
				DataRow currentRow = rowIterator.next();
				int numberOfCells = currentRow.getNumCells();


				
				for (int i = 0; i < numberOfCells; i++) {
					writeXlsSSCell(workbook, sheet,currentRowCounter + xlsxRowOffset, i + xlsxColOffset, currentRow.getCell(i));
					}
					
				

				
				exec.checkCanceled();
				
				currentRowCounter++;
				exec.setProgress(currentRowCounter / (double) inputTable.size(), "writing row " + currentRowCounter);				
				

			}
			
			
		
			
			
			
			
			try(
					FileOutputStream out = new FileOutputStream(new File(outputPath))		
			)
			{
				
			writeXlsWithPassword(workbook,outputPath, out);	
			out.close();
			
			} catch (Exception c) {
				 throw new InvalidSettingsException(
							"Error while saving output file " + c.getMessage(), c);

			} 
			

			

			return new PortObject[]{};
			
		} catch (Exception c) {
			 throw new InvalidSettingsException(
						"Error while writing " + c.getMessage(), c);

		} 
		
		
		
		
		
		
	}
	 catch (Exception e) {
		 throw new InvalidSettingsException(
					"Reason: "  + e.getMessage(), e);
		 }

	} 
	
		
	
	
	
	/**
	 * handles the different output write options 
	 * writes if selected the Excel file with password or without
	 */
		private void writeXlsWithPassword(Workbook workbook, String outputPath, FileOutputStream out) throws Exception {
			
			String outputPass = "";
			
			
			if (m_enablePassOption.getStringValue().equals("Open with PWD")) {
				outputPass = m_pwd.getPassword();
            } else if (m_enablePassOption.getStringValue().equals("Remove PWD")) {
            	outputPass = "";
            }else if (m_enablePassOption.getStringValue().equals("Change PWD")) {
            	outputPass = m_outPwd.getPassword();
            }else if (m_enablePassOption.getStringValue().equals("Add PWD")) {
            	outputPass = m_outPwd.getPassword();
            }
			
			

			
			if (outputPass.length()>0)
			{
				if (workbook instanceof HSSFWorkbook)
				{
	
					encryptXLS(workbook, out, outputPass);
	
				}
				else if (workbook instanceof XSSFWorkbook)
				{
						encryptXLSX(workbook, out, outputPass);
				}
				else
				{
					throw new IOException("Unsuppored encrypted format");
				}
			}
			else
			{
				//write out withtout passwort
			 workbook.write(out);
			}

		
		
	}
		

		/**
		 * handles xls file encryption
		 */
		 public static void encryptXLS(Workbook workbook, FileOutputStream out, String outputPass) throws Exception {

	
			 Biff8EncryptionKey.setCurrentUserPassword(outputPass);
			 workbook.write(out);
				
			 }
		 
			/**
			 * handles xlsx/xslm file encryption
			 */	 
		 public static void encryptXLSX(Workbook workbook, FileOutputStream out,String outputPass) throws Exception {

			  POIFSFileSystem fs = new POIFSFileSystem();
			  EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);

			  Encryptor enc = info.getEncryptor();
			  enc.confirmPassword(outputPass);

			  OutputStream encOutp = enc.getDataStream(fs);
			  workbook.write(encOutp);
			  workbook.close();
			  encOutp.close();

			  fs.writeFilesystem(out);
			  out.close();
			  fs.close();
			 }


		 
		 
		 /**
			 * loads input file as workbook
			 * handles input password if required
			 */		
private Workbook openWorkBook(InputStream file) throws IOException, GeneralSecurityException,EncryptedDocumentException,InvalidFormatException {
			
			Workbook workbook = null;


			workbook = WorkbookFactory.create(file,m_pwd.getPassword());
			file.close();
			
	
		return workbook;
	}





	

/**
	 * writes cell to excel and set current format if row/column have to be created
	 */		
	private void writeXlsSSCell(Workbook workbook, Sheet sheet, int rowIndex, int colIndex,  DataCell cell)	
	{
		CreationHelper createHelper = workbook.getCreationHelper();
		FormulaEvaluator evaluator = createHelper.createFormulaEvaluator();
		DataFormat df = workbook.createDataFormat();
		CellStyle textStyle = workbook.createCellStyle();
		
		
		Row row = sheet.getRow(rowIndex);
		

		if(row == null)
		{
			
			createRowAtIndexWithColFormats(sheet, rowIndex);
			row = sheet.getRow(rowIndex);
			
			
		}
		
		
		
		//shift xls col by offset
		Cell xlsxCell = row.getCell(colIndex); 

			
		if(xlsxCell == null)
		{
			
			xlsxCell = row.createCell(colIndex);
			xlsxCell.setCellStyle(getCellStype(row, colIndex));

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
		
		else if(cell.getType().getCellClass().equals((FSLocationCell.class))) 
		{
			FSLocationCell locCell = (FSLocationCell) cell;
			
			xlsxCell.setCellValue(
					locCell.getFSLocation().getPath());
			
		}
		
		
		
		else if(cell.getType().getCellClass().equals((StringCell.class))) 
		{
			StringCell stringCell = (StringCell) cell;
			
			if (stringCell.getStringValue().startsWith("=") && m_writeFormulaOption.getBooleanValue())
			{
				
				textStyle = xlsxCell.getCellStyle();
				textStyle.setDataFormat(df.getFormat("text"));
				xlsxCell.setCellStyle(textStyle);
				//escape formula 
				xlsxCell.setCellValue(createHelper.createRichTextString(stringCell.getStringValue()));
				
				
			} else if (stringCell.getStringValue().startsWith("=") && !m_writeFormulaOption.getBooleanValue())
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
			Drawing drawing = (Drawing) sheet.createDrawingPatriarch();
			
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

	private CellStyle getCellStype(Row row, int colIndex) {
			Cell cell = row.createCell(colIndex);
			CellStyle cellStyle = cell.getCellStyle();
			if (cellStyle.getIndex() == 0) 
			{
				cellStyle = row.getRowStyle();
			}
			if (cellStyle == null) 
			{
				cellStyle = cell.getSheet().getColumnStyle(colIndex);
			}
			if (cellStyle == null) 
			{
				cellStyle = cell.getCellStyle();
			}
			return cellStyle;
			
			
		}


	private void createRowAtIndexWithColFormats(Sheet sheet, int rowIndex) {
			
		
		sheet.createRow(rowIndex);
		Row row = sheet.getRow(rowIndex);
		row.getLastCellNum();
		
		for (int i = 0; i < row.getLastCellNum(); i++)
		{
			row.getCell(i).setCellStyle(sheet.getColumnStyle(i));
		}
		
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
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
		return new PortObjectSpec[]{};
	}

	
	
	
	 /**
		 * try to read sheet names from given 
		 */		
	static List<String> tryGetExcelSheetNames(String filePath, String pass)
	{
		
		
		Workbook workbook;
	
		File checkFile = new File(filePath);
	
		

		
		if (checkFile.exists() && checkFile.canRead())
		{
			
			
			
			
			try (FileInputStream fileStream = new FileInputStream(checkFile))
			{
				workbook = WorkbookFactory.create(fileStream,pass);
					
				List<String> sheetNames = new ArrayList<String>();
				for (int i=0; i<workbook.getNumberOfSheets(); i++) {
				    sheetNames.add( workbook.getSheetName(i) );
				}
				
				return sheetNames;
			} catch (Exception e) { 
				return null;
			}
		}
		else
		{
			return null;
		}

	}
	

	
	
	 /**
		 * try to reach file and return file from filepath/url if exists
		 */				
	public File isFileReachable(String filePathOrUrl) {
        
			File returnFile = null;
			
	
            try {
                URL url = new URL(filePathOrUrl);
                returnFile = new File(url.getFile());
                }
            catch (Exception e)
            { 
            }
            if (returnFile == null)
            {
            	try {
            		File f = new File(filePathOrUrl);
            		if (f.exists() && f.canRead() && f.isFile()) {
            			returnFile = f;
            		}
            	}catch (Exception e)
                { 
                }
            }
            return returnFile;
            

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
		//m_numberFormatSettings.saveSettingsTo(settings);
		
		m_sheetName.saveSettingsTo(settings);
		m_copyOrWrite.saveSettingsTo(settings);
		m_colOffset.saveSettingsTo(settings);
		m_rowOffset.saveSettingsTo(settings);
		m_sheetOrIndex.saveSettingsTo(settings);
		m_sheetIndex.saveSettingsTo(settings);
		m_writeLastRowOption.saveSettingsTo(settings);
		m_writeHeaderOption.saveSettingsTo(settings);
		m_writeFormulaOption.saveSettingsTo(settings);
		m_overrideOrFail.saveSettingsTo(settings);
		m_pwd.saveSettingsTo(settings);
		m_outPwd.saveSettingsTo(settings);
		m_templatefilePath2.saveSettingsTo(settings);
		m_outputfilePath2.saveSettingsTo(settings);
		m_enablePassOption.saveSettingsTo(settings);
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

		

		m_sheetName.loadSettingsFrom(settings);
		m_colOffset.loadSettingsFrom(settings);
		m_rowOffset.loadSettingsFrom(settings);
		
		m_copyOrWrite.loadSettingsFrom(settings);
		m_sheetOrIndex.loadSettingsFrom(settings);
		m_sheetIndex.loadSettingsFrom(settings);
		m_writeLastRowOption.loadSettingsFrom(settings);
		m_writeHeaderOption.loadSettingsFrom(settings);
		m_writeFormulaOption.loadSettingsFrom(settings);
		m_overrideOrFail.loadSettingsFrom(settings);
		m_pwd.loadSettingsFrom(settings);
		m_outPwd.loadSettingsFrom(settings);
		m_templatefilePath2.loadSettingsFrom(settings);
		m_outputfilePath2.loadSettingsFrom(settings);
		m_enablePassOption.loadSettingsFrom(settings);
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

		m_sheetName.validateSettings(settings);
		m_colOffset.validateSettings(settings);
		m_rowOffset.validateSettings(settings);
		m_copyOrWrite.validateSettings(settings);
		m_sheetOrIndex.validateSettings(settings);
		m_sheetIndex.validateSettings(settings);
		m_writeLastRowOption.validateSettings(settings);
		m_writeHeaderOption.validateSettings(settings);
		m_writeFormulaOption.validateSettings(settings);
		m_overrideOrFail.validateSettings(settings);
		m_pwd.validateSettings(settings);
		m_outPwd.validateSettings(settings);
		m_templatefilePath2.validateSettings(settings);
		m_outputfilePath2.validateSettings(settings);
		m_enablePassOption.validateSettings(settings);
		validateUserInput();

	}
	
	protected void validateUserInput() throws InvalidSettingsException
	{
		int colOff = m_colOffset.getIntValue();
		int rowOff = m_rowOffset.getIntValue();
		

		if(
				colOff > 16384
		)
		{
			throw new InvalidSettingsException(
					"Starting column offset is to large - Excel only supports 16384 columns");	
		} else if(colOff < 1 )
		{
			throw new InvalidSettingsException(
					"Starting column offset cannot be below 1");	
		}
		if(
				rowOff > 1048575
		)
		{
			throw new InvalidSettingsException(
					"Starting row offset is to large - Excel only supports 1048575 rows");	
		} else if(rowOff < 1 )
		{
			throw new InvalidSettingsException(
					"Starting row offset cannot be below 1");	
		}
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

