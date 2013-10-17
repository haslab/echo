package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

public class EchoReporter {

	protected static EchoReporter instance;

	public EchoReporter () {}
	
	public static EchoReporter getInstance() {
		return instance;
	}

	public static void init(EchoReporter i) {
		instance = i;
	}
	
	public void debug (String msg) {
		if (EchoOptionsSetup.getInstance().isVerbose())
			System.out.println(msg);
	}

	public void result (Task qualifier, boolean result, long time) {
		switch (qualifier) {
			case ECHO_RUN :
				if (result) System.out.println("Bye (" + time + "ms).");
				else System.out.println("No more instances (" + time + "ms).");
				break;
			case PROCESS_RESOURCES :
				if (result) System.out.println("Resources loaded ("+ time + "ms).");
				else System.out.println("Failed to load resources (" + time + "ms).");
				break;
			case CONFORMS_TASK :
				if (result) System.out.println("Models conform to the meta-models (" + time + "ms).");
				else System.out.println("Models do not conform to the meta-models (" + time + "ms).");
				break;
			case GENERATE_TASK :
				if (result) System.out.println("Model generated (" + time + "ms).");
				else System.out.println("No possible solution (" + time + "ms).");
				break;
			case REPAIR_TASK:
			case ENFORCE_TASK:
				if (result) System.out.println("Model repaired (" + time + "ms).");
				else System.out.println("Failed to repair model (" + time + "ms).");
				break;
			case CHECK_TASK :
				if (result) System.out.println("Models consistent by QVT-R constraints (" + time + "ms).");
				else System.out.println("Models inconsistent by QVT-R constraints (" + time + "ms).");
				break;
		default:
			break;

		}
	}
	
	public void iteration (int delta) {
		System.out.println(delta);
	}



}
