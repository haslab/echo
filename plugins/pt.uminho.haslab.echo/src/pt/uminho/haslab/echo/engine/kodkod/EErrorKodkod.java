package pt.uminho.haslab.echo.engine.kodkod;

import edu.mit.csail.sdg.alloy4.Err;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorCore;

public class EErrorKodkod extends  EErrorCore {
	private static final long serialVersionUID = 1L;
	
	public EErrorKodkod(String code, String msg, Err err, Task task) { 
		super(code,msg,task);
	}

	@Override public String toString() {
		return "Kodkod Error: "+super.getMessage();
	}

}
