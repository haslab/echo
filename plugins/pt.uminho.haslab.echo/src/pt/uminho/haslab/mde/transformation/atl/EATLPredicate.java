package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorTransform;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.alloy.ATLOCLTranslator;
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

	@Override
	public void addCondition(EObject expr) {
		exps.add(expr);
	}

	@Override
	public List<EObject> getConditions() {
		return exps;
	}

	@Override
	public IFormula translate(ITContext context) throws EError {
		ATLOCLTranslator trad = new ATLOCLTranslator(context);
		return trad.translateExpressions(exps);
	}

	@Override
	public Map<EVariable,String> getVariables(String metamodel) throws EErrorUnsupported, EErrorTransform {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (Object predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression((EObject) predicate,metamodel));
		}
		return res;

	}


}
