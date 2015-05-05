package pt.uminho.haslab.mde;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTTransformation;

/**
 * Manages EMF resources (identified by URI) as Echo artifacts.
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

	public MDEManager() {
		if (EchoOptionsSetup.getInstance().isStandalone()) {
			EMFParser.initStandAlone();
		}
	}
	
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
	 * @throws EErrorUnsupported
	 * @throws EError
	 */
	public EModel getModel(String modelURI, boolean forceReload) throws EErrorParser, EErrorUnsupported {
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
	 * @throws EErrorParser
	 * @throws EErrorUnsupported
	 */
	public EModel getModelID(String modelID) throws EErrorParser, EErrorUnsupported {
		String URI = id2uri.get(modelID);
		if (URI == null) throw new EErrorParser(EErrorParser.MODEL,"Model ID "+ modelID +" not found.",Task.PROCESS_RESOURCES);
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
	 * Adds a fresh new model (not yet persisted as a resource) to the manager.
	 * @param model the new model
	 * @throws EErrorParser if the URI is already registered
	 */
	public void addNew(EModel model) throws EErrorParser {
		if (models.get(model.getURI()) != null)
			throw new EErrorParser(EErrorParser.MODEL, "Model already processed with the new model's URI.", Task.GENERATE_TASK);
		models.put(model.getURI(), model);				
		id2uri.put(model.ID, model.getURI());
	}

	/**
	 * Gets an ECore package identified by its URI.
	 * Parses the meta-model if first time or <code>forceReload</code>
	 * @param metamodelURI the meta-model URI
	 * @param forceReload force meta-model reload
	 * @return the processed meta-model
	 * @throws EErrorParser
	 * @throws EErrorUnsupported
	 */
	public EMetamodel getMetamodel(String metamodelURI, boolean forceReload) throws EErrorParser, EErrorUnsupported {
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
			StringBuilder sb = new StringBuilder("Available root classes: ");
			for (EClass c : metamodel.getRootClass())
				sb.append(c.getName()+" ");
			EchoReporter.getInstance().debug(sb.toString());
		}
		id2uri.put(metamodel.ID, metamodelURI);
		return metamodel;
	}

	/**
	 * Gets a metamodel identified by its ID
	 * If there is an ID, then the metamodel was already parser previously
	 * @param modelID the model ID
	 * @return the processed model
	 * @throws EErrorParser
	 * @throws EErrorUnsupported
	 */
	public EMetamodel getMetamodelID(String metamodelID) throws EErrorParser, EErrorUnsupported {
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
	 * @throws EError
	 */
	public ETransformation getETransformation(String transURI, boolean forceReload) throws EError {
		EchoReporter.getInstance().debug(transURI + " at "+transformations);
		ETransformation trans = transformations.get(transURI);
		if (trans == null || forceReload) {
			EchoReporter.getInstance().start(Task.PROCESS_RESOURCES, transURI);
			if (transURI.endsWith(".qvtr")) {
				RelationalTransformation qtrans = EMFParser.loadQVT(transURI);
				if (trans == null) {
					trans = new EQVTTransformation(qtrans);
					
//					BufferedReader br = null;
//					try {
//						InputStream is = qtrans.eResource().getResourceSet().getURIConverter().createInputStream(URI.createPlatformResourceURI(transURI,true));
//						br = new BufferedReader(new InputStreamReader(is));
//						String line = br.readLine();
//						while (line.startsWith("--")) {
//							if (line.startsWith("-- @dependencies")) {
//								String[] sdeps = line.substring(17).split(" -> ");
//								EchoReporter.getInstance().debug(qtrans.getModelParameter(sdeps[0])+" -> "+sdeps[1]);
//							}
//							EDependency dep = new EDependency(target, sources);
//							line = br.readLine();
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} finally {
//						try {
//							br.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					transformations.put(transURI, trans);				
				} else {
					trans.update(qtrans);
				}
			} else if (transURI.endsWith("atl")) {
				EObject atrans = EMFParser.loadATL(transURI);
				if (trans == null) {
					trans = new EATLTransformation(atrans);
					transformations.put(transURI, trans);				
				} else {
					trans.update(atrans);
				}
			}
			EchoReporter.getInstance().result(Task.PROCESS_RESOURCES,transURI, true);
		}
		
		
		
		
		
		id2uri.put(trans.ID,transURI);
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

	public void backUpTarget(String targetPath) throws EErrorParser {
		EMFParser.backUpTarget(targetPath);
	}

	public ETransformation getETransformationID(String transformationID) throws EError {
		String URI = id2uri.get(transformationID);
		if (URI == null) throw new EErrorParser(EErrorParser.MODEL,"Transformation ID "+ transformationID +" not found.",Task.PROCESS_RESOURCES);
		return getETransformation(URI, false);
	}

	public EModel createEmpty(String metamodelID, String modelURI, String root) throws EErrorUnsupported, EErrorParser {
		EMetamodel metamodel = MDEManager.getInstance().getMetamodelID(metamodelID);
		EClass rootclass;
		// if root is null, check whether there is a single root 
		if (root == null || root.equals("")) {
			List<EClass> rootobjects = metamodel.getRootClass();
			if (rootobjects.size() != 1) {
				StringBuilder sb = new StringBuilder(
						"Could not resolve root class: ");
				for (EClass o : rootobjects)
					sb.append(o.getName());
				throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT,
						sb.toString(), "Check the meta-model containment tree.",
						Task.GENERATE_TASK);
			}
			rootclass = rootobjects.get(0);
		} else
			rootclass = (EClass) metamodel.getEObject().getEClassifier(root);
		EModel empty = null;
		EObject x = rootclass.getEPackage().getEFactoryInstance().create(rootclass);
		empty = new EModel(x,modelURI); 
		MDEManager.getInstance().addNew(empty);
		return empty;
	}

}
