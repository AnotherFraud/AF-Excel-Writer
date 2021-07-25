package org.AF.SeleniumFire.Screenshot;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

/**
 * This is an example implementation of the node dialog of the
 * "Screenshot" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class ScreenshotNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected ScreenshotNodeDialog() {
        super();
        
        final SettingsModelFileChooser2 screenshotPathModel2 = ScreenshotNodeModel.createScreenshotPathSettingsModel();
        
        
        final FlowVariableModel fvmS = createFlowVariableModel(
                new String[]{screenshotPathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);
  
        createNewGroup("save screenshot to path");
        addDialogComponent(new DialogComponentFileChooser2(0, screenshotPathModel2, "default screenshot path", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, fvmS));
        closeCurrentGroup();
        
        
    }
}

