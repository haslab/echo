package pt.uminho.haslab.echo.emf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.IfExp;
import org.eclipse.ocl.examples.pivot.IteratorExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.PrimitiveLiteralExp;
import org.eclipse.ocl.examples.pivot.PropertyCallExp;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.ocl.examples.pivot.internal.impl.TypeExpImpl;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.consistency.Variable;

public class OCLUtil {

	
	// retrieves the list of variable occurrences of an OCL expression (very incomplete)
	public static Map<Variable,String> variablesOCLExpression (OCLExpression exp, String mdl) throws ErrorUnsupported, ErrorTransform {
		Map<Variable,String> vars = new HashMap<Variable,String>();
		if (exp == null) return vars;
		if (exp instanceof VariableExp) {
			VariableDeclaration x = ((VariableExp) exp).getReferredVariable();
			if (vars.get(x) == null)
				vars.put(Variable.getVariable(x),null);
		}
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
		}
		else if (exp instanceof OperationCallExp) {
			Map<Variable,String> aux = variablesOCLExpression(((OperationCallExp) exp).getSource(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			for (OCLExpression e : ((OperationCallExp) exp).getArgument()) {
				aux = variablesOCLExpression(e,mdl);
				for (Variable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));
				}
		}
		else if (exp instanceof PropertyCallExp) {	
			Map<Variable,String> aux = variablesOCLExpression(((PropertyCallExp) exp).getSource(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
		}
		else if (exp instanceof IteratorExp) {
			EchoReporter.getInstance().debug("\n\nRetrieving vars from iterator: ");
			EchoReporter.getInstance().debug("pre: "+vars);
			Map<Variable,String> aux = variablesOCLExpression(((IteratorExp) exp).getSource(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			aux = variablesOCLExpression(((IteratorExp) exp).getBody(),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			for (VariableDeclaration x : ((IteratorExp) exp).getIterator())
				for (Variable y : new ArrayList<Variable>(vars.keySet()))
					if (x.getName().equals(y.getName())) vars.remove(y);
			EchoReporter.getInstance().debug("pos: "+vars+"\n\nf");
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
		else if (exp instanceof PrimitiveLiteralExp) {}
		else if (exp instanceof TypeExpImpl) {}
		else throw new ErrorUnsupported ("OCL expression not supported: "+exp.getClass()+".");

		return vars;
	}

	
	public static Map<Variable,String> variablesOCLExpression (EObject exp, String mdl) throws ErrorUnsupported, ErrorTransform {
		Map<Variable,String> vars = new HashMap<Variable,String>();
		if (exp == null) return vars;
		if (exp.eClass().getName().equals("VariableExp")) {
			EStructuralFeature var = exp.eClass().getEStructuralFeature("referredVariable");
			EObject x = (EObject) exp.eGet(var);
			if (vars.get(x) == null)
				vars.put(Variable.getVariable(x),null);
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
			EStructuralFeature source = exp.eClass().getEStructuralFeature("source");
			EStructuralFeature parameters = exp.eClass().getEStructuralFeature("arguments");
			Map<Variable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);
				
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
			EList<EObject> ps = (EList<EObject>) exp.eGet(parameters);
			for (EObject e : ps) {
				aux = variablesOCLExpression(e,mdl);
				for (Variable x : aux.keySet())
					if (vars.get(x) == null)
						vars.put(x, aux.get(x));
				}
		}
		else if (exp.eClass().getName().equals("NavigationOrAttributeCallExp")) {
			EStructuralFeature source = exp.eClass().getEStructuralFeature("source");

			Map<Variable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);
			for (Variable x : aux.keySet())
				if (vars.get(x) == null)
					vars.put(x, aux.get(x));
		} else if (exp.eClass().getName().equals("Binding")) {
			EStructuralFeature var = exp.eClass().getEStructuralFeature("outPatternElement");
			EObject xx = (EObject) exp.eGet(var);
			if (vars.get(xx) == null)
				vars.put(Variable.getVariable(xx),null);

			EStructuralFeature source = exp.eClass().getEStructuralFeature("value");

			Map<Variable,String> aux = variablesOCLExpression((EObject) exp.eGet(source),mdl);
			for (Variable x : aux.keySet())
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
