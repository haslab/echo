package pt.uminho.haslab.echo;

public abstract class EchoError extends Exception {

	private String code;
	
	public EchoError(String msg) { super(msg); }

	public EchoError(String msg, String code) { 
		super(msg); 
		this.code = code;
	}

}
