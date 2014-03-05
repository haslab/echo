package pt.uminho.haslab.mde;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTTransformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages EMF resources (identified by URI) as Echo artifacts.
 * 
 * Manages <code>EModel</code> from XMI files, <code>EMetamodel</code> from
 * Ecore packages, <code>QVTTransformation</code> from QVT-R specifications,
 * <code>ATLTransformation</code> from ATL specifications.
 * 
 * @author nmm, tmg
 * @version 0.4 13/02/2014
 */
public class MDEManager {

	//TODO: there should be some helper classes for stuff like getRootClass, which are totally independent from kodkod or alloy

	private static final MDEManager instance = new MDEManager();

	public static MDEManager getInstance() {
		return instance;
	}

	/** maps XMI model URIs to processed models */
	private Map<String,EModel> models = new HashMap<String,EModel>();

	/** maps Ecore package URIs to processed metamodels */
	private Map<String,EMetamodel> metamodels = new HashMap<String,EMetamodel>();

	/** maps QVT-R transformation URIs to processed transformations */
	private Map<String,ETransformation> transformations = new HashMap<>();

	/** maps artifacts IDs to the respective URIs */
	private Map<String,String> id2uri = new HashMap<String,String>();

	/**
	 * Gets an XMI model identified by its URI
	 * Parses the model if first time or <code>forceReload</code>
	 * @param modelURI the XMI model URI
	 * @param forceReload force model reload
	 * @return the processed model
	 * @throws ErrorUnsupported
	 * @throws EchoError
	 */
	public EModel getModel(String modelURI, boolean forceReload) throws ErrorParser, ErrorUnsupported {
		EModel model = models.get(modelURI);
		if (model == null || forceReload) {
			EchoReporter.getInstance().start(Task.PROCESS_RESOURCES, modelURI);
			EObject eobj = EMFParser.loadModel(modelURI);
			if (model == null) {
				model = new EModel(eobj);
				models.put(modelURI, model);				
			} else {
				model.update(eobj);
			}
			EchoReporter.getInstance().result(Task.PROCESS_RESOURCES, modelURI, true);
		}
		id2uri.put(model.ID, modelURI);
		return model;
	}

	/**
	 * Gets an XMI model identified by its ID
	 * If there is an ID, then the model was already parser previously
	 * @param modelID the model ID
	 * @return the processed model
	 * @throws ErrorParser
	 * @throws ErrorUnsupported
	 */
	public EModel getModelID(String modelID) throws ErrorParser, ErrorUnsupported {
		String URI = id2uri.get(modelID);
		if (URI == null) throw new ErrorParser("Model ID "+ modelID +" not found.");
		return getModel(URI, false);
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
	 * Gets an ECore package identified by its URI
	 * Parses the metamodel if first time or <code>forceReload</code>
	 * @param metamodelURI the metamodel URI
	 * @param forceReload force model reload
	 * @return the processed metamodel
	 * @throws ErrorParser
	 * @throws ErrorUnsupported
	 */
	public EMetamodel getMetamodel(String metamodelURI, boolean forceReload) throws ErrorParser, ErrorUnsupported {
		EMetamodel metamodel = metamodels.get(metamodelURI);
		if (metamodel == null || forceReload) {
			EchoReporter.getInstance().start(Task.PROCESS_RESOURCES, metamodelURI);
			EPackage epackage = EMFParser.loadMetaModel(metamodelURI);
			if (metamodel == null) {
				metamodel = new EMetamodel(epackage);
				metamodels.put(metamodelURI, metamodel);				
			} else {
				metamodel.update(epackage);
			}
			EchoReporter.getInstance().result(Task.PROCESS_RESOURCES, metamodelURI, true);
		}
		id2uri.put(metamodel.ID, metamodelURI);
		return metamodel;
	}

	/**
	 * Gets a metamodel identified by its ID
	 * If there is an ID, then the metamodel was already parser previously
	 * @param modelID the model ID
	 * @return the processed model
	 * @throws ErrorParser
	 * @throws ErrorUnsupported
	 */
	public EMetamodel getMetamodelID(String metamodelID) throws ErrorParser, ErrorUnsupported {
		String URI = id2uri.get(metamodelID);
		if (URI == null) {
			EchoReporter.getInstance().warning("Metamodel ID "+ metamodelID +" not found.", Task.PROCESS_RESOURCES);
			return null;
		}
		return getMetamodel(URI, false);
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
	 * @param transPath the QVT-R transformation URI
	 * @param forceReload TODO
	 * @return the processed QVT-R transformation
	 * @throws EchoError
	 */
	public ETransformation getETransformation(IPath transPath, boolean forceReload) throws EchoError {
		ETransformation trans = transformations.get(transPath);
		if (trans == null || forceReload) {
			EchoReporter.getInstance().start(Task.PROCESS_RESOURCES, transPath.toString());
			if (transPath.getFileExtension().equals("qvtr")) {
				RelationalTransformation qtrans = EMFParser.loadQVT(transPath);
				if (trans == null) {
					trans = new EQVTTransformation(qtrans);
					transformations.put(transPath.toString(), trans);				
				} else {
					trans.update(qtrans);
				}
			} else if (transPath.getFileExtension().equals("atl")) {
				EObject atrans = EMFParser.loadATL(transPath);
				if (trans == null) {
					trans = new EATLTransformation(atrans);
					transformations.put(transPath.toString(), trans);				
				} else {
					trans.update(atrans);
				}
			}
			EchoReporter.getInstance().result(Task.PROCESS_RESOURCES, transPath.toString(), true);
		}
		id2uri.put(trans.ID,transPath.toString());
		return trans;
	}

	/**
	 * Removes a QVT-R transformation identified by its URI
	 * @param transURI
	 * @return the removed transformation
	 */
	public ETransformation disposeETransformation(String transURI) {
		return transformations.remove(transURI);
	}

	/**
	 * Tests if a QVT-R transformation identified by URI is already loaded
	 * @param transURI the transformation URI
	 * @return whether the transformation is loaded
	 */
	public boolean hasETransformation(String transURI) {
		return transformations.get(transURI) != null;
	}

	public void backUpTarget(String targetPath) throws ErrorParser {
		EMFParser.backUpTarget(targetPath);
	}



}
