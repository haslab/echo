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
			ExprList x = (ExprList) translator.getQVTFact(options.getQVTURI()).getBody();
			for (Expr y : x.args) {
				if (y instanceof ExprCall)
					sb.append("Constraint: "+y+" : "+((ExprCall) y).fun.getBody()+"\n");
			
			}
			}

		return sb.toString();
	}
}
