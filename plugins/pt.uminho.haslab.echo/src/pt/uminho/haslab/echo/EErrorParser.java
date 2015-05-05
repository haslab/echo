package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public class EErrorParser extends EError {
	private static final long serialVersionUID = 1L;

	public static final String OCL = "P01";
	public static final String METAMODEL = "P02";
	public static final String CONSTRAINT = "P03";
	public static final String QVT = "P04";
	public static final String ATL = "P05";
	public static final String MODEL = "P06";
	public static final String FRAME = "P07";
	public static final String MODE = "P08";
	public static final String SCOPE = "P09";

	private Object object;

	public EErrorParser(String code, String msg, Task task, Object obj) { 
		super(code,msg,task);
		object = obj;
	}

	public EErrorParser(String code, String msg, String additional, Task task) { super(code,msg,task); }
	public EErrorParser(String code, String msg, Task task) { super(code,msg,task); }

	@Override public String toString() {
		if (object == null) return "Parsing error: "+super.getMessage();
		else return  "Parsing error "+object.getClass()+ ": "+super.getMessage();
	}

}
