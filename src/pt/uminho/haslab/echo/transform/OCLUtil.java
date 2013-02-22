package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.haslab.echo.ErrorUnsupported;

import net.sourceforge.qvtparser.model.essentialocl.BooleanLiteralExp;
import net.sourceforge.qvtparser.model.essentialocl.OclExpression;
import net.sourceforge.qvtparser.model.essentialocl.Variable;
import net.sourceforge.qvtparser.model.essentialocl.VariableExp;
import net.sourceforge.qvtparser.model.qvtrelation.RelationCallExp;
import net.sourceforge.qvtparser.model.qvttemplate.ObjectTemplateExp;
import net.sourceforge.qvtparser.model.qvttemplate.PropertyTemplateItem;

public class OCLUtil {

	
	// retrieves the list of variable occurrences of an OCL expression (very incomplete)
	public static List<Variable> variablesOCLExpression (OclExpression exp) throws ErrorUnsupported {
		List<Variable> vars = new ArrayList<Variable>();
		if (exp instanceof VariableExp) vars.add(((VariableExp) exp).getReferredVariable()); 
		else if (exp instanceof ObjectTemplateExp) {
			vars.add(((ObjectTemplateExp) exp).getBindsTo()); 
			for (Object part : ((ObjectTemplateExp) exp).getPart())
				vars.addAll(variablesOCLExpression( ((PropertyTemplateItem) part).getValue() ) );
		}
		else if (exp instanceof BooleanLiteralExp) {}
		else if (exp instanceof RelationCallExp) {
			for (Object e : ((RelationCallExp) exp).getArgument())
				vars.addAll(variablesOCLExpression((OclExpression) e));
		}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCLUtil",exp);
		return vars;
	}
	
}
