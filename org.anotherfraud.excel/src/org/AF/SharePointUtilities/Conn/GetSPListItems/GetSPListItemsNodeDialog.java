package org.AF.SharePointUtilities.Conn.GetSPListItems;

import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "GetSPListItems" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class GetSPListItemsNodeDialog extends DefaultNodeSettingsPane {

    protected GetSPListItemsNodeDialog() {
        super();
        
        final SettingsModelString spListNameModel = GetSPListItemsNodeModel.createSpListNameSettingsModel();
        final SettingsModelString sharePointNameModel = GetSPListItemsNodeModel.createSharePointNameSettingsModel();
        final SettingsModelString loadingOrder = GetSPListItemsNodeModel.createLoadingOrderSettingsModel();
        final SettingsModelIntegerBounded itemLimit = GetSPListItemsNodeModel.createItemLimitSettingsModel();
        final SettingsModelBoolean loadAll = GetSPListItemsNodeModel.createLoadAllSettingsModel();
        
  
    	//listener try to read in sheet names from given template file
        loadAll.addChangeListener(e -> {	
            if (loadAll.getBooleanValue()) {
            	itemLimit.setEnabled(false);
            } else {
            	itemLimit.setEnabled(true);
            }
            
        });
        
       	createNewGroup("General Information"); 

       	addDialogComponent(new DialogComponentString(sharePointNameModel, "SharePoint Site Name", true, 60));
       	addDialogComponent(new DialogComponentString(spListNameModel, "List Title", true, 60));
   
        closeCurrentGroup();
        createNewGroup("Loading Options"); 
        
        addDialogComponent(new DialogComponentBoolean(loadAll, "Load all items in list"));
        addDialogComponent(new DialogComponentNumber(itemLimit, "Item Limit", 5000));
        
        addDialogComponent(
                new DialogComponentStringSelection(loadingOrder, "Loading Order",
                		Arrays.asList( "Ascending Creation Date","Descending Creation Date"),false));
    
    }
}

