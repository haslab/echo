package pt.uminho.haslab.echo.cli;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.emf.EchoParser;
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
		
		CLIReporter reporter = new CLIReporter();
		EchoOptionsSetup.init(options);

		reporter.beginStage(Task.ECHO_RUN);
		EchoParser parser = EchoParser.getInstance();
		EchoRunner runner = EchoRunner.getInstance();

		reporter.beginStage(Task.PROCESS_RESOURCES);		
		for (String metamodeluri : options.getMetamodels()){
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			runner.addMetamodel(metamodel);
			reporter.debug(metamodeluri + " loaded.");
		}
		if (options.isQVT()){
			RelationalTransformation qvt = parser.loadQVT(options.getQVTURI());
			runner.addQVT(qvt);
			reporter.debug(options.getQVTURI() + " loaded.");
		}
		for (String modeluri : options.getModels()){
			EObject model = parser.loadModel(modeluri);
			runner.addModel(model);
			reporter.debug(modeluri + " loaded.");
		}
		reporter.result(Task.PROCESS_RESOURCES,true);
		//reporter.debug(reporter.printModel());
		
		boolean conforms = true;
		boolean success = false;
		if (options.isConformance()) {
			reporter.beginStage(Task.CONFORMS_TASK);
			conforms = runner.conforms(Arrays.asList(options.getModels()));
			reporter.result(Task.CONFORMS_TASK,conforms);
		} else if (options.isGenerate()) {
			reporter.beginStage(Task.GENERATE_TASK);
			success = runner.generate(options.getMetamodels()[0],options.getScopes());
			reporter.result(Task.GENERATE_TASK,success);
		} else if (options.isRepair()) {
			reporter.beginStage(Task.REPAIR_TASK);
			reporter.beginStage(Task.ITERATION);
			success = runner.repair(options.getDirection());
			reporter.result(Task.ITERATION,success);
			while (!success) {
				reporter.beginStage(Task.ITERATION);
				success = runner.increment();			
				reporter.result(Task.ITERATION,success);
			}
			reporter.result(Task.REPAIR_TASK,success);
		}
		if (options.isCheck() && conforms) {
			reporter.beginStage(Task.CHECK_TASK);
			success = runner.check(options.getQVTURI(),Arrays.asList(options.getModels()));
			reporter.result(Task.CHECK_TASK,success);
		} else if (options.isEnforce() && conforms) {
			reporter.beginStage(Task.ENFORCE_TASK);
			/*if (options.isNew())
				success = echo.enforcenew(options.getQVTPath(),Arrays.asList(options.getInstances()),options.getDirection());
			else*/ 
			reporter.beginStage(Task.ITERATION);
			success = runner.enforce(options.getQVTURI(),Arrays.asList(options.getModels()),options.getDirection());
			reporter.result(Task.ITERATION,success);
			while (!success) {
				reporter.beginStage(Task.ITERATION);
				success = runner.increment();			
				reporter.result(Task.ITERATION,success);
			}
			reporter.result(Task.ENFORCE_TASK,success);
		}
		String next = "n";

		if ((options.isEnforce() || options.isGenerate() || options.isRepair()) && success) {
			/*if (options.isEnforce() && !options.isNew()) {
				String sb = runner.backUpInstance(options.getDirection());
				printer.print("Backup file created: " + sb);	
			}*/
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			next = "y";
			A4Solution sol = runner.getAInstance();
			sol.writeXML("alloy_output.xml");
			VizGUI viz = new VizGUI(true, "alloy_output.xml",null, null, null, true);
			Window win = SwingUtilities.getWindowAncestor(viz.getPanel());
			win.setVisible(true);
			viz.loadXML("alloy_output.xml", true);
			
			runner.generateTheme(viz.getVizState());
			viz.doShowViz();
			reporter.askUser("Search another instance? (y)");
			next = in.readLine();
			while (success&&next.equals("y")) {
				success = runner.next();
				if (success) {
					sol = runner.getAInstance();
					sol.writeXML("alloy_output.xml");
					viz.loadXML("alloy_output.xml", true);
					reporter.askUser("Search another instance? (y)");
					next = in.readLine();
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
		}
		reporter.result(Task.ECHO_RUN,next.equals("y"));
	}

	
}
