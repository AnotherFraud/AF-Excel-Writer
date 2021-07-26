package org.AF.SharePointUtilities.Conn.DownloadFileFromSP;

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
 * "DownloadFileFromSP" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author 
 */
public class DownloadFileFromSPNodeDialog extends DefaultNodeSettingsPane {

    protected DownloadFileFromSPNodeDialog() {
        super();
        
        final SettingsModelFileChooser2 downloadFilePathModel2 = DownloadFileFromSPNodeModel.createDownloadFilePath2SettingsModel();
        final SettingsModelString spFolderPathModel = DownloadFileFromSPNodeModel.createSpFolderPathSettingsModel();
        final SettingsModelString sharePointNameModel = DownloadFileFromSPNodeModel.createSharePointNameSettingsModel();
 
       	
       	
       	createNewGroup("General Information"); 
       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathModel, "Download File SP Path (complete path)", true, 60));



        final FlowVariableModel fvm = createFlowVariableModel(
                new String[]{downloadFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            
            addDialogComponent(new DialogComponentFileChooser2(0, downloadFilePathModel2, "Save File to", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, fvm));
            
        
        closeCurrentGroup();
           
    }
 
}

