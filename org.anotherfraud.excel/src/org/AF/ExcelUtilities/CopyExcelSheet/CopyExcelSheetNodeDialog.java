package org.AF.ExcelUtilities.CopyExcelSheet;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;



/**
 * This is an example implementation of the node dialog of the
 * "CopyExcelSheet" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class CopyExcelSheetNodeDialog extends DefaultNodeSettingsPane {
	
	
    protected CopyExcelSheetNodeDialog() {
        super();
        
       	final SettingsModelString sheetNamesInModel = CopyExcelSheetNodeModel.createInputSheetNamesModel();
       	final SettingsModelString sheetNamesOutModel = CopyExcelSheetNodeModel.createOutputSheetNamesModel();
        final SettingsModelString sheetOrIndexModel = CopyExcelSheetNodeModel.createSheetNameOrIndexSettingsModel();
        final SettingsModelIntegerBounded sheetIndexModel = CopyExcelSheetNodeModel.createSheetIndexModel();

        final SettingsModelFileChooser2 templateFilePathModel2 = CopyExcelSheetNodeModel.createTemplateFilePath2SettingsModel();
        final SettingsModelAuthentication passwordModel = CopyExcelSheetNodeModel.createPassSettingsModel();
        final SettingsModelAuthentication outPasswordModel = CopyExcelSheetNodeModel.createOutPassSettingsModel();
        
        
        final DialogComponentStringSelection sheetNameSelection = new DialogComponentStringSelection(sheetNamesInModel, "Sheet Name",
        		Arrays.asList("default", ""),true);


    	final SettingsModelFileChooser2 outputFilePathModel2 = CopyExcelSheetNodeModel.createOutputFilePath2SettingsModel();
        
    	
    	
    	
    	
    	final SettingsModelString enablePassModel = CopyExcelSheetNodeModel.enablePasswordSettingsModel();
    	
    	
    	
    	
         
    	//listener try to read in sheet names from given template file
        templateFilePathModel2.addChangeListener(e -> {	
            if (templateFilePathModel2.getPathOrURL().length() > 0) {
            	List<String> sheetNames = CopyExcelSheetNodeModel.tryGetExcelSheetNames(templateFilePathModel2.getPathOrURL(),passwordModel.getPassword()); 	
            		if(sheetNames != null)
            		{
            			if (!sheetNames.contains(sheetNamesInModel.getStringValue()))
            			{
            			sheetNames.add(0, sheetNamesInModel.getStringValue());
            			}
            			sheetNameSelection.replaceListItems(sheetNames, null);
            		}

            } else {
 
            }
            
        });
        
        

            
         
        
      //listener check selection for password usage
        enablePassModel.addChangeListener(e -> {
            if (enablePassModel.getStringValue().equals("Open with PWD")) {
            	passwordModel.setEnabled(true);
            	outPasswordModel.setEnabled(false);
            } else if (enablePassModel.getStringValue().equals("Remove PWD")) {
            	passwordModel.setEnabled(true);
            	outPasswordModel.setEnabled(false);
            }else if (enablePassModel.getStringValue().equals("Change PWD")) {
            	passwordModel.setEnabled(true);
            	outPasswordModel.setEnabled(true);
            }else if (enablePassModel.getStringValue().equals("Add PWD")) {
            	passwordModel.setEnabled(false);
            	outPasswordModel.setEnabled(true);
            }
            else  {
            	passwordModel.setEnabled(false); 
            	outPasswordModel.setEnabled(false);
            }
        });
        
        


      //listener check option for sheet name selection
        sheetOrIndexModel.addChangeListener(e -> {
            if (sheetOrIndexModel.getStringValue().equals("name")) {
            	sheetIndexModel.setEnabled(false);  
            	sheetNamesInModel.setEnabled(true); 
            } else if (sheetOrIndexModel.getStringValue().equals("index")) {
            	sheetIndexModel.setEnabled(true); 
            	sheetNamesInModel.setEnabled(false);  
            }
        });
        
        
        
      

        
        
    
        
        createNewGroup("Template File Selection");
        

        final FlowVariableModel fvm = createFlowVariableModel(
            new String[]{templateFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
            Type.STRING);


        
        
        addDialogComponent(new DialogComponentFileChooser2(0, templateFilePathModel2, "XLStemplate", JFileChooser.OPEN_DIALOG,
            JFileChooser.FILES_ONLY, fvm));
        
        
        createNewGroup("Template Sheet Name Selection");
        
       
        
        addDialogComponent(new DialogComponentButtonGroup(
        		sheetOrIndexModel      		
        		, "", false
        			,new String[] {  "Get Sheet By Index", "Get Sheet By Name"}
        			,new String[] {  "index", "name"}
        		));
        
        
        addDialogComponent(new DialogComponentNumber(sheetIndexModel, "Sheet Index", 1));
        addDialogComponent(sheetNameSelection); 
        
        
        
        closeCurrentGroup();
        createNewGroup("Output File Selection");
        

         
        final FlowVariableModel tplfvm = createFlowVariableModel(
                new String[]{outputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);
        
        addDialogComponent(new DialogComponentFileChooser2(0, outputFilePathModel2, "XLSoutput", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, tplfvm));
            
        
        addDialogComponent(new DialogComponentString(sheetNamesOutModel, "Output Sheetname", true, 60));
        
        
        closeCurrentGroup();
        
        
        
        createNewTab("Advanced Options");
        createNewGroup("Header and Formulas"); 
                
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		CopyExcelSheetNodeModel.froceFormulaUpdate, false), "Should all exising formulas be recalculated?"));
        
        closeCurrentGroup();

        createNewGroup("Excel Template Password"); 

        addDialogComponent(
        new DialogComponentStringSelection(enablePassModel, "Password action",
        		Arrays.asList( "No PWD needed", "Open with PWD", "Remove PWD","Change PWD","Add PWD"),false));
        
        
        
        addDialogComponent(new  DialogComponentAuthentication(passwordModel, "Excel Template Password", AuthenticationType.PWD));
        addDialogComponent(new  DialogComponentAuthentication(outPasswordModel, "Excel Output Password", AuthenticationType.PWD));

        
        
        closeCurrentGroup();
        
   	   
  
       
        
        
    }


    
}

