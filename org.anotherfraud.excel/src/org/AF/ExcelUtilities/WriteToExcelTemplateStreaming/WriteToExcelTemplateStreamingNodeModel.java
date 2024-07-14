package org.AF.ExcelUtilities.WriteToExcelTemplateStreaming;





import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.filehandling.core.connections.FSFiles;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.ReadPathAccessor;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.FileOverwritePolicy;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.WritePathAccessor;
import org.knime.filehandling.core.defaultnodesettings.status.NodeModelStatusConsumer;
import org.knime.filehandling.core.defaultnodesettings.status.StatusMessage.MessageType;



public class WriteToExcelTemplateStreamingNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(WriteToExcelTemplateStreamingNodeModel.class);
	
	


	
	
	
    static final String colOffset = "colOff";
    static final String rowOffset = "rowOff";
    static final String outputfilePath = "outputFile";
    static final String copyOrWrite = "copywrite";
    static final String sheetName = "sheet";
    static final String sheetIndex = "sheetIndex";
    static final String sheetNameOrIndex = "sheetOrIndex";
    static final String writeHeaderOption = "writeHeader";
    static final String writeFormulaOption = "writeFormula";
    static final String froceFormulaUpdate = "forceFormula";
    
    static final String clearDataFromSheet = "clearData";
    static final String writeLastRowOption = "lastRowOption";
    static final String password = "pwd";
    static final String outPassword = "outpwd";
    static final String enablePassOption = "enablePwd";
    
	static final String templatefilePathOld = "templateFile2";
	static final String outputfilePathOld = "outputFile2";
    static final String overrideOrFailOld = "overridefail";

    
    
    
    
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
	
	
	static SettingsModelBoolean createClearDataSettingsModel() {
		SettingsModelBoolean wlr = new SettingsModelBoolean(clearDataFromSheet, false);
		return wlr;				
	}		
	
	

	static SettingsModelString enablePasswordSettingsModel() {
		SettingsModelString epw = new SettingsModelString(enablePassOption, "No PWD needed");
		return epw;				
	}	    
    

    static SettingsModelFileChooser2 createTemplatePathOldSettingsModel() {
		return new SettingsModelFileChooser2(templatefilePathOld, new String[] { ".xlsx", ".xls", ".xlsm" });
	}
    


	static SettingsModelFileChooser2 createOldOutputFilePathSettingsModel() {
		return new SettingsModelFileChooser2(outputfilePathOld, new String[] { ".xlsx", ".xls", ".xlsm" });
	}
	
	
	static SettingsModelString createOverrideOrFailOldModelSettingsModel() {
		return new SettingsModelString(overrideOrFailOld, "");			
	}	
	
	
	private final SettingsModelString m_overrideOrFailOld = createOverrideOrFailOldModelSettingsModel();
	
	
	
	
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();
	private final SettingsModelAuthentication m_outPwd = createOutPassSettingsModel();
	private final SettingsModelString m_sheetName = createSheetNamesModel();


	private final SettingsModelString m_copyOrWrite = createCopyOrWriteSettingsModel();
	private final SettingsModelString m_sheetOrIndex = createSheetNameOrIndexSettingsModel();
	private final SettingsModelIntegerBounded m_sheetIndex = createSheetIndexModel();
	
	private final SettingsModelIntegerBounded m_rowOffset = createRowOffsetSettingsModel();
    private final SettingsModelIntegerBounded m_colOffset = createColOffsetSettingsModel();
    private final SettingsModelBoolean m_writeLastRowOption = createWriteLastRowSettingsModel();   
    private final SettingsModelBoolean m_clearData = createClearDataSettingsModel();   
    
	private final SettingsModelFileChooser2 m_templatePathOld = createTemplatePathOldSettingsModel();
	private final SettingsModelFileChooser2 m_outputPathOld = createOldOutputFilePathSettingsModel();
	

    private final SettingsModelString m_enablePassOption = enablePasswordSettingsModel();   

    private final SettingsModelBoolean m_writeHeaderOption =
            new SettingsModelBoolean(writeHeaderOption, false);
    
    private final SettingsModelBoolean m_writeFormulaOption =
            new SettingsModelBoolean(writeFormulaOption, false);
    
    private final SettingsModelBoolean m_forceFormulaUpdateOption =
            new SettingsModelBoolean(froceFormulaUpdate, false);
    
    private final WriteToExcelTemplateStreamingConfig m_cfg;
    private final NodeModelStatusConsumer m_statusConsumer;
    final Map<String, CellStyle> m_cellStyles;
    
    


	/**
	 * Constructor for the node model.
	 */
	protected WriteToExcelTemplateStreamingNodeModel(final PortsConfiguration portsConfig) {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */       
		super(portsConfig.getInputPorts(), portsConfig.getOutputPorts());
		
		m_cfg = new WriteToExcelTemplateStreamingConfig(portsConfig);
		
		
		
		m_statusConsumer = new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING));
		m_cellStyles = new HashMap<String, CellStyle>();
		
	
	}
	
	

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec)
			throws Exception {
		LOGGER.info("Start execution of excel file to template");


		
		m_cellStyles.clear();
		SettingsModelReaderFileChooser m_templatefilePath = m_cfg.getSrcFileChooserModel();
		SettingsModelWriterFileChooser m_outputfilePath = m_cfg.getDestFileChooserModel();


		System.out.println(m_templatefilePath.getLocation().getPath());
		System.out.println(m_outputfilePath.getLocation().getPath());
		
		
		//System.out.println(m_templatefilePath.getLocation().getPath());
		//System.out.println(m_templatefilePath.getFileSystemName());
		

		//System.out.println(m_outputfilePath.getLocation().getPath());
		//System.out.println(m_outputfilePath.getFileSystemName());
		

		BufferedDataTable inputTable =  (BufferedDataTable)inObjects[0];
		
		/*
		 * A counter for how many rows have already been processed. This is used to
		 * calculate the progress of the node, which is displayed as a loading bar under
		 * the node icon.
		 */
		
		
		CloseableRowIterator rowIterator = inputTable.iterator();

	
	try 
	{
		
		NodeModelStatusConsumer statusMessage = new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR));

		
		
		ReadPathAccessor readAccessor = m_templatefilePath.createReadPathAccessor();		
		FSPath templatePath = readAccessor.getFSPaths(statusMessage).get(0);
		          

		FSPath outPath = templatePath ;
		
		String templatefilePath = m_templatefilePath.getPath();
		String outputPath;


		
		if (m_copyOrWrite.getStringValue().equals("CopyFrom"))
		{	
			WritePathAccessor writeAccessor = m_outputfilePath.createWritePathAccessor();
			
			outPath = writeAccessor.getOutputPath(statusMessage);
			outputPath = writeAccessor.getOutputPath(statusMessage).toFSLocation().getPath();
			
		}
		else
		{
			outputPath = templatefilePath;
		}
		
		
		
		

		
		
		pushFlowVariableString("templatefilePath", templatefilePath);
		pushFlowVariableString("outputFilePath", outputPath);
		

		
		int xlsxRowOffset;
		int xlsxColOffset =  m_colOffset.getIntValue() - 1;
		int currentRowCounter = 0;
		
		
		
		
		
		//get last row in Excel if write to last row option is selected
		if(m_writeLastRowOption.getBooleanValue())
		{

			xlsxRowOffset = 0;
		}
		else
		{
			xlsxRowOffset =	m_rowOffset.getIntValue() - 1;

		};
		
		
		
		String unZipLocation = "C:\\Temp\\ZipExcel";
		String sheetFolder = unZipLocation + File.separator + "xl" + File.separator + "worksheets" + File.separator;
		String sheetName = "sheet1.xml";
		String targetSheet = sheetFolder + sheetName;
		String tmpSheet = sheetFolder + "_tempp_" + sheetName;
			
		int searchRow 
		String searchColumn = "A1";
		String replacementValue = "new value";
		
		try {
            // Example usage
            InputStream fis = FSFiles.newInputStream(templatePath, StandardOpenOption.READ);
            unzip(fis, "C:\\Temp\\ZipExcel");
            
            

            
            
            if(doesSheetExists(sheetFolder, sheetName))
            {

            	
            	
            
	    		try
	    		{
	    			currentRowCounter = 0;
	    		boolean endOfData = false;
	    		
	            try {
	                XMLInputFactory factory = XMLInputFactory.newInstance();
	                XMLEventReader reader = factory.createXMLEventReader(new FileReader(targetSheet));
	                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	                XMLEventWriter writer = outputFactory.createXMLEventWriter(new FileWriter(tmpSheet));
	                XMLEventFactory creatorFactory = XMLEventFactory.newInstance();   		
	    		
	    		
	                XMLEvent event = null;
	                List<XMLEvent> backlogEvents = new ArrayList<XMLEvent>();
	                
	                
	                
	    			// Iterate over the rows of the input table.
	    			while (rowIterator.hasNext()) {
	    				
	    				
	    				
	    				searchRow = currentRowCounter + xlsxRowOffset;
	    				
	    				DataRow currentRow = rowIterator.next();
	    				int numberOfCells = currentRow.getNumCells();
	    				boolean rowMissing = false;
	    				

	    				
		                while (reader.hasNext()) {
		                    event = reader.nextEvent();  	
		                    
		                    
		                    if (event.isStartElement()) {
		                        StartElement startElement = event.asStartElement();
	
		                        if ("row".equals(startElement.getName().getLocalPart())) {
		                          	String rValue = startElement.getAttributeByName(QName.valueOf("r")).getValue();
		                          	
		                          	
		                          	if (searchRow == Integer.parseInt(rValue))
		                          	{
		                          		writer.add(event);
		                          		int nextColNumber;
		                          		
		                          		for (int i = 0; i < numberOfCells; i++) {
		                          			
		                          			nextColNumber = i + xlsxColOffset;
		                          			String nextColumn = convertNumberToColumnChar(nextColNumber) + String.valueOf(searchRow);
		                          			
		                          			
		                          			
		                                    while (reader.hasNext()) {
		                                        XMLEvent innerEvent = reader.nextEvent();
		                                        
		               
		                                        if (innerEvent.isStartElement()) {
		                                            StartElement innerStartElement = innerEvent.asStartElement();

		                                            if ("c".equals(innerStartElement.getName().getLocalPart())) {
		                                                String cRValue = innerStartElement.getAttributeByName(QName.valueOf("r")).getValue();

		                                                
		                                                
		                                                
		                                                //column found
		                                                if (nextColumn.equals(cRValue)) {
		                                                    writer.add(innerEvent); // Add <c> element

		                                                    innerEvent = reader.nextEvent();

		                                                    //set value
		                                                    if (innerEvent.isStartElement() && "v".equals(innerEvent.asStartElement().getName().getLocalPart())) {
		                                                        writer.add(innerEvent); // Add <v> element

		                                                        innerEvent = reader.nextEvent();

		                                                        if (innerEvent.isCharacters()) {
		                                                            Characters characters = innerEvent.asCharacters();
		                                                            String oldValue = characters.getData();
		                                                            StringCell stringCell = (StringCell) currentRow.getCell(nextColNumber);
		                                                                  
		                                                            String newValue = stringCell.getStringValue();

		                                                            		
		                                                            XMLEvent newCharacters = creatorFactory.createCharacters(newValue);
		                                                            writer.add(newCharacters);
		                                                        }

		                                                        innerEvent = reader.nextEvent();
		                                                        writer.add(innerEvent); // Add closing </v> tag
		                                                    }
		                                                    
		                                                    
		                                                //check if column is missing
		                                                } else if(convertColumnCharToNumber(cRValue) > nextColNumber)
		                                                {
		                                                	
		                                                	
		                                                	
		                                                	
		                                                	
		                                                	
		                                                	
		                                                }

		                                                else {
		                                                    writer.add(innerEvent); // If it's not c r="A1", copy the element
		                                                }
		                                            } else {
		                                                writer.add(innerEvent); // If it's not a <c> element, copy the element
		                                            }
		                                            
		                                        //closing row
		                                        } else if (innerEvent.isEndElement() && "row".equals(innerEvent.asEndElement().getName().getLocalPart())) {
		                                            event = innerEvent;
		                                            break;
		                                        } 
		                                        else {
		                                            writer.add(innerEvent); // If it's not a start element, copy the event
		                                        }
		                                        
		                                        

		                                    }
		                                    

		                				}
		                          		
		                          		
		                          		
		                          	} else if (searchRow < Integer.parseInt(rValue))
		                          	{
		                          		writer.add(event);
		                          	} 

		                        }
		                    } 
		                    
		                    
		                    else if (event.isEndElement()) {
		                    	
		                    	
		                    	EndElement endElement = event.asEndElement();
	
		                    	//no data so create initial structure 
		                        if ("sheetData".equals(endElement.getName().getLocalPart())) {
		                        	endOfData = true;
		                        	
		                        	//create new start for sheetData
		                            StartElement specDataStartElement = creatorFactory.createStartElement("", "", "sheetData");
		                            writer.add(specDataStartElement);
		                            
		                            //add closing for sheetdata and row into backlog
		                            backlogEvents.add(creatorFactory.createEndElement("", "", "row"));
		                            backlogEvents.add(event);
		                           
		                            event = createRow(creatorFactory, writer, 1);
		                        	break;
		                        } 	
		                    } else {
		                    //write data until row is found
		                    writer.add(event); 
		                    }
		                }
	                
	                
	                
	                
		                for (int i = 0; i < numberOfCells; i++) {
	  
	    					}
	    					
	    				
	
	    				
	    				exec.checkCanceled();
	    				
	    				currentRowCounter++;
	    				exec.setProgress(currentRowCounter / (double) inputTable.size(), "writing row " + currentRowCounter);				
    				

    			}
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	                
	            } catch (Exception c) {
	      			 throw new InvalidSettingsException(
	      						"Error while writing " + c.getMessage(), c);

	       		} 
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	
	    			// Iterate over the rows of the input table.
	    			while (rowIterator.hasNext()) {
	    				
	    				DataRow currentRow = rowIterator.next();
	    				int numberOfCells = currentRow.getNumCells();

	    				
	    				
	    				processXML(targetSheet, tmpSheet, currentRow, searchColumn, currentRow.getCell(i));
	    				
	    			}
	    			
	    		} catch (Exception c) {
	      			 throw new InvalidSettingsException(
	      						"Error while writing " + c.getMessage(), c);

	       		} 
	    		
	    			
    		}
    			
 
            
            
            
            
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
		


			return new PortObject[]{};
			
		} catch (Exception c) {
			 throw new InvalidSettingsException(
						"Error while writing " + c.getMessage(), c);

		} 
		

	} 
	
	
	
    public static String convertNumberToColumnChar(int number) {
        StringBuilder columnName = new StringBuilder();

        while (number > 0) {
            int remainder = (number - 1) % 26;
            char digit = (char) (65 + remainder); // 'A' corresponds to 65 in ASCII
            columnName.insert(0, digit);
            number = (number - 1) / 26;
        }

        return columnName.toString();
    }
    
 
    public static int convertColumnCharToNumber(String columnName) {
        int result = 0;
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);
            if (Character.isLetter(c)) {
                result = result * 26 + (c - 'A' + 1);
            }
        }
        return result;
    }
    
    
	
    public static XMLEvent createRow(XMLEventFactory creatorFactory, XMLEventWriter writer, int rowNumber ) throws Exception {	
    	
    	
    	

        
        
        // Create attributes for <row>
        Attribute rAttribute = creatorFactory.createAttribute("r", String.valueOf(rowNumber));
        Attribute spansAttribute = creatorFactory.createAttribute("spans", "1:52");
        Attribute dyDescentAttribute = creatorFactory.createAttribute("x14ac:dyDescent", "0.25");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(rAttribute);
        attributeList.add(spansAttribute);
        attributeList.add(dyDescentAttribute);
        
        // Create a StartElement for <row> with attributes
        List nsList = Arrays.asList();
        StartElement rowStartElement = creatorFactory.createStartElement("", "", "row",attributeList.iterator(), nsList.iterator());
        
        return rowStartElement;
	
	
    }
  

    
    
    
    
	
    public static void processXML(String inputFileName, String tempFileName, int searchRow, String searchColumn, String replacementValue) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(inputFileName));
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter writer = outputFactory.createXMLEventWriter(new FileWriter(tempFileName));
            XMLEventFactory creatorFactory = XMLEventFactory.newInstance();


            boolean startOfRows = false;
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                
                
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if ("row".equals(startElement.getName().getLocalPart())) {
                    	
                    	startOfRows = true;
                    	
                        String rValue = startElement.getAttributeByName(QName.valueOf("r")).getValue();
                        int r = Integer.parseInt(rValue);

                        if (r == searchRow) {
                            System.out.println("Row r is found");
                            
                            
                            writer.add(event);
                            
                            
                            while (reader.hasNext()) {
                                XMLEvent innerEvent = reader.nextEvent();
                                
       
                                if (innerEvent.isStartElement()) {
                                    StartElement innerStartElement = innerEvent.asStartElement();

                                    if ("c".equals(innerStartElement.getName().getLocalPart())) {
                                        String cRValue = innerStartElement.getAttributeByName(QName.valueOf("r")).getValue();

                                        //column found
                                        if (searchColumn.equals(cRValue)) {
                                            writer.add(innerEvent); // Add <c> element

                                            innerEvent = reader.nextEvent();

                                            //set value
                                            if (innerEvent.isStartElement() && "v".equals(innerEvent.asStartElement().getName().getLocalPart())) {
                                                writer.add(innerEvent); // Add <v> element

                                                innerEvent = reader.nextEvent();

                                                if (innerEvent.isCharacters()) {
                                                    Characters characters = innerEvent.asCharacters();
                                                    String oldValue = characters.getData();
                                                    String newValue = replacementValue; // Change the value
                                                    XMLEvent newCharacters = creatorFactory.createCharacters(newValue);
                                                    writer.add(newCharacters);
                                                }

                                                innerEvent = reader.nextEvent();
                                                writer.add(innerEvent); // Add closing </v> tag
                                            }
                                        } else {
                                            writer.add(innerEvent); // If it's not c r="A1", copy the element
                                        }
                                    } else {
                                        writer.add(innerEvent); // If it's not a <c> element, copy the element
                                    }
                                    
                                //closing row
                                } else if (innerEvent.isEndElement() && "row".equals(innerEvent.asEndElement().getName().getLocalPart())) {
                                    event = innerEvent;
                                    break;
                                } 
                                else {
                                    writer.add(innerEvent); // If it's not a start element, copy the event
                                }
                                
                                

                            }      
                        }
                        
                    }
                }
                       
                writer.add(event);
     
            }
                
                
                

            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	    
	    
	    
		
    public static void unzip(InputStream inputStream, String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];
        
        // Create output directory if it doesn't exist
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        } else
        {
        	FileUtils.deleteDirectory(new File(outputFolder));
        }
        

        
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry = zis.getNextEntry();
            
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                // Create parent directories if they don't exist
                new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                zipEntry = zis.getNextEntry();
            }
        }
    }
    
    
    public static boolean doesSheetExists(String folderPath, String fileName) {
        File file = new File(folderPath + File.separator + fileName);
        return file.exists();
    }
    
    




	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

		
	       m_cfg.getSrcFileChooserModel().configureInModel(inSpecs, m_statusConsumer);
	       
	       
	       if (m_cfg.getDestFileChooserModel().isEnabled())
	       {
	       m_cfg.getDestFileChooserModel().configureInModel(inSpecs, m_statusConsumer);
	       }

	       /*
	        try {
	            m_cfg.getDestFileChooserModel().configureInModel(inSpecs, m_statusConsumer);
	            m_statusConsumer.setWarningsIfRequired(this::setWarningMessage);
	        } catch (InvalidSettingsException e) {
	        	throw e;
	        }
	        */
	        
		return new PortObjectSpec[]{};
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
		

		
		
		m_templatePathOld.saveSettingsTo(settings);    	
		m_outputPathOld.saveSettingsTo(settings); 
		m_sheetName.saveSettingsTo(settings);
		m_copyOrWrite.saveSettingsTo(settings);
		m_colOffset.saveSettingsTo(settings);
		m_rowOffset.saveSettingsTo(settings);
		m_sheetOrIndex.saveSettingsTo(settings);
		m_sheetIndex.saveSettingsTo(settings);
		m_writeLastRowOption.saveSettingsTo(settings);
		m_writeHeaderOption.saveSettingsTo(settings);
		m_writeFormulaOption.saveSettingsTo(settings);
		m_pwd.saveSettingsTo(settings);
		m_outPwd.saveSettingsTo(settings);
		m_cfg.saveSettingsForModel(settings);
		m_clearData.saveSettingsTo(settings);
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

		m_copyOrWrite.loadSettingsFrom(settings);
		
    	try {
    		m_cfg.loadSettingsForModel(settings); 
    		if(m_copyOrWrite.getStringValue().equals("CopyFrom"))
    		{
    			m_cfg.getDestFileChooserModel().setEnabled(true);
    		}
    	} catch (InvalidSettingsException e) {}   	
    	
    	
    	
		//old setting do not have to be filled
    	loadOldSettings(settings);	
    	
    	
		
    	//new setting do not have to be filled
    	try {
    		m_clearData.loadSettingsFrom(settings);    	
    	} catch (InvalidSettingsException e) {}
    	
    	

		m_sheetName.loadSettingsFrom(settings);
		m_colOffset.loadSettingsFrom(settings);
		m_rowOffset.loadSettingsFrom(settings);
		
		
		
		m_sheetOrIndex.loadSettingsFrom(settings);
		m_sheetIndex.loadSettingsFrom(settings);
		m_writeLastRowOption.loadSettingsFrom(settings);
		m_writeHeaderOption.loadSettingsFrom(settings);
		m_writeFormulaOption.loadSettingsFrom(settings);
		m_pwd.loadSettingsFrom(settings);
		m_outPwd.loadSettingsFrom(settings);
		m_enablePassOption.loadSettingsFrom(settings);
		m_forceFormulaUpdateOption.loadSettingsFrom(settings);
	}


	//this is needed to map old path models to new without breaking existing workflows
	private void loadOldSettings(final NodeSettingsRO settings) {
		
		
		try {
    		m_templatePathOld.loadSettingsFrom(settings); 
    		if(!m_templatePathOld.getPathOrURL().isEmpty())
        	{
    			m_cfg.getSrcFileChooserModel().setPath(m_templatePathOld.getPathOrURL());
        	}
    	} catch (InvalidSettingsException e) {}
    	
		
    	try {	
    		m_outputPathOld.loadSettingsFrom(settings); 
    		if(!m_outputPathOld.getPathOrURL().isEmpty())
        	{
    			m_cfg.getDestFileChooserModel().setPath(m_outputPathOld.getPathOrURL());
        	}		
    	} catch (InvalidSettingsException e) {}	
		
    	
    	try {	
    		m_overrideOrFailOld.loadSettingsFrom(settings); 
    		
    		if (m_overrideOrFailOld.getStringValue().equals("Override"))
    		{
    			m_cfg.getDestFileChooserModel().setFileOverwritePolicy(FileOverwritePolicy.OVERWRITE);
    		}
    		else if (m_overrideOrFailOld.getStringValue().equals("Fail"))
    		{
    			m_cfg.getDestFileChooserModel().setFileOverwritePolicy(FileOverwritePolicy.FAIL);
    		}
    		
    	} catch (InvalidSettingsException e) {}
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
		m_pwd.validateSettings(settings);
		m_outPwd.validateSettings(settings);
		//m_cfg.validateSettingsForModel(settings);
		m_enablePassOption.validateSettings(settings);
		m_forceFormulaUpdateOption.validateSettings(settings);
		//m_clearData.validateSettings(settings);

	}
	
	protected void validateUserInput() throws InvalidSettingsException
	{
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

