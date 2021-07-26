package org.AF.SeleniumFire.Submit;

import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "Submit" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class SubmitNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected SubmitNodeDialog() {
        super();
        
        

       	final SettingsModelString locatorString = SubmitNodeModel.createlocatorStringSettingsModel();
       	final SettingsModelString searchIn = SubmitNodeModel.createSearchInSettingsModel();
       	final SettingsModelString findBy = SubmitNodeModel.createFindBySettingsModel();
       	
        addDialogComponent(
        new DialogComponentStringSelection(searchIn, "Send text with locator string or current webwelement in connection:",
        		Arrays.asList( "with locator","current element", "with locator in current element"),false));  
        
        
        addDialogComponent(new DialogComponentString(locatorString, "Locator search string", true, 30));
        
        addDialogComponent(
        new DialogComponentStringSelection(findBy, "FindBy type",
        		Arrays.asList("ById","ByName","ByClassName","ByXPath","ByCssSelector","ByLinkText","ByPartialLinkText","ByTagName"),false));
    }
}

