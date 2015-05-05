package pt.uminho.haslab.echo.cli;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EException;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
import pt.uminho.haslab.mde.transformation.ETransformation;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

/**
 * Runs Echo through the command-line.
 * 
 * @author nmm
 * @version 0.4 23/03/2015
 */
public class CLIMain {

	public static void main(String[] args) throws IOException, EException, Err {	
	
		CLIOptions options = null;
		
		try {
			options = new CLIOptions(args);
			if (options.isHelp()) {
				options.printHelp();
				return;
			}
		} catch (EErrorParser e) { 
			System.out.println(e.getMessage());
			String[] a = {"--help"};
			options = new CLIOptions(a);
			options.printHelp();
			return; 
		}
		
		CLIReporter reporter = new CLIReporter();
		EchoOptionsSetup.init(options);
		
		EchoRunner runner = new EchoRunner();
		MDEManager parser = MDEManager.getInstance();
		
		for (String metamodelURI : options.getMetamodels()){
			EMetamodel metamodel = parser.getMetamodel(metamodelURI, true);
			runner.addMetamodel(metamodel);
		}
		
		List<String> modelIDs = new ArrayList<String>();
		for (String modelURI : options.getModels()) {
			EModel model = null;
			if (modelURI.equals(options.getDirection())&&(options.isGenerate()||options.isBatch())) {
				EMetamodel metamodel = MDEManager.getInstance().getMetamodel(options.getMetamodels()[0], false);
				model = MDEManager.getInstance().createEmpty(metamodel.ID,options.getDirection(), "");
			}
			else {
				model = parser.getModel(modelURI, true);
			}
			modelIDs.add(model.ID);
			runner.addModel(model);		
		}	
		
		EConstraint cons = null;
		if (options.isQVT()){
			ETransformation tran = parser.getETransformation(options.getQVTURI(),true);
			runner.addTransformation(tran);
			cons = runner.addConstraint(tran.ID, modelIDs);
			reporter.debug(options.getQVTURI() + " loaded.");
		}
		
		boolean conforms = true;
		boolean success = false;
		if (options.isConformance()) {
			conforms = runner.conforms(modelIDs);
		} 
		else if (options.isGenerate()) {
			EModel m = parser.getModel(options.getDirection(), false);
			success = runner.generate(options.getScopes(m.getMetamodel().ID),m.ID);
		} 
		else if (options.isRepair()) {
			EModel m = parser.getModel(options.getDirection(), false);
			success = runner.repair(m.ID);
		}
		else if (options.isCheck()) {
			success = runner.check(cons.ID);
		} 
		else if (options.isEnforce() && conforms) {
			EModel m = parser.getModel(options.getDirection(), false);
			success = runner.enforce(cons.ID,Arrays.asList(m.ID));
		}
		else if (options.isBatch()) {
			EModel m = parser.getModel(options.getDirection(), false);
			success = runner.batch(options.getScopes(m.getMetamodel().ID),Arrays.asList(m.ID),cons.ID);
		}
		String next = "n";
		EchoSolution esol = runner.getAInstance();
		if ((options.isEnforce() || options.isGenerate() || options.isRepair() || options.isBatch()) && success) {
			/*if (options.isEnforce() && !options.isNew()) {
				String sb = runner.backUpInstance(options.getDirection());
				printer.print("Backup file created: " + sb);	
			}*/
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			next = "y";
			esol.writeXML("alloy_output.xml");
			VizGUI viz = new VizGUI(true, "alloy_output.xml",null, null, null, true);
			Window win = SwingUtilities.getWindowAncestor(viz.getPanel());
			win.setVisible(true);
			viz.loadXML("alloy_output.xml", true);
			
			runner.generateTheme(viz.getVizState());
			viz.doShowViz();
			reporter.askUser("Search another instance? (y)");
			next = in.readLine();
			while (next.equals("y")) {
				runner.next();
				esol = runner.getAInstance();
				esol.writeXML("alloy_output.xml");
				viz.loadXML("alloy_output.xml", true);
				reporter.askUser("Search another instance? (y)");
				next = in.readLine();
			}  
			in.close();
			if (options.isGenerate()) {
				EModel newm = MDEManager.getInstance().getModel(options.getDirection(), false);
				runner.writeInstance(newm.ID);
			}
			else if(options.isRepair()&&options.isOverwrite())
				runner.writeInstance(options.getDirection());	
			else if(options.isEnforce()&&options.isOverwrite())
				runner.writeInstance(options.getDirection());			
			SwingUtilities.getWindowAncestor(viz.getPanel()).dispose();
			new File("alloy_output.xml").delete();
		}
	}

	
}
