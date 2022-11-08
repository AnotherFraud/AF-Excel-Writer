package org.AF.Views.TableEditWithSelection;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 * This is an example implementation of the node model of the
 * "TableEditWithSelection" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author AnotherFraud
 */
public class TableEditWithSelectionNodeModel extends AbstractWizardNodeModel<TableEditWithSelectionViewRepresentation, TableEditWithSelectionViewValue> {
    
		// Input and output port types
		private static final PortType[] IN_TYPES = {BufferedDataTable.TYPE};
		private static final PortType[] OUT_TYPES = { FlowVariablePortObject.TYPE};
		
		private static final NodeLogger LOGGER = NodeLogger.getLogger(TableEditWithSelectionNodeModel.class);
		
		
		
		
		
		static final String valColName = "formulaColName";

		
		
		static SettingsModelString createValColNameStringSettingsModel() {
			SettingsModelString coof = new SettingsModelString(valColName,null);
			coof.setEnabled(true);
			return coof;				
		}	

		
		private final SettingsModelString m_valColName = createValColNameStringSettingsModel();
		
		
		
		
		
	/**
	 * Constructor for the node model.
	 */
	protected TableEditWithSelectionNodeModel() {
		super(IN_TYPES, OUT_TYPES, "table_Edit_With_Selection");
	}

	@Override
	public TableEditWithSelectionViewRepresentation createEmptyViewRepresentation() {
		return new TableEditWithSelectionViewRepresentation();
	}

	@Override
	public TableEditWithSelectionViewValue createEmptyViewValue() {
		return new TableEditWithSelectionViewValue();
	}

	@Override
	public String getJavascriptObjectID() {
		return "table_Edit_With_Selection";
	}

	@Override
	public boolean isHideInWizard() {
		return false;
	}

	@Override
	public void setHideInWizard(boolean hide) {
	}

	@Override
	public ValidationError validateViewValue(TableEditWithSelectionViewValue viewContent) {
		return null;
	}

	@Override
	public void saveCurrentValue(NodeSettingsWO content) {
	}

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] {null};
	}

	
	@Override
	protected PortObject[] performExecute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		final String firstName;
		final String lastName;

		synchronized (getLock()) {
			TableEditWithSelectionViewValue value = getViewValue();
			firstName = value.firstName;
			lastName = value.lastName;
		}
		
		

		pushFlowVariableString("firstName", firstName);
		pushFlowVariableString("lastName", lastName);

		// The FlowVariablePortObject ports are a mockup. They are not actually
		// necessary as the flow
		// variables are shared across the workflow.
		return new PortObject[] { FlowVariablePortObject.INSTANCE };
	}

	@Override
	protected void performReset() {
	}

	@Override
	protected void useCurrentValueAsDefault() {
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		m_valColName.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		m_valColName.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		m_valColName.loadSettingsFrom(settings);
	}
}

