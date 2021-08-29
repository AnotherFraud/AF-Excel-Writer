package org.AF.TableUtil.RegexToList;



import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "RegexToList" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class RegexToListNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected RegexToListNodeDialog() {
        super();
  
      	final SettingsModelString valColName = RegexToListNodeModel.createValColNameStringSettingsModel();
    	final SettingsModelString regexString = RegexToListNodeModel.createRegexStringSettingsModel();

    	
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		valColName
                		,"Select counter column:"
                		,0
                		,true
                		,StringValue.class
                ));
		
		
		
		// Add a new String component to the dialog.
		addDialogComponent(new DialogComponentString(regexString, "regex string", true, 10));
		
		
    }
}

