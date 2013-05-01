package pt.uminho.haslab.echo;

public class ErrorAlloy extends Exception {
	private static final long serialVersionUID = 1L;

	public ErrorAlloy(String msg) { super(msg); }

	@Override public String toString() {
		return "Alloy error. "+super.getMessage();
	}

}
