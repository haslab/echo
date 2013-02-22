package pt.uminho.haslab.echo;

public final class ErrorUnsupported extends Exception {

	private static final long serialVersionUID = 1L;

	private Object object;
	private String trans;

	public ErrorUnsupported(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorUnsupported(String msg, String trans, Object obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}

	@Override public String toString() {
		if (object == null) return "Unsupported object on "+trans+": "+super.getMessage();
		else return  "Unsupported "+object.getClass().getSimpleName()+ " on "+trans+": "+super.getMessage();
	}

}
