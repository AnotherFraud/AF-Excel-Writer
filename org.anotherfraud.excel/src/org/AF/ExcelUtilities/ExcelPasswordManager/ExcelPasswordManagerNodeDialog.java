package org.AF.ExcelUtilities.ExcelPasswordManager;

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


import java.util.Arrays;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
/**
 * This is an example implementation of the node dialog of the
 * "ExcelPasswordManager" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author AnotherFraudExcel
 */



public class ExcelPasswordManagerNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected ExcelPasswordManagerNodeDialog() {
            super();


        	
             final SettingsModelFileChooser2 templateFilePathModel2 = ExcelPasswordManagerNodeModel.createTemplateFilePath2SettingsModel();
            final SettingsModelAuthentication passwordModel = ExcelPasswordManagerNodeModel.createPassSettingsModel();
            final SettingsModelAuthentication outPasswordModel = ExcelPasswordManagerNodeModel.createOutPassSettingsModel();
            
        
        	final SettingsModelFileChooser2 outputFilePathModel2 = ExcelPasswordManagerNodeModel.createOutputFilePath2SettingsModel();
            
        	final SettingsModelString copyOrWriteModel = ExcelPasswordManagerNodeModel.createCopyOrWriteSettingsModel();
        	final SettingsModelString overrideOrFailModel = ExcelPasswordManagerNodeModel.createOverrideOrFailModelSettingsModel();
        	
        	
        	final SettingsModelString enablePassModel = ExcelPasswordManagerNodeModel.enablePasswordSettingsModel();
        	
        	
                
            //listener check selection for password usage
            enablePassModel.addChangeListener(e -> {
                if (enablePassModel.getStringValue().equals("Add PWD")) {
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
                	passwordModel.setEnabled(true);
                	outPasswordModel.setEnabled(false);
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
            

            
        
            
            createNewGroup("Input File Selection");
            
            final FlowVariableModel fvm = createFlowVariableModel(
                new String[]{templateFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);



            addDialogComponent(new DialogComponentFileChooser2(0, templateFilePathModel2, "XLStemplate", JFileChooser.OPEN_DIALOG,
                JFileChooser.FILES_ONLY, fvm));
            
            

            createNewGroup("Output File Selection");
            
            addDialogComponent(new DialogComponentButtonGroup(
            		copyOrWriteModel      		
            		, "", false
            			,new String[] { "Create new file", "Change PWD in Input File"}
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
            
            

            createNewGroup("Password Options"); 
            
            addDialogComponent(
            new DialogComponentStringSelection(enablePassModel, "Password action",
            		Arrays.asList( "Add PWD","Remove PWD","Change PWD"),false));
            
            
            
            addDialogComponent(new  DialogComponentAuthentication(passwordModel, "Excel Template Password", AuthenticationType.PWD));
            addDialogComponent(new  DialogComponentAuthentication(outPasswordModel, "Excel Output Password", AuthenticationType.PWD));

            
            
            closeCurrentGroup();
            
    }
}

