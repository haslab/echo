package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.views.GraphView;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * Manages the project resources being tracked by Echo
 * 
 * @author nmm
 * 
 */
public class ResourceManager {

	private EchoReporter reporter = EchoReporter.getInstance();
	private EchoRunner runner = EchoPlugin.getInstance().getRunner();
	private MDEManager parser = MDEManager.getInstance();

	/** The map of managed model resources: MetamodelURI -> ListModelResources **/
	private Map<String, List<IResource>> tracked = new HashMap<String, List<IResource>>();
	private Map<String, List<IResource>> ctracked = new HashMap<String, List<IResource>>();
	private Map<IResource, String> metas = new HashMap<IResource, String>();
	/** The map of managed qvtr constraints: QVTRURI -> ListModelResources **/

	/** the following are temporary variables that store resources while the user is selecting the new model
	/** temporary qvt constraint resource */
	private IResource qvtwaiting;
	/** temporary first model resource */
	private IResource fstwaiting;
	/** temporary second model resource */
	private IResource sndwaiting;

	/**
	 * Model management
	 */

	public ResourceManager() {	}

	/**
	 * Tracks a new model.
	 * If model is already being tracked, reloads it. 
	 * If the corresponding meta-model is not being tracked, it is loaded.
	 * 
	 * @param modelRes
	 *            the model resource to be tracked
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	private EModel addModelAction(IResource modelRes) throws EchoError {

		if (isManagedModel(modelRes))
			return reloadModel(modelRes);

		String modelURI = modelRes.getFullPath().toString();

		EModel model = parser.getModel(modelURI, true);
		
		EMetamodel metamodel = model.getMetamodel();
		
		if (!runner.hasMetaModel(metamodel.ID)) {
			runner.addMetaModel(metamodel);
		}
		runner.addModel(model);
		List<IResource> aux = tracked.get(metamodel.getURI());
		if (aux == null)
			aux = new ArrayList<IResource>();
		aux.add(modelRes);
		tracked.put(metamodel.getURI(), aux);
		metas.put(modelRes, metamodel.getURI());
		
		return model;
	}
	
	public EModel addModel(IResource resmodel) throws EchoError {
		EModel o = addModelAction(resmodel);
		conformMeta(resmodel);
		return o;
	}

	/**
	 * Reloads a model resource.
	 * Assumes metamodel is already tracked.
	 * 
	 * @param resmodel
	 *            the model resource to be reloaded
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	private EModel reloadModelAction(IResource resmodel) throws EchoError {
		String modelURI = resmodel.getFullPath().toString();
		EModel model = parser.getModel(modelURI, true);
		runner.reloadModel(model);
		reporter.debug("Model " + modelURI + " re-processed.");
		return model;
	}
	
	public EModel reloadModel(IResource resmodel) throws EchoError {
		EModel o = reloadModelAction(resmodel);
		conformMeta(resmodel);
		conformAllQVT(resmodel);
		return o;
	}

	
	/**
	 * Untracks a model resource.
	 * Removes from the the Echo core, parser, plugin properties and all error markers.
	 * 
	 * @param resModel the model resource to be untracked
	 * @throws ErrorAPI 
	 * @throws ErrorParser 
	 */
	public void remModel(IResource resModel) throws EchoError {
		String modelUri = resModel.getFullPath().toString();

		EModel model = parser.getModel(modelUri, false);
		EMetamodel metamodel = model.getMetamodel();
		
		runner.remModel(model.ID);
		tracked.get(metamodel.getURI()).remove(resModel);


		//Doesn't Eclipse auto-removes markers from deleted resources? 
		EchoMarker.removeIntraMarkers(resModel);
		//EchoMarker.removeInterMarkers(resModel);

		reporter.debug("Model " + modelUri + " removed.");
	}

	/**
	 * Tests if a resource model is being tracked by the system
	 * 
	 * @param resmodel
	 *            the resource model to be tested
	 * @return if {@code resmodel} is being tracked
	 * @throws EchoError 
	 */
	public boolean isManagedModel(IResource modelRes) throws EchoError {
		String modelURI = modelRes.getFullPath().toString();
		if(!MDEManager.getInstance().hasModel(modelURI)) return false;
		EModel model = MDEManager.getInstance().getModel(modelURI, false);
		return runner.hasModel(model.ID);
		
	}
	
	public List<IResource> getModels() {
		List<IResource> aux = new ArrayList<IResource>();
		for (List<IResource> x : tracked.values())
			aux.addAll(x);
		return aux;
	}
	
	public List<IResource> getModels(IResource metamodel) {
		return tracked.get(metamodel.getFullPath().toString());
	}

	public IResource getMetamodel(IResource r) {
		return ResourcesPlugin.getWorkspace().getRoot()
				.findMember(metas.get(r));
	}
	
	/**
	 * Metamodel management
	 */

	/**
	 * Reload a metamodel Reloads all depending models
	 * 
	 * @param resmetamodel
	 *            the metamodel resource to be reloaded
	 * @throws ErrorInternalEngine
	 * @throws ErrorUnsupported
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	public void reloadMetamodel(IResource resmetamodel) throws EchoError {
		String metamodeluri = resmetamodel.getFullPath().toString();
		runner.remMetaModel(metamodeluri);
		EMetamodel metamodel = parser.getMetamodel(metamodeluri, true);
		runner.addMetaModel(metamodel);

		List<IResource> ctrackeds = ctracked.get(metamodeluri);
		if (ctrackeds != null)
			for (IResource resqvt : ctrackeds)
				reloadQVTConstraintAction(resqvt);

		for (IResource resmodel : tracked.get(metamodeluri)) {
			reloadModelAction(resmodel);
			conformMeta(resmodel);
			conformAllQVT(resmodel);
		}

		reporter.debug("Metamodel " + metamodeluri + " reloaded.");
	}

	/**
	 * Untracks a metamodel.
	 * Also untracks all depending models.
	 * 
	 * @param resmetamodel
	 *            the metamodel to be untracked
	 * @throws ErrorAPI 
	 * @throws ErrorParser 
	 */
	public void remMetamodel(IResource resmetamodel) throws EchoError {
		String metamodeluri = resmetamodel.getFullPath().toString();
		runner.remMetaModel(metamodeluri);

		for (IResource resmodel : tracked.get(metamodeluri))
			remModel(resmodel);

		reporter.debug("Metamodel " + metamodeluri + " removed.");
	}

	/**
	 * Tests if a metamodel is being tracked.
	 * 
	 * @param resmetamodel
	 *            the metamodel resource to be tested
	 * @return if {@code resmetamodel} is being tracked
	 * @throws EchoError 
	 */
	public boolean isManagedMetamodel(IResource metamodelRes) throws EchoError {
		String metamodelURI = metamodelRes.getFullPath().toString();
		if(!MDEManager.getInstance().hasMetaModel(metamodelURI)) return false;
		EMetamodel metamodel = MDEManager.getInstance().getMetamodel(metamodelURI, false);
		return runner.hasMetaModel(metamodel.ID);
	}

	/**
	 * QVT constraint management
	 */

	/**
	 * Adds a new QVT constraint to the system.
	 * If models are not tracked, adds them to the system.
	 * @param qvtRes the qvt resource
<<<<<<< HEAD
	 * @param deps 
	 * @param resmodelfst the first model to be related
	 * @param resmodelsnd the second model to be related
=======
	 * @param modelsRes the first model to be related
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	private EConstraint addQVTConstraintAction(IResource qvtRes, List<IResource> modelsRes) throws EchoError {
		String qvtURI = qvtRes.getFullPath().toString();
		List<EModel> models = new ArrayList<EModel>();
		List<String> metamodelURIs = new ArrayList<String>();

		for (IResource resmodel : modelsRes){
			if (!isManagedModel(resmodel)) addModel(resmodel);
			EModel model = parser.getModel(resmodel.getFullPath().toString(), false);
			models.add(model);
			metamodelURIs.add(model.getMetamodel().getURI());
		}
			
		for (String mm : metamodelURIs) {
			List<IResource> l = ctracked.get(mm);
			if (l == null) l = new ArrayList<IResource>();
			l.add(qvtRes);
			ctracked.put(mm, l);
		}
		
		ETransformation trans = parser.getETransformation(qvtRes.getFullPath(), false);
		
		for (int i=0;i<trans.getModelParams().size();i++) {
			if (!trans.getModelParams().get(i).getMetamodel().ID.equals(models.get(i).getMetamodel().ID))
				throw new ErrorAPI("Model does not type-check.");
		}
		
		if (!runner.hasTransformation(trans.ID)) {
			runner.addTransformation(trans);
		}
		
		return runner.addConstraint(trans, models);
	}
	
	public EConstraint addQVTConstraint(IResource resqvt, List<IResource> resmodels)
			throws EchoError {
		
		EConstraint c = addQVTConstraintAction(resqvt, resmodels);
		conformQVT(c);
		return c;
	}

	
	/**
	 * Removes a particular QVT-R constraint between two model resources
	 * QVT-R representation (vs particular constraint) remains in the system
	 * Model resources remain tracked
	 * @param c the qvt-r constraint
	 * @throws ErrorAPI
	 * @throws ErrorParser 
	 */
	public void removeQVTConstraint(EConstraint c) throws EchoError {
		runner.removeConstraint(c);
		EchoMarker.removeRelatedInterMarker(c);
	}
	
	public void removeAllQVTConstraint(IResource transformationRes) throws EchoError {
		ETransformation etrans = parser.getETransformation(transformationRes.getFullPath(), false);
		runner.removeAllConstraint(etrans.ID);
	}

	public List<EConstraint> getConstraints() {
		return runner.getConstraints();
	}
	
	public List<EConstraint> getConstraints(IResource transformationRes) {
		ETransformation trans;
		try {
			trans = parser.getETransformation(transformationRes.getFullPath(), false);
			return runner.getConstraintsTransformation(trans.ID);
		} catch (EchoError e) {
			e.printStackTrace();
			return new ArrayList<EConstraint>();
		}
		
	}
	
	
	/**
	 * Reloads a QVT constraint
	 * Assumes QVT constraint was previously in the system
	 * Does not reload related models nor meta-models
	 * Launches inter-model checks
	 * @param transforamtionRes the updated qvt resource
	 * @throws ErrorParser 
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine 
	 * @throws ErrorUnsupported 
	 * @throws ErrorAPI 
	 */
	private ETransformation reloadQVTConstraintAction(IResource transforamtionRes) throws EchoError {
		String qvtURI = transforamtionRes.getFullPath().toString();
		ETransformation trans = parser.getETransformation(transforamtionRes.getFullPath(), true);
		runner.remTransformation(trans.ID);
		runner.addTransformation(trans);
		reporter.debug("QVT " + qvtURI + " reloaded.");
		return trans;
	}

	public ETransformation reloadQVTConstraint(IResource transformationRes) throws EchoError {
		ETransformation trans = reloadQVTConstraintAction(transformationRes);
		for (EConstraint c : runner.getConstraintsTransformation(trans.ID)) {
             reporter.debug("Checking " + c);
             conformQVT(c);
		 }
		return trans;
	}
	
	public boolean isManagedQVT(IResource qvtRes) throws EchoError {
		String qvtURI = qvtRes.getFullPath().toString();
		if(!MDEManager.getInstance().hasETransformation(qvtURI)) return false;
		ETransformation transformation = MDEManager.getInstance().getETransformation(qvtRes.getFullPath(), false);
		return runner.hasTransformation(transformation.ID);
	}
	
	/**
	 * Running tests
	 */
	
	/**
	 * Tests if a model resource conforms to the metamodel
	 * @param res the resource to be tested
	 * @throws ErrorInternalEngine
	 * @throws ErrorAPI 
	 */
	private void conformMeta(IResource res) throws EchoError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		String path = res.getFullPath().toString();
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(MDEManager.getInstance().getModel(path, false).ID);
		if (runner.conforms(list))
			EchoMarker.removeIntraMarkers(res);
		else
			EchoMarker.createIntraMarker(res);
	}

	/**
	 * Tests if a particular pair of models is consistent over a QVT specification
	 * @param c the QVT specification
	 * @throws ErrorInternalEngine
	 * @throws ErrorAPI
	 */
	private void conformQVT(EConstraint c) throws EchoError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		List<String> modelIDs = new ArrayList<String>(2);
		for (EModel model : c.getModels())
			modelIDs.add(model.ID);

		if (!runner.check(c.transformation.ID, modelIDs))
			EchoMarker.createInterMarker(c);
		else 
			EchoMarker.removeRelatedInterMarker(c);
	}

	/**
	 * Tests all QVT constraints over a single model
	 * @param res the model over which QVT constraints are tested
	 * @throws ErrorInternalEngine
	 * @throws ErrorAPI 
	 */
	private void conformAllQVT(IResource res) throws EchoError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		EModel model = parser.getModel(res.getFullPath().toString(), false);
		for (EConstraint c : runner.getConstraintsModel(model.ID)) {
			List<String> modelIDs = new ArrayList<String>();
			for (EModel m : c.getModels())
				modelIDs.add(m.ID);
			if (runner.check(c.transformation.ID, modelIDs))
				EchoMarker.removeRelatedInterMarker(c);	
			else
				EchoMarker.createInterMarker(c);
		}

	}

	public void generate(IResource metamodelRes,
			Map<Entry<String, String>, Integer> scopes, String targetURI)
			throws EchoError {
		String metamodelURI = metamodelRes.getFullPath().toString();
		
		EMetamodel metamodel = parser.getMetamodel(metamodelURI, false);
		
		if (!runner.hasMetaModel(metamodel.ID))
			runner.addMetaModel(metamodel);
			
		runner.generate(metamodel.ID, scopes, targetURI);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(targetURI, true, metamodelRes);
		amv.drawGraph();
	}

	public void addQVTgenerate(IResource resqvt, IResource ressource,
			String target, int newp) throws EchoError {

		if (!isManagedModel(ressource))
			addModelAction(ressource);
		
		ETransformation trans = parser.getETransformation(resqvt.getFullPath(), false);

		String metamodelURI = trans.getModelParams().get(newp).getMetamodel().getURI();
		EMetamodel metamodel = parser.getMetamodel(metamodelURI, false);
		if (!runner.hasMetaModel(metamodel.ID))
			runner.addMetaModel(metamodel);
		IResource resmetamodel = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(metamodelURI);

		if (!runner.hasTransformation(trans.ID)) {
			runner.addTransformation(trans);
		}

		qvtwaiting = resqvt;
		List<String> modelIDs = new ArrayList<String>();
		if (newp == 0) {
			modelIDs.add(target);
			modelIDs.add(parser.getModel(ressource.getFullPath().toString(), false).ID);
			sndwaiting = ressource;
		} else {
			modelIDs.add(parser.getModel(ressource.getFullPath().toString(), false).ID);
			modelIDs.add(target);
			fstwaiting = ressource;
		}
		runner.generateQvt(resqvt.getFullPath().toString(), metamodelURI,
				modelIDs, target);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target, true, resmetamodel);
		amv.drawGraph();
	}

	/**
	 * Warns the manager that the new model was selected, creating the constraint over it
	 * @param resmodel
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI
	 */
	public void modelGenerated(IResource resmodel) throws EchoError {
		addModel(resmodel);
		List<IResource> modelres = new ArrayList<IResource>();
		if (qvtwaiting != null && fstwaiting != null) {
			modelres.add(fstwaiting);
			modelres.add(resmodel);
			addQVTConstraint(qvtwaiting, modelres);
		}
		if (qvtwaiting != null && sndwaiting != null) {
			modelres.add(resmodel);
			modelres.add(sndwaiting);
			addQVTConstraint(qvtwaiting, modelres);		
		}
		qvtwaiting = null;
		fstwaiting = null;
		sndwaiting = null;
	}

	public void show(IFile res) throws EchoError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		String path = res.getFullPath().toString();
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(MDEManager.getInstance().getModel(path, false).ID);
		runner.show(list);
	}
	

}
