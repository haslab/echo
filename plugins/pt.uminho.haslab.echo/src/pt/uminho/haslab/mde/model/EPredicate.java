package pt.uminho.haslab.mde.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.alloy.EAlloyRelation;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;

/**
 * Echo representation of a condition
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public interface EPredicate {

	public void addCondition(Object expr);
	public List<Object> getConditions();
	public void initTranslation(EAlloyRelation q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);
	public Expr translate() throws EchoError;
	public Map<EVariable,String> getVariables(String metamodel) throws EchoError;


}
