package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public final class ErrorTransform extends EchoError {

	private static final long serialVersionUID = 1L;

	public static final String BITWIDTH = "T001";

	public ErrorTransform(String msg) { super(msg); }

	public ErrorTransform(String code, String msg, String additional, Task task) { super(msg); }

	
	@Override public String toString() {
		return  "Error transforming: "+super.getMessage();
	}

}
