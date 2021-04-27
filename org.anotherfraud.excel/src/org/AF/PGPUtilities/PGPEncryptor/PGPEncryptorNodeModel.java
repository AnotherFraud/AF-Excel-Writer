package org.AF.PGPUtilities.PGPEncryptor;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Optional;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
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
 * "PGPEncryptor" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author Another Fraud
 */
public class PGPEncryptorNodeModel extends NodeModel {
    
    /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(PGPEncryptorNodeModel.class);

	private Optional<FSConnection> m_fs = Optional.empty();
	private static final int defaulttimeoutInSeconds = 5;
	
	static final String inputfilePath2 = "inputFile2";
	static final String outputfilePath2 = "outputFile2";
	static final String keyfilePath2 = "keyFile2";
	
	
	
	static SettingsModelFileChooser2 createInputFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(inputfilePath2);
	}
	
	
	static SettingsModelFileChooser2 createOutFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(outputfilePath2);
	}

	static SettingsModelFileChooser2 createKeeFilePath2SettingsModel() {
		return new SettingsModelFileChooser2(keyfilePath2, new String[] { ".pub" });
	}


	

	private final SettingsModelFileChooser2 m_inputfilePath2 = createInputFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_ouputfilePath2 = createOutFilePath2SettingsModel();
	private final SettingsModelFileChooser2 m_keyfilePath2 = createKeeFilePath2SettingsModel();


	/**
	 * Constructor for the node model.
	 */
	protected PGPEncryptorNodeModel() {
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


		
		LOGGER.info("Start encrypting file");

		
		
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
		

	    
	    
       //Other settings
       boolean armor = true;
       boolean integrityCheck = true;
       String message;
       
       


       OutputStream out = new BufferedOutputStream(new FileOutputStream(outfilePath));
       PGPPublicKey encKey = readPublicKey(keyfilePath);
       message = encryptFile(out, inputfilePath, encKey, armor, integrityCheck);
       pushFlowVariableString("encryptionMessage", message);
       out.close();


		}
		 catch (Exception e) {
			 throw new InvalidSettingsException(
						"Reason: "  + e.getMessage(), e);
			 }
		
		
		
		
		
		return new FlowVariablePortObject[]{FlowVariablePortObject.INSTANCE};
	}

	
	
	
	
	static PGPPublicKey readPublicKey(String fileName) throws IOException, PGPException
    {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
        PGPPublicKey pubKey = readPublicKey(keyIn);
        keyIn.close();
        return pubKey;
    }

    /**
     * A simple routine that opens a key ring file and loads the first available key
     * suitable for encryption.
     * 
     * @param input data stream containing the public key data
     * @return the first public key found.
     * @throws IOException
     * @throws PGPException
     */
    static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException
    {
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
            PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //

        Iterator keyRingIter = pgpPub.getKeyRings();
        while (keyRingIter.hasNext())
        {
            PGPPublicKeyRing keyRing = (PGPPublicKeyRing)keyRingIter.next();

            Iterator keyIter = keyRing.getPublicKeys();
            while (keyIter.hasNext())
            {
                PGPPublicKey key = (PGPPublicKey)keyIter.next();

                if (key.isEncryptionKey())
                {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }

    static byte[] compressFile(String fileName, int algorithm) throws IOException
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
        PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY,
            new File(fileName));
        comData.close();
        return bOut.toByteArray();
    }
    


private static String encryptFile(
        OutputStream    out,
        String          fileName,
        PGPPublicKey    encKey,
        boolean         armor,
        boolean         withIntegrityCheck)
        throws IOException, NoSuchProviderException
    {
        if (armor)
        {
            out = new ArmoredOutputStream(out);
        }

        try
        {
        	BouncyCastleProvider bouncy = new BouncyCastleProvider();
            byte[] bytes = compressFile(fileName, CompressionAlgorithmTags.ZIP);

            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom()).setProvider(bouncy));

            encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider(bouncy));

            OutputStream cOut = encGen.open(out, bytes.length);

            cOut.write(bytes);
            cOut.close();

            if (armor)
            {
                out.close();
            }
            return "file encrypted";
            
        }
        catch (PGPException e)
        {
            System.err.println(e);
            if (e.getUnderlyingException() != null)
            {
                e.getUnderlyingException().printStackTrace();
            }
            return e.getMessage();
            
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

