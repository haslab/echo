package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorCore;

public class EErrorAlloy extends EErrorCore {
	private static final long serialVersionUID = 1L;
	
	public EErrorAlloy(String code, String msg, Err err, Task task) { 
		super(code,msg,task);
	}

	@Override public String toString() {
		return "Alloy Error: "+super.getMessage();
	}

}
