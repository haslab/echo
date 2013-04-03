package pt.uminho.haslab.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class Echo {
	
	private static EchoOptions options;
	private static EMFParser parser = new EMFParser();
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

		for (String path : options.getModels())
			parser.loadPackage(path);
		
		if (!options.isConformance()){
			parser.loadQVT(options.getQVTPath());
			if (parser.getTransformation() == null) throw new Error ("Empty transformation.");
		
			int i = 0;
			for (TypedModel mdl : parser.getTransformation().getModelParameter()) {
				if (options.isVerbose()) System.out.println(mdl.getName() +" : "+options.getInstances()[i]);
				parser.loadObject(options.getInstances()[i++],mdl.getName());
			}
		} else {
			for (String uri : options.getInstances())
				parser.loadObject(uri);
		}

		if (options.isVerbose()) System.out.println("\n** Processing metamodels.");

		translator = new EMF2Alloy(parser,options);
		if (options.isVerbose()) System.out.println("State signatures: "+translator.getStateSignatures() +", "+translator.getInstanceStateSignatures() +", "+translator.getTargetSig());

		translator.translateMetaModels();
		if (options.isVerbose()) System.out.println("Model signatures: ");
		if (options.isVerbose()) System.out.println("\n** Processing Instances.");

		translator.translateInstances();
		

		if (!options.isConformance()) {
			if (options.isVerbose()) System.out.println("\n** Processing QVT transformation "+parser.getTransformation().getName()+".");
			translator.translateQVT();
			System.out.println("Running Alloy command: "+(options.isCheck()?"check.":("enforce "+parser.getTransformation().getName()+" on the direction of "+options.getDirection()+".")));
		}
		
		AlloyRunner alloyrunner = new AlloyRunner(translator,options);
		
		if (options.isConformance()) {
			alloyrunner.conforms();
			if (alloyrunner.getSolution().satisfiable()) {
				System.out.println("Instance found. Models consistent.");
				alloyrunner.show();
			}
			else System.out.println("Instance not found. Models inconsistent.");
		} else if (options.isCheck()) {
			alloyrunner.check();
			if (alloyrunner.getSolution().satisfiable()) System.out.println("Instance found. Models consistent.");
			else System.out.println("Instance not found. Models inconsistent.");
		} else {
			alloyrunner.enforce();
			while (!alloyrunner.getSolution().satisfiable()) {
				System.out.println("No instance found for delta "+alloyrunner.getDelta()+((options.isVerbose())?(" (for "+alloyrunner.getScopes()+", int "+alloyrunner.getIntScope()+")."):""));
				alloyrunner.enforce();			
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			String end = "y";
			while (alloyrunner.getSolution().satisfiable()&&end.equals("y")) {		
				System.out.println("Instance found for delta "+alloyrunner.getDelta()+".");
				alloyrunner.show();
				if(options.isOverwrite()) {
					String sb = parser.backUpTarget(options.getDirection());
					if (options.isVerbose()) System.out.println("** Backup file created: " + sb);
					translator.writeInstance(alloyrunner.getSolution());
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
		System.out.println("* Abstract state signatures: "+translator.getStateSignatures());
		System.out.println("* Instance state signatures: "+translator.getInstanceStateSignatures());
		System.out.println("** Models ");
		for(EPackage m: parser.getPackages()){
			System.out.println("* Signatures for model "+m.getName());
			for(Sig s: translator.getModelSignatures(m.getName())) {
				System.out.println(s.toString() + " : "+((PrimSig) s).parent.toString()+" ("+s.attributes+")");
				System.out.println("Fields of "+s);
				for (Field f : s.getFields())
					System.out.println(f + " : " + f.type());
				System.out.println("Facts of "+s);
				for (Expr e : s.getFacts())
					System.out.println(e);
			}
		}
		System.out.println("** Instances ");
		System.out.println("* Instance signatures: "+translator.getInstanceSignatures());
		System.out.println("* Instance fact: "+translator.getInstanceFact());

	}
}

