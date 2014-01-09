package pt.uminho.haslab.echo.consistency.atl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.consistency.ECondition;
import pt.uminho.haslab.echo.consistency.EVariable;
import pt.uminho.haslab.echo.emf.OCLUtil;
import pt.uminho.haslab.echo.transform.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.transform.alloy.OCL2Alloy2;
import pt.uminho.haslab.echo.transform.alloy.Relation2Alloy;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;

public class ATLCondition implements ECondition {
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

	public void initTranslation(Relation2Alloy q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(q2a,vardecls,argsvars,prevars);

	}
	
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy2(vardecls,argsvars,prevars);

	}
	
	public Object translate() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {

		
		return trad.translateExpressions(exps);
	}
	
	public Map<EVariable,String> getVariables(String metamodel) throws ErrorUnsupported, ErrorTransform {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (Object predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression((EObject) predicate,metamodel));
		}
		return res;

	}

}
