package org.AF.ExcelUtilities.ExcelPasswordManager;

/*
 * This program is free software: you can redistribute it and/or modify
 * Copyright [2021] [Another Fraud]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;




public class ExcelPasswordManagerNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ExcelPasswordManagerNodeModel.class);
	private Optional<FSConnection> m_fs = Optional.empty();
	private int defaulttimeoutInSeconds = 5;
    static final String templatefilePath2 = "templateFile2";
    static final String templatefilePath = "templateFile";
     static final String outputfilePath2 = "outputFile2";
    static final String outputfilePath = "outputFile";
    static final String copyOrWrite = "copywrite";
    static final String overrideOrFail = "overridefail";
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
		cps.setEnabled(true);
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
	
	
	
	
	
	
	static SettingsModelString createOverrideOrFailModelSettingsModel() {
		SettingsModelString coof = new SettingsModelString(overrideOrFail, "Fail");
		coof.setEnabled(false);
		return coof;				
	}	
	

	static SettingsModelString enablePasswordSettingsModel() {
		SettingsModelString epw = new SettingsModelString(enablePassOption, "Add PWD");
		return epw;				
	}	    
    

	
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();
	private final SettingsModelAuthentication m_outPwd = createOutPassSettingsModel();
		private final SettingsModelFileChooser2 m_templatefilePath2 = createTemplateFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_outputfilePath2 = createOutputFilePath2SettingsModel();

	private final SettingsModelString m_copyOrWrite = createCopyOrWriteSettingsModel();
    private final SettingsModelString m_enablePassOption = enablePasswordSettingsModel();   
    private final SettingsModelString m_overrideOrFail = createOverrideOrFailModelSettingsModel();


    
    
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
	 * in the constructor of the {@link ExcelPasswordManagerNodeDialog} as the settings 
	 * models are also used to create simple dialogs.
	 */


	/**
	 * Constructor for the node model.
	 */
	protected ExcelPasswordManagerNodeModel() {
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
		LOGGER.info("Start execution of Excel password manager");



	
	try 
	{
			
		
		FileChooserHelper fileHelperTemplate = new FileChooserHelper(m_fs, m_templatefilePath2, defaulttimeoutInSeconds * 1000);
		Path templatePath = fileHelperTemplate.getPathFromSettings();
		
		
		
		
		
		String templatefilePath = templatePath.toAbsolutePath().toString();
		Path outputPath;
		String outputPathString;

		
		Workbook workbook;
		
		try(
				InputStream in = Files.newInputStream(templatePath);
		)
		{
			workbook = openWorkBook(in);
		};
		
	
	
		if (m_copyOrWrite.getStringValue().equals("CopyFrom"))
		{	
			
			FileChooserHelper fileHelperOutput;
			fileHelperOutput = new FileChooserHelper(m_fs, m_outputfilePath2, defaulttimeoutInSeconds * 1000);
			outputPath = fileHelperOutput.getPathFromSettings();
			outputPathString = outputPath.toAbsolutePath().toString();
			
		}
		else
		{
			outputPath = templatePath;
			outputPathString = templatefilePath;
		}
		
		pushFlowVariableString("templatefilePath", templatefilePath);
		pushFlowVariableString("outputFilePath", outputPathString);
		
		
		
		//fail if file exists and fail on exists was selected
		if (isFileReachable(outputPathString) != null 
				&& m_overrideOrFail.getStringValue().equals("Fail") 
				&& m_copyOrWrite.getStringValue().equals("CopyFrom") 
		)
		{
			throw new IOException("Output file exists and fail overwrite option was selected");
		}
		
			

	
		
		
		
		try
		{
			

			
			try(
					OutputStream out = Files.newOutputStream(outputPath);
			)
			{
				
			writeXlsWithPassword(workbook, out);	
			out.close();
			
			} catch (Exception c) {
				 throw new InvalidSettingsException(
							"Error while saving output file " + c.getMessage(), c);

			} 
			
			return new PortObject[0];
			
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
		private void writeXlsWithPassword(Workbook workbook, OutputStream out) throws Exception {
			
			String outputPass = "";
			
			
			if (m_enablePassOption.getStringValue().equals("Add PWD")) {
				outputPass = m_pwd.getPassword(getCredentialsProvider());
            } else if (m_enablePassOption.getStringValue().equals("Remove PWD")) {
            	outputPass = "";
            }else if (m_enablePassOption.getStringValue().equals("Change PWD")) {
            	outputPass = m_outPwd.getPassword(getCredentialsProvider());
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


			workbook = WorkbookFactory.create(file,m_pwd.getPassword(getCredentialsProvider()));
			file.close();
			
	
		return workbook;
	}






	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
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
		

		m_copyOrWrite.saveSettingsTo(settings);

	
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

		


		m_copyOrWrite.loadSettingsFrom(settings);


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

		m_copyOrWrite.validateSettings(settings);
		m_overrideOrFail.validateSettings(settings);
		m_pwd.validateSettings(settings);
		m_outPwd.validateSettings(settings);
		m_templatefilePath2.validateSettings(settings);
		m_outputfilePath2.validateSettings(settings);
		m_enablePassOption.validateSettings(settings);


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
