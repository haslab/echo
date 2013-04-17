package pt.uminho.haslab.echo.cli;


public class CLIPrinter {
	CLIOptions options;
	
	public CLIPrinter(CLIOptions options) {
		this.options = options;
	}
	
	public void printTitle(String o) {
		if (options.isVerbose())
			System.out.println("** "+o);
	}
	
	public void print(String o) {
		if (options.isVerbose())
			System.out.println(o);
	}
	
	public void printForce(String o) {
		System.out.println(o);
	}
	

	/*public static void print(){
		System.out.println("** States ");
		System.out.println("* Abstract state signatures: "+translator.getModelStateSigs());
		System.out.println("* Instance state signatures: "+translator.getInstanceStateSigs());
		System.out.println("** Models ");
		for(EPackage m: parser.getModels()){
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
	}*/
}
