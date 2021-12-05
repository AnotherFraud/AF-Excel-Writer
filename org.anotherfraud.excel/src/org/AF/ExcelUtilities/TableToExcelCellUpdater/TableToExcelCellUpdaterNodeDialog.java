package org.AF.ExcelUtilities.TableToExcelCellUpdater;


import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "TableToExcelCellUpdater" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class TableToExcelCellUpdaterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring the node. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
    protected TableToExcelCellUpdaterNodeDialog() {
        super();
        
    	final SettingsModelIntegerBounded rowOffModel = TableToExcelCellUpdaterNodeModel.createRowOffsetSettingsModel();
    	final SettingsModelIntegerBounded colOffModel = TableToExcelCellUpdaterNodeModel.createColOffsetSettingsModel();
    	final SettingsModelString cellAddress = TableToExcelCellUpdaterNodeModel.createCellAddressSettingsModel();
    	final SettingsModelBoolean ignoreMissing = TableToExcelCellUpdaterNodeModel.createIgnoreMissingSettingsModel();
    	
        addDialogComponent(new DialogComponentNumber(rowOffModel, "Row offset", 0));
        
        addDialogComponent(new DialogComponentNumber(colOffModel, "Column offset", 0));
        
        
        addDialogComponent(new DialogComponentButtonGroup(
        		cellAddress      		
        		, "", false
        			,new String[] {  "Excel cell addresses, e.g. A5", "Number addresses"}
        			,new String[] {  "excelAddr", "numberAddr"}
        		));
        
        addDialogComponent(new DialogComponentBoolean(ignoreMissing, "Ignore Missing Cells?"));     
    	
    }
}

