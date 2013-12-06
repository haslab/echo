package pt.uminho.haslab.echo;

public final class ErrorTransform extends EchoError {

	private static final long serialVersionUID = 1L;

	public ErrorTransform(String msg) { super(msg); }

	@Override public String toString() {
		return  "Error transforming: "+super.getMessage();
	}

}
