package org.AF.SeleniumFire.ExecuteJavaScript;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "ExecuteJavaScript" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class ExecuteJavaScriptNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected ExecuteJavaScriptNodeDialog() {
        super();
        
       	final SettingsModelString javaScript = ExecuteJavaScriptNodeModel.createJavaScriptStringSettingsModel();
       	addDialogComponent(new DialogComponentString(javaScript, "JavaScript to execute", true, 30));
       	
    }
}

