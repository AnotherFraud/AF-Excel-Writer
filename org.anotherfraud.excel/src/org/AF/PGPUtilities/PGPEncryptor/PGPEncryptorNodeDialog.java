package org.AF.PGPUtilities.PGPEncryptor;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

/**
 * This is an example implementation of the node dialog of the
 * "PGPEncryptor" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class PGPEncryptorNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected PGPEncryptorNodeDialog() {
        super();
        
        final SettingsModelFileChooser2 inputFilePathModel2 = PGPEncryptorNodeModel.createInputFilePath2SettingsModel();
        final SettingsModelFileChooser2 outputFilePathModel2 = PGPEncryptorNodeModel.createOutFilePath2SettingsModel();
        final SettingsModelFileChooser2 keyFilePathModel2 = PGPEncryptorNodeModel.createKeeFilePath2SettingsModel();
        
        
    
        
        createNewGroup("PGP File Selection");
        

        final FlowVariableModel fvm = createFlowVariableModel(
            new String[]{inputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
            Type.STRING);

        addDialogComponent(new DialogComponentFileChooser2(0, inputFilePathModel2, "InputFile", JFileChooser.OPEN_DIALOG,
            JFileChooser.FILES_ONLY, fvm));
        
        
        closeCurrentGroup();
        createNewGroup("Output Folder Selection");
        
        final FlowVariableModel fvmOut = createFlowVariableModel(
                new String[]{outputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            addDialogComponent(new DialogComponentFileChooser2(0, outputFilePathModel2, "OuputFile", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, fvmOut));        
        
        closeCurrentGroup();
        
        createNewGroup("Keyfile Selection");
        
        final FlowVariableModel fvmKee = createFlowVariableModel(
                new String[]{keyFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            addDialogComponent(new DialogComponentFileChooser2(0, keyFilePathModel2, "Public Key File", JFileChooser.OPEN_DIALOG,
                JFileChooser.FILES_ONLY, fvmKee));     
            

        closeCurrentGroup();
    }
}

