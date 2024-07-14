package org.AF.ExcelUtilities.WriteToExcelTemplateStreaming;

import java.util.Optional;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.filehandling.core.port.FileSystemPortObject;


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


public class WriteToExcelTemplateStreamingNodeFactory extends ConfigurableNodeFactory<WriteToExcelTemplateStreamingNodeModel>{

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
    public WriteToExcelTemplateStreamingNodeModel createNodeModel(final NodeCreationConfiguration creationConfig) {
		// Create and return a new node model.
        return new WriteToExcelTemplateStreamingNodeModel(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
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
    public NodeView<WriteToExcelTemplateStreamingNodeModel> createNodeView(final int viewIndex,
            final WriteToExcelTemplateStreamingNodeModel nodeModel) {
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
        return new WriteToExcelTemplateStreamingNodeDialog(creationConfig.getPortConfig().orElseThrow(IllegalStateException::new));
    }

}
