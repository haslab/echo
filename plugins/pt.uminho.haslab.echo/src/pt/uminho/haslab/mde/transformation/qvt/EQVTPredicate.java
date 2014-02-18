package pt.uminho.haslab.mde.transformation.qvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.examples.pivot.OCLExpression;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.OCLTranslator;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.OCLUtil;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;

/**
 * An embedding of a predicate of an EMF QVT-R transformations in Echo.
 *
 * @author nmm
 * @version 0.4 15/02/2014
 */
public class EQVTPredicate implements EPredicate {
	
	/** the original EMF predicates */
	private List<OCLExpression> exps = new ArrayList<OCLExpression>();

	@Override
	public void addCondition(EObject expr) {
		exps.add((OCLExpression) expr);
	}

	@Override
	public List<OCLExpression> getConditions() {
		return exps;
	}

	@Override
	public IFormula translate(IContext context) throws EchoError {
		OCLTranslator trad = new OCLTranslator(context);
		return trad.translateExpressions(exps);
	}


	@Override
	public Map<EVariable,String> getVariables(String metamodel) throws EchoError {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (OCLExpression predicate : exps)
			res.putAll(OCLUtil.variablesOCLExpression(predicate,metamodel));
		return res;
	}
}
