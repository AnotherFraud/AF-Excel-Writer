package org.AF.ExecuteShellScript;



import org.AF.SeleniumFire.Utilities.DialogComponentFirefoxPreferences;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "ExecuteShellScript" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class ExecuteShellScriptNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected ExecuteShellScriptNodeDialog() {
        super();
        
        
     	final SettingsModelString scriptStringModel = ExecuteShellScriptNodeModel.createShellScriptSettingsModel();
        final SettingsModelString executionDirModel = ExecuteShellScriptNodeModel.createExecutionDirSettingsModel();

        
        final SettingsModelScriptEnvironmentSettings environmentModel = ExecuteShellScriptNodeModel.createEnviromentSettingsModel(); 
        
        

	
       	addDialogComponent(new DialogComponentString(scriptStringModel, "Script to execute", true, 60));
       	addDialogComponent(new DialogComponentString(executionDirModel, "Path to execution dir (complete path)", false, 60));

       	
        createNewTab("Environment Preferences");
        createNewGroup("Environment Preferences"); 
        addDialogComponent(new DialogComponentScriptEnvironmentPreferences(environmentModel, "Environment Preferences"));
        
        closeCurrentGroup();
        
        

    }
}

