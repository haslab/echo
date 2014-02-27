package pt.uminho.haslab.mde.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;

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
	private String metamodelURI;
	private EObject type;

	private EVariable(EObject var) {
		// standard EMF variable
		if (var instanceof VariableDeclaration) {
			this.type = ((VariableDeclaration) var).getType();
			this.name = ((VariableDeclaration) var).getName();
			this.metamodelURI = EcoreUtil.getURI(((Type) type).getPackage()).path().replace(".oclas", "").replace("resource/", "");
		} 
		// ATL variable
		else{
			EObject aux = (EObject) type.eGet(type.eClass()
					.getEStructuralFeature("model"));
			metamodelURI = EATLTransformation.metamodeluris.get(aux
					.eGet(aux.eClass().getEStructuralFeature("name")));
			EStructuralFeature type = var.eClass().getEStructuralFeature("type");
			this.type = (EObject) var.eGet(type);
			EStructuralFeature name = var.eClass().getEStructuralFeature("name");
			if (name == null)
				name = var.eClass().getEStructuralFeature("varName");
			this.name = (String) var.eGet(name);
		}
		EchoReporter.getInstance().debug("** Created var: "+name+"::"+metamodelURI);
	}

	public String getName() {
		return name;
	}
	
	public String getMetamodel() {
		return metamodelURI;
	}
	
	public String getType() throws ErrorParser, ErrorUnsupported {
		String stype = null;
	if (type instanceof Type) {
			stype = ((Type) type).getName();
			
		}
		else {
			// for ATL
			stype = (String) type.eGet(type.eClass().getEStructuralFeature(
					"name"));
		}
		return stype;
	}
}
