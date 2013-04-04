package pt.uminho.haslab.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.emf.ecore.EPackage;

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class Echo {
	
	private static EchoOptions options;
	private static EMFParser parser;
	private static EMF2Alloy translator;
	
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
		
		if (options.isVerbose()) System.out.println("** Parsing input files.");
		long time = System.currentTimeMillis();
		long totaltime = time;
		
		parser = new EMFParser(options);
		parser.loadModels();
		if (options.isQVT()) parser.loadQVT(options.getQVTPath());
		parser.loadInstances();
		
		if (options.isVerbose()) System.out.println("Files parsed ("+(System.currentTimeMillis()-time)+"ms).");

		if (options.isVerbose()) System.out.println("** Processing metamodels.");
		totaltime = totaltime + time;
		time = System.currentTimeMillis();

		translator = new EMF2Alloy(parser,options);
		if (options.isVerbose()) System.out.println("State signatures: "+translator.getModelStateSigs() +", "+translator.getInstanceStateSigs() +", "+translator.getTargetStateSig());

		translator.translateModels();
		if (options.isVerbose()) {
			System.out.println("Model signatures: ");
			System.out.println("** Processing Instances.");
		}
		
		translator.translateInstances();
		if (options.isVerbose()) {
			System.out.println("Instance signatures: "+translator.getInstanceSigs());
			System.out.println("Instance facts: "+translator.getInstanceFact());
		}
		
		if (options.isQVT()) {
			if (options.isVerbose()) System.out.println("** Processing QVT transformation "+parser.getTransformation().getName()+".");
			translator.translateQVT();

			if (options.isVerbose()) System.out.println("Alloy model generated ("+(System.currentTimeMillis()-time)+"ms).");

			System.out.println("Running Alloy command: "+(options.isCheck()?"check.":("enforce "+parser.getTransformation().getName()+" on the direction of "+options.getDirection()+".")));

			if (options.isVerbose()) {
				System.out.println("Delta function: "+translator.getDeltaFact());
				System.out.println("Initial scope: "+translator.getTargetScopes());
				System.out.println("QVT facts: "+translator.getQVTFact());
			}
		}
		
		
		AlloyRunner alloyrunner = new AlloyRunner(translator,options);
		
		if (options.isConformance()) {
			totaltime = totaltime + time;
			time = System.currentTimeMillis();
			alloyrunner.conforms();
			if (alloyrunner.getSolution().satisfiable())
				System.out.println("Instances conform to the models ("+(System.currentTimeMillis()-time)+"ms).");
			else {
				System.out.println("Instances do not conform to the models ("+(System.currentTimeMillis()-time)+"ms).");
				return;
			}
		} if (options.isCheck()) {
			totaltime = totaltime + time;
			time = System.currentTimeMillis();
			alloyrunner.check();
			if (alloyrunner.getSolution().satisfiable()) System.out.println("Instances consistent ("+(System.currentTimeMillis()-time)+"ms).");
			else System.out.println("Instances inconsistent ("+(System.currentTimeMillis()-time)+"ms).");
		} else if (options.isEnforce()) {
			totaltime = totaltime + time;
			time = System.currentTimeMillis();
			alloyrunner.enforce();
			while (!alloyrunner.getSolution().satisfiable()) {
				System.out.println("No instance found for delta "+alloyrunner.getDelta()+((options.isVerbose())?(" (for "+alloyrunner.getScopes()+", int "+alloyrunner.getIntScope()+")"):"")+" ("+(System.currentTimeMillis()-time)+"ms).");
				alloyrunner.enforce();			
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			while (alloyrunner.getSolution().satisfiable()&&end.equals("y")) {		
				System.out.println("Instance found for delta "+alloyrunner.getDelta()+" ("+(System.currentTimeMillis()-time)+"ms).");
				alloyrunner.show();
				if(options.isOverwrite()) {
					String sb = parser.backUpTarget();
					if (options.isVerbose()) System.out.println("** Backup file created: " + sb);
					translator.writeTargetInstance(alloyrunner.getSolution());
				}
				System.out.println("Search another instance? (y)");
				alloyrunner.nextInstance();
				end = in.readLine(); 
			}
			in.close();
			alloyrunner.closeViz();
			if (end.equals("y")) System.out.println("No more instances for delta "+alloyrunner.getDelta()+".");
		}
	}
	
	
	public static void print(){
		System.out.println("** States ");
		System.out.println("* Abstract state signatures: "+translator.getModelStateSigs());
		System.out.println("* Instance state signatures: "+translator.getInstanceStateSigs());
		System.out.println("** Models ");
		for(EPackage m: parser.getPackages()){
			System.out.println("* Signatures for model "+m.getName());
			for(PrimSig s: translator.getModelSigs(m.getName())) {
				System.out.println(s.toString() + " : "+s.parent.toString()+" ("+s.attributes+")");
				System.out.println("Fields of "+s);
				for (Field f : s.getFields())
					System.out.println(f + " : " + f.type());
				System.out.println("Facts of "+s);
				for (Expr e : s.getFacts())
					System.out.println(e);
			}
		}
		System.out.println("** Instances ");
		System.out.println("* Instance signatures: "+translator.getInstanceSigs());
		System.out.println("* Instance fact: "+translator.getInstanceFact());

	}
}

