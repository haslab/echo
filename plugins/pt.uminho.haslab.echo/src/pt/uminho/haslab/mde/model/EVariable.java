package pt.uminho.haslab.mde.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.transformation.atl.EATLModelParameter;

/**
 * Echo representation of a variable.
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EVariable {

	private static Map<EObject,EVariable> vars = new HashMap<EObject,EVariable>();

	public static EVariable getVariable(VariableDeclaration xx) {
		if (vars.get(xx)==null) {
			try {
				vars.put(xx, new EVariable(xx));
			} catch (EErrorUnsupported | EErrorParser e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return vars.get(xx);
	}
	
	public static EVariable getVariable(EObject xx) {
		if (vars.get(xx)==null) {
			try {
				vars.put(xx, new EVariable(xx));
			} catch (EErrorUnsupported | EErrorParser e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return vars.get(xx);
	}
	
	public static EVariable getVariable(EObject xx, String classID) {
		try {
			vars.put(xx, new EVariable(xx,classID));
		} catch (EErrorUnsupported | EErrorParser e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vars.get(xx);
	}


	private String name;
	private String metamodelURI;
	private EObject type;

	private EVariable(VariableDeclaration var) throws EErrorUnsupported, EErrorParser {
		// standard EMF variable
		this.type = ((VariableDeclaration) var).getType();
		this.name = ((VariableDeclaration) var).getName();
		this.metamodelURI = EcoreUtil.getURI(((Type) type).getPackage()).path().replace(".oclas", "").replace("resource/", "");	
	}
	
	private EVariable(EObject var) throws EErrorUnsupported, EErrorParser {
		// ATL variable
		// InPatternElement or OutPatternElement
		if (var.eClass().getName().equals("SimpleInPatternElement") || var.eClass().getName().equals("SimpleOutPatternElement")) {
			for (Object x : var.eContents()) {
				if (((EObject) x).eClass().getName().equals("OclModelElement")) {
					type = (EObject) x;
				}
			}
		}
		else if (var.eClass().getName().equals("VariableDeclaration") || var.eClass().getName().equals("Iterator")) {
			EStructuralFeature t = var.eClass().getEStructuralFeature("type");
			type = (EObject) var.eGet(t);
			if (type == null) {
				EchoReporter.getInstance().debug("Null type: "+this.name);
				EchoReporter.getInstance().debug("And: "+var.eContents());
				EchoReporter.getInstance().debug("And: "+var.eAllContents());
				EchoReporter.getInstance().debug("And: "+var);
			}
		}
		else throw new EErrorUnsupported(EErrorUnsupported.ATL,"Var type: "+var.eClass().getName(),Task.TRANSLATE_TRANSFORMATION);
		
		if (type != null) {
			EObject mdlref = type.eCrossReferences().get(0);
			EObject metamdlref = ((EList<EObject>) mdlref.eGet(mdlref.eClass().getEStructuralFeature("model"))).get(0);
			String mname = (String) metamdlref.eGet(metamdlref.eClass().getEStructuralFeature("name"));
			metamodelURI = EATLModelParameter.get(mname).getMetamodel().getURI();
		}
		
		EStructuralFeature name = var.eClass().getEStructuralFeature("name");
		if (name == null)
			name = var.eClass().getEStructuralFeature("varName");
		this.name = (String) var.eGet(name);
//		EchoReporter.getInstance().debug("** Created var: "+name+"::"+metamodelURI);
	}

	public EVariable(EObject var, String classID) throws EErrorUnsupported, EErrorParser {
		// ATL variable
		// InPatternElement or OutPatternElement
		String metamodelID = EchoHelper.getMetamodelIDfromLabel(classID);
		EMetamodel metamodel = MDEManager.getInstance().getMetamodelID(metamodelID);
		String className = EchoHelper.getClassifierName(classID);
		type = metamodel.getEObject().getEClassifier(className);

		metamodelURI = metamodel.getURI();
		
		EStructuralFeature name = var.eClass().getEStructuralFeature("name");
		if (name == null)
			name = var.eClass().getEStructuralFeature("varName");
		this.name = (String) var.eGet(name);	
		
		EchoReporter.getInstance().debug("** Created var: "+this.name+":"+type+"::"+metamodelURI);

	}

	public String getName() {
		return name;
	}
	
	public String getMetamodel() {
		return metamodelURI;
	}
	
	public String getType() throws EErrorParser, EErrorUnsupported {
		String stype = null;
		if (type == null)
			return null;
		else if (type instanceof Type)
			stype = ((Type) type).getName();
		else {
			// for ATL
			stype = (String) type.eGet(type.eClass().getEStructuralFeature(
					"name"));
		}
		return stype;
	}
}
