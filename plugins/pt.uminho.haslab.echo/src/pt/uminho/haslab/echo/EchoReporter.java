package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.EchoRunner.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EchoReporter {

	private Map<Task,List<String>> warnings = new HashMap<Task,List<String>>();
	private Map<Task,Long> inittime = new HashMap<Task,Long>();
	private Map<Task,Long> endtime = new HashMap<Task,Long>();
	private Map<Task,String> messages = new HashMap<Task,String>();
	
	protected static EchoReporter instance;

	public EchoReporter () {}
	
	public static EchoReporter getInstance() {
		return instance;
	}

	public static void init(EchoReporter i) {
		instance = i;
	}
	
	public void debug(String msg) {
		if (EchoOptionsSetup.getInstance().isVerbose())
			System.out.println(msg);
	}

	public void start(Task qualifier,String message) {
		inittime.put(qualifier, System.currentTimeMillis());
		switch (qualifier) {
		case ALLOY_RUN :
			System.out.println(message);
			break;
		default:
			break;
		}
	}
	
	public void result (Task qualifier, String string, boolean result) {
		long time = System.currentTimeMillis() - inittime.get(qualifier);
		endtime.put(qualifier, time);
		switch (qualifier) {
			case ALLOY_RUN :
				System.out.println(string);
				break;
			case ECHO_RUN :
				if (result) System.out.println("Bye (" + time + "ms).");
				else System.out.println("No more instances (" + time + "ms).");
				break;
			case PROCESS_RESOURCES :
				if (result) System.out.println("Resource "+string+" loaded ("+ time + "ms).");
				else System.out.println("Failed to load "+string+" (" + time + "ms).");
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
			case TRANSLATE_METAMODEL :
				System.out.println("Meta-model "+messages.get(qualifier)+" translated (" + time + "ms).");
				break;
		default:
			break;

		}
	}
	
	public void warning (String message, Task task) {
		List<String> ws = warnings.get(task);
		if (ws == null) ws = new ArrayList<String>();
		ws.add(message);
		warnings.put(task, ws);		

		if (EchoOptionsSetup.getInstance().isVerbose())
			System.out.println(message);
	}
	
	public void iteration (int delta) {
		System.out.println(delta);
	}



}
