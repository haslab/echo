package pt.uminho.haslab.echo;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.qvtparser.QvtParserRunner;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import pt.uminho.haslab.echo.transform.AlloyUtil;
import pt.uminho.haslab.echo.transform.QVT2Alloy;
import pt.uminho.haslab.echo.transform.XMI2Alloy;
import pt.uminho.haslab.echo.transform.ECore2Alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;

import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;


public class Echo {
	
	// metamodels
	private static Map<String,EPackage> mms = new HashMap<String,EPackage>();
	// instances
	private static Map<String,EObject> insts = new HashMap<String,EObject>();
	
	public static EObject loadModelInstance(String argURI,EPackage p) {

		ResourceSet load_resourceSet = new ResourceSetImpl();

		// Register XML Factory implementation to handle files with any extension
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap().put("*",
						new XMIResourceFactoryImpl());

		load_resourceSet.getPackageRegistry().put(p.getNsURI(),p);

		// Create empty resource with the given URI
		Resource load_resource = load_resourceSet.getResource(URI
				.createURI(argURI), true);
		return load_resource.getContents().get(0);
	}
	
	public static EObject loadObjectFromEcore(String uri)
	{
		ResourceSet load_resourceSet = new ResourceSetImpl();

		// Register XML Factory implementation to handle files with ecore extension
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			    "ecore", new EcoreResourceFactoryImpl());
		
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(load_resourceSet.getPackageRegistry());
		load_resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
		    extendedMetaData);

		// Create empty resource with the given URI
		Resource load_resource = load_resourceSet.getResource(URI
				.createURI(uri), true);
		
		return load_resource.getContents().get(0);
	}
		
	
	private static Transformation getTransformation(String qvtFile, String MMfile1, String MMfile2) throws Exception
	{
		List<String> metamodelFiles = new java.util.ArrayList<String>();
		metamodelFiles.add(MMfile1);
		metamodelFiles.add(MMfile2);
		
		PrintStream tmp=System.out;
		System.setOut(new PrintStream(new NullStream()));
		QvtParserRunner qvtRun = new QvtParserRunner(qvtFile, metamodelFiles);
		System.setOut(tmp);
		
		Transformation t = null;
		EObject o = qvtRun.getQvtModel().getModelElements().get(0);
		
		if(o instanceof Transformation)
		{	
			t = (Transformation) o;
		} else throw new Error("Parser failer: " + o.getClass());
		return t;
	}
	
	public static void main(String[] args) throws Exception{
		if (args.length != 7) throw new Error ("Wrong number of arguments: [mode] [qvt] [direction] [mm1] [inst1] [mm2] [inst2]\n" +
													"E.g. \"check UML2RDBMS.qvt UML UML.ecore PackageExample.xmi RDBMS.ecore SchemeExample.xmi\"");
		Boolean check;
		Expr deltaexpr = null;
		int delta = 0;
		String target = args[2];
		List<CommandScope> targetscopes = new ArrayList<CommandScope>();

		if (args[0].equals("check")) check = true;
		else if (args[0].equals("enforce")) check = false;
		else throw new Error ("Invalid running mode: should be \"check\" or \"enforce\"");

		// eventually should be an arbitrary number
		EPackage paux; EObject iaux;
		paux = (EPackage) loadObjectFromEcore(args[3]);
		mms.put(paux.getName(),paux);
		iaux = loadModelInstance(args[4],paux);
		insts.put(paux.getName(),iaux);
		
		paux = (EPackage) loadObjectFromEcore(args[5]);
		mms.put(paux.getName(),paux);
		iaux = loadModelInstance(args[6],paux);
		insts.put(paux.getName(),iaux);
		
		Expr modelfact = Sig.NONE.no();
		List<Sig> allsigs = new ArrayList<Sig>();
		allsigs.add(AlloyUtil.STATE);
		
		for (String name : mms.keySet()) {
			
			System.out.println("** Processing metamodel "+name+".");
			
			EPackage pck = mms.get(name);
			EObject inst = insts.get(name);
			boolean istarget = pck.getName().equals(target);
			if (inst == null || pck == null) throw new Error ("Bad file parsing");
			
			// generating state instances
			List<PrimSig> stateinstances; 
			// only the target needs an extra state instance, and only if enforce mode
			stateinstances = AlloyUtil.createStateSig(pck.getName(),istarget&&!check);
			
			System.out.println("State signatures: "+stateinstances);
				
			ECore2Alloy mmtrans = new ECore2Alloy(pck,stateinstances.get(0));
			
			List<PrimSig> sigList = mmtrans.getSigList();
			
			/*System.out.println("Metamodel signatures: "+sigList);
			for (Sig sig : sigList)
					System.out.println(sig.attributes);
			for(Sig s:sigList) {
				System.out.println("Factos de " + s + "  :");
				for(Expr f : s.getFacts())
					System.out.println(f); }*/
			
			// only the target needs the delta function and only if enforce mode
			if (istarget&&!check) { 
				deltaexpr = (mmtrans.getDeltaExpr(stateinstances.get(2),stateinstances.get(1)));
				System.out.println("Delta function: "+deltaexpr);
			}
			
			XMI2Alloy insttrans;
			insttrans = new XMI2Alloy(inst,mmtrans,"",stateinstances.get(istarget&&!check?1:0));
			
			//System.out.println("Singleton sigs (object instances):");
			//for(Sig s: insttrans.getSigList()) {
			//	System.out.println(((PrimSig) s).parent.toString()+" : "+s.toString());}

			List<PrimSig> instList = insttrans.getSigList();
			sigList.addAll(instList);
			
			System.out.println("Instance signatures: "+sigList);
			
			targetscopes = AlloyUtil.createScope(instList);
			System.out.println("Scope: "+targetscopes);
			
			Expr instFact = insttrans.getFact();
			
			modelfact = modelfact.and(instFact);
			
			System.out.println("Instance facts: "+instFact);
			
			allsigs.addAll(sigList);
			allsigs.addAll(stateinstances);
			
			System.out.println("");
		}
		
		Transformation qtrans = getTransformation(args[1],args[3],args[5]);
		if (qtrans == null) throw new Error ("Empty transformation.");

		System.out.println("** Processing QVT transformation "+qtrans.getName()+".");

		//TypedModel mdl = (TypedModel) qtrans.getModelParameter().get(0);
		QVT2Alloy qvtrans = new QVT2Alloy(qtrans.getModelParameter(),allsigs,qtrans);
		Expr qvtfact = qvtrans.getFact();
				
		System.out.println("QVT fact "+qvtfact);
		System.out.println("");		
		
		// starting Alloy
		A4Reporter rep = new A4Reporter() {
			// For example, here we choose to display each "warning" by printing it to System.out
			@Override public void warning(ErrorWarning msg) {
			System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
			System.out.flush();
			}
		};
		
		A4Options options = new A4Options();
		options.solver = A4Options.SatSolver.SAT4J;
		options.noOverflow = true;
		
		System.out.println("** Processing Alloy command: "+args[0]+" "+qtrans.getName()+" on the direction of "+target+".");

		Expr commandfact;
		if (check) commandfact = (modelfact.and(qvtfact));		 
		else commandfact = (modelfact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(delta)));		
		//commandfact = ExprConstant.TRUE;
		
		
		System.out.println("Final command fact: "+(commandfact));
		System.out.println("Final sigs: "+(allsigs)+"\n");

		System.out.println("** Running Alloy.");
		// enforce and check mode are run and check commands respectively
		Command cmd = new Command(false, 10, 5, 2, commandfact);


		A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);
		//sol = sol.next().next().next().next().next();
		
		
		if (check) {
			if (sol.satisfiable()) System.out.println("Instance found. Models consistent.");
			else System.out.println("No instance found..");
		} else {
			while (!sol.satisfiable()) {
				System.out.println("No instance found for delta "+delta+".");

				commandfact = (modelfact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(++delta)));
				//commandfact = ExprConstant.TRUE;

				// enforce and check mode are run and check commands respectively
				cmd = new Command(check, 0, 5, 2, commandfact);
				cmd = cmd.change(AlloyUtil.incrementScope(targetscopes));

				sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);
				//sol = sol1.next().next().next().next().next();
				
			}
			System.out.println("Instance found for delta "+delta+".");
				
		}
		
		
		if (sol.satisfiable()) {
			sol.writeXML("alloy_output.xml");
	        // opens the visualizer with the resulting model
			VizGUI viz = new VizGUI(true, "alloy_output.xml", null);
			String theme = (args[1]).replace(".qvt", ".thm");
			if (new File(theme).isFile())
				viz.loadThemeFile("Examples/UML2RDBMS/UML2RDBMS.thm");
		}
	}
	
	private static class NullStream extends OutputStream {
	    @Override
	    public void write(int b){ return; }
	    @Override
	    public void write(byte[] b){ return; }
	    @Override
	    public void write(byte[] b, int off, int len){ return; }
	    public NullStream(){}
	}
}

