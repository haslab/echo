package pt.uminho.haslab.mde;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.*;
import org.eclipse.ocl.examples.pivot.internal.impl.TypeExpImpl;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OCLUtil {

	// retrieves the list of variable occurrences of an OCL expression (very incomplete)
	public static Map<EVariable,String> variablesOCLExpression (OCLExpression exp, String mdl) throws ErrorUnsupported, ErrorTransform {
		final Map<EVariable,String> vars = new HashMap<EVariable,String>();
		if (exp == null) return vars;
		if (exp instanceof VariableExp) {
			final VariableDeclaration x = ((VariableExp) exp).getReferredVariable();
			if (vars.get(x) == null)
				vars.put(EVariable.getVariable(x),null);
		}
		else if (exp instanceof ObjectTemplateExp) {
			final VariableDeclaration x = ((ObjectTemplateExp) exp).getBindsTo();
			if (vars.get(x) == null)
				vars.put(EVariable.getVariable(x),mdl);
			Map<EVariable,String> aux = variablesOCLExpression(((ObjectTemplateExp) exp).getWhere(),mdl);
			for (final EVariable y : aux.keySet())
				if (vars.get(y) == null)
					vars.put(y, aux.get(y));

			for (final PropertyTemplateItem part : ((ObjectTemplateExp) exp).getPart()) {
				aux = variablesOCLExpression(part.getValue(),mdl);
				for (final EVariable y : aux.keySet())
					if (vars.get(y) == null)
						vars.put(y, aux.get(y));
			}
		}
		else if (exp instanceof RelationCallExp) {
			for (final OCLExpression e : ((RelationCallExp) exp).getArgument()) {
				final Map<EVariable,String> aux = variablesOCLExpression(e,mdl);
				for (final EVariable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));

			}
		}
		else if (exp instanceof OperationCallExp) {
			Map<EVariable,String> aux = variablesOCLExpression(((OperationCallExp) exp).getSource(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			for (final OCLExpression e : ((OperationCallExp) exp).getArgument()) {
				aux = variablesOCLExpression(e,mdl);
				for (final EVariable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));
			}
		}
		else if (exp instanceof PropertyCallExp) {
			final Map<EVariable,String> aux = variablesOCLExpression(((PropertyCallExp) exp).getSource(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
		}
		else if (exp instanceof IteratorExp) {
			Map<EVariable,String> aux = variablesOCLExpression(((IteratorExp) exp).getSource(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IteratorExp) exp).getBody(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			for (final VariableDeclaration x : ((IteratorExp) exp).getIterator())
				for (final EVariable y : new ArrayList<EVariable>(vars.keySet()))
					if (x.getName().equals(y.getName())) vars.remove(y);
		}
		else if (exp instanceof IfExp) {
			Map<EVariable,String> aux = variablesOCLExpression(((IfExp) exp).getCondition(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IfExp) exp).getThenExpression(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IfExp) exp).getElseExpression(),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));

		}
		else if (exp instanceof PrimitiveLiteralExp) {}
		else if (exp instanceof TypeExpImpl) {}
		else throw new ErrorUnsupported ("OCL expression not supported: "+exp.getClass()+".");

		return vars;
	}


	public static Map<EVariable,String> variablesOCLExpression (final EObject exp, final String mdl) throws ErrorUnsupported, ErrorTransform {
		final Map<EVariable,String> vars = new HashMap<EVariable,String>();
		if (exp == null) return vars;
		if (exp.eClass().getName().equals("VariableExp")) {
			final EStructuralFeature var = exp.eClass().getEStructuralFeature("referredVariable");
			final EObject x = (EObject) exp.eGet(var);
			if (vars.get(x) == null)
				vars.put(EVariable.getVariable(x),null);
		}/*
		else if (exp instanceof ObjectTemplateExp) {
			VariableDeclaration x = ((ObjectTemplateExp) exp).getBindsTo();
			if (vars.get(x) == null)
				vars.put(Variable.getVariable(x),mdl);
			Map<Variable,String> aux = variablesOCLExpression(((ObjectTemplateExp) exp).getWhere(),mdl);
			for (Variable y : aux.keySet())
				if (vars.get(y) == null)
					vars.put(y, aux.get(y));

			for (PropertyTemplateItem part : ((ObjectTemplateExp) exp).getPart()) {
				aux = variablesOCLExpression(part.getValue(),mdl);
				for (Variable y : aux.keySet())
					if (vars.get(y) == null)
						vars.put(y, aux.get(y));
			}
		}
		else if (exp instanceof RelationCallExp) {
			for (OCLExpression e : ((RelationCallExp) exp).getArgument()) {
				Map<Variable,String> aux = variablesOCLExpression(e,mdl);
				for (Variable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));

			}
		}*/
		else if (exp.eClass().getName().equals("OperatorCallExp") || exp.eClass().getName().equals("OperationCallExp")) {
			final EStructuralFeature source = exp.eClass().getEStructuralFeature("source");
			final EStructuralFeature parameters = exp.eClass().getEStructuralFeature("arguments");
			Map<EVariable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);

			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			final EList<EObject> ps = (EList<EObject>) exp.eGet(parameters);
			for (final EObject e : ps) {
				aux = variablesOCLExpression(e,mdl);
				for (final EVariable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));
			}
		}
		else if (exp.eClass().getName().equals("NavigationOrAttributeCallExp")) {
			final EStructuralFeature source = exp.eClass().getEStructuralFeature("source");

			final Map<EVariable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
		} else if (exp.eClass().getName().equals("Binding")) {
			final EStructuralFeature var = exp.eClass().getEStructuralFeature("outPatternElement");
			final EObject xx = (EObject) exp.eGet(var);
			if (vars.get(xx) == null)
				vars.put(EVariable.getVariable(xx),null);

			final EStructuralFeature source = exp.eClass().getEStructuralFeature("value");

			final Map<EVariable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);
			for (final EVariable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
		}
		/*
		else if (exp instanceof IteratorExp) {
			Map<Variable,String> aux = variablesOCLExpression(((IteratorExp) exp).getSource(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IteratorExp) exp).getBody(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			for (VariableDeclaration x : ((IteratorExp) exp).getIterator())
				vars.remove(x);
		}
		else if (exp instanceof IfExp) {
			Map<Variable,String> aux = variablesOCLExpression(((IfExp) exp).getCondition(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IfExp) exp).getThenExpression(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IfExp) exp).getElseExpression(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));

		}
		else if (exp instanceof PrimitiveLiteralExp) {}*/
		else if (exp.eClass().getName().equals("BooleanExp")) {}
		else throw new ErrorUnsupported ("OCL expression not supported: "+exp+".");

		return vars;
	}

}
