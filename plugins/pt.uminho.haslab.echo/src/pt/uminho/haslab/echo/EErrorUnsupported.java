package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public final class EErrorUnsupported extends EError {

	private static final long serialVersionUID = 1L;

	public static final String ECORE = "U001";
	public static final String MULTIPLE_ROOT = "U002";
	public static final String MULTIPLE_INHERITANCE = "U003";
	public static final String PRIMITIVE_TYPE = "U004";
	public static final String OCL = "U005";
	public static final String MULTIPLE_QUANTIFIER = "U006";
	public static final String ATL = "U007";
	public static final String QVT = "U008";
	public static final String MULTIDIRECTIONAL = "U009";
	public static final String ALLOY = "U010";
	public static final String KODKOD = "U011";

	public EErrorUnsupported(String code, String msg, String additional, Task task) { super(code,msg,task); }

	public EErrorUnsupported(String code, String msg, Task task) { super(code,msg,task); }

	@Override public String toString() {
		return  "Unsupported Error: "+super.getMessage();
	}
	

}
