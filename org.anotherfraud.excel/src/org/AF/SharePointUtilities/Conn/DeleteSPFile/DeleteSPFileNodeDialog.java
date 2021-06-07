package org.AF.SharePointUtilities.Conn.DeleteSPFile;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "DeleteSPFile" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class DeleteSPFileNodeDialog extends DefaultNodeSettingsPane {
	
    protected DeleteSPFileNodeDialog() {
        super();
        
        
        final SettingsModelString spFolderPathModel = DeleteSPFileNodeModel.createSpFolderPathSettingsModel();
        final SettingsModelString sharePointNameModel = DeleteSPFileNodeModel.createSharePointNameSettingsModel();
        final SettingsModelBoolean recycleBinModel = DeleteSPFileNodeModel.createMoveToRecycleBinSettingsModel();
    	

       	
       	
       	createNewGroup("General Information"); 
       	
       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathModel, "Delete File:  (complete path)", true, 60));


        addDialogComponent(new DialogComponentBoolean(recycleBinModel, "Move file to recycle bin?"));
       	       
        closeCurrentGroup();

   
    }
}

