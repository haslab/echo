package pt.uminho.haslab.echo.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.emf.EMFParser;
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
		EMFParser parser = new EMFParser(options);
		EchoRunner runner = new EchoRunner(options);

		printer.printTitle("Processing input files.");
		
		for (String metamodeluri : options.getMetamodels()){
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			runner.addMetamodel(metamodel);
		}
		if (options.isQVT()){
			RelationalTransformation qvt = parser.loadQVT(options.getQVTURI());
			runner.addQVT(qvt);
		}
		for (String modeluri : options.getModels()){
			EObject model = parser.loadModel(modeluri);
			runner.addModel(model);
		}
		runner.timer.setTime("Translate");
		printer.printForce("Files processed ("+runner.timer.getTime("Translate")+"ms).");
		printer.print(printer.printModel(runner.translator));
		
		boolean conforms = true;
		boolean success = false;
		if (options.isConformance()) {
			printer.printTitle("Testing instances conformity.");
			conforms = runner.conforms(Arrays.asList(options.getModels()));
			runner.timer.setTime("Conforms");
			if (conforms)
				printer.printForce("Instances conform to the models ("+runner.timer.getTime("Conforms")+"ms).");
			else
				printer.printForce("Instances do not conform to the models ("+runner.timer.getTime("Conforms")+"ms).");
		} else if (options.isGenerate()) {
			printer.printTitle("Generating instance with size "+options.getOverallScope()+" but "+options.getScopes()+".");
			success = runner.generate(options.getMetamodels()[0],options.getScopes());
			runner.timer.setTime("Generate");
			if (success)
				printer.printForce("Intance generated ("+runner.timer.getTime("Generate")+"ms).");
			else
				printer.printForce("No possible instances ("+runner.timer.getTime("Generate")+"ms).");
		} else if (options.isRepair()) {
			printer.printTitle("Repairing instance.");
			success = runner.repair(options.getDirection());
			while (!success) {
				printer.printForce("No instance found for delta "+runner.getCurrentDelta()+".");
				success = runner.increment();			
			}
			runner.timer.setTime("Repair");
			printer.printForce("Instance found ("+runner.timer.getTime("Repair")+"ms).");
		}
		if (options.isCheck() && conforms) {
			printer.printTitle("Checking consistency.");
			success = runner.check(options.getQVTURI(),Arrays.asList(options.getModels()));
			runner.timer.setTime("Check");
			if (success) printer.printForce("Instances consistent ("+runner.timer.getTime("Check")+"ms).");
			else printer.printForce("Instances inconsistent ("+runner.timer.getTime("Check")+"ms).");
		} else if (options.isEnforce() && conforms) {
			printer.printTitle("Enforcing consistency.");
			/*if (options.isNew())
				success = echo.enforcenew(options.getQVTPath(),Arrays.asList(options.getInstances()),options.getDirection());
			else*/ 
			success = runner.enforce(options.getQVTURI(),Arrays.asList(options.getModels()),options.getDirection());
			runner.timer.setTime("Enforce");
			while (!success) {
				printer.printForce("No instance found for delta "+(runner.getCurrentDelta()-1)+" ("+runner.timer.getTime("Enforce")+"ms).");
				success = runner.increment();			
				runner.timer.setTime("Enforce");
			}
			printer.printForce("Instance found ("+runner.timer.getTime("Enforce")+"ms).");
		}
		if ((options.isEnforce() || options.isGenerate() || options.isRepair()) && success) {
			/*if (options.isEnforce() && !options.isNew()) {
				String sb = runner.backUpInstance(options.getDirection());
				printer.print("Backup file created: " + sb);	
			}*/
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			A4Solution sol = runner.getAInstance();
			sol.writeXML("alloy_output.xml");
			VizGUI viz = new VizGUI(true, "alloy_output.xml", null,null,null,true);
			
			viz.loadXML("alloy_output.xml", true);
			
			runner.generateTheme(viz.getVizState());
			viz.doShowViz();
			printer.printForce("Search another instance? (y)");
			end = in.readLine();
			while (success&&end.equals("y")) {
				success = runner.next();
				if (success) {
					sol = runner.getAInstance();
					sol.writeXML("alloy_output.xml");
					viz.loadXML("alloy_output.xml", true);
					printer.printForce("Search another instance? (y)");
					end = in.readLine();
				}
			}  
			in.close();
			if (success) {
				if (options.isGenerate())
					for (String uri : options.getMetamodels())
						runner.writeAllInstances(uri,"new.xmi");	
				else if(options.isRepair()&&options.isOverwrite())
					runner.writeInstance(options.getDirection());	
				else if(options.isEnforce()&&options.isOverwrite())
					runner.writeInstance(options.getDirection());			
			}
			SwingUtilities.getWindowAncestor(viz.getPanel()).dispose();
			new File("alloy_output.xml").delete();
		
			if (end.equals("y")) printer.printForce("No more instances.");
		}
		printer.printForce("Bye ("+runner.timer.getTotalTime()+"ms).");
	}

	
}
