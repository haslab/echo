package pt.uminho.haslab.mde.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
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
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;

<<<<<<< HEAD:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/mde/emf/EMFParser.java
public class EMFParser {
=======
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EchoParser {
	//TODO: there should be some helper classes for stuff like getRootClass, which are totally independent from kodkod or alloy
	private static final EchoParser instance = new EchoParser();
		
	public static EchoParser getInstance() {
		return instance;
	}
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/echo/emf/EchoParser.java

	private static final EMFParser instance = new EMFParser();
    
    public static EMFParser getInstance() {
    	return instance;
    }
    
	/** the ECore resource set */
	static private XtextResourceSet resourceSet = new XtextResourceSet();
	
	private EMFParser(){	
		if (EchoOptionsSetup.getInstance().isStandalone()) {

            // install the OCL standard library
            OCLstdlib.install();
            resourceSet.getResourceFactoryRegistry()
             .getExtensionToFactoryMap().put("xmi",
                             new XMIResourceFactoryImpl());
			 resourceSet.getResourceFactoryRegistry()
             .getExtensionToFactoryMap().put(
                     "ecore", new EcoreResourceFactoryImpl());
			OCLstdlib.install();		
			QVTrelationStandaloneSetup.doSetup();
		}
	}
	
	/**
	 * Loads the EObject its uri
	 * @throws ErrorParser 
	 */
	static public EObject loadModel(String uri) throws ErrorParser {
		Resource load_resource = resourceSet.getResource(URI.createURI(uri), true);
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
	 * Loads the EPackages its uri
	 */
	static public EPackage loadMetaModel(String uri) throws ErrorParser{
		Resource load_resource = null;
		if (!EchoOptionsSetup.getInstance().isStandalone())
			load_resource = resourceSet.getResource(URI.createURI(uri),true);
		else {
			File file = new File(uri);
			try {
				load_resource = resourceSet.getResource(URI.createFileURI(file.getCanonicalPath()),true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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
	 * Loads the QVT specification from the CLI argument
	 * @throws ErrorTransform 
	 */
	static public RelationalTransformation loadQVT(String uri) throws ErrorParser, ErrorTransform {
		Resource pivotResource;
        try{
        	CS2PivotResourceAdapter adapter = null;
        	URI inputURI;
        	if (EchoOptionsSetup.getInstance().isStandalone())
        		inputURI = URI.createURI(uri);
        	else 
        		inputURI = URI.createPlatformResourceURI(uri,true);
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
            pivotResource.setURI(URI.createURI(uri));
            String message = PivotUtil.formatResourceDiagnostics(pivotResource.getErrors(), "Error parsing QVT.", "\n\t");
			if (message != null) throw new ErrorParser (message,"QVT Parser");
			
		} catch (Exception e) { throw new ErrorParser (e.getMessage(),"QVT Parser");}

        try{	
			RelationModel rm = (RelationModel) pivotResource.getContents().get(0);
			RelationalTransformation transformation = (RelationalTransformation) rm.eContents().get(0);
		
			return transformation;
		} catch (Exception e) { throw new ErrorTransform(e.getMessage());}
		
	}

	static public String backUpTarget(String uri) throws ErrorParser{
		StringBuilder sb = new StringBuilder(uri);
		sb.insert(sb.length()-4,".old");

		XMIResource resource = (XMIResource) resourceSet.createResource(URI.createURI(sb.toString()));
		resource.getContents().add(EMFParser.loadModel(uri));

		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMIResource.OPTION_SCHEMA_LOCATION, true);
		try{
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
		
	}

	public static EObject loadATL(String atlURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
