package org.AF.SharePointUtilities.Conn.SPConnListFiles;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * This is an example implementation of the node factory of the
 * "SPConnListFiles" node.
 *
 * @author AnotherFraud
 */
public class SPConnListFilesNodeFactory 
        extends NodeFactory<SPConnListFilesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SPConnListFilesNodeModel createNodeModel() {
		// Create and return a new node model.
        return new SPConnListFilesNodeModel();
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
    public NodeView<SPConnListFilesNodeModel> createNodeView(final int viewIndex,
            final SPConnListFilesNodeModel nodeModel) {
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
    public NodeDialogPane createNodeDialogPane() {
		// This example node has a dialog, hence we create and return it here. Also see "hasDialog()".
        return new SPConnListFilesNodeDialog();
    }

}

