package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;

public class ErrorAlloy extends ErrorInternalEngine {
	private static final long serialVersionUID = 1L;
	
	public ErrorAlloy(String msg) { super(msg); }
	public ErrorAlloy(String code, String msg, Err err, Task task) { 
		super(msg);
	}

	@Override public String toString() {
		return "Alloy error. "+super.getMessage();
	}

}
