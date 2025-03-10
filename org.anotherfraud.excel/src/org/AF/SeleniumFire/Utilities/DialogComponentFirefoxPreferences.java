package org.AF.SeleniumFire.Utilities;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.port.PortObjectSpec;




public final class DialogComponentFirefoxPreferences extends DialogComponent {    
	private final JPanel contentPanel = new JPanel();
	private final JTable table;
	
	

    /**
     * Constructor puts a checkbox with the specified label into the panel.
     *
     * @param booleanModel an already created settings model
     * @param label the label for checkbox.
     */
    public DialogComponentFirefoxPreferences(final SettingsModelFirefoxSettings foxModel,
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
        final SettingsModelFirefoxSettings model = (SettingsModelFirefoxSettings)getModel();
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
    	 
    	panel.setBounds(100, 100, 450, 300);
    	panel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(contentPanel, BorderLayout.CENTER);

		
		
		
		
		contentPanel.setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 0, 424, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			{
				JButton addButton = new JButton("Add Preference");
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PreferencesTableModel model = (PreferencesTableModel) table.getModel();
					      model.addRow("<empty>", "<empty>", "<empty>");	
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
		
		contentPanel.add(table);

	}
	
	private JTable tablesetup(List<FirefoxPreferences> list)
	{
		
		JTable table = new JTable();
		table.setLocation(0, 31);
		table.setSize(424, 360);
		//table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
 		table.setModel(new PreferencesTableModel(list));

		table.getColumnModel().getColumn(0).setPreferredWidth(157);
		table.getColumnModel().getColumn(1).setPreferredWidth(157);
		table.getColumnModel().getColumn(2).setPreferredWidth(30);
		
		table.getColumnModel().getColumn(2).setResizable(false);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		TableColumn typeColumn = table.getColumnModel().getColumn(2);
		 

        //Fiddle with the Sport column's cell editors/renderers.
        setUpComboColumn(table, typeColumn);
        return table;
	}
	
	
	
	  public void setUpComboColumn (JTable table,
              TableColumn comboColumn) {
		  
			//Set up the editor for the cells.
			JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.addItem("integer");
			comboBox.addItem("string");
			comboBox.addItem("boolean");
			comboBox.addItem("argument");
			comboBox.addItem("argument2");
			
			
			
			
			comboColumn.setCellEditor(new DefaultCellEditor(comboBox));
			
			//Set up tool tips for the sport cells.
			DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
			renderer.setToolTipText("Click for combo box");
			comboColumn.setCellRenderer(renderer);
}

	  
	  
	   class PreferencesTableModel extends AbstractTableModel {
		   

		private static final long serialVersionUID = -5640093389665121533L;
		
		
			private String[] columnNames = {"Config Name", "Value", "Type"};
			
			private List<FirefoxPreferences> pref = new ArrayList<FirefoxPreferences>();
			
			
			
		    public PreferencesTableModel(List<FirefoxPreferences> initalPrefs) {
		    	
		    	
		    	this.pref = initalPrefs;
		    	fireTableDataChanged();
		    	
		    	
		    	//this.setData(data);
				//pref.add(new FirefoxPreferences("browser.download.folderList", "2","integer"));
		    	//pref.add(new FirefoxPreferences("browser.link.open_newwindow", "3","integer"));
		    	//pref.add(new FirefoxPreferences("browser.link.open_newwindow.restriction", "0","integer"));
		    	//pref.add(new FirefoxPreferences("browser.helperApps.neverAsk.saveToDisk", "application/msexcel","string"));
		    	//pref.add(new FirefoxPreferences("security.fileuri.strict_origin_policy", "false","string"));	
				//pref.add(new FirefoxPreferences("network.proxy.autoconfig_url", "http://inetprox.inet.cns.fra.dlh.de/lhpproxy.pac","string"));
		    	//pref.add(new FirefoxPreferences("network.proxy.type", "2","integer"));
		    	//pref.add(new FirefoxPreferences("browser.tabs.remote.autostart", "false","boolean"));
		    	//pref.add(new FirefoxPreferences("browser.tabs.remote.autostart.2", "false","boolean"));
		    	//pref.add(new FirefoxPreferences("browser.tabs.remote.warmup.maxTabs","2","integer"));
		    	
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

	        	FirefoxPreferences[] prefs = new FirefoxPreferences[pref.size()];
	        	prefs = pref.toArray(prefs);

	        	String[][] stringPref = new String[3][prefs.length];
	        	
	           int i = 0;
        	   for (FirefoxPreferences pref: prefs) {
        		   stringPref[0][i] =  pref.getColumnAt(0);
        		   stringPref[1][i] =  pref.getColumnAt(0);
        		   stringPref[2][i] =  pref.getColumnAt(0);
        		   i++;
        	      }
	        	
	            return stringPref;
	        }
	        
	        
	        public List<FirefoxPreferences> getPrefs() {
	            return this.pref;
	        }
	        
	        
	        
	        public void setStringData(String[][] newData)
	        {
	        	
	        	
	        	pref = new ArrayList<FirefoxPreferences>();
	        	
	        	
	            for (int i = 0; i < newData[0].length; i++) {
	            	pref.add(new FirefoxPreferences(newData[0][i],newData[1][i],newData[2][i]));
	            }
	            fireTableDataChanged();
	        }

	        
	        public void setData(List<FirefoxPreferences> newPref)
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
	        
	        
	        public void addRow(String name, String value, String type ) {
	        	pref.add(new FirefoxPreferences(name,value,type));
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