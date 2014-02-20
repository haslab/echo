package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Echo representation of a variable.
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
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

	private EVariable(EObject var) {
		// standard EMF variable
		if (var instanceof VariableDeclaration) {
			this.type = ((VariableDeclaration) var).getType();
			this.name = ((VariableDeclaration) var).getName();
		} 
		// ATL variable
		else{
			EStructuralFeature type = var.eClass().getEStructuralFeature("type");
			this.type = (EObject) var.eGet(type);
			EStructuralFeature name = var.eClass().getEStructuralFeature("name");
			if (name == null)
				name = var.eClass().getEStructuralFeature("varName");
			this.name = (String) var.eGet(name);
		}
	}

	public String getName() {
		return name;
	}
	public EClass getType() throws ErrorParser, ErrorUnsupported {
		String stype = null;
		String metamodelURI = null;
		if (type instanceof Type) {
			stype = ((Type) type).getName();
			metamodelURI = EcoreUtil.getURI(((Type) type).getPackage()).path().replace(".oclas", "").replace("resource/", "");
		}
		else {
			// for ATL
			stype = (String) type.eGet(type.eClass().getEStructuralFeature(
					"name"));
			EObject aux = (EObject) type.eGet(type.eClass()
					.getEStructuralFeature("model"));
			metamodelURI = EATLTransformation.metamodeluris.get(aux
					.eGet(aux.eClass().getEStructuralFeature("name")));
		}
		
		EMetamodel metamodel = MDEManager.getInstance().getMetamodel(metamodelURI, false);
		return (EClass) metamodel.getEObject().getEClassifier(stype);

	}
}
