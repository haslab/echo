package pt.uminho.haslab.echo.alloy;

import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;

public class ErrorAlloy extends  ErrorInternalEngine {
	private static final long serialVersionUID = 1L;

	public static final String FAIL_CREATE_VAR = "A001";
	
	public ErrorAlloy(String msg) { super(msg); }
	public ErrorAlloy(String code, String msg, String additional, Task task) { super(msg); }

	@Override public String toString() {
		return "Alloy error. "+super.getMessage();
	}

}
