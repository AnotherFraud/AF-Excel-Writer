package org.AF.ExcelUtilities.ExcelPasswordManager;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;


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

/**
 * This is an example implementation of the node factory of the
 * "ExcelPasswordManager" node.
 *
 * @author AnotherFraudExcel
 */
public class ExcelPasswordManagerNodeFactory 
        extends NodeFactory<ExcelPasswordManagerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ExcelPasswordManagerNodeModel createNodeModel() {
		// Create and return a new node model.
        return new ExcelPasswordManagerNodeModel();
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
    public NodeView<ExcelPasswordManagerNodeModel> createNodeView(final int viewIndex,
            final ExcelPasswordManagerNodeModel nodeModel) {
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
        return new ExcelPasswordManagerNodeDialog();
    }

}

