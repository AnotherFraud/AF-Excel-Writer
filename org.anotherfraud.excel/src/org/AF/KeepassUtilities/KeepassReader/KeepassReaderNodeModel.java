package org.AF.KeepassUtilities.KeepassReader;
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
 * this is an implementation of the openkeepass library of cternes
 * see  https://github.com/cternes/openkeepass
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.Node;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.CredentialsStore;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.CredentialsStore.CredentialsNode;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.ICredentials;
import org.knime.core.node.workflow.VariableType;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.node.workflow.WorkflowManager;


import com.google.common.io.*;
import java.security.Security;
import org.linguafranca.pwdb.*;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.jaxb.JaxbDatabase;


import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.knime.core.node.workflow.VariableType;

/**
 * This is an example implementation of the node model of the
 * "KeepassReader" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author AnotherFraudUser
 */
@SuppressWarnings("unused")
public class KeepassReaderNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(KeepassReaderNodeModel.class);

	private Optional<FSConnection> m_fs = Optional.empty();
    static final String inputfilePath2 = "templateFile2";
    static final String sheetName = "sheet";
    static final String password = "pwd";
    static final String entryPassword = "entryPwd";

	private static final int defaulttimeoutInSeconds = 5;
    
    



    

	public static SettingsModelString createKeepassEntryNamesModel() {
		SettingsModelString sn = new SettingsModelString(sheetName, "");
        return sn;
	}

	public static SettingsModelFileChooser2 createInputFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(inputfilePath2, new String[] { ".kdbx" });
	}

	public static SettingsModelAuthentication createPassSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(password, AuthenticationType.PWD);
		return cps;
	
	}

	public static SettingsModelAuthentication createEntryPassSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(entryPassword, AuthenticationType.USER_PWD);
		return cps;
	
	}
	

	private final SettingsModelString m_keypassEntryName = createKeepassEntryNamesModel();
	private final SettingsModelFileChooser2 m_inputfilePath2 = createInputFilePath2SettingsModel();
	
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();
	private final SettingsModelAuthentication m_entryPwd = createEntryPassSettingsModel();
	
	protected KeepassReaderNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[] {FlowVariablePortObject.TYPE_OPTIONAL}, new PortType[] {FlowVariablePortObject.TYPE});
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec)
			throws Exception {


		
		LOGGER.info("This is an example info.");

		
		
		try
		{
			


			String title = m_keypassEntryName.getStringValue();
			FileChooserHelper fileHelperTemplate = new FileChooserHelper(m_fs, m_inputfilePath2, defaulttimeoutInSeconds * 1000);
			Path pathTemplate = fileHelperTemplate.getPathFromSettings();

			String inputfilePath = pathTemplate.toAbsolutePath().toString();
				
			
		    KdbxCreds creds = new KdbxCreds(m_pwd.getPassword(getCredentialsProvider()).getBytes());
		    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(inputfilePath);
		    
		    
		    Database database = JaxbDatabase.load(creds, inputStream);
		    
		    		
		    Entry entry = (Entry) database.findEntries(title).get(0);
		    
		  
			
			String password = entry.getPassword();
			String userName = entry.getUsername();

			
			//create credentials variable
			FlowVariable flowVar =   CredentialsStore.newCredentialsFlowVariable("PWD", userName, password, false, false);
			Node.invokePushFlowVariable(this, flowVar);
			
			pushFlowVariableString("property", "value");
			
			//List<Property> properties = passEntry.getCustomProperties();
			//for (Property property : properties) {
			//	pushFlowVariableString(property.getKey(),property.getValue());
			//	}
		

		}
		 
		
		
		
		 catch (Exception e) {
			 throw new InvalidSettingsException(
						"Reason: "  + e.getMessage(), e);
			 }
		
		
		
		
		
		return new FlowVariablePortObject[]{FlowVariablePortObject.INSTANCE};
	}
	
	
	
	
        
public static List<String> tryLoadKeePassEntryTitles(String filePath, String pass) {

		
		File checkFile = new File(filePath);
	
		

		
		if (checkFile.exists() && checkFile.canRead())
		{
			

			try (FileInputStream fileStream = new FileInputStream(checkFile))
			{
				
				List<String> entryNames = new ArrayList<String>();
				entryNames.add("Test");
				
				
				return entryNames;
				
				 //Database database = JaxbDatabase.load(creds, inputStream);
				 //Entry entry = (Entry) database.findEntries(title).get(0);
				    
				    
				//KeePassFile database = KeePassDatabase.getInstance(checkFile).openDatabase(pass);
				
				//List<String> entryNames = new ArrayList<String>();
				//List<Entry> entries = database.getEntries();
				//for (Entry entry : entries) {
				//	entryNames.add(entry.getTitle());
				//}
			
				

				
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
		
		

		
		return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
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
		
		m_keypassEntryName.saveSettingsTo(settings);
		m_inputfilePath2.saveSettingsTo(settings);
		m_pwd.saveSettingsTo(settings);
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
		m_keypassEntryName.loadSettingsFrom(settings);
		m_inputfilePath2.loadSettingsFrom(settings);
		m_pwd.loadSettingsFrom(settings);
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
		m_keypassEntryName.validateSettings(settings);
		m_inputfilePath2.validateSettings(settings);
		m_pwd.validateSettings(settings);
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

