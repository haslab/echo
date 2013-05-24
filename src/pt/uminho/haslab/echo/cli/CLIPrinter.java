package pt.uminho.haslab.echo.cli;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprCall;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.transform.EMF2Alloy;


public class CLIPrinter {
	CLIOptions options;
	
	public CLIPrinter(CLIOptions options) {
		this.options = options;
	}
	
	public void printTitle(String o) {
		System.out.println("** "+o);
	}
	
	public void print(String o) {
		if (options.isVerbose())
			System.out.println(o);
	}
	
	public void printForce(String o) {
		System.out.println(o);
	}
	

	public String printModel(EMF2Alloy translator) throws ErrorAlloy{
		StringBuilder sb = new StringBuilder();
		for (String uri : options.getModels()) {
			String name = translator.parser.getModelsFromUri(uri).getName();
			sb.append("* Meta-model "+name+"\n");
			sb.append("State sig: "+translator.getModelStateSig(name)+"\n");
			sb.append("Model sigs: "+translator.getModelSigs(name)+"\n");
			sb.append("Enum sigs: "+translator.getEnumSigsFromName(name)+"\n");
			ExprCall exp = (ExprCall) translator.getConformsAllInstances(uri);
			sb.append("Constraints: "+exp.fun.getBody()+"\n");
			sb.append("Delta exp: "+translator.getModelDeltaExpr(name).getBody()+"\n");
		}
		
		for (String uri : options.getInstances()) {
			sb.append("* Instance model "+uri+"\n");
			sb.append("State sig: "+translator.getInstanceStateSigFromURI(uri)+"\n");
			sb.append("Instance sigs: "+translator.getInstanceSigs(uri)+"\n");
			sb.append("Constraints: "+translator.getInstanceFact(uri)+"\n");
		}
		
		if (options.isQVT()) {
			sb.append("* QVT-R transformation "+options.getQVTPath()+"\n");
			ExprList x = (ExprList) translator.getQVTFact(options.getQVTPath()).getBody();
			for (Expr y : x.args) {
				if (y instanceof ExprCall)
					sb.append("Constraint: "+y+" : "+((ExprCall) y).fun.getBody()+"\n");
			
			}
			}

		return sb.toString();
	}
}
