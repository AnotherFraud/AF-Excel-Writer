package org.AF.ExcelUtilities.WriteToExcelTemplateV2;

import org.knime.core.node.NodeView;


/**
 * This is an example implementation of the node view of the
 * "WriteToExcelTemplateV2" node.
 * 
 * As this example node does not have a view, this is just an empty stub of the 
 * NodeView class which not providing a real view pane.
 *
 * @author Another Fraud
 */
public class WriteToExcelTemplateV2NodeView extends NodeView<WriteToExcelTemplateV2NodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link WriteToExcelTemplateV2NodeModel})
     */
    protected WriteToExcelTemplateV2NodeView(final WriteToExcelTemplateV2NodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        WriteToExcelTemplateV2NodeModel nodeModel = 
            (WriteToExcelTemplateV2NodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

