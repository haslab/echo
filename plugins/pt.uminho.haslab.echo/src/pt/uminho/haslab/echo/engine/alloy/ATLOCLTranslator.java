package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EErrorType;
import pt.uminho.haslab.echo.EErrorTransform;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;
import pt.uminho.haslab.echo.engine.ast.INode;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.atl.EATLRelation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;

import java.util.ArrayList;
import java.util.List;

public class ATLOCLTranslator {

	protected final ITContext context;

	public ATLOCLTranslator(ITContext context) {
		this.context = context;
	}

	public INode translateAtlOcl(EObject expr) throws EError {
		if (expr.eClass().getName().equals("OperatorCallExp") || 
				expr.eClass().getName().equals("OperationCallExp") ||
				expr.eClass().getName().equals("CollectionOperationCallExp")) {
			return translateAtlOclOperationCall(expr);
		} else if (expr.eClass().getName()
				.equals("NavigationOrAttributeCallExp")) {
			return translateAtlOclAttribute(expr);
		} else if (expr.eClass().getName().equals("VariableExp")) {
			return translateAtlOclVariable(expr);
		} else if (expr.eClass().getName().equals("BooleanExp")) {
			return translateAtlOclBooleanLit(expr);
		} else if (expr.eClass().getName().equals("Binding")) {
			return translateAtlOclBinding(expr);
		} else if (expr.eClass().getName().equals("IfExp")) {
			return translateAtlOclIf(expr);
		} else if (expr.eClass().getName().equals("IteratorExp")) {
			return translateAtlOclIterator(expr);
		} else
			throw new EErrorUnsupported(EErrorUnsupported.OCL,"OCL expression not supported: " + expr
					+ ".",Task.TRANSLATE_OCL);
	}

	private INode translateAtlOclIf(EObject expr) throws EError {
		EStructuralFeature x = expr.eClass().getEStructuralFeature("condition");
		IFormula eif = (IFormula) translateAtlOcl((EObject) expr.eGet(x));
		x = expr.eClass().getEStructuralFeature("thenExpression");
		INode thenExpr = translateAtlOcl((EObject) expr.eGet(x));
		x = expr.eClass().getEStructuralFeature("elseExpression");
		INode elseExpr = translateAtlOcl((EObject) expr.eGet(x));

		if (thenExpr instanceof IExpression && elseExpr instanceof IExpression)
			return eif.thenElse((IExpression) thenExpr,(IExpression) elseExpr);
		else if (thenExpr instanceof IFormula && elseExpr instanceof IFormula)
			return (IFormula) eif.thenElse((IFormula) thenExpr,(IFormula) elseExpr);
		
		throw new EErrorType(EErrorType.EXPR,"Expression: "+expr.getClass(),Task.TRANSLATE_OCL);
	}

	IExpression translateAtlOclVariable(EObject expr) {
		EStructuralFeature x = expr.eClass().getEStructuralFeature(
				"referredVariable");
		EObject vardecl = (EObject) expr.eGet(x);
		EStructuralFeature name = vardecl.eClass().getEStructuralFeature(
				"varName");
		String varname = (String) vardecl.eGet(name);
		context.setCurrentModel(context.getVarModel(varname));
		IExpression res = context.getVar(varname);
		EchoReporter.getInstance().debug("Translated "+varname+" to "+res);
		return res;
	}

	IFormula translateAtlOclBooleanLit(EObject expr) {
		EStructuralFeature symb = expr.eClass().getEStructuralFeature(
				"booleanSymbol");
		if (expr.eGet(symb).toString().equals("true"))
			return Constants.TRUE();
		else
			return Constants.FALSE();
	}

	INode translateAtlOclAttribute(EObject expr) throws EError {
		INode res = null;
		EStructuralFeature source = expr.eClass().getEStructuralFeature(
				"source");
		EObject sourceo = (EObject) expr.eGet(source);

		context.setCurrentModel(null);

		IExpression var = (IExpression) translateAtlOcl(sourceo);

		EStructuralFeature oname = expr.eClass().getEStructuralFeature("name");
		IExpression aux = propertyToField((String) expr.eGet(oname), var);

		String varsig = null;
		try {
			varsig = ((AlloyExpression) var).EXPR.type().toExpr().toString();
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String metamodelID = EchoHelper.getMetamodelIDfromLabel(varsig);
		EMetamodel metamodel = MDEManager.getInstance().getMetamodelID(
				metamodelID);
		EStructuralFeature feature = ((EClass) metamodel.getEObject()
				.getEClassifier(EchoHelper.getClassifierName(varsig)))
				.getEStructuralFeature((String) expr.eGet(oname));
		String nameo = feature.getEType().getName();
		if (nameo.equals("EBoolean"))
			res = var.in(aux);
		else
			res = var.join(aux);
		return res;
	}

	INode translateAtlOclBinding(EObject expr) throws EError {
		INode res = null;

		EStructuralFeature value = expr.eClass().getEStructuralFeature("value");
		INode val = translateAtlOcl((EObject) expr.eGet(value));
		
		
		String valmodel = context.getCurrentModel();
		
		EStructuralFeature source = expr.eClass().getEStructuralFeature(
				"outPatternElement");
		EObject sourceo = (EObject) expr.eGet(source);
		EStructuralFeature name = sourceo.eClass().getEStructuralFeature(
				"varName");
		String varname = (String) sourceo.eGet(name);
		context.setCurrentModel(context.getVarModel(varname));
		IExpression var = context.getVar(varname);

		String varmodel = context.getCurrentModel();

		EStructuralFeature oname = expr.eClass().getEStructuralFeature(
				"propertyName");
		IExpression aux = propertyToField((String) expr.eGet(oname), var);

		if (valmodel != null && !varmodel.equals(valmodel) && val instanceof IExpression) {
			EchoReporter.getInstance().debug(" *** Call implicit trace! "+context.getCallerRel().transformation.callAllRelation(context, (IExpression) val));
			val  = context.getCallerRel().transformation.callAllRelation(context, (IExpression) val);
		}
		String varsig = null;
		try {
			varsig = ((AlloyExpression) var).EXPR.type().toExpr().toString();
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String metamodelID = EchoHelper.getMetamodelIDfromLabel(varsig);
		EMetamodel metamodel = MDEManager.getInstance().getMetamodelID(
				metamodelID);
		EStructuralFeature feature = ((EClass) metamodel.getEObject()
				.getEClassifier(EchoHelper.getClassifierName(varsig)))
				.getEStructuralFeature((String) expr.eGet(oname));
		String nameo = feature.getEType().getName();
		if (nameo.equals("EBoolean"))
			res = ((IFormula) var.join(aux)).iff((IFormula) val);
		else {
			if (((EObject) expr.eGet(value)).eClass().getName().equals("IteratorExp") && ((EObject) expr.eGet(value)).eGet(((EObject) expr.eGet(value)).eClass().getEStructuralFeature("name")).equals("any"))
				res = var.join(aux).in((IExpression) val).and(var.join(aux).one());
			else
				res = var.join(aux).eq((IExpression) val);
		}
		return res;
	}

	INode translateAtlOclOperationCall(EObject expr) throws EError {
		INode res = null;
		EStructuralFeature source = expr.eClass().getEStructuralFeature(
				"source");
		EStructuralFeature operat = expr.eClass().getEStructuralFeature(
				"operationName");
		String operatorname = (String) expr.eGet(operat);
		INode src = translateAtlOcl((EObject) expr.eGet(source));
		EStructuralFeature arguments = expr.eClass().getEStructuralFeature(
				"arguments");
		EList<EObject> argumentso = (EList<EObject>) expr.eGet(arguments);
		if (operatorname.equals("not"))
			res = ((IFormula) src).not();
		else if (operatorname.equals("isEmpty"))
			res = ((IExpression) src).no();
		else if (operatorname.equals("notEmpty"))
			res = ((IExpression) src).some();
		else if (operatorname.equals("size"))
			res = ((IExpression) src).cardinality();
		else if (operatorname.equals("=")) {
			INode aux = translateAtlOcl(argumentso.get(0));
			if (src instanceof IFormula)
				res = ((IFormula) src).iff((IFormula) aux);
			else {
				if (argumentso.get(0).eClass().getName().equals("IteratorExp") && argumentso.get(0).eGet(argumentso.get(0).eClass().getEStructuralFeature("name")).equals("any"))
					res = (((IExpression) src).in((IExpression) aux)).and(((IExpression) src).one());
				else
					res = ((IExpression) src).eq((IExpression) aux); 	
			}
		} else if (operatorname.equals("<>")) {
			INode aux = translateAtlOcl(argumentso.get(0));
			EStructuralFeature type = argumentso.get(0).eClass()
					.getEStructuralFeature("type");
			EObject typeo = (EObject) argumentso.get(0).eGet(type);
			EStructuralFeature name = typeo.eClass().getEStructuralFeature(
					"name");
			String nameo = (String) typeo.eGet(name);
			if (nameo.equals("Boolean"))
				res = ((IFormula) src).iff((IFormula) aux).not();
			else
				res = ((IExpression) src).eq((IExpression) aux).not();
		} else if (operatorname.equals("and"))
			res = ((IFormula) src).and((IFormula) translateAtlOcl(argumentso
					.get(0)));
		else if (operatorname.equals("or"))
			res = ((IFormula) src).or((IFormula) translateAtlOcl(argumentso
					.get(0)));
		else if (operatorname.equals("implies"))
			res = ((IFormula) src).implies((IFormula) translateAtlOcl(argumentso
					.get(0)));
		else if (operatorname.equals("<"))
			res = ((IIntExpression) src)
					.lt((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals(">"))
			res = ((IIntExpression) src)
					.gt((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("<="))
			res = ((IIntExpression) src)
					.lte((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals(">="))
			res = ((IIntExpression) src)
					.gte((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("union"))
			res = ((IExpression) src)
					.union((IExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("including"))
			res = ((IExpression) src)
					.union((IExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("excluding"))
			res = ((IExpression) src)
					.difference((IExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("intersection"))
			res = ((IExpression) src)
					.intersection((IExpression) translateAtlOcl(argumentso
							.get(0)));
		else if (operatorname.equals("includes"))
			res = ((IExpression) translateAtlOcl(argumentso.get(0)))
					.in((IExpression) src);
		else if (operatorname.equals("includesAll"))
			res = ((IExpression) translateAtlOcl(argumentso.get(0)))
					.in((IExpression) src);
		else if (operatorname.equals("excludes"))
			res = ((IExpression) translateAtlOcl(argumentso.get(0)))
					.in((IExpression) src).not();
		else if (operatorname.equals("excludesAll"))
			res = ((IExpression) translateAtlOcl(argumentso.get(0)))
					.in((IExpression) src).not();
		else if (operatorname.equals("oclAsSet")
				|| operatorname.equals("asSet"))
			res = src;
		else if (operatorname.equals("oclIsKindOf"))
			res = ((IExpression) src).in((IExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("oclAsType"))
			res = ((IExpression) src);
		else if (operatorname.equals("+"))
			res = ((IIntExpression) src)
					.plus((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("-")) {
			INode x = translateAtlOcl(argumentso.get(0));
			if (src instanceof IIntExpression && x instanceof IIntExpression)
				res = ((IIntExpression) src).minus((IIntExpression) x);
			else
				res = ((IIntExpression) src).difference((IIntExpression) x);
		}
		else if (operatorname.equals("allInstances"))
			res = src;
		else if (((EATLTransformation) context.getCallerRel().transformation.transformation).getRelation(operatorname) != null) {
			EATLRelation rel = ((EATLTransformation) context.getCallerRel().transformation.transformation).getRelation(operatorname);
			
			// translates variable parameters
			List<IExpression> params = new ArrayList<IExpression>();
			for (EObject arg : argumentso) {
				IExpression param = (IExpression) translateAtlOcl(arg);
				params.add(param);
			}

			// tries to call referred relation
			res = ((ITContext) context).getCallerRel().transformation
					.callRelation(rel,((ITContext) context),params);

			// if it doesn't exist, process it
			if (res == null) {
				((ITContext) context).getCallerRel().newRelation(rel);
				res = ((ITContext) context).getCallerRel().transformation
						.callRelation(rel, ((ITContext) context), params);
			}
			context.setCurrentModel(null);
			EchoReporter.getInstance().debug("Call rule result: "+res);
		}
		else
			throw new EErrorUnsupported(EErrorUnsupported.OCL,"OCL operation not supported: "
					+ expr.toString() + ".",Task.TRANSLATE_OCL);

		return res;
	}
	
	INode translateAtlOclIterator(EObject expr) throws EError {
		INode res = null;
		
		EStructuralFeature source = expr.eClass().getEStructuralFeature(
				"source");
		IExpression src = (IExpression) translateAtlOcl((EObject) expr.eGet(source));
		EStructuralFeature iterator = expr.eClass().getEStructuralFeature("iterators");
		EVariable x = null;
		try {
			x = EVariable.getVariable(((EList<EObject>) expr.eGet(iterator)).get(0),((AlloyExpression) src).EXPR.type().toExpr().toString());
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDecl d = context.getDecl(x, true);
		
		// tries to determine owning model of the iterator variable
		for (String s : context.getVars()) {
			IExpression var = context.getVar(s);
			if (src.hasVar(var) && context.getVarModel(s) != null)
				context.addVar(d, context.getVarModel(s));
			else
				context.addVar(d);
		}

		EStructuralFeature operat = expr.eClass().getEStructuralFeature(
				"name");
		String operatorname = (String) expr.eGet(operat);
		EStructuralFeature arguments = expr.eClass().getEStructuralFeature(
				"body");
		INode body = translateAtlOcl((EObject) expr.eGet(arguments));
		IFormula aux;
		
		if (operatorname.equals("forAll")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).implies(aux));
			res = aux.forAll(d);
		}
		else if (operatorname.equals("exists")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).and(aux));
			res = aux.forSome(d);			
		}
		else if (operatorname.equals("one")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).and(aux));
			res = aux.forOne(d);
		}
		else if (operatorname.equals("collect")) {
			aux = d.variable().in(src);
			res = src.join(aux.comprehension(d,
					((IExpression) body).oneOf("2_")));
		}
		else if (operatorname.equals("select")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).and(aux));
			res = aux.comprehension(d);			
		}
		else if (operatorname.equals("any")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).and(aux));
			res = aux.comprehension(d);
		}
		else if (operatorname.equals("reject")) {
			aux = (IFormula) body;
			aux = ((d.variable().in(src)).and(aux.not()));
			res = aux.comprehension(d);
		}
		else if (operatorname.equals("closure")) {
			IDecl dd = ((IExpression) body).oneOf("2_");
			res = Constants.TRUE().comprehension(d, dd);
			res = src.join(((IExpression) res).closure());
		} else
			throw new EErrorUnsupported(EErrorUnsupported.OCL,"OCL iterator not supported: "
					+ operatorname + ".",Task.TRANSLATE_OCL);
		context.remove(d.name());
	
		return res;
	}

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	IExpression propertyToField(String propn, IExpression var)
			throws EErrorTransform {
		IExpression exp = null;
		String metamodelID = null;
		String varsig = null;
		try {
			varsig = ((AlloyExpression) var).EXPR.type().toExpr().toString();
			metamodelID = EchoHelper.getMetamodelIDfromLabel(varsig);
			EMetamodel metamodel = MDEManager.getInstance().getMetamodelID(metamodelID);

			EStructuralFeature feature = ((EClass) metamodel.getEObject()
					.getEClassifier(EchoHelper.getClassifierName(varsig)))
					.getEStructuralFeature(propn);
			exp = context.getPropExpression(metamodelID,
					EchoHelper.getClassifierName(varsig), propn);

			if (exp == null && feature instanceof EReference && ((EReference) feature).getEOpposite() != null
					&& EchoOptionsSetup.getInstance().isOptimize())
				exp = context.getPropExpression(metamodel.ID,
						((EReference) feature).getEOpposite().getEContainingClass().getName(),
						((EReference) feature).getEOpposite().getName()).transpose();
			
			EchoReporter.getInstance().debug("Type of proptofield: "+feature.getEType().getName());
			if (feature.getEType().getName().equals("EString") || 
					feature.getEType().getName().equals("EBoolean") ||
					feature.getEType().getName().equals("EInt"))
				context.setCurrentModel(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (exp == null)
			throw new Error("Field not found: " + metamodelID + ", "+ varsig+", " + propn);
		return exp;
	}

	public IFormula translateExpressions(List<EObject> lex) throws EError {
		IFormula expr = Constants.TRUE();
		for (Object ex : lex) {
			expr = expr.and((IFormula) translateAtlOcl((EObject) ex));
		}
		return expr;
	}

}
