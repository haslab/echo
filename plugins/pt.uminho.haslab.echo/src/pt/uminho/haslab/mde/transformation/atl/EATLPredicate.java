package pt.uminho.haslab.mde.transformation.atl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.engine.alloy.OCL2Alloy2;
import pt.uminho.haslab.echo.engine.ast.alloy.EAlloyRelation;
import pt.uminho.haslab.mde.OCLUtil;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;

/**
 * An embedding of a predicate of an EMF ATL transformations in Echo.
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLPredicate implements EPredicate {
	private List<Object> exps = new ArrayList<Object>();
	private OCL2Alloy2 trad;

	@Override
	public void addCondition(Object expr) {
		exps.add(expr);
	}

	@Override
	public List<Object> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initTranslation(EAlloyRelation q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(q2a,vardecls,argsvars,prevars);

	}

	@Override
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(vardecls,argsvars,prevars);

	}

	@Override
	public Expr translate() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {


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
