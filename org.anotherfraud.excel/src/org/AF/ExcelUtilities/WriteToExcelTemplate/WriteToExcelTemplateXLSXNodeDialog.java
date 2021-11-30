package org.AF.ExcelUtilities.WriteToExcelTemplate;



import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;


/*
 * This program is free software: you can redistribute it and/or modify
 * Copyright [2021] [Another Fraud]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */


public class WriteToExcelTemplateXLSXNodeDialog extends DefaultNodeSettingsPane {


	
	
    
    
	@SuppressWarnings("deprecation")
	protected WriteToExcelTemplateXLSXNodeDialog() {
        super();


    
    	
        
    	
    	
    	final SettingsModelString sheetNamesModel = WriteToExcelTemplateXLSXNodeModel.createSheetNamesModel();
        final SettingsModelString sheetOrIndexModel = WriteToExcelTemplateXLSXNodeModel.createSheetNameOrIndexSettingsModel();
        final SettingsModelIntegerBounded sheetIndexModel = WriteToExcelTemplateXLSXNodeModel.createSheetIndexModel();

        final SettingsModelFileChooser2 templateFilePathModel2 = WriteToExcelTemplateXLSXNodeModel.createTemplateFilePath2SettingsModel();
        final SettingsModelAuthentication passwordModel = WriteToExcelTemplateXLSXNodeModel.createPassSettingsModel();
        final SettingsModelAuthentication outPasswordModel = WriteToExcelTemplateXLSXNodeModel.createOutPassSettingsModel();
        
        
        final DialogComponentStringSelection sheetNameSelection = new DialogComponentStringSelection(sheetNamesModel, "Sheet Name",
        		Arrays.asList("default", ""),true);


    	final SettingsModelFileChooser2 outputFilePathModel2 = WriteToExcelTemplateXLSXNodeModel.createOutputFilePath2SettingsModel();
        
    	final SettingsModelString copyOrWriteModel = WriteToExcelTemplateXLSXNodeModel.createCopyOrWriteSettingsModel();
    	final SettingsModelString overrideOrFailModel = WriteToExcelTemplateXLSXNodeModel.createOverrideOrFailModelSettingsModel();
    	
    	
    	final SettingsModelBoolean lastRowModel = WriteToExcelTemplateXLSXNodeModel.createWriteLastRowSettingsModel();
    	final SettingsModelString enablePassModel = WriteToExcelTemplateXLSXNodeModel.enablePasswordSettingsModel();
    	final SettingsModelIntegerBounded rowOffModel = WriteToExcelTemplateXLSXNodeModel.createRowOffsetSettingsModel();
    	final SettingsModelIntegerBounded colOffModel = WriteToExcelTemplateXLSXNodeModel.createColOffsetSettingsModel();

    	
    	
    	
         
    	//listener try to read in sheet names from given template file
        templateFilePathModel2.addChangeListener(e -> {	
            if (templateFilePathModel2.getPathOrURL().length() > 0) {
            	List<String> sheetNames = WriteToExcelTemplateXLSXNodeModel.tryGetExcelSheetNames(templateFilePathModel2.getPathOrURL(),passwordModel.getPassword()); 	
            		if(sheetNames != null)
            		{
            			if (!sheetNames.contains(sheetNamesModel.getStringValue()))
            			{
            			sheetNames.add(0, sheetNamesModel.getStringValue());
            			}
            			sheetNameSelection.replaceListItems(sheetNames, null);
            		}

            } else {
 
            }
            
        });
        
        

    
      //listener check selection for output file
        copyOrWriteModel.addChangeListener(e -> {
            if (copyOrWriteModel.getStringValue().equals("WriteInto")) {
            	outputFilePathModel2.setEnabled(false);  
            	overrideOrFailModel.setEnabled(false); 
            	
            } else if (copyOrWriteModel.getStringValue().equals("CopyFrom")) {
            	outputFilePathModel2.setEnabled(true); 
            	overrideOrFailModel.setEnabled(true);  
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
            	sheetNamesModel.setEnabled(true); 
            } else if (sheetOrIndexModel.getStringValue().equals("index")) {
            	sheetIndexModel.setEnabled(true); 
            	sheetNamesModel.setEnabled(false);  
            }
        });
        
        
        
        //listener check option for last row
        lastRowModel.addChangeListener(e -> {
            if (lastRowModel.getBooleanValue()) {
            	rowOffModel.setEnabled(false);  
            } else {
            	
            	rowOffModel.setEnabled(true); 
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
        
        addDialogComponent(new DialogComponentButtonGroup(
        		copyOrWriteModel      		
        		, "", false
        			,new String[] { "Create new file", "Write Into Template File"}
        			,new String[] { "CopyFrom", "WriteInto"}
        		));
        

         
        final FlowVariableModel tplfvm = createFlowVariableModel(
                new String[]{outputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);
        
        addDialogComponent(new DialogComponentFileChooser2(0, outputFilePathModel2, "XLSoutput", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, tplfvm));
            
        addDialogComponent(new DialogComponentButtonGroup(
        		overrideOrFailModel      		
        		, "", false
        			,new String[] { "Override existing file", "Fail if file exists"}
        			,new String[] { "Override", "Fail"}
        		));
        
        
        closeCurrentGroup();
        
        
        
        createNewTab("Advanced Options");
        createNewGroup("Header and Formulas"); 
        
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		WriteToExcelTemplateXLSXNodeModel.writeHeaderOption, false), "Write column header?"));
        
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		WriteToExcelTemplateXLSXNodeModel.writeFormulaOption, false), "Do not write string cells starting with = as formulas?"));
        
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		WriteToExcelTemplateXLSXNodeModel.froceFormulaUpdate, false), "Should all exising formulas be recalculated?"));
        
        closeCurrentGroup();
        
        
        createNewGroup("Offsets"); 
        
        addDialogComponent(new DialogComponentBoolean(lastRowModel, "Write to last physical row?"));
        
        addDialogComponent(new DialogComponentNumber(rowOffModel, "Starting row", 1));
        
        addDialogComponent(new DialogComponentNumber(colOffModel, "Starting column", 1));
        

        createNewGroup("Excel Template Password"); 

        addDialogComponent(
        new DialogComponentStringSelection(enablePassModel, "Password action",
        		Arrays.asList( "No PWD needed", "Open with PWD", "Remove PWD","Change PWD","Add PWD"),false));
        
        
        
        addDialogComponent(new  DialogComponentAuthentication(passwordModel, "Excel Template Password", AuthenticationType.PWD));
        addDialogComponent(new  DialogComponentAuthentication(outPasswordModel, "Excel Output Password", AuthenticationType.PWD));

        
        
        closeCurrentGroup();
        
   	   
  
       
        
        
    }


    
}

