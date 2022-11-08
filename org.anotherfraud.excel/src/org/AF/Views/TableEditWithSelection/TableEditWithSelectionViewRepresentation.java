package org.AF.Views.TableEditWithSelection;

import java.util.Random;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;



@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TableEditWithSelectionViewRepresentation extends JSONViewContent {

	public final int pseudoIdentifier = (new Random()).nextInt();

	public void saveToNodeSettings(NodeSettingsWO settings) {
	}

	public void loadFromNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		return false; // maybe add other criteria here
	}

	@Override
	public int hashCode() {
		return pseudoIdentifier;
	}
}