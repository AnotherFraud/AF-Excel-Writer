package org.AF.KeepassUtilities.KeepassReader;
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
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.core.util.Pair;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

/**
 * This is an example implementation of the node dialog of the
 * "KeepassReader" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author AnotherFraudUser
 */
public class KeepassReaderNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
	
	private final DialogComponentAuthentication m_authenticationPanel;
	private final SettingsModelAuthentication passwordModel;
	
	
    protected KeepassReaderNodeDialog() {
        super();

        
       

        	
        	final SettingsModelString keepassEntryNameModel = KeepassReaderNodeModel.createKeepassEntryNamesModel();
            final SettingsModelFileChooser2 inputFilePathModel2 = KeepassReaderNodeModel.createInputFilePath2SettingsModel();
            passwordModel = KeepassReaderNodeModel.createPassSettingsModel();
            
            final DialogComponentButton loadEntrys = new DialogComponentButton("Load entrys from file");
            final DialogComponentStringSelection entryNameSelection = new DialogComponentStringSelection(keepassEntryNameModel, "Entry Name",
            		Arrays.asList("default", ""),true);


            //listener check selection for password usage
            loadEntrys.addActionListener(e -> {
            	
                if (inputFilePathModel2.getPathOrURL().length() > 0) {
                	List<String> entryNames = KeepassReaderNodeModel.tryLoadKeePassEntryTitles(inputFilePathModel2.getPathOrURL(),passwordModel.getPassword(getCredentialsProvider()));	
                		if(entryNames != null)
                		{
                			if (!entryNames.contains(keepassEntryNameModel.getStringValue()))
                			{
                				entryNames.add(0, keepassEntryNameModel.getStringValue());
                			}
                			entryNameSelection.replaceListItems(entryNames, null);
                		}

                } else {
     
                }
            });   
            
           	//Map<AuthenticationType, Pair<String, String>> map;
           	HashMap<AuthenticationType, Pair<String, String>> mapPass = new HashMap<AuthenticationType, Pair<String, String>>()
           	{
    		private static final long serialVersionUID = -3983032017710953380L;

    		{
           	     put(AuthenticationType.CREDENTIALS, new Pair<String, String>("Keepass Store Credential","Keepass Store Credential"));
           	     put(AuthenticationType.PWD, new Pair<String, String>("Keepass Store Password","Keepass Store Password"));

           	}}; 
           	
            createNewGroup("File Selection");
            

            final FlowVariableModel fvm = createFlowVariableModel(
                new String[]{inputFilePathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

            addDialogComponent(new DialogComponentFileChooser2(0, inputFilePathModel2, "templateFile", JFileChooser.OPEN_DIALOG,
                    JFileChooser.FILES_ONLY, fvm));
            
            
            createNewGroup("Entry Selection");
            
            addDialogComponent(loadEntrys);
            addDialogComponent(entryNameSelection); 
            
            
               
            createNewGroup("Keepass Password");
            m_authenticationPanel = new  DialogComponentAuthentication(passwordModel, "Keepass Store Password", Arrays.asList(AuthenticationType.CREDENTIALS, AuthenticationType.PWD), mapPass);
            addDialogComponent(m_authenticationPanel);
            
            
            
            
            closeCurrentGroup();
            
       	   
      
           
            
            
        }

    @Override
    public void saveAdditionalSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
    	passwordModel.saveSettingsTo(settings);    	
 
    	
    }

    @Override
    public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
    	try {
    		passwordModel.loadSettingsFrom(settings);
    		m_authenticationPanel.loadSettingsFrom(settings, specs, getCredentialsProvider());

    		
    	} catch (InvalidSettingsException e) {
    		throw new NotConfigurableException(e.getMessage(), e);
    	}
    }
    
        
    }


