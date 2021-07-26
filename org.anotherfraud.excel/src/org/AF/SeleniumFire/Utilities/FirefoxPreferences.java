package org.AF.SeleniumFire.Utilities;
import java.io.Serializable;

public class FirefoxPreferences implements Serializable{

	private static final long serialVersionUID = -3700830944265892265L;
	
	private String[] preferences;
	
	
	public FirefoxPreferences(String name, String value, String type)
	{
		this.preferences = new String[] {name,value,type};
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
