package org.AF.ExcelUtilities.EvaluateExcelFormula;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "EvaluateExcelFormula" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class EvaluateExcelFormulaNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected EvaluateExcelFormulaNodeDialog() {
        super();
        
    	final SettingsModelString valColName = EvaluateExcelFormulaNodeModel.createValColNameStringSettingsModel();

    	
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		valColName
                		,"Select formula column to evaluate:"
                		,0
                		,true
                		,StringValue.class
                ));
		
    }
}

