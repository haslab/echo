package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;
import pt.uminho.haslab.echo.engine.ast.INode;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.atl.EATLRelation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;

import java.util.ArrayList;
import java.util.List;

public class ATLOCLTranslator {

	protected final ITContext context;

	public ATLOCLTranslator(ITContext context) {
		this.context = context;
	}

	public INode translateAtlOcl(EObject expr) throws EchoError {
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
		} else
			throw new ErrorUnsupported("OCL expression not supported: " + expr
					+ ".");
	}

	IExpression translateAtlOclVariable(EObject expr) {
		EStructuralFeature x = expr.eClass().getEStructuralFeature(
				"referredVariable");
		EObject vardecl = (EObject) expr.eGet(x);
		EStructuralFeature name = vardecl.eClass().getEStructuralFeature(
				"varName");
		String varname = (String) vardecl.eGet(name);
		context.setCurrentModel(context.getVarModel(varname));
		return context.getVar(varname);
	}

	IFormula translateAtlOclBooleanLit(EObject expr) {
		EStructuralFeature symb = expr.eClass().getEStructuralFeature(
				"booleanSymbol");
		if (expr.eGet(symb).toString().equals("true"))
			return Constants.TRUE();
		else
			return Constants.FALSE();
	}

	INode translateAtlOclAttribute(EObject expr) throws EchoError {
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

	INode translateAtlOclBinding(EObject expr) throws EchoError {
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
		else
			res = var.join(aux).eq((IExpression) val);
		return res;
	}

	INode translateAtlOclOperationCall(EObject expr) throws EchoError {
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
		else if (operatorname.equals("size"))
			res = ((IExpression) src).cardinality();
		else if (operatorname.equals("=")) {
			INode aux = translateAtlOcl(argumentso.get(0));
			if (src instanceof IFormula)
				res = ((IFormula) src).iff((IFormula) aux);
			else
				res = ((IExpression) src).eq((IExpression) aux);
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
		else if (operatorname.equals("intersection"))
			res = ((IExpression) src)
					.intersection((IExpression) translateAtlOcl(argumentso
							.get(0)));
		else if (operatorname.equals("includes"))
			res = ((IExpression) translateAtlOcl(argumentso.get(0)))
					.in((IExpression) src);
		else if (operatorname.equals("oclAsSet")
				|| operatorname.equals("asSet"))
			res = src;
		else if (operatorname.equals("+"))
			res = ((IIntExpression) src)
					.plus((IIntExpression) translateAtlOcl(argumentso.get(0)));
		else if (operatorname.equals("-"))
			res = ((IIntExpression) src)
					.minus((IIntExpression) translateAtlOcl(argumentso.get(0)));
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
			throw new ErrorUnsupported("OCL operation not supported: "
					+ expr.toString() + ".");

		return res;
	}

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	IExpression propertyToField(String propn, IExpression var)
			throws ErrorTransform {
		IExpression exp = null;
		String metamodelID = null;
		String varsig = null;
		try {
			varsig = ((AlloyExpression) var).EXPR.type().toExpr().toString();
			metamodelID = EchoHelper.getMetamodelIDfromLabel(varsig);
			EMetamodel metamodel;
			metamodel = MDEManager.getInstance().getMetamodelID(metamodelID);
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

	public IFormula translateExpressions(List<EObject> lex) throws EchoError {
		IFormula expr = Constants.TRUE();
		for (Object ex : lex) {
			expr = expr.and((IFormula) translateAtlOcl((EObject) ex));
		}
		return expr;
	}

}
