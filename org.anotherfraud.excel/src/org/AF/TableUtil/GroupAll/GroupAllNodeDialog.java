package org.AF.TableUtil.GroupAll;

import java.util.Arrays;


import org.knime.core.data.IntValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "GroupAll" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class GroupAllNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    @SuppressWarnings("unchecked")
	protected GroupAllNodeDialog() {
        super();
        
    	final SettingsModelString valColName = GroupAllNodeModel.createValColNameStringSettingsModel();
    	final SettingsModelString groupMode = GroupAllNodeModel.createGroupModeSettingsModel();
    	final SettingsModelIntegerBounded minTotal = GroupAllNodeModel.createMinTotalSettingsModel();
    	final SettingsModelIntegerBounded minCounter = GroupAllNodeModel.createMinCounterSettingsModel();
    	final SettingsModelIntegerBounded maxCounter = GroupAllNodeModel.createMaxCounterSettingsModel();
    	
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		valColName
                		,"Select counter column:"
                		,0
                		,true
                		,IntValue.class
                ));
		
		
		createNewGroup("Grouping filter"); 
		addDialogComponent(new DialogComponentNumber(minTotal, "filter out groups with less total cases than", 0));
		addDialogComponent(new DialogComponentNumber(minCounter, "filter out groups with less counter cases than", 0));
		addDialogComponent(new DialogComponentNumber(maxCounter, "filter out groups with more counter cases than", 999999));
		
		closeCurrentGroup();
		
		 createNewTab("Advanced Options");
	     createNewGroup("Grouping Mode"); 
	     
	     addDialogComponent(
			        new DialogComponentStringSelection(groupMode, "Select Grouping Mode",
			        		Arrays.asList( "all columns", "only string","only numbers"),false));
		
	     closeCurrentGroup();
		
	   }
}

