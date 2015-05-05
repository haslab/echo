package pt.uminho.haslab.mde;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import org.eclipse.ocl.examples.pivot.utilities.PivotUtil;
import org.eclipse.ocl.examples.xtext.base.utilities.BaseCSResource;
import org.eclipse.ocl.examples.xtext.base.utilities.CS2PivotResourceAdapter;
import org.eclipse.ocl.examples.xtext.essentialocl.EssentialOCLStandaloneSetup;
import org.eclipse.qvtd.pivot.qvtrelation.RelationModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.qvtd.xtext.qvtrelation.QVTrelationStandaloneSetup;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorTransform;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;

import com.google.inject.Injector;

/**
 * Parses EMF resources (XMI models, ECore packages, QVT-R and ATL transformations)
 * 
 * @author nmm, tmg
 * @version 0.4 20/02/2014
 */
class EMFParser {

	/** the ECore resource set */
	static private XtextResourceSet resourceSet = new XtextResourceSet();

	/**
	 * Initializes the libraries
	 * Should not be needed in plug-in mode (only in standalone, e.g. CLI)
	 * @return 
	 */
	static void initStandAlone(){
		resourceSet.getResourceFactoryRegistry()
		.getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry()
		.getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());
		
		//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("atl", new AtlResourceFactoryImpl());
		EssentialOCLStandaloneSetup.doSetup();
		QVTrelationStandaloneSetup.doSetup();
		Injector injector = new QVTrelationStandaloneSetup().createInjectorAndDoEMFRegistration();
		resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
	}

	/**
	 * Parses an EObject from an XMI resource
	 * @param objURI the object URI
	 * @return the parsed object
	 * @throws EErrorParser
	 */
	static public EObject loadModel(String objURI) throws EErrorParser {
		Resource load_resource = resourceSet.getResource(URI.createURI(objURI), true);
		load_resource.unload();
		try {
			load_resource.load(resourceSet.getLoadOptions());
		} catch (IOException e) {
			//throw new ErrorParser(e.getMessage());
		}
		EObject res = load_resource.getContents().get(0);

		EcoreUtil.resolveAll(res);

		return res;
	}

	/**
	 * Parses an EPackage from an ECore resource
	 * @param packURI the package URI
	 * @return the parsed package
	 * @throws EErrorParser
	 */
	static public EPackage loadMetaModel(String packURI) throws EErrorParser{
		Resource load_resource = resourceSet.getResource(URI.createURI(packURI),true);

		load_resource.unload();
		try {
			load_resource.load(resourceSet.getLoadOptions());
		} catch (IOException e) {
			throw new EErrorParser(EErrorParser.METAMODEL,e.getMessage(),Task.PROCESS_RESOURCES);
		}
		EPackage res = (EPackage) load_resource.getContents().get(0);

		if (res.getNsURI() == null) throw new EErrorParser(EErrorParser.METAMODEL,"nsUri required",Task.PROCESS_RESOURCES);
		
		EcoreUtil.resolveAll(resourceSet);

		resourceSet.getPackageRegistry().put(res.getNsURI(),res);

		return res;
	}

	/**
	 * Parses a QVT-R transformation from a QVT-R specification
	 * @param transURI the transformation URI
	 * @return the parsed transformation
	 * @throws EErrorParser
	 */
	static public RelationalTransformation loadQVT(String transURI) throws EErrorParser {
		URI inputURI;
		
		if (EchoOptionsSetup.getInstance().isStandalone()) {
			inputURI = URI.createFileURI(transURI);
		} else {
			inputURI = URI.createPlatformResourceURI(transURI,true);
		}
		
		BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(inputURI, true);

		if (!EchoOptionsSetup.getInstance().isStandalone()) {
			xtextResource.unload();
			try {
				xtextResource.load(resourceSet.getLoadOptions());
			} catch (IOException e) {
				throw new EErrorParser(EErrorParser.QVT,e.getMessage(),Task.PROCESS_RESOURCES);
			}	
		}

		String message = PivotUtil.formatResourceDiagnostics(xtextResource.getErrors(), "Error parsing QVT.", "\n\t");
		if (message != null) throw new EErrorParser(EErrorParser.QVT,message,Task.PROCESS_RESOURCES);

		CS2PivotResourceAdapter adapter = xtextResource.getCS2ASAdapter(null);

		Resource pivotResource = adapter.getASResource(xtextResource);
		pivotResource.setURI(URI.createURI(transURI));
		message = PivotUtil.formatResourceDiagnostics(pivotResource.getErrors(), "Error parsing QVT.", "\n\t");
		if (message != null) throw new EErrorParser (EErrorParser.QVT,message,"QVT Parser",Task.PROCESS_RESOURCES);

		
		RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
		RelationalTransformation transformation = (RelationalTransformation) rm.eContents().get(0);
		
		EcoreUtil.resolveAll(rm);
		
		return transformation;
	}

	/**
	 * Parses an ATL transformation from an ATL specification
	 * @param atlURI the transformation URI
	 * @return the parsed transformation
	 * @throws EErrorParser
	 */
	public static EObject loadATL(String atlURI) {
		EObject module = null;
		try {
			resourceSet.getURIConverter().createInputStream(URI.createURI(atlURI));
			InputStream f = resourceSet.getURIConverter().createInputStream(URI.createURI(atlURI));
			module = AtlParser.getDefault().parse(f);
			
			Resource resource = resourceSet.createResource(URI.createURI("/pt.uminho.haslab.echo.examples/adsateste.xmi"));
			resource.getContents().add(module);
			Map<Object,Object> options = new HashMap<Object,Object>();
			options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			try{
			    resource.save(options);
		    }catch (Exception e) {
		    	throw new EErrorTransform(EErrorParser.ATL,e.getMessage(),Task.PROCESS_RESOURCES);
		    }
			
			EchoReporter.getInstance().debug(module.eClass().toString());
			EchoReporter.getInstance().debug(module.eContents().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return module;
	}

	/**
	 * Backs-up a XMI resource
	 * @param objURI the resource URI
	 * @return the URI of the back-up
	 * @throws EErrorParser
	 */
	static public String backUpTarget(String objURI) throws EErrorParser{
		StringBuilder backupURI = new StringBuilder(objURI);
		backupURI.insert(backupURI.length()-4,".old");

		XMIResource resource = (XMIResource) resourceSet.createResource(URI.createURI(backupURI.toString()));
		resource.getContents().add(EMFParser.loadModel(objURI));

		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
		try{
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}

		return backupURI.toString();
	}

}
