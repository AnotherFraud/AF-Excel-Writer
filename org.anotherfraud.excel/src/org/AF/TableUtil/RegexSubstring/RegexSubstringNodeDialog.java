package org.AF.TableUtil.RegexSubstring;

import java.util.Arrays;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "RegexSubstring" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class RegexSubstringNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected RegexSubstringNodeDialog() {
        super();
        
     	final SettingsModelString valColName = RegexSubstringNodeModel.createValColNameStringSettingsModel();
    	final SettingsModelString regexString = RegexSubstringNodeModel.createRegexStringSettingsModel();
    	final SettingsModelString regexMode = RegexSubstringNodeModel.createRegexModeSettingsModel();
    	final SettingsModelString delimiter = RegexSubstringNodeModel.createDelimiterCharSettingsModel();
    	final SettingsModelIntegerBounded returnNumber = RegexSubstringNodeModel.createReturnNumberModel();
  	
    	
        //listener check selection for output file
    	regexMode.addChangeListener(e -> {
            if (regexMode.getStringValue().equals("Concat all matches")) {
            	delimiter.setEnabled(true);  
            	returnNumber.setEnabled(false); 
            	
            } else if (regexMode.getStringValue().equals("Return match by number")) {
            	delimiter.setEnabled(false);  
            	returnNumber.setEnabled(true); 
            }
            else 
            {
            	delimiter.setEnabled(false);  
            	returnNumber.setEnabled(false);          	
            }
        });
        
        

    	
		addDialogComponent(
                new DialogComponentColumnNameSelection(
                		valColName
                		,"Select column to apply regex:"
                		,0
                		,true
                		,StringValue.class
                ));
		

    	
		// Add a new String component to the dialog.
		addDialogComponent(new DialogComponentString(regexString, "regex string", true, 10));
		
		
    	addDialogComponent(
                new DialogComponentStringSelection(regexMode, "Return Mode",
                		Arrays.asList( "First match","Last match","Concat all matches","Return match by number"),false));
    	
    	addDialogComponent(new DialogComponentString(delimiter, "Concat delimiter", true, 10));
    	
    	
    	addDialogComponent(new DialogComponentNumber(returnNumber, "Match number:", 1));
    	
    	
    }
}

