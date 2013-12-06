package pt.uminho.haslab.echo;

public final class ErrorAPI extends EchoError {

	private static final long serialVersionUID = 1L;

	public ErrorAPI(String msg) { super(msg); }

	@Override public String toString() {
		return  "Error at the plugin: "+super.getMessage();
	}

}
