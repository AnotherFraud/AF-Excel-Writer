package org.AF.SharePointUtilities.Conn.MoveSPFile;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "MoveSPFile" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class MoveSPFileNodeDialog extends DefaultNodeSettingsPane {

    protected MoveSPFileNodeDialog() {
        super();
        
        final SettingsModelString spFolderPathFromModel = MoveSPFileNodeModel.createSpFolderPathFromSettingsModel();
        final SettingsModelString spFolderPathToModel = MoveSPFileNodeModel.createSpFolderPathToSettingsModel();
        
        final SettingsModelString sharePointNameModel = MoveSPFileNodeModel.createSharePointNameSettingsModel();
        final SettingsModelBoolean copyFile = MoveSPFileNodeModel.createCopyFileSettingsModel();
    	
 
       	createNewGroup("General Information"); 

       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathFromModel, "File From:  (complete path)", true, 60));
       	addDialogComponent(new DialogComponentString(spFolderPathToModel, "File To:  (complete path)", true, 60));

        addDialogComponent(new DialogComponentBoolean(copyFile, "Copy file instead of move?"));
             	
     
        closeCurrentGroup();

    }
}

