package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public class ErrorParser extends EchoError {
	private static final long serialVersionUID = 1L;

	public static final String OCL = "P001";

	private Object object;
	private String trans;

	public ErrorParser(String msg) { super(msg); }

	public ErrorParser(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorParser(String msg, String trans, Object obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}


	public ErrorParser(String code, String msg, String additional, Task task) { super(msg); }

	@Override public String toString() {
		if (object == null) return "Parsing error on "+trans+": "+super.getMessage();
		else return  "Parsing error "+object.getClass()+ " on "+trans+": "+super.getMessage();
	}

}
