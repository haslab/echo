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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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

	/** the Echo CLI options */
	private EchoOptions options;
	/** the ECore resource set */
	private ResourceSet resourceSet = new ResourceSetImpl();

	/** the loaded metamodels */
	private Map<String,EPackage> metamodels = new HashMap<String,EPackage>();
	/** the loaded instances (key is the XMI file uri) */
	private Map<String,EObject> instances = new HashMap<String,EObject>();
	/** the loaded QVT-R transformation */
	private RelationalTransformation rt;
	/** maps the XMI instance paths into the QVT-R argument names (if any)  */
	private BiMap<String,String> argpaths;

	
	public EMFParser(EchoOptions options){
		/*
		// register Pivot globally
		org.eclipse.ocl.examples.pivot.OCL.initialize(resourceSet);

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
	
		this.options = options;
	}
	
	/**
	 * Loads the EObjects from the CLI arguments
	 */
	public void loadObjects() {
		for (int i = 0; i<options.getInstances().length; i++) {
			String uri = options.getInstances()[i];
			Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);
			EObject res = load_resource.getContents().get(0);
			instances.put(uri,res);
		}
	}

	/**
	 * Loads the EPackages from the CLI arguments
	 */
	public void loadPackages() {
		for (String uri : options.getModels()) {
			Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);
			EPackage res = (EPackage) load_resource.getContents().get(0);
			
			resourceSet.getPackageRegistry().put(res.getNsURI(),res);
			metamodels.put(uri,res);
		}
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
		    for (String u: metamodels.keySet())
		    	fbw.write("import "+metamodels.get(u).getName()+" : \'"+u+"\'::"+metamodels.get(u).getName()+";\n\n");

		    fbw.write(qvtcontent);
	        fbw.close();

			BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(URI.createFileURI("aux.qvtr"), true);
	        qvtaux.delete();
		
			String message = PivotUtil.formatResourceDiagnostics(xtextResource.getErrors(), "Error parsing QVT.", "\n\t");
			if (message != null) throw new ErrorParser (message,"QVT Parser");
			
			adapter = CS2PivotResourceAdapter.getAdapter(xtextResource, null);
			Resource pivotResource = adapter.getPivotResource(xtextResource);
					
			RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
			rt = (RelationalTransformation) rm.eContents().get(0);
			
			argpaths = HashBiMap.create();
			for (int i = 0; i < rt.getModelParameter().size(); i++)
				argpaths.put(options.getInstances()[i],rt.getModelParameter().get(i).getName());				
			
			return rt;
		} catch (Exception e) { throw new ErrorParser (e.getMessage(),"QVT Parser");}
		
	}
	
	public RelationalTransformation getTransformation(){
		return rt;
	}

	public String getInstanceArgName(String uri) {
		String res = uri;
		if (argpaths != null) res = argpaths.get(uri);
		return res;
	}

	public String getInstanceUri(String arg) {
		String res = argpaths.inverse().get(arg);
		return res;
	}

	public Collection<EPackage> getPackages(){
		return metamodels.values();
	}
	public List<EObject> getObjects(){
		List<EObject> res = new ArrayList<EObject>();
		for (String s : options.getInstances())
			res.add(instances.get(s));
		return res;
	}

	public EObject getObjectFromUri(String uri){
		return instances.get(uri);
	}

	public EObject getObjectFromArg(String arg){
		return instances.get(argpaths.inverse().get(arg));
	}

	
	public String backUpTarget(){
		String dir = getInstanceUri(options.getDirection());

		String oldPath = getObjectFromUri(dir).eResource().getURI().toString();
		StringBuilder sb = new StringBuilder(oldPath);
		sb.insert(sb.length()-4,".old");

		XMIResource resource = (XMIResource) resourceSet.createResource(URI.createURI(sb.toString()));
		resource.getContents().add(getObjectFromUri(dir));
		
		/*
		* Save the resource using OPTION_SCHEMA_LOCATION save option toproduce 
		* xsi:schemaLocation attribute in the document
		*/
		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMIResource.SCHEMA_LOCATION, "aaa");
		try{
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
		
	}

}
