package org.AF.SharePointUtilities.Conn.UploadFileToSP;


import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;



/**
 * This is an example implementation of the node dialog of the
 * "UploadFileToSP" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class UploadFileToSPNodeDialog extends DefaultNodeSettingsPane {




	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected UploadFileToSPNodeDialog() {
        super();
        
        final SettingsModelFileChooser2 uploadFilePathModel2 = UploadFileToSPNodeModel.createUploadFilePath2SettingsModel();
        final SettingsModelString spFolderPathModel = UploadFileToSPNodeModel.createSpFolderPathSettingsModel();
        final SettingsModelString sharePointNameModel = UploadFileToSPNodeModel.createSharePointNameSettingsModel();
 
        
              	
       	createNewGroup("General Information"); 
       	
       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathModel, "Upload into Sp Folder (complete path)", true, 60));


       	

        final FlowVariableModel fvm = createFlowVariableModel(
                new String[]{uploadFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            
            addDialogComponent(new DialogComponentFileChooser2(0, uploadFilePathModel2, "UploadFile", JFileChooser.OPEN_DIALOG,
                JFileChooser.FILES_ONLY, fvm));
            
        
        closeCurrentGroup();

    }

}

