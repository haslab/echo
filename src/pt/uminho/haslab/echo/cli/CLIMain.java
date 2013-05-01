package pt.uminho.haslab.echo.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class CLIMain {


	public static void main(String[] args) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform, Err, IOException {	
	
		CLIOptions options = null;
		
		try {
			options = new CLIOptions(args);
			if (options.isHelp()) {
				options.printHelp();
				return;
			}
		} catch (ErrorParser e) { System.out.println(e.getMessage()); }
		
		CLIPrinter printer = new CLIPrinter(options);
		EchoRunner echo = new EchoRunner(options);

		printer.printTitle("Parsing input files.");
		
		for (String uri : options.getModels())
			echo.addModel(uri);
		if (options.isQVT()) echo.addQVT(options.getQVTPath());
		for (String uri : options.getInstances())
			echo.addInstance(uri);
		
		echo.timer.setTime("Parsing");
		printer.print("Files parsed ("+echo.timer.getTime("Parsing")+"ms).");
		printer.printTitle("Processing metamodels.");
		//printer.print("State signatures: "+echo.translator.getModelStateSigs() +", "+echo.translator.getInstanceStateSigs() +", "+echo.translator.getTargetStateSig());

		printer.print("Model signatures: ");
		
		
		//printer.print("Instance signatures: "+echo.translator.getInstanceSigs());
		//printer.print("Instance facts: "+echo.translator.getInstanceFact());
		
		/*if (options.isQVT()) {
			printer.printTitle("Processing QVT transformation "+echo.parser.getTransformation().getName()+".");

			printer.printForce("Running Alloy command: "+(options.isCheck()?"check.":("enforce "+echo.parser.getTransformation().getName()+" on the direction of "+options.getDirection()+".")));

			printer.print("Delta function: "+echo.translator.getDeltaFact());
			printer.print("Initial scope: "+echo.translator.getScopes());
			printer.print("QVT facts: "+echo.translator.getQVTFact());
		}*/

		echo.timer.setTime("Translating");
		printer.print("Alloy model generated ("+echo.timer.getTime("Translating")+"ms).");		
		
		boolean conforms = true;
		boolean success = false;
		if (options.isConformance()) {
			conforms = echo.conforms(Arrays.asList(options.getInstances()));
			echo.timer.setTime("Conforms");
			if (conforms)
				printer.printForce("Instances conform to the models ("+echo.timer.getTime("Conforms")+"ms).");
			else
				printer.printForce("Instances do not conform to the models ("+echo.timer.getTime("Conforms")+"ms).");
		} else if (options.isGenerate()) {
			success = echo.generate(Arrays.asList(options.getModels()));
			echo.timer.setTime("Generate");
			if (success)
				printer.printForce("Intance generated ("+echo.timer.getTime("Generate")+"ms).");
			else
				printer.printForce("No possible instances ("+echo.timer.getTime("Generate")+"ms).");
		} else if (options.isRepair()) {
			success = echo.repair(Arrays.asList(options.getInstances()),options.getDirection());
			echo.timer.setTime("Repair");
			while (!success) {
				printer.printForce("No instance found for delta ");//+echo.alloyrunner.getDelta()+((options.isVerbose())?(" (for "+echo.alloyrunner.getScopes()+", int "+echo.alloyrunner.getIntScope()+")"):"")+" ("+echo.timer.getTime("Enforce")+"ms).");
				success = echo.increment();			
				echo.timer.setTime("Repair");
			}
			printer.printForce("Instance found for delta ");//+echo.alloyrunner.getDelta()+" ("+echo.timer.getTime("Enforce")+"ms).");
		}
		if (options.isCheck() && conforms) {
			success = echo.check(options.getQVTPath(),Arrays.asList(options.getInstances()));
			echo.timer.setTime("Check");
			if (success) printer.printForce("Instances consistent ("+echo.timer.getTime("Check")+"ms).");
			else printer.printForce("Instances inconsistent ("+echo.timer.getTime("Check")+"ms).");
		} else if (options.isEnforce() && conforms) {
			success = echo.enforce(options.getQVTPath(),Arrays.asList(options.getInstances()),options.getDirection());
			echo.timer.setTime("Enforce");
			while (!success) {
				printer.printForce("No instance found for delta ");//+echo.alloyrunner.getDelta()+((options.isVerbose())?(" (for "+echo.alloyrunner.getScopes()+", int "+echo.alloyrunner.getIntScope()+")"):"")+" ("+echo.timer.getTime("Enforce")+"ms).");
				success = echo.increment();			
				echo.timer.setTime("Enforce");
			}
			printer.printForce("Instance found for delta ");//+echo.alloyrunner.getDelta()+" ("+echo.timer.getTime("Enforce")+"ms).");
		}
		if ((options.isEnforce() || options.isGenerate() || options.isRepair()) && success) {
			if (options.isEnforce() && !options.isNew()) {
				//String sb = echo.parser.backUpTarget();
				//printer.print("Backup file created: " + sb);	
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			A4Solution sol = echo.getAInstance();
			sol.writeXML("alloy_output.xml");
			VizGUI viz = new VizGUI(true, "alloy_output.xml", null);
			echo.generateTheme(viz.getVizState());
			viz.doShowViz();
			while (success&&end.equals("y")) {
				sol = echo.getAInstance();
				sol.writeXML("alloy_output.xml");
				viz.loadXML("alloy_output.xml", true);
				printer.printForce("Search another instance? (y)");
				end = in.readLine(); 
				success = echo.next();
			}
			in.close();
			if (success) {
				/*if(options.isEnforce()&&options.isOverwrite())
					echo.translator.writeTargetInstance(echo.alloyrunner.getSolution());
				else*/ if (options.isGenerate())
					for (String uri : options.getModels())
						echo.writeInstances(uri);	
			}
		
			SwingUtilities.getWindowAncestor(viz.getPanel()).dispose();
			new File("alloy_output.xml").delete();
		
			if (end.equals("y")) printer.printForce("No more instances.");
		}
		printer.printForce("Bye ("+echo.timer.getTotalTime()+"ms).");
	}

	
}
