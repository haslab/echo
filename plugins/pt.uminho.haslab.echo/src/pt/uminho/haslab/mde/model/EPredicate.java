package pt.uminho.haslab.mde.model;

import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;

/**
 * Echo representation of a condition
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public interface EPredicate {

	public void addCondition(Object expr);
	public List<Object> getConditions();
	public void initTranslation(EEngineRelation q2a, IContext context);
	public void initTranslation(IContext context);
	public Expr translate() throws EchoError;
	public Map<EVariable,String> getVariables(String metamodel) throws EchoError;


}
