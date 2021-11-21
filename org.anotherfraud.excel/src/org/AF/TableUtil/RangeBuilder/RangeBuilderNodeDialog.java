package org.AF.TableUtil.RangeBuilder;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentDoubleRange;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleRange;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "RangeBuilder" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author AnotherFraud
 */
public class RangeBuilderNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    @SuppressWarnings("unchecked")
	protected RangeBuilderNodeDialog() {
        super();
        
    	final SettingsModelString counterColName = RangeBuilderNodeModel.createCounterColNameStringSettingsModel();
    	final SettingsModelString totalColName = RangeBuilderNodeModel.createTotalColNameStringSettingsModel();
    	final SettingsModelString valColName = RangeBuilderNodeModel.createValColNameStringSettingsModel();
    	final SettingsModelString catColName = RangeBuilderNodeModel.createCatColNameStringSettingsModel();
    	final SettingsModelDoubleRange percRange = RangeBuilderNodeModel.createPercRangeSettingsModel();
    	
    	final SettingsModelIntegerBounded minTotal = RangeBuilderNodeModel.createMinTotalSettingsModel();
    	final SettingsModelIntegerBounded minCounter = RangeBuilderNodeModel.createMinCounterSettingsModel();
    	final SettingsModelIntegerBounded maxCounter = RangeBuilderNodeModel.createMaxCounterSettingsModel();
    	

		
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		catColName
                		,"Select Category column:"
                		,0
                		,true
                		,StringValue.class
                ));	

		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		valColName
                		,"Select value column:"
                		,0
                		,true
                		,DoubleValue.class
                ));	
		
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		counterColName
                		,"Select counter column:"
                		,0
                		,true
                		,IntValue.class
                ));
		
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		totalColName
                		,"Select total column:"
                		,0
                		,true
                		,IntValue.class
                ));		
		
		
		createNewGroup("Range filter"); 
		addDialogComponent(new DialogComponentNumber(minTotal, "filter out groups with less total cases than", 0));
		
		addDialogComponent(new DialogComponentNumber(minCounter, "filter out groups with less counter cases than", 0));
		addDialogComponent(new DialogComponentNumber(maxCounter, "filter out groups with more counter cases than", 0));
		
		addDialogComponent(new DialogComponentDoubleRange(percRange, 0, 100, 0.1, "include Percenage between"));
		
		
	     closeCurrentGroup();
    }
}

