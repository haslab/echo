package pt.uminho.haslab.echo.cli;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprCall;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.transform.alloy.EchoTranslator;

public class CLIReporter extends EchoReporter {
		
	private Map<Task,BigInteger> times = new HashMap<Task,BigInteger>();

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
	
	public long result(Task task, boolean result) {
		BigInteger now = BigInteger.valueOf(System.currentTimeMillis());
		BigInteger time = now.subtract(times.get(task));
		times.put(task, time);
		super.result(task,result,time.longValue());
		return time.longValue();

	}
	
	public void beginStage(Task task) {
		BigInteger now = BigInteger.valueOf(System.currentTimeMillis());
		times.put(task, now);
		super.debug("Starting: "+ task);
	}
	


	public void askUser(String msg) {
		System.out.println(msg);
	}
	public String printModel() throws ErrorAlloy{
		EchoTranslator translator = EchoTranslator.getInstance();
		CLIOptions options = (CLIOptions) EchoOptionsSetup.getInstance();
		StringBuilder sb = new StringBuilder();
		for (String uri : options.getMetamodels()) {
			sb.append("* Meta-model "+uri+"\n");
			sb.append("State sig: "+translator.getMetamodelStateSig(uri)+"\n");
			sb.append("Meta-model sigs: "+translator.getMetamodelSigs(uri)+"\n");
			sb.append("Enum sigs: "+translator.getEnumSigs(uri)+"\n");
			ExprCall exp = (ExprCall) translator.getConformsAllInstances(uri);
			sb.append("Constraints: "+exp.fun.getBody()+"\n");
			sb.append("Delta exp: "+translator.getMetamodelDeltaExpr(uri).getBody()+"\n");
		}
		
		for (String uri : options.getModels()) {
			sb.append("* Instance model "+uri+"\n");
			sb.append("State sig: "+translator.getModelStateSig(uri)+"\n");
			sb.append("Instance sigs: "+translator.getInstanceSigs(uri)+"\n");
			sb.append("Constraints: "+translator.getModelFact(uri)+"\n");
		}
		
		if (options.isQVT()) {
			sb.append("* QVT-R transformation "+options.getQVTURI()+"\n");
			ExprList x = (ExprList) translator.getQVTFact(((CLIOptions)EchoOptionsSetup.getInstance()).getQVTURI()).getBody();
			for (Expr y : x.args) {
				if (y instanceof ExprCall)
					sb.append("Constraint: "+y+" : "+((ExprCall) y).fun.getBody()+"\n");
			
			}
			}

		return sb.toString();
	}
}
