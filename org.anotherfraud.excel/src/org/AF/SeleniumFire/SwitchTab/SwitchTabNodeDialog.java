package org.AF.SeleniumFire.SwitchTab;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * This is an example implementation of the node dialog of the
 * "SwitchTab" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class SwitchTabNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected SwitchTabNodeDialog() {
        super();
        
       	final SettingsModelIntegerBounded tabNumberModel = SwitchTabNodeModel.createTabNumberSettingsModel();
       	
       	addDialogComponent(new DialogComponentNumber(tabNumberModel, "Switch to tab number", 1));
    }
}

