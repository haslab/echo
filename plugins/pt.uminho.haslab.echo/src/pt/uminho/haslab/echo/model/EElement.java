package pt.uminho.haslab.echo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class EElement extends EValue {
	private Map<EStructuralFeature,EProperty> properties = new HashMap<EStructuralFeature,EProperty>();
	private List<EElement> children = new ArrayList<EElement>();
	private EElement parent;
	public final EClass type;

	public EElement(EClass eclass) {
		type = eclass;
	}

	public void addValue(EReference reference, EElement value) {
		EProperty prop = properties.get(reference);
		if (prop == null) {
			prop = new EProperty(reference);
			properties.put(reference,prop);
		}
		prop.addValue(value);
		
		if (reference.isContainment()) {
			children.add(value);
			value.addParent(this);
		}
		
	}	
	
	public void addValue(EStructuralFeature feature, Boolean value) {
		EBoolean bool = new EBoolean(value);
		EProperty prop = properties.get(feature);
		if (prop == null) {
			prop = new EProperty(feature);
			properties.put(feature,prop);
		}
		prop.addValue(bool);
	}	
	
	
	public void addValue(EStructuralFeature feature, String value) {
		EString string = new EString(value);
		EProperty prop = properties.get(feature);
		if (prop == null) {
			prop = new EProperty(feature);
			properties.put(feature,prop);
		}
		prop.addValue(string);
	}	
	
	public void addValue(EStructuralFeature feature, Integer value) {
		EInteger integer = new EInteger(value);
		EProperty prop = properties.get(feature);
		if (prop == null) {
			prop = new EProperty(feature);
			properties.put(feature,prop);
		}
		prop.addValue(integer);
	}	
	
	public void addValue(EStructuralFeature feature, org.eclipse.emf.ecore.EEnumLiteral value) {
		EEnumLiteral literal = new EEnumLiteral(value);
		EProperty prop = properties.get(feature);
		if (prop == null) {
			prop = new EProperty(feature);
			properties.put(feature,prop);
		}
		prop.addValue(literal);
	}
	
	private void addParent(EElement ref) {
		parent = ref;
	}	
	
	public String toString() {
		StringBuilder s = new StringBuilder(type.getName());
		s.append("[");
		for (EProperty p : properties.values()) {
			s.append(p.toString());
			s.append(";");
		}
		s.append("] : ");
		
		for (EElement e : children) {
			s.append(e.toString());
			s.append(";");
		}

		return s.toString();
	}

	@Override
	public Object getValue() {
		return type.getClass();
	}

	public Collection<EProperty> getProperties() {
		return properties.values();
	}
	
}