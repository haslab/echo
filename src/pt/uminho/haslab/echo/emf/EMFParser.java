package pt.uminho.haslab.echo.emf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
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

import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorParser;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Injector;

public class EMFParser {

	/** the ECore resource set */
	private ResourceSet resourceSet = new ResourceSetImpl();

	/** the loaded metamodels */
	private Map<String,EPackage> models = new HashMap<String,EPackage>();
	/** the loaded instances (key is the XMI file uri) */
	private Map<String,EObject> instances = new HashMap<String,EObject>();
	/** the loaded QVT-R transformation */
	private Map<String,RelationalTransformation> transformations = new HashMap<String, RelationalTransformation>();
	private BiMap<String,String> modelpaths;

	
	public EMFParser(EchoOptions options){
		/*
		// register Pivot globally
		org.eclipse.ocl.examples.pivot.OCL.kialize(resourceSet);

		String oclDelegateURI = OCLDelegateDomain.OCL_DELEGATE_URI_PIVOT;
		EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLInvocationDelegateFactory.Global());
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLSettingDelegateFactory.Global());
		EValidator.ValidationDelegate.Registry.INSTANCE.put(oclDelegateURI,
		    new OCLValidationDelegateFactory.Global());*/
		
		OCLinEcoreStandaloneSetup.doSetup();
		// install the OCL standard library 		
		OCLstdlib.install();
		
		// Register XML Factory implementation to handle files with ecore extension
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
		resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);

		// Register XML Factory implementation to handle files with any extension
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*",new XMIResourceFactoryImpl());
	
		
		
		modelpaths = HashBiMap.create();

	}
	
	/**
	 * Loads the EObject its uri
	 */
	public EObject loadInstance(String uri) {
		Resource load_resource = resourceSet.createResource(URI.createURI(uri));
		/*Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);*/
		try {
			load_resource.load(resourceSet.getLoadOptions());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EObject res = load_resource.getContents().get(0);
		System.out.println(load_resource.getContents().size());
		instances.put(uri,res);
		return res;
	}

	/**
	 * Loads the EPackages its uri
	 */
	public EPackage loadModel(String uri) {
		Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);
	
		EPackage res = (EPackage) load_resource.getContents().get(0);
		
		resourceSet.getPackageRegistry().put(res.getNsURI(),res);
		models.put(uri,res);	
		modelpaths.put(uri, res.getName());
		
		return res;
	}
	
	/**
	 * Loads the QVT specification from the CLI argument
	 */
	public RelationalTransformation loadQVT(String uri) throws ErrorParser {
		CS2PivotResourceAdapter adapter = null;

		QVTrelationStandaloneSetup.doSetup();
		
		Injector injector = new QVTrelationStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
		try{
			Scanner scan = new Scanner(new File(uri));
			String qvtcontent = scan.useDelimiter("\\Z").next();
			scan.close();
			File qvtaux = new File("aux.qvtr");
			qvtaux.createNewFile();
			FileWriter out = new FileWriter(qvtaux, true);
		    BufferedWriter fbw = new BufferedWriter(out);
		    for (String u: models.keySet())
		    	fbw.write("import "+models.get(u).getName()+" : \'"+u+"\'::"+models.get(u).getName()+";\n\n");

		    fbw.write(qvtcontent);
	        fbw.close();

			BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(URI.createFileURI("aux.qvtr"), true);
	        qvtaux.delete();
		
			String message = PivotUtil.formatResourceDiagnostics(xtextResource.getErrors(), "Error parsing QVT.", "\n\t");
			if (message != null) throw new ErrorParser (message,"QVT Parser");
			
			adapter = CS2PivotResourceAdapter.getAdapter(xtextResource, null);
			Resource pivotResource = adapter.getPivotResource(xtextResource);
					
			RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
			RelationalTransformation transformation = (RelationalTransformation) rm.eContents().get(0);
			/*argpaths = HashBiMap.create();
			int j = 0;
			for (int i = 0; i < transformation.getModelParameter().size(); i++){
				String arg = transformation.getModelParameter().get(i).getName();
				/* if (options.isNew() && arg.equals(options.getDirection())) {
					String nuri = "New.xmi";
					Package mdl = transformation.getModelParameter(options.getDirection()).getUsedPackage().get(0);
					String mdluri = modelpaths.inverse().get(mdl.getName());
					EPackage pck = models.get(mdluri);
					Resource resource = resourceSet.createResource(URI.createURI(nuri));
					EObject obj = pck.getEFactoryInstance().create(getTopObject(mdluri).get(0));
					resource.getContents().add(obj);
					argpaths.put(nuri, arg);
					instances.put(nuri, obj);
				}
				else argpaths.put(args.get(j++),arg);	
			}*/

			transformations.put(uri, transformation);
			return transformation;
		} catch (Exception e) { throw new ErrorParser (e.getMessage(),"QVT Parser");}
		
	}
	
	public RelationalTransformation getTransformation(String uri){
		return transformations.get(uri);
	}

	/*public String getInstanceArgName(String uri) {
		String res = uri;
		if (argpaths != null) res = argpaths.get(uri);
		return res;
	}

	public String getInstanceUri(String arg) {
		String res = argpaths.inverse().get(arg);
		return res;
	}*/


	public Collection<EPackage> getModels(){
		return models.values();
	}
	
	public List<EObject> getInstances(){
		List<EObject> res = new ArrayList<EObject>();
		for (EObject s : instances.values())
			res.add(s);
		return res;
	}

	public EPackage getModelsFromUri(String uri){
		return models.get(uri);
	}

	public EObject getInstanceFromUri(String uri){
		return instances.get(uri);
	}

	public List<EClass> getTopObject(String m) {
		EPackage pck = models.get(m);
		List<EClass> classes = new ArrayList<EClass>();
		for (EClassifier obj : pck.getEClassifiers())
			if (obj instanceof EClass) classes.add((EClass) obj);
		List<EClass> candidates = new ArrayList<EClass>(classes);
			
		for (EClass obj : classes) {
			for (EReference ref : obj.getEReferences())
				if (ref.isContainment()) candidates.remove(ref.getEReferenceType());
		}			
		return candidates;
	}

	public String backUpTarget(String uri){
		StringBuilder sb = new StringBuilder(uri);
		sb.insert(sb.length()-4,".old");

		XMIResource resource = (XMIResource) resourceSet.createResource(URI.createURI(sb.toString()));
		resource.getContents().add(getInstanceFromUri(uri));

		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMIResource.OPTION_SCHEMA_LOCATION, "aaa");
		try{
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
		
	}
}
