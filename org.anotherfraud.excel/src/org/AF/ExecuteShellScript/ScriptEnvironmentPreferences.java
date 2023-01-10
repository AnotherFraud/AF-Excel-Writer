package org.AF.ExecuteShellScript;
import java.io.Serializable;

public class ScriptEnvironmentPreferences implements Serializable{

	private static final long serialVersionUID = -3700835944265892265L;
	
	private String[] preferences;
	
	
	public ScriptEnvironmentPreferences(String name, String value)
	{
		this.preferences = new String[] {name,value};
	}
	
	
	public String[] getPreferenceName() {
		return preferences;
	}

	public void setPreferenceName(String[] preferenceName) {
		preferences = preferenceName;
	}

	public String getColumnAt(int i) {
			return preferences[i];
	}
	
	
	public void setColumnAt(int i, String value) {
		preferences[i] = value;
	}	
}
