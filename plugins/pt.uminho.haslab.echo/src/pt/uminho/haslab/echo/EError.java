package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public abstract class EError extends EException {

	private static final long serialVersionUID = 1L;
	
	public EError(String code, String msg, Task task) { super(code,msg,task); }

}
