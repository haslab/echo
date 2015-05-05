package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public final class EErrorAPI extends EError {

	private static final long serialVersionUID = 1L;
	public static final String TYPE = "I01";
	public static final String PROPERTIES = "I02";
	public static final String MARKER = "I03";

	public EErrorAPI(String code, String msg, Task task) { super(code,msg,task); }

	@Override public String toString() {
		return  "API Error: "+super.getMessage();
	}

}
