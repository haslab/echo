package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;

public class ErrorAlloy extends Exception {
	private static final long serialVersionUID = 1L;

	private Expr object;
	private String trans;


	public ErrorAlloy(String msg) { super(msg); }

	public ErrorAlloy(String msg, String trans) { super(msg); this.trans = trans; }

	public ErrorAlloy(String msg, String trans, Expr obj) { 
		super(msg);
		object = obj;
		this.trans = trans;
	}

	@Override public String toString() {
		if (object == null) return "Alloy error on "+trans+": "+super.getMessage();
		else return  "Alloy error "+object.getClass()+ " on "+trans+": "+super.getMessage();
	}

}
