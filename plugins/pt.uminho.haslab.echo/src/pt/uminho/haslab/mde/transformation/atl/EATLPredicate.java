package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.alloy.OCL2Alloy2;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.OCLUtil;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedding of a predicate of an EMF ATL transformations in Echo.
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLPredicate implements EPredicate {
	private List<EObject> exps = new ArrayList<EObject>();
	private OCL2Alloy2 trad;

	@Override
	public void addCondition(EObject expr) {
		exps.add(expr);
	}

	@Override
	public List<EObject> getConditions() {
		return exps;
	}

	@Override
	public IFormula translate(IContext context) throws EchoError {
		return trad.translateExpressions(exps);
	}

	@Override
	public Map<EVariable,String> getVariables(String metamodel) throws ErrorUnsupported, ErrorTransform {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (Object predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression((EObject) predicate,metamodel));
		}
		return res;

	}


}
