package org.AF.ExecuteShellScript;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;


import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.port.PortObjectSpec;




public final class DialogComponentScriptEnvironmentPreferences extends DialogComponent {    
	private final JPanel contentPanel = new JPanel();
	private final JTable table;
	
	
	

	

    /**
     * Constructor puts a checkbox with the specified label into the panel.
     *
     * @param booleanModel an already created settings model
     * @param label the label for checkbox.
     */
    public DialogComponentScriptEnvironmentPreferences(final SettingsModelScriptEnvironmentSettings foxModel,
            final String label) {
        super(foxModel);

        table = tablesetup(foxModel.getSettings());

        initLayout();
        updateComponent();
        
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateComponent() {
        // only update component if values are off
        final SettingsModelScriptEnvironmentSettings model = (SettingsModelScriptEnvironmentSettings)getModel();
        setEnabledComponents(model.isEnabled());
        
      
        
        PreferencesTableModel prefTableModel = (PreferencesTableModel) table.getModel();
        

        if (!prefTableModel.getPrefs().equals(model.getSettings())) {
        	prefTableModel.setData(model.getSettings());
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsBeforeSave()
            throws InvalidSettingsException {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs)
            throws NotConfigurableException {
        // we're always good.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledComponents(final boolean enabled) {
    	table.setEnabled(enabled);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(final String text) {
        table.setToolTipText(text);
    }
    
    
    
    private final void initLayout() {
    	
    	final JPanel panel = getComponentPanel();
   	 
    	panel.setBounds(100, 100, 900, 900);
    	//panel.setLayout(new BorderLayout());
		panel.add(contentPanel, BorderLayout.CENTER);

		
		
		{
			JPanel buttonPane = new JPanel();
			contentPanel.add(buttonPane);
			{
				JButton addButton = new JButton("Add Preference");
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PreferencesTableModel model = (PreferencesTableModel) table.getModel();
					      model.addRow("<empty>", "<empty>");	
					}
				});
				addButton.setActionCommand("AddParameter");
				buttonPane.add(addButton);
			}
			{
				JButton removeButton = new JButton("Remove Preference");
				removeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PreferencesTableModel model = (PreferencesTableModel) table.getModel();
						model.removeRows(table.getSelectedRows());
					}
				});			
				
				
				
				removeButton.setActionCommand("RemoveParameter");
				buttonPane.add(removeButton);
			}
		}
		
		
		
	    JScrollPane js = new JScrollPane(table);
	    js.setPreferredSize(new Dimension(600, 600));
	    
	    
	    //contentPanel.add(table);
		contentPanel.add(js);
		

		
		
		

	}
	
	private JTable tablesetup(List<ScriptEnvironmentPreferences> list)
	{
		
		JTable table = new JTable();
		table.setLocation(0, 31);
		table.setSize(600, 600);

		table.setPreferredScrollableViewportSize(new Dimension(600, 1200));
		//table.setAutoscrolls(true);
        //table.setFillsViewportHeight(true);
 		table.setModel(new PreferencesTableModel(list));

		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(400);

		
		//table.setPreferredScrollableViewportSize(new Dimension(300, 200));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		 

        return table;
	}
	
	
	

	  
	  
	   class PreferencesTableModel extends AbstractTableModel {
		   

		private static final long serialVersionUID = -5640093380665121533L;
		
		
			private String[] columnNames = {"Config Name", "Value"};
			
			private List<ScriptEnvironmentPreferences> pref = new ArrayList<ScriptEnvironmentPreferences>();
			
			
			
		    public PreferencesTableModel(List<ScriptEnvironmentPreferences> initalPrefs) {
		    	
		    	
		    	this.pref = initalPrefs;
		    	fireTableDataChanged();

		    	
		     }

	        public int getColumnCount() {
	            return columnNames.length;
	        }

	        public int getRowCount() {
	            return pref.size();
	        }

	        public String getColumnName(int col) {
	            return columnNames[col];
	        }

	        public Object getValueAt(int row, int col) {
	        	return pref.get(row).getColumnAt(col);
	        }
	        
	        public String[][] getStringData() {

	        	ScriptEnvironmentPreferences[] prefs = new ScriptEnvironmentPreferences[pref.size()];
	        	prefs = pref.toArray(prefs);

	        	String[][] stringPref = new String[2][prefs.length];
	        	
	           int i = 0;
        	   for (ScriptEnvironmentPreferences pref: prefs) {
        		   stringPref[0][i] =  pref.getColumnAt(0);
        		   stringPref[1][i] =  pref.getColumnAt(0);
        		   i++;
        	      }
	        	
	            return stringPref;
	        }
	        
	        
	        public List<ScriptEnvironmentPreferences> getPrefs() {
	            return this.pref;
	        }
	        
	        
	        
	        public void setStringData(String[][] newData)
	        {
	        	
	        	
	        	pref = new ArrayList<ScriptEnvironmentPreferences>();
	        	
	        	
	            for (int i = 0; i < newData[0].length; i++) {
	            	pref.add(new ScriptEnvironmentPreferences(newData[0][i],newData[1][i]));
	            }
	            fireTableDataChanged();
	        }

	        
	        public void setData(List<ScriptEnvironmentPreferences> newPref)
	        {
	        	
	        	this.pref = newPref;
	            fireTableDataChanged();
	        }	        

	        /*
	         * JTable uses this method to determine the default renderer/
	         * editor for each cell.  If we didn't implement this method,
	         * then the last column would contain text ("true"/"false"),
	         * rather than a check box.
	         */
			public Class<? extends Object> getColumnClass(int c) {
	            return getValueAt(0, c).getClass();
	        }

	        /*
	         * Don't need to implement this method unless your table's
	         * editable.
	         */
	        public boolean isCellEditable(int row, int col) {
	        	return true;
	        }
	        
	        /*
	         * Don't need to implement this method unless your table's
	         * data can change.
	         */
	        public void setValueAt(Object value, int row, int col) {

	            pref.get(row).setColumnAt(col, (String) value);
	            fireTableCellUpdated(row, col);
	        }
	        
	        
	        public void addRow(String name, String value ) {
	        	pref.add(new ScriptEnvironmentPreferences(name,value));
	        	fireTableRowsInserted(pref.size()-1, pref.size());
	        }
	        
	        public void removeRows(int[] rows ) {
	        	if (rows.length > 0)
	        	{
	        	int i = 0;
	            for (int row : rows) {
	            	pref.remove(row-i);
	            	fireTableRowsDeleted(row-i, row-i);
	            	i++;
	            }
	        	}
	            
	        }	        
	        
	    }
}