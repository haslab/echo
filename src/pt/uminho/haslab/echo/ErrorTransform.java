package pt.uminho.haslab.echo;

public final class ErrorTransform extends Exception {

	private static final long serialVersionUID = 1L;

	private Object object;
	private String trans;

	public ErrorTransform(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorTransform(String msg, String trans, Object obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}

	@Override public String toString() {
		if (object == null) return "Error on transformation "+trans+": "+super.getMessage();
		else return  "Error transforming "+object.getClass().getSimpleName()+ " on "+trans+": "+super.getMessage();
	}

}
