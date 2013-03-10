package pt.uminho.haslab.echo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.examples.pivot.delegate.OCLDelegateDomain;
import org.eclipse.ocl.examples.pivot.delegate.OCLInvocationDelegateFactory;
import org.eclipse.ocl.examples.pivot.delegate.OCLSettingDelegateFactory;
import org.eclipse.ocl.examples.pivot.delegate.OCLValidationDelegateFactory;
import org.eclipse.ocl.examples.pivot.model.OCLstdlib;
import org.eclipse.ocl.examples.pivot.utilities.PivotUtil;
import org.eclipse.ocl.examples.xtext.base.utilities.BaseCSResource;
import org.eclipse.ocl.examples.xtext.base.utilities.CS2PivotResourceAdapter;
import org.eclipse.ocl.examples.xtext.oclinecore.OCLinEcoreStandaloneSetup;
import org.eclipse.qvtd.pivot.qvtrelation.RelationModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.qvtd.xtext.qvtrelation.QVTrelationStandaloneSetup;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.uminho.haslab.echo.transform.AlloyUtil;
import pt.uminho.haslab.echo.transform.QVT2Alloy;
import pt.uminho.haslab.echo.transform.XMI2Alloy;
import pt.uminho.haslab.echo.transform.ECore2Alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
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
	private static Map<String,EPackage> metamodels = new HashMap<String,EPackage>();
	// instances
	private static Map<String,List<EObject>> instances = new HashMap<String,List<EObject>>();
	// model fact
	private static Expr modelfact = Sig.NONE.no();
	// delta fact
	private static Expr deltaexpr = null;
	// check vs. enforce
	private static Boolean check;
	// delta
	private static int delta = 0;
	// target scopes (only these need be increased)
	private static ConstList<CommandScope> targetscopes;
	// qvt file path
	private static String qvtpath;
	// execution direction
	private static String targetfile;
	private static String targetmodel;
	private static EObject targetinstance;

	private static Map<String,List<PrimSig>> statesigs = new HashMap<String,List<PrimSig>>();
	private static Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	private static Map<String,List<PrimSig>> instsigs = new HashMap<String,List<PrimSig>>();

	
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
		
	private static RelationalTransformation getTransformation(String qvtFile) throws Exception {
		CS2PivotResourceAdapter adapter = null;

	//	OCLstdlib.install();
		QVTrelationStandaloneSetup.doSetup();
		
		Injector injector = new QVTrelationStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
		BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(URI.createFileURI(qvtFile), true);

		String message = PivotUtil.formatResourceDiagnostics(xtextResource.getErrors(), "Error parsing QVT.", "\n\t");
		if (message != null) throw new ErrorParser (message,"QVT Parser");
		
		adapter = CS2PivotResourceAdapter.getAdapter(xtextResource, null);
		Resource pivotResource = adapter.getPivotResource(xtextResource);
				
		RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
		RelationalTransformation rt = (RelationalTransformation) rm.eContents().get(0);
		return rt;
	}
	
	
	public static void main(String[] args) throws Exception{
		if (args.length != 7) throw new Error ("Wrong number of arguments: [mode] [qvt] [direction] [mm1] [inst1] [mm2] [inst2]\n" +
												"E.g. \"check UML2RDBMS.qvt UML UML.ecore PackageExample.xmi RDBMS.ecore SchemeExample.xmi\"");
		qvtpath = args[1];
		targetfile = args[2];

		if (args[0].equals("check")) check = true;
		else if (args[0].equals("enforce")) check = false;
		else throw new ErrorParser ("Invalid running mode: should be \"check\" or \"enforce\"","Command Parser");

		//ocl starter
		// register Pivot globally (resourceSet == null)
		org.eclipse.ocl.examples.pivot.OCL.initialize(null);

		String oclDelegateURI = OCLDelegateDomain.OCL_DELEGATE_URI_PIVOT;
		EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLInvocationDelegateFactory.Global());
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLSettingDelegateFactory.Global());
		EValidator.ValidationDelegate.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLValidationDelegateFactory.Global());

		OCLinEcoreStandaloneSetup.doSetup();
		// install the OCL standard library
		OCLstdlib.install();
		
		// eventually should be an arbitrary number
		EPackage paux; EObject iaux;
		paux = (EPackage) loadObjectFromEcore(args[3]);
		metamodels.put(paux.getName(),paux);
		iaux = loadModelInstance(args[4],paux);
		if(instances.get(paux.getName())!=null) instances.get(paux.getName()).add(iaux);
		else instances.put(paux.getName(),new ArrayList<EObject>(Arrays.asList(iaux)));
		if(args[4].equals(targetfile)) {
			targetinstance = iaux;
			targetmodel = paux.getName();
		}
		
		paux = (EPackage) loadObjectFromEcore(args[5]);
		metamodels.put(paux.getName(),paux);
		iaux = loadModelInstance(args[6],paux);
		if(instances.get(paux.getName())!=null) instances.get(paux.getName()).add(iaux);
		else instances.put(paux.getName(),new ArrayList<EObject>(Arrays.asList(iaux)));
		if(args[6].equals(targetfile)) {
			targetinstance = iaux;
			targetmodel = paux.getName();
		}
		
		for (String name : metamodels.keySet()) {
			
			System.out.println("** Processing metamodel "+name+".");
			EPackage pck = metamodels.get(name);
			List<EObject> instmodel = instances.get(name);
			boolean istarget = name.equals(targetmodel);
			
			// generating state instances
			List<PrimSig> stateinstances = AlloyUtil.createStateSig(pck.getName(),instmodel.size(),istarget&&!check);			
			statesigs.put(name, stateinstances);
			
			// only the target needs an extra state instance, and only if enforce mode
			System.out.println("State signatures: "+stateinstances);
				
			ECore2Alloy mmtrans = new ECore2Alloy(pck,stateinstances.get(0));
			modelsigs.put(name,mmtrans.getSigList());
			

			for(Sig s:mmtrans.getSigList()) {
				System.out.println("Factos de " + s + "  :");
				for(Expr f : s.getFields()) System.out.println(f); 
				for(Expr f : s.getFacts()) System.out.println(f); 
				System.out.println(((PrimSig)s).parent);
			}
			
			List<PrimSig> modelinstsig = new ArrayList<PrimSig>();
					
			int instcounter = 1;
			for (EObject inst : instmodel) {
				istarget = inst.equals(targetinstance);
				PrimSig state = stateinstances.get(instcounter);
				
				// only the target needs the delta function and only if enforce mode
				if (istarget&&!check) { 
					deltaexpr = (mmtrans.getDeltaExpr(stateinstances.get(stateinstances.size()-1),state));
					System.out.println("Delta function: "+deltaexpr);
				}
				
				XMI2Alloy insttrans = new XMI2Alloy(inst,mmtrans,"",state);
				
				//System.out.println("Singleton sigs (object instances):");
				//for(Sig s: insttrans.getSigList()) {
				//	System.out.println(((PrimSig) s).parent.toString()+" : "+s.toString());}
	
				modelinstsig.addAll(insttrans.getSigList());
				System.out.println("Instance signatures: "+insttrans.getSigList());
				
				if (istarget&&!check) { 
					targetscopes = AlloyUtil.createScope(insttrans.getSigList());
					System.out.println("Scope: "+targetscopes);
				}
				
				modelfact = modelfact.and(insttrans.getFact());
				System.out.println("Instance facts: "+insttrans.getFact());
				System.out.println("");
				
				instcounter++;
			}
			instsigs.put(name,modelinstsig);
		}
		
		RelationalTransformation qtrans = getTransformation(qvtpath);
		if (qtrans == null) throw new Error ("Empty transformation.");

		System.out.println("** Processing QVT transformation "+qtrans.getName()+".");
		//System.out.println("* "+statesigs);
		//System.out.println("* "+modelsigs);

		QVT2Alloy qvtrans = new QVT2Alloy(qtrans.getModelParameter(),statesigs,modelsigs,qtrans);
		Expr qvtfact = Sig.NONE.no();
		Map<String,Expr> qvtfacts = qvtrans.getFact();
		for (String e : qvtfacts.keySet()){
			qvtfact = AlloyUtil.cleanAnd(qvtfact, qvtfacts.get(e));
			System.out.println(e +": "+qvtfacts.get(e));
		}
				
		System.out.println("QVT final: "+qvtfact);
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
		
		System.out.println("** Processing Alloy command: "+args[0]+" "+qtrans.getName()+" on the direction of "+targetfile+".");

		Expr commandfact = modelfact;
		//if (check) commandfact = (commandfact.and(qvtfact));		 
		//else commandfact = (commandfact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(delta)));		
		int intscope = 1;
		
		List<Sig> allsigs = new ArrayList<Sig>(Arrays.asList(AlloyUtil.STATE));
		for (String x : statesigs.keySet()){
			allsigs.addAll(statesigs.get(x));
			allsigs.addAll(modelsigs.get(x));
			allsigs.addAll(instsigs.get(x));			
		}
		
		System.out.println("Final command fact: "+(commandfact));
		System.out.println("Final sigs: "+(allsigs)+"\n");

		System.out.println("** Running Alloy.");
		// enforce and check mode are run and check commands respectively
		Command cmd = new Command(check, 5, 5, intscope, commandfact);

		A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);
		
		if (check) {
			if (sol.satisfiable()) System.out.println("Instance found. Models consistent.");
			else System.out.println("Instance not found. Models inconsistent.");
		} else {
			while (!sol.satisfiable()) {
				System.out.println("No instance found for delta "+delta+" ("+targetscopes+", int "+intscope+").");

				commandfact = (modelfact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(++delta)));

				// calculates integer bitwidth
				intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
				// enforce and check mode are run and check commands respectively
				cmd = new Command(check, 5, intscope, -1, commandfact);
				// increases the target signatures' scopes
				targetscopes = AlloyUtil.incrementScopes(targetscopes);
				cmd = cmd.change(targetscopes);
				
				sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);				
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
			VizGUI viz = new VizGUI(true, "", null);
			String theme = (qvtpath).replace(".qvtr", ".thm");
		
			while (sol.satisfiable()) {		
				System.out.println("Instance found for delta "+delta+".");
				sol.writeXML("alloy_output.xml");
		        // opens the visualizer with the resulting model
				viz.loadXML("alloy_output.xml", true);
				if (new File(theme).isFile()) viz.loadThemeFile(theme);
				in.readLine(); 
				sol = sol.next();
			}
			in.close();
			System.out.println("No more instances for delta "+delta+".");
		}
	}
	
}

