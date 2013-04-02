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

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.transform.Alloy2XMI;
import pt.uminho.haslab.echo.transform.QVTTransformation2Alloy;
import pt.uminho.haslab.echo.transform.XMI2Alloy;
import pt.uminho.haslab.echo.transform.ECore2Alloy;

import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class Echo {
	
	private static EchoOptions options;
	private static EMFParser parser = new EMFParser();
	

	// target scopes (only these need be increased)
	private static ConstList<CommandScope> targetscopes;

	// the qvt transformation being processed
	private static RelationalTransformation qvttrans;
	
	// the state model signatures (one for each metamodel)
	private static Map<String,PrimSig> statesigs;
	// the state instance signatures (one for each instance)
	private static Map<String,PrimSig> stateinstancesigs;
	// the model signatures (a set for each metamodel)
	private static Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	// the instance signatures (a set for each instance)
	private static Map<String,List<PrimSig>> instsigs = new HashMap<String,List<PrimSig>>();
	
	private static Expr instancefact = Sig.NONE.no();
	
	private static Map<String,ECore2Alloy> mmtranses = new HashMap<String,ECore2Alloy>();
	

	
	public static void main(String[] args) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform, Err, IOException {	
		

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
			qvttrans = parser.loadQVT(options.getQVTPath());
			if (qvttrans == null) throw new Error ("Empty transformation.");
		
			int i = 0;
			for (TypedModel mdl : qvttrans.getModelParameter()) {
				if (options.isVerbose()) System.out.println(mdl.getName() +" : "+options.getInstances()[i]);
				parser.loadObject(options.getInstances()[i++],mdl.getName());
			}
		}

		if (options.isVerbose()) System.out.println("\n** Processing metamodels.");

		statesigs = AlloyUtil.createStateSig(qvttrans.getModelParameter());
		stateinstancesigs = AlloyUtil.createStateInstSig(statesigs, qvttrans.getModelParameter());
		PrimSig trgsig = null;
		if (!options.isCheck()) trgsig = AlloyUtil.createTargetState(stateinstancesigs,options.getDirection());
		if (options.isVerbose()) System.out.println("State signatures: "+statesigs +", "+stateinstancesigs +", "+trgsig);

		for (EPackage epck : parser.getPackages()) {
			ECore2Alloy mmtrans = new ECore2Alloy(epck,statesigs.get(epck.getName()));
			modelsigs.put(epck.getName(),mmtrans.getSigList());
			mmtranses.put(epck.getName(),mmtrans);
		}		
		if (options.isVerbose()) System.out.println("Model signatures: "+modelsigs);
		Expr deltaexpr = Sig.NONE.no();

		ECore2Alloy trgMM = null;
		XMI2Alloy trgIns = null;
		
		if (options.isVerbose()) System.out.println("\n** Processing Instances.");

		for (TypedModel modelarg: qvttrans.getModelParameter()) {
			String name = modelarg.getName();
			String mdl = modelarg.getUsedPackage().get(0).getName();
			PrimSig state = stateinstancesigs.get(name);
			boolean istarget = name.equals(options.getDirection());
			ECore2Alloy mmtrans = mmtranses.get(mdl);
			
			EObject instmodel = parser.getObjectVar(name);
			XMI2Alloy insttrans = new XMI2Alloy(instmodel,mmtrans,state);
			// only the target needs the delta function and scopes, and only if enforce mode
			if (istarget&&!options.isCheck()) { 
				deltaexpr = (mmtrans.getDeltaExpr(trgsig,state));
				if (options.isVerbose()) System.out.println("Delta function: "+deltaexpr);
				targetscopes = AlloyUtil.createScope(insttrans.getSigList(),mmtrans.getSigList());
				if (options.isVerbose()) System.out.println("Initial scope: "+targetscopes);
				trgIns = insttrans;
				trgMM = mmtrans;
			}

			instsigs.put(name,insttrans.getSigList());
			instancefact = AlloyUtil.cleanAnd(instancefact,insttrans.getFact());
			
			if (options.isVerbose()) System.out.println("Instance signatures: "+insttrans.getSigList());
			if (options.isVerbose()) System.out.println("Instance facts: "+insttrans.getFact());
		}

		if (options.isVerbose()) System.out.println("\n** Processing QVT transformation "+qvttrans.getName()+".");
		
		Map<String,Expr> sigaux = new HashMap<String, Expr>(stateinstancesigs);
		sigaux.putAll(statesigs);
		if (!options.isCheck()) AlloyUtil.insertTargetState(sigaux, options.getDirection(), trgsig);
		QVTTransformation2Alloy qvtrans = new QVTTransformation2Alloy(sigaux,modelsigs,qvttrans);
		Expr qvtfact = Sig.NONE.no();
		Map<String,Expr> qvtfacts = qvtrans.getFact();
		for (String e : qvtfacts.keySet()){
			qvtfact = AlloyUtil.cleanAnd(qvtfact, qvtfacts.get(e));
			if (options.isVerbose()) System.out.println(e +": "+qvtfacts.get(e));
		}	
		
		System.out.println("Running Alloy command: "+(options.isCheck()?"check.":("enforce "+qvttrans.getName()+" on the direction of "+options.getDirection()+".")));

		List<Sig> allsigs = new ArrayList<Sig>(Arrays.asList(AlloyUtil.STATE));
		for (String x : instsigs.keySet()){
			allsigs.add(stateinstancesigs.get(x));
			allsigs.addAll(instsigs.get(x));			
		}
		for (String x : modelsigs.keySet()){
			allsigs.add(statesigs.get(x));
			allsigs.addAll(modelsigs.get(x));
		}
		if (!options.isCheck()) allsigs.add(trgsig);

		//EObject trgCopy = EcoreUtils.copy(trgIns.eObj);
		
		AlloyRunner alloyrunner = new AlloyRunner(allsigs,instancefact.and(qvtfact),deltaexpr,targetscopes,options);
		
		if (options.isCheck()) {
			alloyrunner.check();
			if (alloyrunner.getSolution().satisfiable()) System.out.println("Instance found. Models consistent.");
			else System.out.println("Instance not found. Models inconsistent.");
		} else {
			
			String oldPath = trgIns.getRootEObject().eResource().getURI().toString();
			StringBuilder sb = new StringBuilder(oldPath);
			sb.insert(sb.length()-4,".old");
			saveEObject(trgIns.getRootEObject(),sb.toString());
			
			if (options.isVerbose()) System.out.println("** Backup file created: " +  sb);
			
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
					Alloy2XMI a2x = new Alloy2XMI(alloyrunner.getSolution(),trgIns,trgMM,trgsig);
					saveEObject(a2x.getModel(),oldPath);
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
	
	public static void saveEObject(EObject obj,String path)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    "*", new  XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI.createURI(path));
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
	
	public static void print(){
		System.out.println("** States ");
		System.out.println("* Abstract state signatures: "+statesigs);
		System.out.println("* Instance state signatures: "+stateinstancesigs);
		System.out.println("** Models ");
		for(String m: modelsigs.keySet()){
			System.out.println("* Signatures for model "+m);
			for(Sig s: modelsigs.get(m)) {
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
		System.out.println("* Instance signatures: "+instsigs.values());
		System.out.println("* Instance fact: "+instancefact);

	}
}

