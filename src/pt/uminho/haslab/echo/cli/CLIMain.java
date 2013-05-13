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

	public static void main(String[] args) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform, IOException, Err {	
	
		CLIOptions options = null;
		
		try {
			options = new CLIOptions(args);
			if (options.isHelp()) {
				options.printHelp();
				return;
			}
		} catch (ErrorParser e) { 
			System.out.println(e.getMessage());
			String[] a = {"--help"};
			options = new CLIOptions(a);
			options.printHelp();
			return; 
		}
		
		CLIPrinter printer = new CLIPrinter(options);
		EchoRunner echo = new EchoRunner(options);

		printer.printTitle("Processing input files.");
		
		for (String uri : options.getModels())
			echo.addModel(uri);
		if (options.isQVT()) echo.addQVT(options.getQVTPath());
		for (String uri : options.getInstances())
			echo.addInstance(uri);
		
		echo.timer.setTime("Translate");
		printer.printForce("Files processed ("+echo.timer.getTime("Translate")+"ms).");
		printer.print(printer.printModel(echo.translator));
		
		boolean conforms = true;
		boolean success = false;
		if (options.isConformance()) {
			printer.printTitle("Testing instances conformity.");
			conforms = echo.conforms(Arrays.asList(options.getInstances()));
			echo.timer.setTime("Conforms");
			if (conforms)
				printer.printForce("Instances conform to the models ("+echo.timer.getTime("Conforms")+"ms).");
			else
				printer.printForce("Instances do not conform to the models ("+echo.timer.getTime("Conforms")+"ms).");
		} else if (options.isGenerate()) {
			printer.printTitle("Generating instance with size "+options.getOverallScope()+" but "+options.getScopes()+".");
			success = echo.generate(Arrays.asList(options.getModels()));
			echo.timer.setTime("Generate");
			if (success)
				printer.printForce("Intance generated ("+echo.timer.getTime("Generate")+"ms).");
			else
				printer.printForce("No possible instances ("+echo.timer.getTime("Generate")+"ms).");
		} else if (options.isRepair()) {
			printer.printTitle("Repairing instance.");
			success = echo.repair(Arrays.asList(options.getInstances()),options.getDirection());
			while (!success) {
				printer.printForce("No instance found for delta "+echo.getCurrentDelta()+".");
				success = echo.increment();			
			}
			echo.timer.setTime("Repair");
			printer.printForce("Instance found ("+echo.timer.getTime("Repair")+"ms).");
		}
		if (options.isCheck() && conforms) {
			printer.printTitle("Checking consistency.");
			success = echo.check(options.getQVTPath(),Arrays.asList(options.getInstances()));
			echo.timer.setTime("Check");
			if (success) printer.printForce("Instances consistent ("+echo.timer.getTime("Check")+"ms).");
			else printer.printForce("Instances inconsistent ("+echo.timer.getTime("Check")+"ms).");
		} else if (options.isEnforce() && conforms) {
			printer.printTitle("Enforcing consistency.");
			success = echo.enforce(options.getQVTPath(),Arrays.asList(options.getInstances()),options.getDirection());
			echo.timer.setTime("Enforce");
			while (!success) {
				printer.printForce("No instance found for delta "+(echo.getCurrentDelta()-1)+" ("+echo.timer.getTime("Enforce")+"ms).");
				success = echo.increment();			
				echo.timer.setTime("Enforce");
			}
			printer.printForce("Instance found ("+echo.timer.getTime("Enforce")+"ms).");
		}
		if ((options.isEnforce() || options.isGenerate() || options.isRepair()) && success) {
			if (options.isEnforce() && !options.isNew()) {
				String sb = echo.backUpInstance(options.getDirection());
				printer.print("Backup file created: " + sb);	
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			A4Solution sol = echo.getAInstance();
			sol.writeXML("alloy_output.xml");
			VizGUI viz = new VizGUI(true, "alloy_output.xml", null);
			
			echo.generateTheme(viz.getVizState());
			viz.doShowViz();
			printer.printForce("Search another instance? (y)");
			end = in.readLine();
			while (success&&end.equals("y")) {
				success = echo.next();
				if (success) {
					sol = echo.getAInstance();
					sol.writeXML("alloy_output.xml");
					viz.loadXML("alloy_output.xml", true);
					printer.printForce("Search another instance? (y)");
					end = in.readLine();
				}
			}  
			in.close();
			if (success) {
				if (options.isGenerate())
					for (String uri : options.getModels())
						echo.writeAllInstances(uri);	
				else if(options.isRepair()&&options.isOverwrite())
					echo.writeInstance(options.getDirection());	
				else if(options.isEnforce()&&options.isOverwrite())
					echo.writeInstance(options.getDirection());			
			}
			SwingUtilities.getWindowAncestor(viz.getPanel()).dispose();
			new File("alloy_output.xml").delete();
		
			if (end.equals("y")) printer.printForce("No more instances.");
		}
		printer.printForce("Bye ("+echo.timer.getTotalTime()+"ms).");
	}

	
}
