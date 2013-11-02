package pt.uminho.haslab.echo.consistency.atl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.consistency.Condition;
import pt.uminho.haslab.echo.consistency.Variable;
import pt.uminho.haslab.echo.emf.OCLUtil;
import pt.uminho.haslab.echo.transform.alloy.OCL2Alloy2;
import pt.uminho.haslab.echo.transform.alloy.Relation2Alloy;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class ATLCondition implements Condition {
	private List<EObject> exps = new ArrayList<EObject>();
	private OCL2Alloy2 trad;

	@Override
	public void addCondition(Object expr) {
		exps.add((EObject) expr);
	}

	@Override
	public List<Object> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initTranslation(Relation2Alloy q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(q2a,vardecls,argsvars,prevars);

	}
	
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(vardecls,argsvars,prevars);

	}
	
	public Expr translate() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {

		Expr expr = Sig.NONE.no();
		for (EObject ex : exps) {
			expr = AlloyUtil.cleanAnd(expr,trad.oclExprToAlloy(ex));
		}
		return expr;
	}
	
	public Map<Variable,String> getVariables(String metamodel) throws ErrorUnsupported, ErrorTransform {
		Map<Variable,String> res = new HashMap<Variable,String>();
		for (EObject predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression(predicate,metamodel));
		}
		return res;

	}

}
