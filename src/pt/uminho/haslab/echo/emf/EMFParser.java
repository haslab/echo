package pt.uminho.haslab.echo.emf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

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
import org.eclipse.qvtd.pivot.qvtrelation.RelationModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.qvtd.xtext.qvtrelation.QVTrelationStandaloneSetup;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import pt.uminho.haslab.echo.ErrorParser;

import com.google.inject.Injector;

public class EMFParser {

	
	private ResourceSet resourceSet = new ResourceSetImpl();

	// the transformation metamodels (package name, epackage)
	private Map<String,EPackage> metamodels = new HashMap<String,EPackage>();
	// the transformation instances (qvt argument name, eobject)
	private Map<String,EObject> instances = new HashMap<String,EObject>();

	private Map<String,String> packagepaths = new HashMap<String,String>();
	
	private RelationalTransformation rt;

	public EMFParser(){
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
	}
	
	public EObject loadObject(String argURI, String var) {
		// Create empty resource with the given URI
		Resource load_resource = resourceSet.getResource(URI.createURI(argURI), true);
		EObject res = load_resource.getContents().get(0);
		instances.put(var,res);	

		return res;
	}

	public EObject loadObject(String argURI) {
		// Create empty resource with the given URI
		Resource load_resource = resourceSet.getResource(URI.createURI(argURI), true);
		EObject res = load_resource.getContents().get(0);
		instances.put(instances.size()+"",res);	

		return res;
	}

	public EPackage loadPackage(String uri)
	{
		// Create empty resource with the given URI
		Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);
		EPackage res = (EPackage) load_resource.getContents().get(0);
		resourceSet.getPackageRegistry().put(res.getNsURI(),res);

		metamodels.put(res.getName(),res);
	    packagepaths.put(res.getName(), uri);
	    return res;

	}
	
	
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
		    for (Entry<String,String> mdl : packagepaths.entrySet())
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
			rt = (RelationalTransformation) rm.eContents().get(0);
			return rt;
		} catch (Exception e) { throw new ErrorParser (e.getMessage(),"QVT Parser");}
		
	}
	
	public RelationalTransformation getTransformation(){
		return rt;
	}

	public Collection<EPackage> getPackages(){
		return metamodels.values();
	}
	public Collection<EObject> getObjects(){
		return instances.values();
	}
	public Collection<String> getInstanceVars(){
		return instances.keySet();
	}


	public EObject getObjectVar(String var){
		return instances.get(var);
	}
	
	public String backUpTarget(String dir){
		String oldPath = getObjectVar(dir).eResource().getURI().toString();
		StringBuilder sb = new StringBuilder(oldPath);
		sb.insert(sb.length()-4,".old");

		Resource resource = resourceSet.createResource(URI.createURI(sb.toString()));
		resource.getContents().add(getObjectVar(dir));

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
		
		return sb.toString();
		
	}

}
