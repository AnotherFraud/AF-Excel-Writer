package org.AF.PGPUtilities.PGPDecryptor;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.defaultnodesettings.FileChooserHelper;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;


/**
 * This is an example implementation of the node model of the
 * "PGPDecrypt" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author 
 */
public class PGPDecryptNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(PGPDecryptNodeModel.class);



	private Optional<FSConnection> m_fs = Optional.empty();
	private static final int defaulttimeoutInSeconds = 5;
	
	static final String inputfilePath2 = "inputFile2";
	static final String outputfilePath2 = "outputFile2";
	static final String keyfilePath2 = "keyFile2";
    static final String password = "pwd";	
    static final String useKeywordPassword = "useKeywordPassword";
	
    
	static SettingsModelBoolean createUseKeyfilePasswordSettingsModel() {
		SettingsModelBoolean wlr = new SettingsModelBoolean(useKeywordPassword, true);
		return wlr;				
	}	
	
	
	static SettingsModelFileChooser2 createInputFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(inputfilePath2);
	}
	
	
	static SettingsModelFileChooser2 createOutFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(outputfilePath2);
	}

	static SettingsModelFileChooser2 createKeeFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(keyfilePath2, new String[] { ".pgp" });
	}

	static SettingsModelAuthentication createPassSettingsModel() {
		SettingsModelAuthentication cps = new SettingsModelAuthentication(password, AuthenticationType.PWD);
		cps.setEnabled(true);
		return cps;
	}
	
	

	private final SettingsModelFileChooser2 m_inputfilePath2 = createInputFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_ouputfilePath2 = createOutFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_keyfilePath2 = createKeeFilePath2SettingsModel();
	private final SettingsModelAuthentication m_pwd = createPassSettingsModel();	
	private final SettingsModelBoolean m_useKeyfilePassword = createUseKeyfilePasswordSettingsModel();
	
	

	/**
	 * Constructor for the node model.
	 */
	protected PGPDecryptNodeModel() {
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
			

		FileChooserHelper inputfileHelperTemplate = new FileChooserHelper(m_fs, m_inputfilePath2, defaulttimeoutInSeconds * 1000);
		Path inputpathTemplate = inputfileHelperTemplate.getPathFromSettings();
		String inputfilePath = inputpathTemplate.toAbsolutePath().toString();
			
	
		FileChooserHelper outfileHelperTemplate = new FileChooserHelper(m_fs, m_ouputfilePath2, defaulttimeoutInSeconds * 1000);
		Path outpathTemplate = outfileHelperTemplate.getPathFromSettings();
		String outfilePath = outpathTemplate.toAbsolutePath().toString();
		
		FileChooserHelper keyfileHelperTemplate = new FileChooserHelper(m_fs, m_keyfilePath2, defaulttimeoutInSeconds * 1000);
		Path keypathTemplate = keyfileHelperTemplate.getPathFromSettings();
		String keyfilePath = keypathTemplate.toAbsolutePath().toString();
		
		pushFlowVariableString("inputfilePath", inputfilePath);
		pushFlowVariableString("outfilePath", outfilePath);
		pushFlowVariableString("keyfilePath", keyfilePath);
		
		decryptPGP(inputfilePath, outfilePath, keyfilePath);			
		
		
		
		}
		 catch (Exception e) {
			 throw new InvalidSettingsException(
						"Reason: "  + e.getMessage(), e);
			 }
		
		
		
		
		
		return new FlowVariablePortObject[]{FlowVariablePortObject.INSTANCE};
	}


	private void decryptPGP(String inputfilePath, String outfilePath, String keyfilePath)
			throws InvalidSettingsException {
		try
			(
			InputStream in = PGPUtil.getDecoderStream(new BufferedInputStream(new FileInputStream(inputfilePath)));
		     InputStream keyIn = new BufferedInputStream(new FileInputStream(keyfilePath));
			)
			{
			
			String password = m_useKeyfilePassword.getBooleanValue() ? m_pwd.getPassword() : "";
			
			String outFile = outfilePath;

            

			BouncyCastleProvider bouncy = new BouncyCastleProvider();
			 



			JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
		            PGPEncryptedDataList    enc;

		            Object                  o = pgpF.nextObject();
		            //
		            // the first object might be a PGP marker packet.
		            //
		            if (o instanceof PGPEncryptedDataList)
		            {
		                enc = (PGPEncryptedDataList)o;
		            }
		            else
		            {
		                enc = (PGPEncryptedDataList)pgpF.nextObject();
		            }

		            
		            
		            //
		            // find the secret key
		            //
		            Iterator<?>                    it = enc.getEncryptedDataObjects();
		            PGPPrivateKey               sKey = null;
		            PGPPublicKeyEncryptedData   pbe = null;
		            PGPSecretKeyRingCollection  pgpSec = new PGPSecretKeyRingCollection(
		                PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

		            while (sKey == null && it.hasNext())
		            {
		                pbe = (PGPPublicKeyEncryptedData)it.next();
		                PGPSecretKey pgpSecKey = pgpSec.getSecretKey(pbe.getKeyID());

		       		 if (pgpSecKey == null)
		       			{
		        			 sKey = null;
		        			}
		        		else
		        			{
		        			//sKey = pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));
						sKey = pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider(bouncy).build(password.toCharArray()));
		        			
		        			}               
		            }
				  
    
		            if (sKey == null)
		            {
		                throw new IllegalArgumentException("secret key for message not found.");
		            }
		    
		            InputStream       clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider(bouncy).build(sKey));
		            
		            JcaPGPObjectFactory    plainFact = new JcaPGPObjectFactory(clear);
		            
		            Object              message = plainFact.nextObject();
		    
		            if (message instanceof PGPCompressedData)
		            {
		                PGPCompressedData   cData = (PGPCompressedData)message;
		                JcaPGPObjectFactory    pgpFact = new JcaPGPObjectFactory(cData.getDataStream());
		                
		                message = pgpFact.nextObject();
		            }
		            
		            
		            
		            if (message instanceof PGPLiteralData)
		            {
		                PGPLiteralData ld = (PGPLiteralData)message;

		                InputStream unc = ld.getInputStream();


	
		                OutputStream fOut = new BufferedOutputStream(new FileOutputStream(outFile));
					int bytesRead = 0;
					    byte[] buffer = new byte[4096];
					    while ((bytesRead = unc.read(buffer)) != -1) {
					        fOut.write(buffer, 0, bytesRead);
					    }

					    
			           //Streams.pipeAll(unc, fOut);
		 
		                fOut.close();
		                unc.close();
		                
		            }
		            else if (message instanceof PGPOnePassSignatureList)
		            {
		            	LOGGER.info("encrypted message contains a signed message - not literal data.");
		                throw new PGPException("encrypted message contains a signed message - not literal data.");
		            }
		            else
		            {
		            	LOGGER.info("wrong file type - not a simple encrypted file - type unknown.");
		                throw new PGPException("wrong file type - not a simple encrypted file - type unknown.");
		            }
	

		            if (pbe.isIntegrityProtected())
		            {
		                if (!pbe.verify())
		                {
		                	LOGGER.info("message failed integrity check");
		                    System.err.println("message failed integrity check");
		                }
		                else
		                {
		                	LOGGER.info("message integrity check passed");
		                    System.err.println("message integrity check passed");
		                }
		            }
		            else
		            {
		                System.err.println("no message integrity check");
		            }
		 
			
			}
			catch(Exception e) {
				 throw new InvalidSettingsException(
							"Reason: "  + e.getMessage(), e);
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
		
		m_inputfilePath2.saveSettingsTo(settings);
		m_ouputfilePath2.saveSettingsTo(settings);
		m_keyfilePath2.saveSettingsTo(settings);
		m_pwd.saveSettingsTo(settings);
		m_useKeyfilePassword.saveSettingsTo(settings);

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
		
		m_inputfilePath2.loadSettingsFrom(settings);
		m_ouputfilePath2.loadSettingsFrom(settings);
		m_keyfilePath2.loadSettingsFrom(settings);
		m_pwd.loadSettingsFrom(settings);
		m_useKeyfilePassword.loadSettingsFrom(settings);
		
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
		m_inputfilePath2.validateSettings(settings);
		m_ouputfilePath2.validateSettings(settings);
		m_keyfilePath2.validateSettings(settings);
		m_pwd.validateSettings(settings);
		m_useKeyfilePassword.validateSettings(settings);
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

