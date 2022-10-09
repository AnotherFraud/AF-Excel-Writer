package org.AF.ExcelUtilities.CopyExcelSheet;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFAnchor;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SimpleShape;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.filehandling.core.connections.DefaultFSConnectionFactory;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.connections.RelativeTo;
import org.knime.filehandling.core.defaultnodesettings.FileSystemChoice;
import org.knime.filehandling.core.defaultnodesettings.FileSystemChoice.Choice;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.ValidationUtils;


/**
 * This is an example implementation of the node model of the
 * "CopyExcelSheet" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class CopyExcelSheetNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(CopyExcelSheetNodeModel.class);

	
	private FSConnection con = DefaultFSConnectionFactory.createLocalFSConnection();
	private Optional<FSConnection> m_fs = Optional.of(con);
	
	
	private int defaulttimeoutInSeconds = 5;
    static final String templatefilePath2 = "templateFile2";
    static final String templatefilePath = "templateFile";
    static final String outputfilePath2 = "outputFile2";
    static final String outputfilePath = "outputFile";
    static final String overrideOrFail = "overridefail";
    static final String inputSheetName = "inputsheet";
    static final String outputSheetName = "outputsheet";
    
    static final String sheetIndex = "sheetIndex";
    static final String sheetNameOrIndex = "sheetOrIndex";
    static final String froceFormulaUpdate = "forceFormula";
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
	

	
	
	
	static SettingsModelString createInputSheetNamesModel() {
		SettingsModelString sn = new SettingsModelString(inputSheetName, "");
		sn.setEnabled(false);	
        return sn;
    }

	static SettingsModelString createOutputSheetNamesModel() {
		SettingsModelString sn = new SettingsModelString(outputSheetName, "");
		sn.setEnabled(true);	
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
	
	

	

	static SettingsModelString enablePasswordSettingsModel() {
		SettingsModelString epw = new SettingsModelString(enablePassOption, "No PWD needed");
		return epw;				
	}	    
    

	
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();
	private final SettingsModelAuthentication m_outPwd = createOutPassSettingsModel();
	private final SettingsModelString m_sheetNameIn = createInputSheetNamesModel();
	private final SettingsModelString m_sheetNameOut = createOutputSheetNamesModel();
	private final SettingsModelFileChooser2 m_templatefilePath2 = createTemplateFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_outputfilePath2 = createOutputFilePath2SettingsModel();

	private final SettingsModelString m_sheetOrIndex = createSheetNameOrIndexSettingsModel();
	private final SettingsModelIntegerBounded m_sheetIndex = createSheetIndexModel();
	
	private final SettingsModelString m_enablePassOption = enablePasswordSettingsModel();   
    

    
    private final SettingsModelBoolean m_forceFormulaUpdateOption =
            new SettingsModelBoolean(froceFormulaUpdate, false);
    
    


	/**
	 * Constructor for the node model.
	 */
	protected CopyExcelSheetNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[] {FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[] {});

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
		LOGGER.info("Start execution of copy excel sheet");


		

	
	try 
	{
			
		FSConnection con2 = retrieveFSConnection(m_templatefilePath2, defaulttimeoutInSeconds * 1000);		
		Path pathTemplate = getPathFromSettings(m_templatefilePath2, con2);	
		
	

		FSConnection con3 = retrieveFSConnection(m_outputfilePath2, defaulttimeoutInSeconds * 1000);
		Path pathOutput = getPathFromSettings(m_outputfilePath2, con3);	

		String templatefilePath = pathTemplate.toAbsolutePath().toString();
		String outputPath = pathOutput.toAbsolutePath().toString();


		
		Workbook workbookIn = openWorkBook(Files.newInputStream(pathTemplate));
		Workbook workbookOut = openWorkBook(Files.newInputStream(pathOutput));
	
		
		

		
		
		
		pushFlowVariableString("templatefilePath", templatefilePath);
		pushFlowVariableString("outputFilePath", outputPath);
		
		
		
		Sheet sheetIn;
		Sheet sheetOut;
		

		
		int currentRowCounter = 0;
		
		
		
		
		

		//use sheet name if available
		if (m_sheetNameIn.getStringValue().length() > 0 && m_sheetOrIndex.getStringValue().equals("name"))
		{
			sheetIn = workbookIn.getSheet(m_sheetNameIn.getStringValue());
			
		} else
		{
			sheetIn = workbookIn.getSheetAt(m_sheetIndex.getIntValue());
		}
		

		
		
		
		
		
		
		Iterator<Row> rowIterator = sheetIn.iterator();
		

			


		try
		{
			
		if (templatefilePath.equals(outputPath))		
				
		{
			Sheet sheetnew = workbookIn.cloneSheet(workbookIn.getSheetIndex(sheetIn));
			workbookIn.setSheetName(workbookIn.getSheetIndex(sheetnew), m_sheetNameOut.getStringValue());
		}
		else
		{
			

			
			
		//create output sheet

		if(workbookOut.getSheetIndex(m_sheetNameOut.getStringValue()) > -1)
		{
			workbookOut.removeSheetAt(workbookOut.getSheetIndex(m_sheetNameOut.getStringValue()));
		}

		
		sheetOut = workbookOut.createSheet(m_sheetNameOut.getStringValue());
		
		
		DataFormat df = workbookIn.createDataFormat();
		CellStyle textStyle = workbookIn.createCellStyle();
		textStyle.setDataFormat(df.getFormat("text"));	
		
		
		copySheetSettings(sheetIn,sheetOut);
		copyColumnFormats(sheetIn,sheetOut);		
				
				
			
			// Iterate over the rows of the input table.
			while (rowIterator.hasNext()) {
				

			Row currentRow = rowIterator.next();
			
			Row outRow = sheetOut.createRow(currentRow.getRowNum());
			
			
			
	        if (currentRow.isFormatted())
	        {

	            CellStyle rowStyle = currentRow.getRowStyle();
            
	    		CellStyle newStyle = workbookOut.createCellStyle();
	    		newStyle.cloneStyleFrom(rowStyle);	

	            outRow.setRowStyle(newStyle);
	        }
	        
	        
	        
	        int defaultHight = sheetIn.getDefaultRowHeight();
	        if (currentRow.getHeight() != defaultHight)
	        {
	        	outRow.setHeight(currentRow.getHeight());
	        }
	        
			
			Iterator <Cell> cellIterator = currentRow.cellIterator();
	    
			while (cellIterator.hasNext()) {
		    	Cell cellIn = cellIterator.next();
		    	Cell cellOut = outRow.createCell(cellIn.getColumnIndex());

		    	copyCell(cellIn, cellOut, workbookOut); 
		      }			
			     
			
			exec.checkCanceled();
			
			currentRowCounter++;
			exec.setProgress(currentRowCounter / (double) sheetIn.getPhysicalNumberOfRows(), "writing row " + currentRowCounter);	
			
			}
			
			
			
			
			//merged regions
	        for (int i = 0; i < sheetIn.getNumMergedRegions(); i++)
	        {
	            CellRangeAddress mergedRegion = sheetIn.getMergedRegion(i);
	            sheetOut.addMergedRegion(mergedRegion);
	        }
	        
	        
	        
	        //pictures and shapes
	        
	 
	        Drawing drawing = sheetIn.getDrawingPatriarch();
	        
	        
	        Drawing drawingOut = sheetOut.createDrawingPatriarch();
	   
	        if (drawing == null)
	        {
	        	
	        }
	        else if (drawing instanceof HSSFPatriarch) {
	            HSSFPatriarch hp = (HSSFPatriarch) drawing;
	            

        
	            
	            for (HSSFShape hs : hp.getChildren()) {	   
	            	
	            	
	            	
	            	
	            	HSSFAnchor xsA = hs.getAnchor();
	            	
	    	        HSSFClientAnchor anc = new HSSFClientAnchor();
	    	        anc.setDx1(xsA.getDx1());
	    	        anc.setDx2(xsA.getDx2());
	    	        anc.setDy1(xsA.getDy1());
	    	        anc.setDy2(xsA.getDy2());
	    	        anc.setCol1(0);
	    	        anc.setRow1(0);

	 
	                if (hs instanceof Picture) {
	                	Picture pic = (Picture) hs; 
	        		        		 int picIndex = workbookOut.addPicture(pic.getPictureData().getData(),  pic.getPictureData().getPictureType());
		        		 drawingOut.createPicture(pic.getClientAnchor(), picIndex);
		        	 }
	                
	                if (hs instanceof SimpleShape) {	  
	                	
	                	
	                	HSSFSimpleShape pic = (HSSFSimpleShape) hs; 
	                	
	           
	                	HSSFSimpleShape shape  = hp.createSimpleShape(anc);
	 
	                	shape.setShapeType(pic.getShapeType());
	                	
	                	
	                	
	                	/*
	                	sp.setText(pic.getText());
	                	sp.setBottomInset(pic.getBottomInset());
	                	sp.setLeftInset(pic.getLeftInset());
	                	sp.setRightInset(pic.getRightInset());
	                	sp.setTopInset(pic.getTopInset());
	                	
	          
	                	sp.setTextDirection(pic.getTextDirection());
	                	sp.setWordWrap(pic.getWordWrap());
	                	sp.setVerticalAlignment(pic.getVerticalAlignment());
	                	sp.setTextAutofit(pic.getTextAutofit());
	                	*/
		        	 }	                
	                
	                
	                
 
	            }
	            
	            
	            
	            
	            
	            
	        } else {
	        	
	        	
	        	
	            XSSFDrawing xdraw = (XSSFDrawing) drawing;
	            for (XSSFShape xs : xdraw.getShapes()) {

	            	
	            	XSSFAnchor xsA = xs.getAnchor();
	            	
	    	        XSSFClientAnchor anc = new XSSFClientAnchor();
	    	        anc.setDx1(xsA.getDx1());
	    	        anc.setDx2(xsA.getDx2());
	    	        anc.setDy1(xsA.getDy1());
	    	        anc.setDy2(xsA.getDy2());
	  
	                if (xs instanceof Picture) {
	                	Picture pic = (Picture) xs; 
	        		        		 int picIndex = workbookOut.addPicture(pic.getPictureData().getData(),  pic.getPictureData().getPictureType());
		        		 drawingOut.createPicture(pic.getClientAnchor(), picIndex);
		        	 }
	                
	                if (xs instanceof SimpleShape) {
	                	
	 
	                	
	                	
	                	//XSSFDrawing xdrawOut = (XSSFDrawing) drawingOut;
	                	/*
	                	
	        
	                	XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1023, 255, (short) 2, 4, (short) 13, 26);
	                	XSSFTextBox textbox = testDraw.createTextbox(anchor);
	                	XSSFRichTextString rtxt = new XSSFRichTextString("ekozhan");
	                	XSSFFont font = (XSSFFont) workbookOut.createFont();
	                	font.setColor((short) 27);
	                	font.setBold(true);
	                	font.setFontHeightInPoints((short) 192);
	                	font.setFontName("Verdana");
	                	rtxt.applyFont(font);
	                	textbox.setText(rtxt);
	              
	                	textbox.setLineWidth(10);
	                	textbox.setLineStyle(1);
	      				*/
	                	
	                	
	    
	                	/*
	                	XSSFSimpleShape sp  = xdrawOut.createSimpleShape(anc);
	                	
	                	sp.setShapeType(pic.getShapeType());
	                	sp.setLineStyle(1);
	                	*/
	                	
	                	
	                	/*
	                	sp.setText(pic.getText());
	                	sp.setBottomInset(pic.getBottomInset());
	                	sp.setLeftInset(pic.getLeftInset());
	                	sp.setRightInset(pic.getRightInset());
	                	sp.setTopInset(pic.getTopInset());
	                	
	          
	                	sp.setTextDirection(pic.getTextDirection());
	                	sp.setWordWrap(pic.getWordWrap());
	                	sp.setVerticalAlignment(pic.getVerticalAlignment());
	                	sp.setTextAutofit(pic.getTextAutofit());
	                	*/
	                	
		        	 }	                
	                
	                
	                
	                
	 
	            }
	        }
	        
	        
		}
					

				
			
			

			
			
			workbookOut.setForceFormulaRecalculation(m_forceFormulaUpdateOption.getBooleanValue());
			
			
			
			
			try(
					OutputStream out = Files.newOutputStream(pathOutput);	
			)
			{
				
			writeXlsWithPassword(workbookOut, out);	
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
	
	
	

	
	
	
	

    private void copyColumnFormats(Sheet sheetIn, Sheet sheetOut) {


     Row firstRow = sheetIn.getRow(sheetIn.getFirstRowNum());
     Iterator <Cell> cellIterator = firstRow.cellIterator();
	    
     
		while (cellIterator.hasNext()) {
	    	Cell cellIn = cellIterator.next();
	    	int cellIndex = cellIn.getColumnIndex();
	    	sheetOut.setDefaultColumnStyle(cellIndex, sheetIn.getColumnStyle(cellIndex));
	    	sheetOut.setColumnWidth(cellIndex, sheetIn.getColumnWidth(cellIndex));

	      }	
    	
    	
		
    	
    	
		
	}



	private void copySheetSettings(Sheet sheetIn, Sheet sheetOut)
    {
        sheetOut.setAutobreaks(sheetIn.getAutobreaks());
        sheetOut.setDefaultColumnWidth(sheetIn.getDefaultColumnWidth());
        sheetOut.setDefaultRowHeight(sheetIn.getDefaultRowHeight());
        sheetOut.setDefaultRowHeightInPoints(sheetIn.getDefaultRowHeightInPoints());
        sheetOut.setDisplayGuts(sheetIn.getDisplayGuts());
        sheetOut.setFitToPage(sheetIn.getFitToPage());

        sheetOut.setForceFormulaRecalculation(sheetIn.getForceFormulaRecalculation());


        Header sheetInHeader = sheetIn.getHeader();
        Header sheetOutHeader = sheetOut.getHeader();
        sheetOutHeader.setCenter(sheetInHeader.getCenter());
        sheetOutHeader.setLeft(sheetInHeader.getLeft());
        sheetOutHeader.setRight(sheetInHeader.getRight());

        Footer sheetInFooter = sheetIn.getFooter();
        Footer sheetOutFooter = sheetOut.getFooter();
        sheetOutFooter.setCenter(sheetInFooter.getCenter());
        sheetOutFooter.setLeft(sheetInFooter.getLeft());
        sheetOutFooter.setRight(sheetInFooter.getRight());

        sheetOut.setHorizontallyCenter(sheetIn.getHorizontallyCenter());
        sheetOut.setMargin(Sheet.LeftMargin, sheetIn.getMargin(Sheet.LeftMargin));
        sheetOut.setMargin(Sheet.RightMargin, sheetIn.getMargin(Sheet.RightMargin));
        sheetOut.setMargin(Sheet.TopMargin, sheetIn.getMargin(Sheet.TopMargin));
        sheetOut.setMargin(Sheet.BottomMargin, sheetIn.getMargin(Sheet.BottomMargin));

        sheetOut.setPrintGridlines(sheetIn.isPrintGridlines());
        sheetOut.setRowSumsBelow(sheetIn.getRowSumsBelow());
        sheetOut.setRowSumsRight(sheetIn.getRowSumsRight());
        sheetOut.setVerticallyCenter(sheetIn.getVerticallyCenter());
        sheetOut.setDisplayFormulas(sheetIn.isDisplayFormulas());
        sheetOut.setDisplayGridlines(sheetIn.isDisplayGridlines());
        sheetOut.setDisplayRowColHeadings(sheetIn.isDisplayRowColHeadings());
        sheetOut.setDisplayZeros(sheetIn.isDisplayZeros());
        sheetOut.setPrintGridlines(sheetIn.isPrintGridlines());
        sheetOut.setRightToLeft(sheetIn.isRightToLeft());

        PrintSetup sheetInPrintSetup = sheetIn.getPrintSetup();
        PrintSetup sheetOutPrintSetup = sheetOut.getPrintSetup();

        sheetOutPrintSetup.setPaperSize(sheetInPrintSetup.getPaperSize());
        sheetOutPrintSetup.setScale(sheetInPrintSetup.getScale());
        sheetOutPrintSetup.setPageStart(sheetInPrintSetup.getPageStart());
        sheetOutPrintSetup.setFitWidth(sheetInPrintSetup.getFitWidth());
        sheetOutPrintSetup.setFitHeight(sheetInPrintSetup.getFitHeight());
        sheetOutPrintSetup.setLeftToRight(sheetInPrintSetup.getLeftToRight());
        sheetOutPrintSetup.setLandscape(sheetInPrintSetup.getLandscape());
        sheetOutPrintSetup.setValidSettings(sheetInPrintSetup.getValidSettings());
        sheetOutPrintSetup.setNoColor(sheetInPrintSetup.getNoColor());
        sheetOutPrintSetup.setDraft(sheetInPrintSetup.getDraft());
        sheetOutPrintSetup.setNotes(sheetInPrintSetup.getNotes());
        sheetOutPrintSetup.setNoOrientation(sheetInPrintSetup.getNoOrientation());
        sheetOutPrintSetup.setUsePage(sheetInPrintSetup.getUsePage());
        sheetOutPrintSetup.setHResolution(sheetInPrintSetup.getHResolution());
        sheetOutPrintSetup.setVResolution(sheetInPrintSetup.getVResolution());
        sheetOutPrintSetup.setHeaderMargin(sheetInPrintSetup.getHeaderMargin());
        sheetOutPrintSetup.setFooterMargin(sheetInPrintSetup.getFooterMargin());
        sheetOutPrintSetup.setCopies(sheetInPrintSetup.getCopies());
     
        
    }
    
    
	
	private void copyCell(Cell cellIn, Cell cellOut, Workbook wb)
    {
		
		
		
		
		CellStyle newStyle = wb.createCellStyle();
		
		newStyle.cloneStyleFrom(cellIn.getCellStyle());	
		cellOut.setCellStyle(newStyle);
		
		
	
        switch (cellIn.getCellType())
        {
        case STRING:
        	cellOut.setCellValue(cellIn.getStringCellValue());
            break;
        case NUMERIC:
        	cellOut.setCellValue(cellIn.getNumericCellValue());
            break;
        case BLANK:
        	cellOut.setBlank();
            break;
        case BOOLEAN:
        	cellOut.setCellValue(cellIn.getBooleanCellValue());
            break;
        case ERROR:
        	cellOut.setCellErrorValue(cellIn.getErrorCellValue());
            break;
        case FORMULA:
        	cellOut.setCellFormula(cellIn.getCellFormula());
            break;
        default:
            break;
        }
        
        
        cellOut.setCellComment(cellIn.getCellComment());
   
        
    }

	
	
	
	/**
	 * handles the different output write options 
	 * writes if selected the Excel file with password or without
	 */
		private void writeXlsWithPassword(Workbook workbook, OutputStream out) throws Exception {
			
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
		 public static void encryptXLS(Workbook workbook, OutputStream out, String outputPass) throws Exception {

	
			 Biff8EncryptionKey.setCurrentUserPassword(outputPass);
			 workbook.write(out);
				
			 }
		 
			/**
			 * handles xlsx/xslm file encryption
			 */	 
		 public static void encryptXLSX(Workbook workbook, OutputStream out,String outputPass) throws Exception {

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
		return new PortObjectSpec[0];
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
	

    public Path getPathFromSettings(SettingsModelFileChooser2 m_settings, FSConnection fs) throws InvalidSettingsException  {
        if (m_settings.getPathOrURL() == null || m_settings.getPathOrURL().isEmpty()) {
            throw new InvalidSettingsException("No path specified");
        }
        
        

        final FSPath pathOrUrl;
        final Choice fileSystemChoice = m_settings.getFileSystemChoice().getType();
        switch (fileSystemChoice) {
            case CONNECTED_FS:
                return fs.getFileSystem().getPath(m_settings.getPathOrURL());
            case CUSTOM_URL_FS:
                return fs.getFileSystem().getPath(toCustomUrlFSLocation(m_settings.getPathOrURL(), defaulttimeoutInSeconds*1000));
            case KNIME_FS:
                pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
                ValidationUtils.validateKnimeFSPath(pathOrUrl);
                return pathOrUrl;
            case KNIME_MOUNTPOINT:
                pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
                if (!pathOrUrl.isAbsolute()) {
                    throw new InvalidSettingsException("The path must be absolute, i.e. it must start with '/'.");
                }
                return pathOrUrl;
            case LOCAL_FS:
                ValidationUtils.validateLocalFsAccess();
                pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
                if (!pathOrUrl.isAbsolute()) {
                    throw new InvalidSettingsException("The path must be absolute.");
                }
                return pathOrUrl;
            default:
                final String errMsg =
                    String.format("Unknown choice enum '%s', make sure the switch covers all cases!", fileSystemChoice);
                throw new RuntimeException(errMsg);
        }
    }
    
    
    public static final FSConnection retrieveFSConnection(final SettingsModelFileChooser2 settings, final int timeoutInMillis) {

            final FileSystemChoice choice = settings.getFileSystemChoice();
            switch (choice.getType()) {
                case LOCAL_FS:
                    return DefaultFSConnectionFactory.createLocalFSConnection();
                case CUSTOM_URL_FS:
                    return DefaultFSConnectionFactory.createCustomURLConnection(settings.getPathOrURL(), timeoutInMillis);
                case KNIME_MOUNTPOINT:
                    final var mountID = settings.getKnimeMountpointFileSystem();
                    return DefaultFSConnectionFactory.createMountpointConnection(mountID);
                case KNIME_FS:
                    final RelativeTo type = RelativeTo.fromSettingsValue(settings.getKNIMEFileSystem());
                    return DefaultFSConnectionFactory.createRelativeToConnection(type);
                default:
                    throw new IllegalArgumentException("Unsupported file system choice: " + choice.getType());
            }
        }
    
    private static FSLocation toCustomUrlFSLocation(final String url, final int timeoutInMillis) {
        return new FSLocation(FSCategory.CUSTOM_URL, Integer.toString(timeoutInMillis), url);
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
		
		m_sheetNameIn.saveSettingsTo(settings);
		m_sheetNameOut.saveSettingsTo(settings);
		m_sheetOrIndex.saveSettingsTo(settings);
		m_sheetIndex.saveSettingsTo(settings);
		m_pwd.saveSettingsTo(settings);
		m_outPwd.saveSettingsTo(settings);
		m_templatefilePath2.saveSettingsTo(settings);
		m_outputfilePath2.saveSettingsTo(settings);
		m_enablePassOption.saveSettingsTo(settings);
		m_forceFormulaUpdateOption.saveSettingsTo(settings);
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

		

		m_sheetNameIn.loadSettingsFrom(settings);
		m_sheetNameOut.loadSettingsFrom(settings);
		m_sheetOrIndex.loadSettingsFrom(settings);
		m_sheetIndex.loadSettingsFrom(settings);
		m_pwd.loadSettingsFrom(settings);
		m_outPwd.loadSettingsFrom(settings);
		m_templatefilePath2.loadSettingsFrom(settings);
		m_outputfilePath2.loadSettingsFrom(settings);
		m_enablePassOption.loadSettingsFrom(settings);
		m_forceFormulaUpdateOption.loadSettingsFrom(settings);
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

		m_sheetNameIn.validateSettings(settings);
		m_sheetNameOut.validateSettings(settings);
		m_sheetOrIndex.validateSettings(settings);
		m_sheetIndex.validateSettings(settings);
		m_pwd.validateSettings(settings);
		m_outPwd.validateSettings(settings);
		m_templatefilePath2.validateSettings(settings);
		m_outputfilePath2.validateSettings(settings);
		m_enablePassOption.validateSettings(settings);
		m_forceFormulaUpdateOption.validateSettings(settings);

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