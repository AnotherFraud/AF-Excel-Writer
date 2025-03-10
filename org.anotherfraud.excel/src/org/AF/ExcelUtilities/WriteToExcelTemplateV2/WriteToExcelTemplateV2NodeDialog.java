package org.AF.ExcelUtilities.WriteToExcelTemplateV2;

import java.util.Arrays;
import java.util.List;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.context.ports.PortsConfiguration;
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
import org.knime.filehandling.core.data.location.variable.FSLocationVariableType;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.DialogComponentReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.DialogComponentWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;

/**
 * This is an example implementation of the node dialog of the
 * "WriteToExcelTemplateV2" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class WriteToExcelTemplateV2NodeDialog extends DefaultNodeSettingsPane {



	private final WriteToExcelTemplateV2Config m_cfg;


 	final SettingsModelString copyOrWriteModel = WriteToExcelTemplateV2NodeModel.createCopyOrWriteSettingsModel();
 	
 	
 	
    protected WriteToExcelTemplateV2NodeDialog(final PortsConfiguration portsConfig) {
        super();


    	
        m_cfg = new WriteToExcelTemplateV2Config(portsConfig);
        

        
        
     	final SettingsModelString sheetNamesModel = WriteToExcelTemplateV2NodeModel.createSheetNamesModel();
        final SettingsModelString sheetOrIndexModel = WriteToExcelTemplateV2NodeModel.createSheetNameOrIndexSettingsModel();
        final SettingsModelIntegerBounded sheetIndexModel = WriteToExcelTemplateV2NodeModel.createSheetIndexModel();

        final SettingsModelReaderFileChooser templateFilePathModel = m_cfg.getSrcFileChooserModel();
         
        final SettingsModelAuthentication passwordModel = WriteToExcelTemplateV2NodeModel.createPassSettingsModel();
        final SettingsModelAuthentication outPasswordModel = WriteToExcelTemplateV2NodeModel.createOutPassSettingsModel();
         

        final DialogComponentStringSelection sheetNameSelection = new DialogComponentStringSelection(sheetNamesModel, "Sheet Name",
         		Arrays.asList("default", ""),true);


     	final SettingsModelWriterFileChooser outputFilePathModel = m_cfg.getDestFileChooserModel();
         
     	
     	
     	
     	final SettingsModelBoolean lastRowModel = WriteToExcelTemplateV2NodeModel.createWriteLastRowSettingsModel();
     	final SettingsModelBoolean clearData = WriteToExcelTemplateV2NodeModel.createClearDataSettingsModel();
     	final SettingsModelString enablePassModel = WriteToExcelTemplateV2NodeModel.enablePasswordSettingsModel();
     	final SettingsModelIntegerBounded rowOffModel = WriteToExcelTemplateV2NodeModel.createRowOffsetSettingsModel();
     	final SettingsModelIntegerBounded colOffModel = WriteToExcelTemplateV2NodeModel.createColOffsetSettingsModel();

     	

     	
     	
     	//listener try to read in sheet names from given template file
         templateFilePathModel.addChangeListener(e -> {	
             if (templateFilePathModel.getPath().length() > 0) {
             	List<String> sheetNames = WriteToExcelTemplateV2NodeModel.tryGetExcelSheetNames(templateFilePathModel,passwordModel.getPassword()); 	
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
             	outputFilePathModel.setEnabled(false);  
             	
             } else if (copyOrWriteModel.getStringValue().equals("CopyFrom")) {
             	outputFilePathModel.setEnabled(true); 
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
         

         final FlowVariableModel fvm = createFlowVariableModel(templateFilePathModel.getKeysForFSLocation(), FSLocationVariableType.INSTANCE);
         addDialogComponent(new DialogComponentReaderFileChooser(templateFilePathModel, "XLStemplate", fvm));
         
         
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
         

        
       
         final FlowVariableModel tplfvm = createFlowVariableModel(outputFilePathModel.getKeysForFSLocation(), FSLocationVariableType.INSTANCE);

         
         
         addDialogComponent(new DialogComponentWriterFileChooser(outputFilePathModel, "XLSoutput", tplfvm));
         


         
         closeCurrentGroup();
         
         
         
         createNewTab("Advanced Options");
         createNewGroup("Header and Formulas"); 
         
         addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		 WriteToExcelTemplateV2NodeModel.writeHeaderOption, false), "Write column header?"));
         
         addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		 WriteToExcelTemplateV2NodeModel.writeFormulaOption, false), "Do not write string cells starting with = as formulas?"));
         
         addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
        		 WriteToExcelTemplateV2NodeModel.froceFormulaUpdate, false), "Should all exising formulas be recalculated?"));
         
         closeCurrentGroup();
         
         
         createNewGroup("Offsets"); 
         
         addDialogComponent(new DialogComponentBoolean(lastRowModel, "Write to last physical row?"));
         
         addDialogComponent(new DialogComponentNumber(rowOffModel, "Starting row", 1));
         
         addDialogComponent(new DialogComponentNumber(colOffModel, "Starting column", 1));
         
         addDialogComponent(new DialogComponentBoolean(clearData, "Remove all data before writing?"));
         
         

         createNewGroup("Excel Template Password"); 

         addDialogComponent(
         new DialogComponentStringSelection(enablePassModel, "Password action",
         		Arrays.asList( "No PWD needed", "Open with PWD", "Remove PWD","Change PWD","Add PWD"),false));
         
         
         
         addDialogComponent(new  DialogComponentAuthentication(passwordModel, "Excel Template Password", AuthenticationType.PWD));
         addDialogComponent(new  DialogComponentAuthentication(outPasswordModel, "Excel Output Password", AuthenticationType.PWD));

         
         
         closeCurrentGroup();
     }   

 }

