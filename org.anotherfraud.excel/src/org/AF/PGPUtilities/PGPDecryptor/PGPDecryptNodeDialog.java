package org.AF.PGPUtilities.PGPDecryptor;
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

import java.util.List;

import javax.swing.JFileChooser;

import org.AF.ExcelUtilities.WriteToExcelTemplate.WriteToExcelTemplateXLSXNodeModel;
import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

/**
 * This is an example implementation of the node dialog of the
 * "PGPDecrypt" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author 
 */
public class PGPDecryptNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected PGPDecryptNodeDialog() {
        super();
        
        final SettingsModelFileChooser2 inputFilePathModel2 = PGPDecryptNodeModel.createInputFilePath2SettingsModel();
        final SettingsModelFileChooser2 outputFilePathModel2 = PGPDecryptNodeModel.createOutFilePath2SettingsModel();
        final SettingsModelFileChooser2 keyFilePathModel2 = PGPDecryptNodeModel.createKeeFilePath2SettingsModel();
        final SettingsModelAuthentication passwordModel = PGPDecryptNodeModel.createPassSettingsModel();
        final SettingsModelBoolean keyfilePassword = PGPDecryptNodeModel.createUseKeyfilePasswordSettingsModel();
        
    
        
    	//listener try to read in sheet names from given template file
        keyfilePassword.addChangeListener(e -> {	
            if (keyfilePassword.getBooleanValue()) {
            	passwordModel.setEnabled(true);
            } else {
            	passwordModel.setEnabled(false);
            }
            
        });
        
        
        
        
        createNewGroup("PGP File Selection");
        

        final FlowVariableModel fvm = createFlowVariableModel(
            new String[]{inputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
            Type.STRING);

        addDialogComponent(new DialogComponentFileChooser2(0, inputFilePathModel2, "InputFile", JFileChooser.OPEN_DIALOG,
            JFileChooser.FILES_ONLY, fvm));
        
        
        closeCurrentGroup();
        createNewGroup("Output Folder Selection");
        
        final FlowVariableModel fvmOut = createFlowVariableModel(
                new String[]{outputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            addDialogComponent(new DialogComponentFileChooser2(0, outputFilePathModel2, "OuputFile", JFileChooser.SAVE_DIALOG,
                JFileChooser.FILES_ONLY, fvmOut));        
        
        closeCurrentGroup();
        
        createNewGroup("Keyfile Selection");
        
        final FlowVariableModel fvmKee = createFlowVariableModel(
                new String[]{keyFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            addDialogComponent(new DialogComponentFileChooser2(0, keyFilePathModel2, "KeeFile", JFileChooser.OPEN_DIALOG,
                JFileChooser.FILES_ONLY, fvmKee));     
            
            
            addDialogComponent(new DialogComponentBoolean(keyfilePassword, "Keyfile is password secured?"));
            addDialogComponent(new  DialogComponentAuthentication(passwordModel, "Kee File Password", AuthenticationType.PWD));
            
        closeCurrentGroup();
    }
}

