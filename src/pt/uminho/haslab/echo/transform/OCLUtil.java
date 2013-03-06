package pt.uminho.haslab.echo.transform;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ocl.examples.pivot.IfExp;
import org.eclipse.ocl.examples.pivot.IteratorExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.PrimitiveLiteralExp;
import org.eclipse.ocl.examples.pivot.PropertyCallExp;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;



public class OCLUtil {

	
	// retrieves the list of variable occurrences of an OCL expression (very incomplete)
	public static Set<VariableDeclaration> variablesOCLExpression (OCLExpression exp) throws ErrorUnsupported, ErrorTransform {
		Set<VariableDeclaration> vars = new HashSet<VariableDeclaration>();
		if (exp == null) return vars;
		if (exp instanceof VariableExp) vars.add(((VariableExp) exp).getReferredVariable()); 
		else if (exp instanceof ObjectTemplateExp) {
			vars.add(((ObjectTemplateExp) exp).getBindsTo()); 
			for (PropertyTemplateItem part : ((ObjectTemplateExp) exp).getPart())
				vars.addAll(variablesOCLExpression(part.getValue() ) );
			vars.addAll(variablesOCLExpression(((ObjectTemplateExp) exp).getWhere())); 
		}
		else if (exp instanceof RelationCallExp) {
			for (OCLExpression e : ((RelationCallExp) exp).getArgument())
				vars.addAll(variablesOCLExpression(e));
		}
		else if (exp instanceof OperationCallExp) {
			for (OCLExpression e : ((OperationCallExp) exp).getArgument())
				vars.addAll(variablesOCLExpression(e));
			vars.addAll(variablesOCLExpression(((OperationCallExp) exp).getSource()));
		}
		else if (exp instanceof PropertyCallExp) {
			vars.addAll(variablesOCLExpression(((PropertyCallExp) exp).getSource()));
		}
		else if (exp instanceof IteratorExp) {
			vars.addAll(variablesOCLExpression(((IteratorExp) exp).getSource()));
			vars.addAll(variablesOCLExpression(((IteratorExp) exp).getBody()));
			vars.removeAll(((IteratorExp) exp).getIterator());
		}
		else if (exp instanceof IfExp) {
			vars.addAll(variablesOCLExpression(((IfExp) exp).getCondition()));
			vars.addAll(variablesOCLExpression(((IfExp) exp).getThenExpression()));
			vars.addAll(variablesOCLExpression(((IfExp) exp).getElseExpression()));
		}
		else if (exp instanceof PrimitiveLiteralExp) {}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCLUtil",exp);

		return vars;
	}
	
}
