package pt.uminho.haslab.echo.consistency;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;

public class Variable {
	
	private static Map<EObject,Variable> vars = new HashMap<EObject,Variable>();
	
	
	public static Variable getVariable(EObject xx) {
		if (vars.get(xx)==null) {
			vars.put(xx, new Variable(xx));
		}
		return vars.get(xx);
	}
	
	private String name;
	private EObject type;
	
	public Variable(String name, EObject type) {
		this.name = name;
		this.type = type;
	}
	
	private Variable(EObject xx) {
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
