package pt.uminho.haslab.mde;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.examples.pivot.model.OCLstdlib;
import org.eclipse.ocl.examples.pivot.utilities.PivotUtil;
import org.eclipse.ocl.examples.xtext.base.utilities.BaseCSResource;
import org.eclipse.ocl.examples.xtext.base.utilities.CS2PivotResourceAdapter;
import org.eclipse.qvtd.pivot.qvtrelation.RelationModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.qvtd.xtext.qvtrelation.QVTrelationStandaloneSetup;
import org.eclipse.xtext.resource.XtextResourceSet;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses EMF resources (XMI models, ECore packages, QVT-R and ATL transformations)
 * 
 * @author nmm, tmg
 * @version 0.4 20/02/2014
 */
public class EMFParser {

	private static EMFParser instance = new EMFParser();

	public static EMFParser getInstance() {
		return instance;
	}

	/** the ECore resource set */
	static private XtextResourceSet resourceSet = new XtextResourceSet();

	/**
	 * Initializes the libraries
	 * Should not be needed in plug-in mode (only in standalone, e.g. CLI)
	 */
	private EMFParser(){
		if (EchoOptionsSetup.getInstance().isStandalone()) {
			// install the OCL standard library
			resourceSet.getResourceFactoryRegistry()
			.getExtensionToFactoryMap().put("xmi",
					new XMIResourceFactoryImpl());
			resourceSet.getResourceFactoryRegistry()
			.getExtensionToFactoryMap().put(
					"ecore", new EcoreResourceFactoryImpl());
//			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("atl", new AtlResourceFactoryImpl());

			OCLstdlib.install();
			QVTrelationStandaloneSetup.doSetup();
		}
	}

	/**
	 * Parses an EObject from an XMI resource
	 * @param objURI the object URI
	 * @return the parsed object
	 * @throws ErrorParser
	 */
	static public EObject loadModel(String objURI) throws ErrorParser {
		Resource load_resource = resourceSet.getResource(URI.createURI(objURI), true);
		load_resource.unload();
		try {
			load_resource.load(resourceSet.getLoadOptions());
		} catch (IOException e) {
			//throw new ErrorParser(e.getMessage());
		}
		EObject res = load_resource.getContents().get(0);

		return res;
	}

	/**
	 * Parses an EPackage from an ECore resource
	 * @param packURI the package URI
	 * @return the parsed package
	 * @throws ErrorParser
	 */
	static public EPackage loadMetaModel(String packURI) throws ErrorParser{
		Resource load_resource = resourceSet.getResource(URI.createURI(packURI),true);

		load_resource.unload();
		try {
			load_resource.load(resourceSet.getLoadOptions());
		} catch (IOException e) {
			throw new ErrorParser(e.getMessage());
		}
		EPackage res = (EPackage) load_resource.getContents().get(0);

		if (res.getNsURI() == null) throw new ErrorParser("nsUri required");

		resourceSet.getPackageRegistry().put(res.getNsURI(),res);

		return res;
	}

	/**
	 * Parses a QVT-R transformation from a QVT-R specification
	 * @param qvtURI the transformation URI
	 * @return the parsed transformation
	 * @throws ErrorParser
	 */
	static public RelationalTransformation loadQVT(String qvtURI) throws ErrorParser {
		Resource pivotResource;
		try{
			CS2PivotResourceAdapter adapter = null;
			URI inputURI;
			if (EchoOptionsSetup.getInstance().isStandalone())
				inputURI = URI.createURI(qvtURI);
			else
				inputURI = URI.createPlatformResourceURI(qvtURI,true);
			BaseCSResource xtextResource = (BaseCSResource) resourceSet.getResource(inputURI, true);

			xtextResource.unload();
			try {
				xtextResource.load(resourceSet.getLoadOptions());
			} catch (IOException e) {
				throw new ErrorParser(e.getMessage());
			}

			adapter = xtextResource.getCS2ASAdapter(null);
			//adapter = BaseCSResource.getCS2ASAdapter(xtextResource, null);
			pivotResource = adapter.getASResource(xtextResource);
			pivotResource.setURI(URI.createURI(qvtURI));
			String message = PivotUtil.formatResourceDiagnostics(pivotResource.getErrors(), "Error parsing QVT.", "\n\t");
			if (message != null) throw new ErrorParser (message,"QVT Parser");

		} catch (Exception e) { throw new ErrorParser (e.getMessage(),"QVT Parser");}

		RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
		RelationalTransformation transformation = (RelationalTransformation) rm.eContents().get(0);

		return transformation;
	}

	/**
	 * Parses an ATL transformation from an ATL specification
	 * @param atlURI the transformation URI
	 * @return the parsed transformation
	 * @throws ErrorParser
	 */
	public static EObject loadATL(String atlURI) {
//		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
//		
//		// Load metamodels
//		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
//		metaModel.setResource(resourceSet.getResource(URI.createURI("/pt.uminho.haslab.echo.examples/metamodels/uml2rdbms_simple/UML.ecore"), true));
//		env.registerMetaModel("UML", metaModel);
//		metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
//		metaModel.setResource(resourceSet.getResource(URI.createURI("/pt.uminho.haslab.echo.examples/metamodels/uml2rdbms_simple/RDBMS.ecore"), true));
//		env.registerMetaModel("RDBMS", metaModel);

		// Load models
//		Model inModel = EmftvmFactory.eINSTANCE.createModel();
//		inModel.setResource(resourceSet.getResource(URI.createURI("input.xmi", true), true));
//		env.registerInputModel("IN", inModel);
//
//		Model inOutModel = EmftvmFactory.eINSTANCE.createModel();
//		inOutModel.setResource(resourceSet.getResource(URI.createURI("inout.xmi", true), true));
//		env.registerInOutModel("INOUT", inOutModel);
//
//		Model outModel = EmftvmFactory.eINSTANCE.createModel();
//		outModel.setResource(resourceSet.createResource(URI.createFileURI("out.xmi")));
//		env.registerOutputModel("OUT", outModel);

		// Load and run module
//		String prefix = "/pt.uminho.haslab.echo.examples/transformations/atl/uml2rdbms_simple/";
//		String prefix = URI.createPlatformPluginURI("pt.uminho.haslab.echo.plugin", true).toString()+"/pt.uminho.haslab.echo.examples/transformations/atl/uml2rdbms_simple/";
//		EchoReporter.getInstance().debug("Pre "+prefix);
//		ModuleResolver mr = new DefaultModuleResolver(prefix, new ResourceSetImpl());
//		TimingData td = new TimingData();
//		Module module = env.loadModule(mr, atlURI.split("/")[atlURI.split("/").length-1].split("\\.emftvm")[0]);

//		td.finishLoading();
//		env.run(td);
//		td.finish();

		// Save models
//		inOutModel.getResource().save(Collections.emptyMap());
//		outModel.getResource().save(Collections.emptyMap());
//	
//		Model model = EmftvmFactory.eINSTANCE.createModel();
//		EObject module = resourceSet.getResource(URI.createURI(atlURI), true).getContents().get(0);
//        EObject inm = resourceSet.getResource(URI.createURI(atlURI), true).getContents().get(1);
//        EObject oum = resourceSet.getResource(URI.createURI(atlURI), true).getContents().get(2);
        return null;
	}

	/**
	 * Backs-up a XMI resource
	 * @param objURI the resource URI
	 * @return the URI of the back-up
	 * @throws ErrorParser
	 */
	static public String backUpTarget(String objURI) throws ErrorParser{
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
