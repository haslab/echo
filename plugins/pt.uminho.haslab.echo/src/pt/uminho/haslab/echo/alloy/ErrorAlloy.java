package pt.uminho.haslab.echo.alloy;

import pt.uminho.haslab.echo.ErrorInternalEngine;

public class ErrorAlloy extends  ErrorInternalEngine {
	private static final long serialVersionUID = 1L;

	public ErrorAlloy(String msg) { super(msg); }

	@Override public String toString() {
		return "Alloy error. "+super.getMessage();
	}

}
