package pt.uminho.haslab.echo.transform;

import java.util.List;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:30 PM
 */
public interface ConditionTranslator {
	
	
	public Object translateExpressions(List<Object> lex) throws ErrorInternalEngine, ErrorTransform, ErrorUnsupported, EchoError;


}
