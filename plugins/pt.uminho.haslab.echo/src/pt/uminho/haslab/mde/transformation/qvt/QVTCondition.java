package pt.uminho.haslab.mde.transformation.qvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ocl.examples.pivot.OCLExpression;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.transform.alloy.OCL2Alloy;
import pt.uminho.haslab.echo.transform.alloy.Relation2Alloy;
import pt.uminho.haslab.mde.emf.OCLUtil;
import pt.uminho.haslab.mde.model.ECondition;
import pt.uminho.haslab.mde.model.EVariable;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTCondition implements ECondition {
	private List<Object> exps = new ArrayList<Object>();
	private OCL2Alloy trad;

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
		trad = new OCL2Alloy(q2a,vardecls,argsvars,prevars);
	}
	
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		trad = new OCL2Alloy(vardecls,argsvars,prevars);

	}
	
	public Object translate() throws EchoError {
		return trad.translateExpressions(exps);
	}
	
	public Map<EVariable,String> getVariables(String metamodel) throws EchoError {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (Object predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression((OCLExpression)predicate,metamodel));
		}
		return res;

	}

}
