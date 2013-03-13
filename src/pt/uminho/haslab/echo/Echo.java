package pt.uminho.haslab.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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
import org.eclipse.emf.ecore.xmi.XMIResource;
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
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.qvtd.xtext.qvtrelation.QVTrelationStandaloneSetup;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.uminho.haslab.echo.transform.Alloy2XMI;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class Echo {
	
	// target scopes (only these need be increased)
	private static ConstList<CommandScope> targetscopes;
	// qvt file path
	private static String qvtpath;
	// execution direction
	private static String target;

	// the qvt transformation being processed
	private static RelationalTransformation qvttrans;
	// the model arguments of the qvt transformation
	private static List<TypedModel> qvttransargs;
	// the transformation metamodels (package name, epackage)
	private static Map<String,EPackage> metamodels = new HashMap<String,EPackage>();
	// the transformation instances (qvt argument name, eobject)
	private static Map<String,EObject> instances = new HashMap<String,EObject>();
	// running mode (check vs. enforce)
	private static Boolean check;
	
	// the state model signatures (one for each metamodel)
	private static Map<String,PrimSig> statesigs;
	// the state instance signatures (one for each instance)
	private static Map<String,PrimSig> stateinstancesigs;
	// the model signatures (a set for each metamodel)
	private static Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	// the instance signatures (a set for each instance)
	private static Map<String,List<PrimSig>> instsigs = new HashMap<String,List<PrimSig>>();
	

	private static Map<String,ECore2Alloy> mmtranses = new HashMap<String,ECore2Alloy>();
	
	private static EObject loadModelInstance(String argURI,EPackage p) {

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
	
	private static EObject loadObjectFromEcore(String uri)
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
		
	private static RelationalTransformation getTransformation(Map<String,String> packpaths) throws Exception {
		CS2PivotResourceAdapter adapter = null;

	//	OCLstdlib.install();
		QVTrelationStandaloneSetup.doSetup();
		
		Injector injector = new QVTrelationStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
		Scanner scan = new Scanner(new File(qvtpath));
		String qvtcontent = scan.useDelimiter("\\Z").next();
		scan.close();
		 
		File qvtaux = new File("aux.qvtr");
		qvtaux.createNewFile();
		FileWriter out = new FileWriter(qvtaux, true);
	    BufferedWriter fbw = new BufferedWriter(out);
	    for (Entry<String,String> mdl : packpaths.entrySet())
	    	fbw.write("import "+mdl.getKey()+" : \'"+mdl.getValue()+"\'::"+mdl.getKey()+";\n\n");
        fbw.write(qvtcontent);
        fbw.close();
        
		BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(URI.createFileURI("aux.qvtr"), true);
        qvtaux.delete();

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
		target = args[2];

		if (args[0].equals("check")) check = true;
		else if (args[0].equals("enforce")) check = false;
		else throw new ErrorParser ("Invalid running mode: should be \"check\" or \"enforce\"","Command Parser");

		System.out.println("** Parsing input files.");

		// ocl starter
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
		
		Map<String,String> packagepaths = new HashMap<String,String>();
		for(int i = 3; i<args.length; i++){
			EPackage paux = (EPackage) loadObjectFromEcore(args[i]);
			metamodels.put(paux.getName(),paux);
			packagepaths.put(paux.getName(), args[i]);
			i++;
		}
		
		qvttrans = getTransformation(packagepaths);
		if (qvttrans == null) throw new Error ("Empty transformation.");
		qvttransargs = qvttrans.getModelParameter();

		// parsing models/instances
		// in order to the number of the qvt-transformation arguments

		int instcounter = 4;
		for (TypedModel modelarg : qvttransargs) {
			EObject iaux = loadModelInstance(args[instcounter],metamodels.get(modelarg.getUsedPackage().get(0).getName()));
			instances.put(modelarg.getName(),iaux);
			instcounter = instcounter+2;
		}
		
		System.out.println("** Processing metamodels.");

		statesigs = AlloyUtil.createStateSig(qvttransargs);
		stateinstancesigs = AlloyUtil.createStateInstSig(statesigs, qvttransargs);
		PrimSig trgsig = AlloyUtil.createTargetState(stateinstancesigs,target);
		System.out.println("State signatures: "+stateinstancesigs.values());

		for (String name : statesigs.keySet()) {
			EPackage epck = metamodels.get(name);
			ECore2Alloy mmtrans = new ECore2Alloy(epck,statesigs.get(name));
			modelsigs.put(name,mmtrans.getSigList());
			mmtranses.put(name,mmtrans);
			/*for(Sig s:mmtrans.getSigList()) {
				System.out.println("Factos de " + s + "  :");
				for(Expr f : s.getFields()) System.out.println(f); 
				for(Expr f : s.getFacts()) System.out.println(f); 
				System.out.println(((PrimSig)s).parent);
			}*/			
		}		
		System.out.println("Model signatures: "+modelsigs);
		Expr deltaexpr = Sig.NONE.no(),instancefact = Sig.NONE.no();

		ECore2Alloy trgMM = null;
		XMI2Alloy trgIns = null;
		
		for (TypedModel modelarg: qvttransargs) {
			String name = modelarg.getName();
			String mdl = modelarg.getUsedPackage().get(0).getName();
			PrimSig state = stateinstancesigs.get(name);
			boolean istarget = name.equals(target);
			ECore2Alloy mmtrans = mmtranses.get(mdl);
			
			EObject instmodel = instances.get(name);
			XMI2Alloy insttrans = new XMI2Alloy(instmodel,mmtrans,"",state);
			// only the target needs the delta function and scopes, and only if enforce mode
			if (istarget&&!check) { 
				deltaexpr = (mmtrans.getDeltaExpr(trgsig,state));
				System.out.println("Delta function: "+deltaexpr);
				targetscopes = AlloyUtil.createScope(insttrans.getSigList());
				System.out.println("Scope: "+targetscopes);
				trgIns = insttrans;
				trgMM = mmtrans;
			}

			instsigs.put(name,insttrans.getSigList());
			instancefact = AlloyUtil.cleanAnd(instancefact,insttrans.getFact());
			
			System.out.println("Instance signatures: "+insttrans.getSigList());
			System.out.println("Instance facts: "+insttrans.getFact());
		}

		System.out.println("** Processing QVT transformation "+qvttrans.getName()+".");
		System.out.println("* "+stateinstancesigs);
		System.out.println("* "+statesigs);
		System.out.println("* "+modelsigs);
		System.out.println("* "+instsigs);
		
		Map<String,PrimSig> sigaux = new HashMap<String, PrimSig>(stateinstancesigs);
		sigaux.putAll(statesigs);
		sigaux.put(target, trgsig);
		QVT2Alloy qvtrans = new QVT2Alloy(sigaux,modelsigs,qvttrans);
		Expr qvtfact = Sig.NONE.no();
		Map<String,Expr> qvtfacts = qvtrans.getFact();
		for (String e : qvtfacts.keySet()){
			qvtfact = AlloyUtil.cleanAnd(qvtfact, qvtfacts.get(e));
			System.out.println(e +": "+qvtfacts.get(e));
		}	
		
		System.out.println("** Processing Alloy command: "+args[0]+" "+qvttrans.getName()+" on the direction of "+target+".");

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

		int intscope = 1, delta = 0;

		Expr commandfact = instancefact;
		if (check) commandfact = (commandfact.and(qvtfact));		 
		else commandfact = (commandfact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(delta)));		
		
		List<Sig> allsigs = new ArrayList<Sig>(Arrays.asList(AlloyUtil.STATE));
		for (String x : instsigs.keySet()){
			allsigs.add(stateinstancesigs.get(x));
			allsigs.addAll(instsigs.get(x));			
		}
		for (String x : modelsigs.keySet()){
			allsigs.add(statesigs.get(x));
			allsigs.addAll(modelsigs.get(x));
		}
		allsigs.add(trgsig);
		print(allsigs);

		
		System.out.println("Final command fact: "+(commandfact));
		System.out.println("Final sigs: "+(allsigs)+"\n");

		System.out.println("** Running Alloy.");
		// enforce and check mode are run and check commands respectively
		Command cmd = new Command(check, 0, intscope, -1, commandfact);

		A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);
		
		if (check) {
			if (sol.satisfiable()) System.out.println("Instance found. Models consistent.");
			else System.out.println("Instance not found. Models inconsistent.");
		} else {
			while (!sol.satisfiable()) {
				System.out.println("No instance found for delta "+delta+" ("+targetscopes+", int "+intscope+").");

				commandfact = (instancefact.and(qvtfact)).and(deltaexpr.equal(ExprConstant.makeNUMBER(++delta)));

				// calculates integer bitwidth
				intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
				// enforce and check mode are run and check commands respectively
				cmd = new Command(check, 0, intscope, -1, commandfact);
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
				
				//saving the result
				saveEObject(new Alloy2XMI(sol,trgIns,trgMM,trgsig).getModel());
				
				
				in.readLine(); 
				sol = sol.next();
			}
			in.close();
			System.out.println("No more instances for delta "+delta+".");
		}
	}
	
	public static void saveEObject(EObject obj)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    "*", new  XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI.createURI("./result.xmi"));
		resource.getContents().add(obj);

		/*
		* Save the resource using OPTION_SCHEMA_LOCATION save option toproduce 
		* xsi:schemaLocation attribute in the document
		*/
		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		try{
		     resource.save(options);
		   }catch (IOException e) {
		     e.printStackTrace();
		   }
	}
	
	public static void print(List<Sig> allsigs){
		for(Sig s: allsigs) {
			System.out.println(s.toString() + " : "+((PrimSig) s).parent.toString()+" ("+s.attributes+")");
			for (Field f : s.getFields())
				System.out.println(f + " : " + f.type());
			for (Expr e : s.getFacts())
				System.out.println(e);
		}
	}
}

