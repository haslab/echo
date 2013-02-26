package pt.uminho.haslab.echo;

public class ErrorAlloy extends Exception {
	private static final long serialVersionUID = 1L;

	private Object object;
	private String trans;

	public ErrorAlloy(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorAlloy(String msg, String trans, Object obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}

	@Override public String toString() {
		if (object == null) return "Alloy error on "+trans+": "+super.getMessage();
		else return  "Alloy error "+object.getClass()+ " on "+trans+": "+super.getMessage();
	}

}
