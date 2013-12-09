package pt.uminho.haslab.echo.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;

public class ErrorAlloy extends  ErrorInternalEngine {
	private static final long serialVersionUID = 1L;

	public static final String FAIL_CREATE_VAR = "A001";
	public static final String FAIL_GET_CHILDREN = "A002";
	public static final String FAIL_CREATE_SIG = "A003";
	public static final String FAIL_CREATE_FIELD = "A004";
	public static final String FAIL_CREATE_FUNC = "A005";
	public static final String FAIL_CREATE_FACT = "A006";
	
	public ErrorAlloy(String msg) { super(msg); }
	public ErrorAlloy(String code, String msg, Err err, Task task) { super(msg); }

	@Override public String toString() {
		return "Alloy error. "+super.getMessage();
	}

}
