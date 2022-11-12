package org.AF.ExcelUtilities.WriteToExcelTemplateWithPath;


import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.filehandling.core.defaultnodesettings.EnumConfig;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.FileOverwritePolicy;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filtermode.SettingsModelFilterMode.FilterMode;



public class WriteToExcelTemplateWithPathConfig {

	

    private static final String CFG_SRC_FILE_CHOOSER = "src_file_selection";
    private static final String CFG_DEST_FILE_CHOOSER = "dest_file_selection";
    
    private final SettingsModelReaderFileChooser m_srcFileChooser;

    private final SettingsModelWriterFileChooser m_destFileChooser;
    
    
    WriteToExcelTemplateWithPathConfig(final PortsConfiguration portsCfg) {
        m_srcFileChooser = new SettingsModelReaderFileChooser(CFG_SRC_FILE_CHOOSER, portsCfg,
        		WriteToExcelTemplateWithPathNodeFactory.FS_CONNECT_GRP_ID
        		,EnumConfig.create(FilterMode.FILE));

        m_destFileChooser = new SettingsModelWriterFileChooser(CFG_DEST_FILE_CHOOSER, portsCfg,
        		WriteToExcelTemplateWithPathNodeFactory.FS_CONNECT_GRP_ID, EnumConfig.create(FilterMode.FILE),
        		EnumConfig.create(FileOverwritePolicy.FAIL, FileOverwritePolicy.OVERWRITE),
	            new String[] { ".xlsx", ".xls", ".xlsm" });
        m_destFileChooser.setEnabled(false);	

		
    }
    
    
    void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_srcFileChooser.validateSettings(settings);
        m_destFileChooser.validateSettings(settings);

    }


    /**
     * Loads the {@link NodeSettingsRO} in the {@link NodeModel}.
     *
     * @param settings the {@link NodeSettingsRO} to be loaded
     * @throws InvalidSettingsException - If loading the settings failed
     */
    void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_srcFileChooser.loadSettingsFrom(settings);
        m_destFileChooser.loadSettingsFrom(settings);
    }



    /**
     * Saves the {@link NodeSettingsWO} in the {@link NodeModel}.
     *
     * @param settings the {@link NodeSettingsWO} to save to
     */
    void saveSettingsForModel(final NodeSettingsWO settings) {
        m_srcFileChooser.saveSettingsTo(settings);
        m_destFileChooser.saveSettingsTo(settings);
    }



    /**
     * Returns the {@link SettingsModelWriterFileChooser}.
     *
     * @return the {@link SettingsModelWriterFileChooser}
     */
    SettingsModelReaderFileChooser getSrcFileChooserModel() {
        return m_srcFileChooser;
    }

    SettingsModelWriterFileChooser getDestFileChooserModel() {
        return m_destFileChooser;
    }






    
    
}
