package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ocl.examples.pivot.BooleanLiteralExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.ocl.examples.pivot.internal.impl.PropertyCallExpImpl;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;



public class OCLUtil {

	
	// retrieves the list of variable occurrences of an OCL expression (very incomplete)
	public static List<VariableDeclaration> variablesOCLExpression (OCLExpression exp) throws ErrorUnsupported, ErrorTransform {
		List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
		if (exp == null) return vars;
		if (exp instanceof VariableExp) vars.add(((VariableExp) exp).getReferredVariable()); 
		else if (exp instanceof ObjectTemplateExp) {
			vars.add(((ObjectTemplateExp) exp).getBindsTo()); 
			for (PropertyTemplateItem part : ((ObjectTemplateExp) exp).getPart())
				vars.addAll(variablesOCLExpression(part.getValue() ) );
		}
		else if (exp instanceof BooleanLiteralExp) {}
		else if (exp instanceof RelationCallExp) {
			for (OCLExpression e : ((RelationCallExp) exp).getArgument())
				vars.addAll(variablesOCLExpression(e));
		}
		else if (exp instanceof OperationCallExp) {
			for (OCLExpression e : ((OperationCallExp) exp).getArgument())
				vars.addAll(variablesOCLExpression(e));
			vars.addAll(variablesOCLExpression(((OperationCallExp) exp).getSource()));
		}
		else if (exp instanceof PropertyCallExpImpl) {
			vars.addAll(variablesOCLExpression(((PropertyCallExpImpl) exp).getSource()));
		}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCLUtil",exp);
		return vars;
	}
	
}
