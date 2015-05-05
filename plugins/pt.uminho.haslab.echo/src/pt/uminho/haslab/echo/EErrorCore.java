package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.alloy.EErrorAlloy;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 3:52 PM
 */
public abstract class EErrorCore extends EError {

	public EErrorCore(String code, String msg, Task task) {
		super(code, msg, task);
	}

	private static final long serialVersionUID = 1L;

	public static final String FAIL_CREATE_VAR = "A001";
	public static final String FAIL_GET_CHILDREN = "A002";
	public static final String FAIL_CREATE_SIG = "A003";
	public static final String FAIL_CREATE_FIELD = "A004";
	public static final String FAIL_CREATE_FUNC = "A005";
	public static final String FAIL_CREATE_FACT = "A006";
	public static final String FAIL_EVAL = "A007";
	public static final String FAIL_TYPE = "A008";
	public static final String FAIL_SCOPE = "A009";
	public static final String FAIL_RUN = "A010";


	
	public static EErrorCore thrownew(String code, String msg,
			Err alloyerror, Task task) {
		
		return new EErrorAlloy(code, msg, alloyerror, task);

	}

	@Override public String toString() {
		return  "Engine Error: "+super.getMessage();
	}

}
