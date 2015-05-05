package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

/**
 * Created by tmg on 2/11/14.
 *
 */
public class EErrorType extends EError{

	private static final long serialVersionUID = 1L;

	public static final String FORM = "Y001";
	public static final String EXPR = "Y002";
	public static final String INT = "Y003";

	public EErrorType(String code, String expected, Task task) {
        super(code,"Typecheck error! \nExpected Type: "+ expected,task);
    }

	@Override public String toString() {
		return  "Type Error: "+super.getMessage();
	}


}
