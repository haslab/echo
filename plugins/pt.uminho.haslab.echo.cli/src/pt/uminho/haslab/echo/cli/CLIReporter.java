package pt.uminho.haslab.echo.cli;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;

public class CLIReporter extends EchoReporter {

	public CLIReporter() {
		super();
		EchoReporter.init(this);
	}

	public void debug(String msg) {
		super.debug(msg);
	}

	public void increment(int delta) {
		super.iteration(delta);
	}

	public void askUser(String msg) {
		System.out.println(msg);
	}

	@Override
	public void start(Task qualifier,String message) {
		super.start(qualifier,message);
		switch (qualifier) {
		case GENERATE_TASK :
			System.out.println("*** "+message);
			break;
		default:
			break;
		}
	}

	@Override
	public void result(Task qualifier, String message, boolean result) {
		super.result(qualifier,message,result);
		switch (qualifier) {
		case CORE_RUN :
			if(EchoOptionsSetup.getInstance().isVerbose()) System.out.println("Solver: "+message);
			break;
		case ECHO_RUN :
			if (result) System.out.println("Bye (" + getEndTime(qualifier) + "ms).");
			else System.out.println("No more instances (" + getEndTime(qualifier) + "ms).");
			break;
		case PROCESS_RESOURCES :
			if (result) System.out.println("*** Resource "+message+" loaded ("+ getEndTime(qualifier) + "ms).");
			else System.out.println("Failed to load "+message+" (" + getEndTime(qualifier) + "ms).");
			break;
		case CONFORMS_TASK :
			if (result) System.out.println("Models conform to the meta-models (" + getEndTime(qualifier) + "ms).");
			else System.out.println("Models do not conform to the meta-models (" + getEndTime(qualifier) + "ms).");
			break;
		case GENERATE_TASK :
			if (result) System.out.println("*** Model generated (" + getEndTime(qualifier) + "ms).");
			else System.out.println("No possible solution (" + getEndTime(qualifier) + "ms).");
			break;
		case REPAIR_TASK:
		case ENFORCE_TASK:
			if (result) System.out.println("Model repaired (" + getEndTime(qualifier) + "ms).");
			else System.out.println("Failed to repair model (" + getEndTime(qualifier) + "ms).");
			break;
		case CHECK_TASK :
			if (result) System.out.println("Models consistent by QVT-R constraints (" + getEndTime(qualifier) + "ms).");
			else System.out.println("Models inconsistent by QVT-R constraints (" + getEndTime(qualifier) + "ms).");
			break;
		case TRANSLATE_METAMODEL :
			System.out.println("*** Meta-model "+message+" translated (" + getEndTime(qualifier) + "ms).");
			break;
		default:
			break;

		}
	}


}
