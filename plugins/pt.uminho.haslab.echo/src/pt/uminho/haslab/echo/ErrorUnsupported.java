package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public final class ErrorUnsupported extends EchoError {

	private static final long serialVersionUID = 1L;
	public static final String ECORE = "U001";
	public static final String MULTIPLE_ROOT = "U002";

	public ErrorUnsupported(String msg) { super(msg); }
	
	public ErrorUnsupported(String code, String msg, String additional, Task task) { super(msg); }


	@Override public String toString() {
		return  "Unsupported feature. "+super.getMessage();
	}
	

}
