package org.AF.SeleniumFire.SendSpecialKeys;

import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "SendSpecialKeys" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class SendSpecialKeysNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected SendSpecialKeysNodeDialog() {
        super();
        
        final SettingsModelString sendKeys = SendSpecialKeysNodeModel.createSendTextStringSettingsModel();
       	final SettingsModelString locatorString = SendSpecialKeysNodeModel.createlocatorStringSettingsModel();
       	final SettingsModelString searchIn = SendSpecialKeysNodeModel.createSearchInSettingsModel();
       	final SettingsModelString findBy = SendSpecialKeysNodeModel.createFindBySettingsModel();
       	
 	

       	addDialogComponent(
       	        new DialogComponentStringSelection(sendKeys, "Type the following special key",
       	        		Arrays.asList( "ADD","ALT","ARROW_DOWN","ARROW_LEFT","ARROW_RIGHT"
       	        				,"ARROW_UP","BACK_SPACE","CANCEL","CLEAR","COMMAND"
       	        				,"CONTROL","DECIMAL","DELETE","DIVIDE","DOWN"
       	        				,"END","ENTER","EQUALS","ESCAPE","F1","F10"
       	        				,"F11","F12","F2","F3","F4","F5","F6"
       	        				,"F7","F8","F9","HELP","HOME","INSERT","LEFT"
       	        				,"LEFT_ALT","LEFT_CONTROL","LEFT_SHIFT","META"
       	        				,"MULTIPLY","NULL","NUMPAD0","NUMPAD1","NUMPAD2"
       	        				,"NUMPAD3","NUMPAD4","NUMPAD5","NUMPAD6","NUMPAD7","NUMPAD8"
       	        				,"NUMPAD9","PAGE_DOWN","PAGE_UP","PAUSE","RETURN","RIGHT"
       	        				,"SEMICOLON","SEPARATOR","SHIFT","SPACE","SUBTRACT","TAB","UP"
       	        				),false));  
       	
       	
       	

        addDialogComponent(
        new DialogComponentStringSelection(searchIn, "Send text with locator string or current webwelement in connection:",
        		Arrays.asList( "with locator","current element", "with locator in current element"),false));  
        
        
        addDialogComponent(new DialogComponentString(locatorString, "Locator search string", true, 30));
        
        addDialogComponent(
        new DialogComponentStringSelection(findBy, "FindBy type",
        		Arrays.asList("ById","ByName","ByClassName","ByXPath","ByCssSelector","ByLinkText","ByPartialLinkText","ByTagName"),false));
    }
}

