package org.AF.SeleniumFire.GetWebElementData;

import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "GetWebElementData" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class GetWebElementDataNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected GetWebElementDataNodeDialog() {
        super();
        
       	final SettingsModelString locatorString = GetWebElementDataNodeModel.createlocatorStringSettingsModel();
       	final SettingsModelString searchIn = GetWebElementDataNodeModel.createSearchInSettingsModel();
       	final SettingsModelString findBy = GetWebElementDataNodeModel.createFindBySettingsModel();
       	
 	
       	addDialogComponent(new DialogComponentString(locatorString, "Search string", true, 30));
       	
        addDialogComponent(
        new DialogComponentStringSelection(searchIn, "Search for element in:",
        		Arrays.asList( "document","current element"),false));  
        
        addDialogComponent(
        new DialogComponentStringSelection(findBy, "FindBy type",
        		Arrays.asList("ById","ByName","ByClassName","ByXPath","ByCssSelector","ByLinkText","ByPartialLinkText","ByTagName"),false));
        

    }
}

