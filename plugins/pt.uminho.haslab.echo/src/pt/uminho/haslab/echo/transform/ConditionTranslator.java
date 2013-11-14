package pt.uminho.haslab.echo.transform;

import java.util.List;

import org.eclipse.ocl.examples.pivot.OCLExpression;

import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.alloy.ErrorAlloy;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:30 PM
 */
public interface ConditionTranslator {
	
	
	public Object translateExpressions(List<Object> lex) throws ErrorAlloy, ErrorTransform, ErrorUnsupported;


}
