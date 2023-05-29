package org.AF.ExcelUtilities.WriteToExcelTemplateV2;

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
 * "WriteToExcelTemplateV2" node.
 *
 * @author Another Fraud
 */
public class WriteToExcelTemplateV2NodeFactory extends ConfigurableNodeFactory<WriteToExcelTemplateV2NodeModel>{

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
    public WriteToExcelTemplateV2NodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
		// Create and return a new node model.
        return new WriteToExcelTemplateV2NodeModel(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
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
    public NodeView<WriteToExcelTemplateV2NodeModel> createNodeView(final int viewIndex,
            final WriteToExcelTemplateV2NodeModel nodeModel) {
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
        return new WriteToExcelTemplateV2NodeDialog(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
    }

}
