package pt.uminho.haslab.echo.engine.kodkod;

import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;

public class ErrorKodkod extends  ErrorInternalEngine {
	private static final long serialVersionUID = 1L;

	
	public ErrorKodkod(String msg) { super(msg); }
	public ErrorKodkod(String code, String msg, Task task) { 
		super(msg);
	}
	@Override public String toString() {
		return "Kodkod error. "+super.getMessage();
	}

}
