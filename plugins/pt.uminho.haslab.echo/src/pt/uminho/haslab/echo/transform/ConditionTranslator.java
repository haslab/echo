package pt.uminho.haslab.echo.transform;

import pt.uminho.haslab.echo.EchoError;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:30 PM
 */
public interface ConditionTranslator {
	
	
	public Object translateExpressions(List<Object> lex) throws  EchoError;


}
