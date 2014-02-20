package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Echo representation of a model property.
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EProperty {
	public final EStructuralFeature feature;
	private List<EValue> values = new ArrayList<EValue>();

	public EProperty(EStructuralFeature feature) {
		this.feature = feature;
	}

	public void addValue(EValue value) {
		values.add(value);
	}

	@Override
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