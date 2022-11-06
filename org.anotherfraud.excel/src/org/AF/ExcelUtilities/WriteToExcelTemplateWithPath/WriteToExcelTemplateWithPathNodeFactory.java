package org.AF.ExcelUtilities.WriteToExcelTemplateWithPath;

import java.util.Optional;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.filehandling.core.port.FileSystemPortObject;

/**
 * This is an example implementation of the node factory of the
 * "WriteToExcelTemplateWithPath" node.
 *
 * @author Another Fraud
 */
public class WriteToExcelTemplateWithPathNodeFactory extends ConfigurableNodeFactory<WriteToExcelTemplateWithPathNodeModel>
 {

	
	
    /** The file system ports group id. */
    static final String FS_CONNECT_GRP_ID = "File System Connection";
    static final String DATA_GRP_ID = "Data Port";

    @Override
    protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
        final var b = new PortsConfigurationBuilder();
        b.addFixedInputPortGroup(DATA_GRP_ID, new PortType[]{BufferedDataTable.TYPE});
        b.addOptionalInputPortGroup(FS_CONNECT_GRP_ID, FileSystemPortObject.TYPE);
        
        return Optional.of(b);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public WriteToExcelTemplateWithPathNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
		// Create and return a new node model.
        return new WriteToExcelTemplateWithPathNodeModel(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
		// The number of views the node should have, in this cases there is none.
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<WriteToExcelTemplateWithPathNodeModel> createNodeView(final int viewIndex,
            final WriteToExcelTemplateWithPathNodeModel nodeModel) {
		// We return null as this example node does not provide a view. Also see "getNrNodeViews()".
		return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
		// Indication whether the node has a dialog or not.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane(final NodeCreationConfiguration creationConfig) {
		// This example node has a dialog, hence we create and return it here. Also see "hasDialog()".
        return new WriteToExcelTemplateWithPathNodeDialog(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
    }

}

