package pt.uminho.haslab.echo;
import static edu.mit.csail.sdg.alloy4compiler.ast.Sig.UNIV;

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
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
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
		
		QvtParserRunner qvtRun = new QvtParserRunner(qvtFile, metamodelFiles);
		Transformation t = null;
		EObject o = qvtRun.getQvtModel().getModelElements().get(0);
		
		if(o instanceof Transformation)
		{	
			System.out.println("yeah");
			t = (Transformation) o;
		} else throw new Error("Parser failer: " + o.getClass());
		return t;
	}
	
	public static void main(String[] args) throws Exception{
		// eventually should be an arbitrary number
		EPackage paux; EObject iaux;
		paux = (EPackage) loadObjectFromEcore(args[0]);
		mms.put(paux.getName(),paux);
		iaux = loadModelInstance(args[1],paux);
		insts.put(paux.getName(),iaux);

		paux = (EPackage) loadObjectFromEcore(args[2]);
		mms.put(paux.getName(),paux);
		iaux = loadModelInstance(args[3],paux);
		insts.put(paux.getName(),iaux);
		
		A4Reporter rep = new A4Reporter() {
			// For example, here we choose to display each "warning" by printing it to System.out
			@Override public void warning(ErrorWarning msg) {
			System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
			System.out.flush();
			}
		};
		
		A4Options options = new A4Options();
		options.solver = A4Options.SatSolver.SAT4J;
		
		Expr commandfact = Sig.NONE.no();
		List<Sig> allsigs = new ArrayList<Sig>();
		
		for (String name : mms.keySet()) {
			EPackage pck = mms.get(name);
			EObject inst = insts.get(name);
			if (inst == null || pck == null) throw new Error ("Bad file parsing");
			
			// generating state instances
			PrimSig stateinstance = AlloyUtil.createStateSig(pck.getName(),false).get(0);
			
			ECore2Alloy mmtrans = new ECore2Alloy(pck);
			
			List<Sig> sigList = mmtrans.getSigList();
			
			for(Sig s:sigList) {
				System.out.println("Factos de " + s + "  :");
				for(Expr f : s.getFacts())
					System.out.println(f); }
			
			XMI2Alloy insttrans = new XMI2Alloy(inst,mmtrans,"",stateinstance);

			System.out.println("Singleton sigs (object instances):");
			for(Sig s: insttrans.getSigList()) {
				System.out.println(((PrimSig) s).parent.toString()+" : "+s.toString());}
		
			sigList.addAll(insttrans.getSigList());
			
			System.out.println("Command fact: "+ insttrans.getFact());
			System.out.println("Sig list: "+ sigList);	
		
			commandfact = commandfact.and(insttrans.getFact());
			allsigs.addAll(sigList);
		}
		
		Transformation qtrans = getTransformation("Examples/UML2RDBMS/UML2RDBMS.qvt","Examples/UML2RDBMS/UML.ecore","Examples/UML2RDBMS/RDBMS.ecore");
		if (qtrans == null) throw new Error ("Empty transformation.");
		// randomly chosen target
		TypedModel mdl = (TypedModel) qtrans.getModelParameter().get(0);
		QVT2Alloy qvtrans = new QVT2Alloy(mdl, qtrans, allsigs);
		// if Alloy isn't satisfiable, try to remove this :)
		commandfact = commandfact.and(qvtrans.getFact());
		
		Command cmd = new Command(false, 5, -1, -1, UNIV.some().and(commandfact));
		A4Solution sol1 = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);
		//sol1 = sol1.next().next().next().next().next();
		
		if (sol1.satisfiable()) {
			sol1.writeXML("alloy_output.xml");
	        // opens the visualizer with the resulting model
			new VizGUI(true, "alloy_output.xml", null);
		} else System.out.println("Formula not satisfiable.");			
	}
}

