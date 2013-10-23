package pt.uminho.haslab.echo;

public class EchoOptionsSetup {
		
	private static EchoOptions instance;

	public static EchoOptions getInstance() {
		return instance;
	}
	
	private EchoOptionsSetup() {}

	public static void init(EchoOptions options){
		instance = options;
	}

}
