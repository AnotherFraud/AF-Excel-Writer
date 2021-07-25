package org.AF.SeleniumFire.Utilities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;

public class SettingsModelFirefoxSettings extends SettingsModel {
    
    private List<FirefoxPreferences> m_value = new ArrayList<FirefoxPreferences>();
    
    

    private final String m_configName;

    /**
     * Creates a new object holding a string value.
     *
     * @param configName the identifier the value is stored with in the
     *            {@link org.knime.core.node.NodeSettings} object
     * @param defaultValue the initial value
     */
    public SettingsModelFirefoxSettings(final String configName,
            final List<FirefoxPreferences> defaultValue) {
        if ((configName == null) || "".equals(configName)) {
            throw new IllegalArgumentException("The configName must be a "
                    + "non-empty string");
        }
        m_value = defaultValue;
        m_configName = configName;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelFirefoxSettings createClone() {
        return new SettingsModelFirefoxSettings(m_configName, m_value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModelTypeID() {
        return "SMID_firefoxsettings";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getConfigName() {
        return m_configName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        try {
        	loadValues(settings);

        } catch (IllegalArgumentException iae) {
            // if the argument is not accepted: keep the old value.
        } catch (InvalidSettingsException e) {
			e.printStackTrace();
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        saveSettingsForModel(settings);
    }

    
   
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	
        settings.getStringArray(m_configName + "_name");
        
        
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            // no default value, throw an exception instead
            
        	
 
        	loadValues(settings);
        	

        } catch (IllegalArgumentException iae) {
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    private void loadValues(NodeSettingsRO settings) throws InvalidSettingsException {
    	
    	String[] stringPref_name = settings.getStringArray(m_configName + "_name");
    	String[] stringPref_val = settings.getStringArray(m_configName + "_val");
    	String[] stringPref_type = settings.getStringArray(m_configName + "_type");
    	
    	
    	List<FirefoxPreferences> prefs = new ArrayList<FirefoxPreferences>();
    	
    	
    	for(int i = 0; i< stringPref_name.length; i++){
    		prefs.add(new FirefoxPreferences(stringPref_name[i], stringPref_val[i], stringPref_type[i]));		
    		}
    	
    	
    	if (!m_value.equals(prefs))
    	{
    		m_value = prefs;
    		notifyChangeListeners();
    	}   
		
	}

	/**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {

    	String[] stringPref_name = new String[m_value.size()];
    	String[] stringPref_val = new String[m_value.size()];
    	String[] stringPref_type = new String[m_value.size()];
    	
    	int i = 0;
    	for (FirefoxPreferences pref : m_value) {
  		   stringPref_name[i] =  pref.getColumnAt(0);
  		   stringPref_val[i] =  pref.getColumnAt(1);
  		   stringPref_type[i] =  pref.getColumnAt(2);
  		 i++;

    	}
  
    	
        settings.addStringArray(m_configName + "_name", stringPref_name);
        settings.addStringArray(m_configName + "_val", stringPref_val);
        settings.addStringArray(m_configName + "_type", stringPref_type);   
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

	public List<FirefoxPreferences> getSettings() {
		return m_value;
	}
    
}
