package pt.uminho.haslab.mde;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.emf.EMFParser;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.atl.ATLTransformation;
import pt.uminho.haslab.mde.transformation.qvt.QVTTransformation;

/**
 * Class that connects EMF resources (identified by URI) to Echo artifacts
 * @author nmm
 *
 */
public class MDEManager {
	
    //TODO: there should be some helper classes for stuff like getRootClass, which are totally independent from kodkod or alloy
   
	private static final MDEManager instance = new MDEManager();
            
    public static MDEManager getInstance() {
    	return instance;
    }

    /** the processed XMI models */
	private Map<String,EModel> models = new HashMap<String,EModel>();

	/** the processed EPackage metamodels */
	private Map<String,EMetamodel> metamodels = new HashMap<String,EMetamodel>();
    
	/** the processed QVT-R transformation */
	private Map<String,QVTTransformation> qvttransformations = new HashMap<String,QVTTransformation>();
    
	/** the processed ATL transformation */
	private Map<String,ATLTransformation> atltransformations = new HashMap<String,ATLTransformation>();

	private Map<String,String> id2uri = new HashMap<String,String>();
	
	/**
	 * Loads an XMI model identified by its URI
	 * @param modelURI the model URI
	 * @param forceReload TODO
	 * @return the processed model
	 * @throws EchoError
	 */
	public EModel getModel(String modelURI, boolean forceReload) throws EchoError {
		EModel model = models.get(modelURI);
		if (model == null) {
			EObject eobj = EMFParser.loadModel(modelURI);
			model = new EModel(eobj);
			models.put(modelURI, model);
		} else if (forceReload) {
			EObject eobj = EMFParser.loadModel(modelURI);
			model.update(eobj);
		}
		id2uri.put(model.ID, modelURI);
		return model;
	}
	
	public EModel getModelID(String modelID, boolean forceReload) throws EchoError {
		return getModel(id2uri.get(modelID), forceReload);
	}

	/**
	 * Removes a model identified by its URI
	 * @param modelURI the model URI
	 * @return the removed model
	 */
	public EModel disposeModel(String modelURI) {
		return models.remove(modelURI);
	}

	/**
	 * Tests if a model identified by URI is already loaded
	 * @param modelURI the model URI
	 * @return whether the model is loaded
	 */
	public boolean hasModel(String modelURI) {
		return models.get(modelURI) != null;
	}
	
	/**
	 * Loads an ECore package identified by its URI
	 * @param metamodelURI the metamodel URI
	 * @param forceReload TODO
	 * @return the processed metamodel
	 * @throws EchoError
	 */
	public EMetamodel getMetamodel(String metamodelURI, boolean forceReload) throws EchoError {
		EMetamodel metamodel = metamodels.get(metamodelURI);
		if (metamodel == null) {
			EPackage epackage = EMFParser.loadMetaModel(metamodelURI);
			metamodel = new EMetamodel(epackage);
			metamodels.put(metamodelURI, metamodel);
		} else if (forceReload) {
			EPackage epackage = EMFParser.loadMetaModel(metamodelURI);
			metamodel.update(epackage);
		}
		id2uri.put(metamodel.ID, metamodelURI);
		return metamodel;	
	}
	
	public EMetamodel getMetamodelID(String metamodelID, boolean forceReload) throws EchoError {
		String URI = id2uri.get(metamodelID);
		if (URI==null) {
			EchoReporter.getInstance().warning("Looking for non-existing URI: "+metamodelID, Task.TRANSLATE_METAMODEL);
			return null;
		}
		return getMetamodel(URI, forceReload);
	}

	/**
	 * Removes a metamodel identified by its URI
	 * @param metamodelURI
	 * @return the removed metamodel
	 */
	public EMetamodel disposeMetaModel(String metamodelURI) {
		return metamodels.remove(metamodelURI);
	}

	/**
	 * Tests if a metamodel identified by URI is already loaded
	 * @param metamodelURI the metamodel URI
	 * @return whether the metamodel is loaded
	 */
	public boolean hasMetaModel(String metamodelURI) {
		return metamodels.get(metamodelURI) != null;
	}
	
	/**
	 * Loads a QVT-R transformation identified by its URI
	 * @param qvtURI the QVT-R transformation URI
	 * @param forceReload TODO
	 * @return the processed QVT-R transformation
	 * @throws EchoError
	 */
	public QVTTransformation getQVTTransformation(String qvtURI, boolean forceReload) throws EchoError {
		QVTTransformation qvt = qvttransformations.get(qvtURI);
		if (qvt == null) {
			RelationalTransformation trans = EMFParser.loadQVT(qvtURI);
			qvt = new QVTTransformation(trans);
			qvttransformations.put(qvtURI, qvt);
		} else if (forceReload) {
			RelationalTransformation trans = EMFParser.loadQVT(qvtURI);
			qvt.update(trans);
		}
		id2uri.put(qvt.ID,qvtURI);
		return qvt;	
	}

	/**
	 * Removes a QVT-R transformation identified by its URI
	 * @param qvtURI
	 * @return the removed transformation
	 */
	public QVTTransformation disposeQVTTransformation(String qvtURI) {
		return qvttransformations.remove(qvtURI);
	}

	/**
	 * Tests if a QVT-R transformation identified by URI is already loaded
	 * @param qvtURI the transformation URI
	 * @return whether the transformation is loaded
	 */
	public boolean hasQVTTransformation(String qvtURI) {
		return qvttransformations.get(qvtURI) != null;
	}
	
	/**
	 * Loads an ATL transformation identified by its URI
	 * @param atlURI the ATL transformation URI
	 * @return the processed ATL transformation
	 * @throws EchoError
	 */
	public ATLTransformation getATLTransformation(String atlURI, boolean forceReload) {
		ATLTransformation atl = atltransformations.get(atlURI);
		if (atl == null) {
			EObject eobj = EMFParser.loadATL(atlURI);
			atl = new ATLTransformation(eobj);
			atltransformations.put(atlURI, atl);
		} else if (forceReload) {
			EObject eobj = EMFParser.loadATL(atlURI);
			atl.update(eobj);
		}
		//id2uri.put(atl.ID,atlURI);
		return atl;	
	}

	/**
	 * Removes an ATL transformation identified by its URI
	 * @param atlURI
	 * @return the removed transformation
	 */
	public ATLTransformation disposeATLTransformation(String atlURI) {
		return atltransformations.remove(atlURI);
	}
	
	/**
	 * Tests if an ATL transformation identified by URI is already loaded
	 * @param atlURI the transformation URI
	 * @return whether the transformation is loaded
	 */
	public boolean hasATLTransformation(String atlURI) {
		return atltransformations.get(atlURI) != null;
	}

	public void backUpTarget(String targetPath) throws ErrorParser {
		EMFParser.backUpTarget(targetPath);
	}


}
