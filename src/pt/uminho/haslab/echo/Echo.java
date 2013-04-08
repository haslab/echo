package pt.uminho.haslab.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4.Err;

public class Echo {
	
	private static EchoOptions options;
	private static EchoPrinter printer;
	private static EMFParser parser;
	private static EMF2Alloy translator;
	private static EchoTimer timer;
	
	public static void main(String[] args) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform, Err, IOException {	
	
		try {
			options = new EchoOptions(args);
			if (options.isHelp()) {
				options.printHelp();
				return;
			}
		} catch (ErrorParser e) { 
			System.out.println("Error parsing CLI: "+e.getMessage());
			options.printHelp();
			return;
		}
		
		printer = new EchoPrinter(options);
		
		printer.printTitle("Parsing input files.");
		timer = new EchoTimer();
		
		parser = new EMFParser(options);
		parser.loadModels();
		if (options.isQVT()) parser.loadQVT();
		parser.loadInstances();
		
		timer.setTime("Parsing");
		printer.print("Files parsed ("+timer.getTime("Parsing")+"ms).");

		printer.printTitle("Processing metamodels.");

		translator = new EMF2Alloy(parser,options);
		printer.print("State signatures: "+translator.getModelStateSigs() +", "+translator.getInstanceStateSigs() +", "+translator.getTargetStateSig());

		translator.translateModels();

		printer.print("Model signatures: ");
		
		if(!options.isGenerate()) {
			printer.printTitle("Processing Instances.");
			translator.translateInstances();
		}
		
		printer.print("Instance signatures: "+translator.getInstanceSigs());
		printer.print("Instance facts: "+translator.getInstanceFact());
		
		if (options.isQVT()) {
			printer.printTitle("Processing QVT transformation "+parser.getTransformation().getName()+".");
			translator.translateQVT();

			printer.printForce("Running Alloy command: "+(options.isCheck()?"check.":("enforce "+parser.getTransformation().getName()+" on the direction of "+options.getDirection()+".")));

			printer.print("Delta function: "+translator.getDeltaFact());
			printer.print("Initial scope: "+translator.getScopes());
			printer.print("QVT facts: "+translator.getQVTFact());
		}

		translator.createScopes();
		
		timer.setTime("Translating");
		printer.print("Alloy model generated ("+timer.getTime("Translating")+"ms).");		
		
		AlloyRunner alloyrunner = new AlloyRunner(translator,options);
		
		boolean conforms = true;
		if (options.isConformance()) {
			alloyrunner.conforms();
			timer.setTime("Conforms");
			conforms = alloyrunner.getSolution().satisfiable();
			if (conforms)
				printer.printForce("Instances conform to the models ("+timer.getTime("Conforms")+"ms).");
			else
				printer.printForce("Instances do not conform to the models ("+timer.getTime("Conforms")+"ms).");
		} else if (options.isGenerate()) {
			alloyrunner.generate();
			timer.setTime("Generate");
			if (alloyrunner.getSolution().satisfiable())
				printer.printForce("Intance generated ("+timer.getTime("Generate")+"ms).");
			else
				printer.printForce("No possible instances ("+timer.getTime("Generate")+"ms).");
		} 
		if (options.isCheck() && conforms) {
			alloyrunner.check();
			timer.setTime("Check");
			if (alloyrunner.getSolution().satisfiable()) printer.printForce("Instances consistent ("+timer.getTime("Check")+"ms).");
			else printer.printForce("Instances inconsistent ("+timer.getTime("Check")+"ms).");
		} else if (options.isEnforce() && conforms) {
			alloyrunner.enforce();
			timer.setTime("Enforce");
			while (!alloyrunner.getSolution().satisfiable()) {
				printer.printForce("No instance found for delta "+alloyrunner.getDelta()+((options.isVerbose())?(" (for "+alloyrunner.getScopes()+", int "+alloyrunner.getIntScope()+")"):"")+" ("+timer.getTime("Enforce")+"ms).");
				alloyrunner.enforce();			
				timer.setTime("Enforce");
			}
			printer.printForce("Instance found for delta "+alloyrunner.getDelta()+" ("+timer.getTime("Enforce")+"ms).");
		}
		if ((options.isEnforce() || options.isGenerate()) && alloyrunner.getSolution().satisfiable()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			while (alloyrunner.getSolution().satisfiable()&&end.equals("y")) {
				alloyrunner.show();
				if(options.isOverwrite()) {
					String sb = parser.backUpTarget();
					printer.print("Backup file created: " + sb);
					translator.writeTargetInstance(alloyrunner.getSolution());
				}
				printer.printForce("Search another instance? (y)");
				alloyrunner.nextInstance();
				end = in.readLine(); 
			}
			in.close();
			alloyrunner.closeViz();
			if (end.equals("y")) printer.printForce("No more instances.");
		}
		printer.printForce("Bye ("+timer.getTotalTime()+"ms).");
	}
}

