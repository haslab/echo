package pt.uminho.haslab.echo;
import static edu.mit.csail.sdg.alloy4compiler.ast.Sig.UNIV;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.qvtparser.QvtParserRunner;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;

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

import pt.uminho.haslab.echo.transform.XMI2Alloy;
import pt.uminho.haslab.echo.transform.ECore2Alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;


public class Echo {
	
	// state sigs (instance,state)
	private static Map<PrimSig,PrimSig> sigStates = new HashMap<PrimSig,PrimSig>();
	
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
		
		QvtParserRunner qvtRun = new QvtParserRunner(qvtFile, metamodelFiles);
		Transformation t = null;
		EObject o = qvtRun.getQvtModel().getModelElements().get(0);
		
		if(o instanceof Transformation)
		{	
			System.out.println("yeah");
			t = (Transformation) o;
		}
		return t;
	}
	
	public static void main(String[] args) throws Exception{
		EPackage pck = (EPackage) loadObjectFromEcore(args[0]);
		//EPackage p2 = (EPackage) loadObjectFromEcore(args[2]);
		EObject ins = loadModelInstance(args[1],pck);
		
		
		A4Reporter rep = new A4Reporter() {
			// For example, here we choose to display each "warning" by printing it to System.out
			@Override public void warning(ErrorWarning msg) {
			System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
			System.out.flush();
			}
		};
		
		A4Options options = new A4Options();
		options.solver = A4Options.SatSolver.SAT4J;
		
		PrimSig statesig = createStateSig(pck.getName());
		PrimSig stateinstance = createStateInstance(statesig);
		sigStates.put(stateinstance, statesig);
		
		System.out.println("*** Processing metamodel.");
		ECore2Alloy t = new ECore2Alloy(pck,pck.getName() + "_",statesig);
		//Transformer t2 = new Transformer(p2,"bs_");
		
		List<Sig> sigList = t.getSigList();
		//sigList.addAll(t2.getSigList());
		
		for(Sig s:sigList)
		{
			System.out.println("Factos de " + s + "  :");
			for(Expr f : s.getFacts())
				System.out.println(f);
			System.out.println("___________________");
		}

		System.out.println("*** Processing instance.");
		XMI2Alloy inst = new XMI2Alloy(ins,t,"",stateinstance);
		//inst.print();
		System.out.println("Singleton sigs (object instances):");
		for(Sig s: inst.getSigList()) {
			if (!s.isTopLevel()) 
				System.out.println(((PrimSig) s).parent.toString()+" : "+s.toString());}
	
		sigList.addAll(inst.getSigList());
		
		for(Sig s:sigList)
		{
			System.out.println(s +" extends " + ((PrimSig) s).parent);
		}

		System.out.println("Command fact: \n "+ inst.getFact());
		System.out.println("Sig list: \n "+ sigList);
		
		Command cmd = new Command(false, 5, -1, -1, UNIV.some().and(inst.getFact()));
		System.out.println(cmd.getAllStringConstants(sigList));
		A4Solution sol1 = TranslateAlloyToKodkod.execute_command(rep, sigList, cmd, options);
		//sol1 = sol1.next().next().next().next().next();
		
		if (sol1.satisfiable()) {
			sol1.writeXML("alloy_output.xml");
	        // opens the visualizer with the resulting model
			new VizGUI(true, "alloy_output.xml", null);
		} else System.out.println("Formula not satisfiable.");
		
		getTransformation("Uml2Rdbms.qvt","UML.ecore","RDBMS.ecore");
		
	}
	
	private static PrimSig createStateSig(String sig) throws Err{
		PrimSig s = new PrimSig(sig,Attr.ABSTRACT);
		return s;
	}
	private static PrimSig createStateInstance(PrimSig sig) throws Err{
		PrimSig s = new PrimSig(sig.toString()+"1",sig,Attr.ONE);
		return s;
	}
}

