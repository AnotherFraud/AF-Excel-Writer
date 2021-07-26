package org.AF.SharePointUtilities.Conn.CreateSPFolder;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "CreateSPFolder" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class CreateSPFolderNodeDialog extends DefaultNodeSettingsPane {
	
    protected CreateSPFolderNodeDialog() {
        super();
        
        final SettingsModelString spFolderPathModel = CreateSPFolderNodeModel.createSpFolderPathSettingsModel();
        final SettingsModelString sharePointNameModel = CreateSPFolderNodeModel.createSharePointNameSettingsModel();
 
        
       	
       	
       	createNewGroup("General Information"); 
       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathModel, "Create Folder:  (complete path)", true, 60));

  
        closeCurrentGroup();

    }
}

