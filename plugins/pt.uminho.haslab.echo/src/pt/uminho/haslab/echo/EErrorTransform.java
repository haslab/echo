package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public final class EErrorTransform extends EError {

	private static final long serialVersionUID = 1L;

	public static final String BITWIDTH = "T001";
	public static final String OCLISNEW = "T002";
	public static final String VAR_DECL = "T003";
	public static final String ROOT = "T004";
	
	public EErrorTransform(String code, String msg, String additional, Task task) { super(code,msg,task); }
	public EErrorTransform(String code, String msg, Task task) { super(code,msg,task); }
	
	@Override public String toString() {
		return  "Transformation Error: "+super.getMessage();
	}

}
