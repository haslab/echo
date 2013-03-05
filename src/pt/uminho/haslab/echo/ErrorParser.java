package pt.uminho.haslab.echo;

public class ErrorParser extends Exception {
	private static final long serialVersionUID = 1L;

	private Object object;
	private String trans;

	public ErrorParser(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorParser(String msg, String trans, Object obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}

	@Override public String toString() {
		if (object == null) return "Parsing error on "+trans+": "+super.getMessage();
		else return  "Parsing error "+object.getClass()+ " on "+trans+": "+super.getMessage();
	}

}
