package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.alloy.OCL2Alloy2;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.ArrayList;
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
	private List<Object> exps = new ArrayList<Object>();
	private OCL2Alloy2 trad;
/*
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
*/
	@Override
	public void addCondition(EObject expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IFormula translate(IContext context) throws EchoError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<EVariable, String> getVariables(String metamodel)
			throws EchoError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EObject> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

}
