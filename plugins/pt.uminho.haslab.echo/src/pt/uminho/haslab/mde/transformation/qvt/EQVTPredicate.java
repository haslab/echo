package pt.uminho.haslab.mde.transformation.qvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ocl.examples.pivot.OCLExpression;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.OCLTranslator;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.alloy.EAlloyRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.mde.OCLUtil;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;

/**
 * An embedding of a predicate of an EMF QVT-R transformations in Echo.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EQVTPredicate implements EPredicate {
	
	/** the original EMF predicates */
	private List<Object> exps = new ArrayList<Object>();
	
	/** the OCL translator 
	 * TODO: replace Alloy by engine */
	private OCLTranslator trad;

	@Override
	public void addCondition(Object expr) {
		exps.add(expr);
	}

	@Override
	public List<Object> getConditions() {
		return exps;
	}

	@Override
	public void initTranslation(EEngineRelation q2a, IContext context) {
		trad = new OCLTranslator(q2a,context);
	}

	@Override
	public void initTranslation(IContext context) {
		trad = new OCLTranslator(context);

	}

	@Override
	public Expr translate() throws EchoError {
		return trad.translate(exps);
	}

	@Override
	public Map<EVariable,String> getVariables(String metamodel) throws EchoError {
		Map<EVariable,String> res = new HashMap<EVariable,String>();
		for (Object predicate : exps) {
			res.putAll(OCLUtil.variablesOCLExpression((OCLExpression)predicate,metamodel));
		}
		return res;
	}
}
