package pt.uminho.haslab.echo.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

public class EProperty {
	public final EStructuralFeature feature;
	private List<EValue> values = new ArrayList<EValue>();
	
	public EProperty(EStructuralFeature feature) {
		this.feature = feature;
	}
	
	public void addValue(EValue value) {
		values.add(value);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder(feature.getName());
		s.append(" : [");
		for (EValue value : values)
			s.append(value.getValue());
		s.append(" ]");
		return s.toString();		
	}
	
	public List<EValue> getValues() {
		return values;
	}

	
}