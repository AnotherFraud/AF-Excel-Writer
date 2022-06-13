package org.AF.PGPUtilities.HmacHashing;


import java.util.Arrays;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "HmacHashing" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class HmacHashingNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected HmacHashingNodeDialog() {
        super();
        
        final SettingsModelString keyColName = HmacHashingNodeModel.createKeyColNameStringSettingsModel();
        final SettingsModelString messageColName = HmacHashingNodeModel.createMessageColNameStringSettingsModel();
        final SettingsModelString hashMode = HmacHashingNodeModel.createHashModeSettingsModel();
 
    	addDialogComponent(
                new DialogComponentStringSelection(hashMode, "Hash Mode",
                		Arrays.asList( "HmacSHA512","HmacSHA256","HmacSHA1","HmacMD5"),false));
    	
    	
    	
    	
    	
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		keyColName
                		,"Select key column:"
                		,0
                		,true
                		,StringValue.class
                ));
		
		
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		messageColName
                		,"Select message column"
                		,0
                		,true
                		,StringValue.class
                ));
		
		
    }
}

