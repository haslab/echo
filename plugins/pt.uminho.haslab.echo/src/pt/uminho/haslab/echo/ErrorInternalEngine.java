package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.alloy.ErrorAlloy;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 3:52 PM
 */
public abstract class ErrorInternalEngine extends EchoError {

	public static final String FAIL_CREATE_VAR = "A001";
	public static final String FAIL_GET_CHILDREN = "A002";
	public static final String FAIL_CREATE_SIG = "A003";
	public static final String FAIL_CREATE_FIELD = "A004";
	public static final String FAIL_CREATE_FUNC = "A005";
	public static final String FAIL_CREATE_FACT = "A006";
	
	public ErrorInternalEngine(String msg) { super(msg); }

	public static ErrorInternalEngine thrownew(String tag, String msg,
			Err alloyerror, Task task) {
		
		return new ErrorAlloy(tag, msg, alloyerror, task);

	}
}
