package pt.uminho.haslab.mde.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;

public class EVariable {
	
	private static Map<EObject,EVariable> vars = new HashMap<EObject,EVariable>();
	
	
	public static EVariable getVariable(EObject xx) {
		if (vars.get(xx)==null) {
			vars.put(xx, new EVariable(xx));
		}
		return vars.get(xx);
	}
	
	private String name;
	private EObject type;
	
	public EVariable(String name, EObject type) {
		this.name = name;
		this.type = type;
	}
	
	private EVariable(EObject xx) {
		if (xx instanceof VariableDeclaration) {
			this.type = ((VariableDeclaration) xx).getType();
			this.name = ((VariableDeclaration) xx).getName();
		} else{
		EStructuralFeature type = xx.eClass().getEStructuralFeature("type");
		this.type = (EObject) xx.eGet(type);
		EStructuralFeature name = xx.eClass().getEStructuralFeature("name");
		if (name == null)
			name = xx.eClass().getEStructuralFeature("varName");
		this.name = (String) xx.eGet(name);
		}
	}

	public String getName() {
		return name;
	}
	public EObject getType() {
		return type;
	}
}
