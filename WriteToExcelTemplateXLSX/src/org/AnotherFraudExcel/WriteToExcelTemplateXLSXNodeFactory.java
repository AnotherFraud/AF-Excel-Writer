package org.AnotherFraudExcel;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * @author Another Fraud
 */


public class WriteToExcelTemplateXLSXNodeFactory 
        extends NodeFactory<WriteToExcelTemplateXLSXNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public WriteToExcelTemplateXLSXNodeModel createNodeModel() {
		// Create and return a new node model.
        return new WriteToExcelTemplateXLSXNodeModel();
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
    public NodeView<WriteToExcelTemplateXLSXNodeModel> createNodeView(final int viewIndex,
            final WriteToExcelTemplateXLSXNodeModel nodeModel) {
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
		// This node has a dialog, hence we create and return it here. Also see "hasDialog()".
        return new WriteToExcelTemplateXLSXNodeDialog();
    }

}

