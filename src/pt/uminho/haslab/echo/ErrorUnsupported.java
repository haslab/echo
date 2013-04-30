package pt.uminho.haslab.echo;

public final class ErrorUnsupported extends Exception {

	private static final long serialVersionUID = 1L;

	public ErrorUnsupported(String msg) { super(msg); }

	@Override public String toString() {
		return  "Unsupported feature. "+super.getMessage();
	}

}
