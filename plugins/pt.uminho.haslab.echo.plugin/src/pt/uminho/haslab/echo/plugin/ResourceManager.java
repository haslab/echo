package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EExceptMaxDelta;
import pt.uminho.haslab.echo.EException;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.EErrorAPI;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorTransform;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
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
	 * @throws EErrorUnsupported
	 * @throws EErrorCore
	 * @throws EErrorTransform
	 * @throws EErrorParser
	 * @throws EErrorAPI 
	 */
	private EModel addModelAction(IResource modelRes) throws EError {

		if (isManagedModel(modelRes))
			return reloadModel(modelRes);

		String modelURI = modelRes.getFullPath().toString();

		EModel model = parser.getModel(modelURI, true);
		
		EMetamodel metamodel = model.getMetamodel();
		
		if (!runner.hasMetaModel(metamodel.ID)) {
			runner.addMetamodel(metamodel);
		}
		runner.addModel(model);
		List<IResource> aux = tracked.get(metamodel.getURI());
		if (aux == null)
			aux = new ArrayList<IResource>();
		aux.add(modelRes);
		tracked.put(metamodel.getURI(), aux);
		metas.put(modelRes, metamodel.getURI());
		EchoReporter.getInstance().debug("Tracked meta-models: "+tracked.keySet());
		return model;
	}
	
	public EModel addModel(IResource resmodel) throws EError {
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
	 * @throws EErrorUnsupported
	 * @throws EErrorCore
	 * @throws EErrorTransform
	 * @throws EErrorParser
	 * @throws EErrorAPI 
	 */
	private EModel reloadModelAction(IResource resmodel) throws EError {
		String modelURI = resmodel.getFullPath().toString();
		EModel model = parser.getModel(modelURI, true);
		runner.reloadModel(model);
		reporter.debug("Model " + modelURI + " re-processed.");
		return model;
	}
	
	public EModel reloadModel(IResource resmodel) throws EError {
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
	 * @throws EErrorAPI 
	 * @throws EErrorParser 
	 */
	public void remModel(IResource resModel) throws EError {
		String modelUri = resModel.getFullPath().toString();

		EModel model = parser.getModel(modelUri, false);
		EMetamodel metamodel = model.getMetamodel();
		
		runner.remModel(model.ID);
		tracked.get(metamodel.getURI()).remove(resModel);

		EchoMarker.removeIntraMarkers(resModel);
		EchoMarker.removeInterMarkers(resModel);

		reporter.debug("Model " + modelUri + " removed.");
	}

	/**
	 * Tests if a resource model is being tracked by the system
	 * 
	 * @param resmodel
	 *            the resource model to be tested
	 * @return if {@code resmodel} is being tracked
	 * @throws EError 
	 */
	public boolean isManagedModel(IResource modelRes) throws EError {
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
	 * @throws EErrorCore
	 * @throws EErrorUnsupported
	 * @throws EErrorTransform
	 * @throws EErrorParser
	 * @throws EErrorAPI 
	 */
	public void reloadMetamodel(IResource resmetamodel) throws EError {
		String metamodeluri = resmetamodel.getFullPath().toString();
		EchoReporter.getInstance().debug("*** Reloading meta-model "+metamodeluri);
		runner.remMetaModel(metamodeluri);
		EMetamodel metamodel = parser.getMetamodel(metamodeluri, true);
		runner.addMetamodel(metamodel);

		List<IResource> ctrackeds = ctracked.get(metamodeluri);
		if (ctrackeds != null)
			for (IResource resqvt : ctrackeds)
				reloadQVTConstraintAction(resqvt);

		EchoReporter.getInstance().debug("*** Tracked meta-models: "+tracked.keySet());
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
	 * @throws EErrorAPI 
	 * @throws EErrorParser 
	 */
	public void remMetamodel(IResource resmetamodel) throws EError {
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
	 * @throws EError 
	 */
	public boolean isManagedMetamodel(IResource metamodelRes) throws EError {
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
	 * @throws EErrorUnsupported
	 * @throws EErrorCore
	 * @throws EErrorTransform
	 * @throws EErrorParser
	 * @throws EErrorAPI 
	 */
	private EConstraint addQVTConstraintAction(IResource qvtRes, List<IResource> modelsRes) throws EError {
		List<EModel> models = new ArrayList<EModel>();
		List<String> modelIDs = new ArrayList<String>();
		List<String> metamodelURIs = new ArrayList<String>();

		for (IResource resmodel : modelsRes){
			if (!isManagedModel(resmodel)) addModel(resmodel);
			EModel model = parser.getModel(resmodel.getFullPath().toString(), false);
			models.add(model);
			modelIDs.add(model.ID);
			metamodelURIs.add(model.getMetamodel().getURI());
		}
			
		for (String mm : metamodelURIs) {
			List<IResource> l = ctracked.get(mm);
			if (l == null) l = new ArrayList<IResource>();
			l.add(qvtRes);
			ctracked.put(mm, l);
		}
		
		ETransformation trans = parser.getETransformation(qvtRes.getFullPath().toString(), false);
		
		for (int i=0;i<trans.getModelParams().size();i++) {
			if (!trans.getModelParams().get(i).getMetamodel().ID.equals(models.get(i).getMetamodel().ID))
				throw new EErrorAPI(EErrorAPI.TYPE,"Model does not type-check.",Task.PLUGIN);
		}
		
		if (!runner.hasTransformation(trans.ID)) {
			runner.addTransformation(trans);
		}
		
		return runner.addConstraint(trans.ID, modelIDs);
	}
	
	public EConstraint addQVTConstraint(IResource resqvt, List<IResource> resmodels)
			throws EError {
		
		EConstraint c = addQVTConstraintAction(resqvt, resmodels);
		conformQVT(c.ID);
		return c;
	}

	
	/**
	 * Removes a particular QVT-R constraint between two model resources
	 * QVT-R representation (vs particular constraint) remains in the system
	 * Model resources remain tracked
	 * @param c the qvt-r constraint
	 * @throws EErrorAPI
	 * @throws EErrorParser 
	 */
	public void removeQVTConstraint(String cID) throws EError {
		runner.removeConstraint(cID);
		EchoMarker.removeRelatedInterMarker(cID);
	}
	
	public void removeAllQVTConstraint(IResource transformationRes) throws EError {
		ETransformation etrans = parser.getETransformation(transformationRes.getFullPath().toString(), false);
		runner.removeAllConstraint(etrans.ID);
	}

	public List<String> getConstraints() {
		return runner.getConstraints();
	}
	
	public List<String> getConstraints(IResource transformationRes) {
		ETransformation trans;
		try {
			trans = parser.getETransformation(transformationRes.getFullPath().toString(), false);
			return runner.getConstraintsTransformation(trans.ID);
		} catch (EError e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
		
	}
	
	
	/**
	 * Reloads a QVT constraint
	 * Assumes QVT constraint was previously in the system
	 * Does not reload related models nor meta-models
	 * Launches inter-model checks
	 * @param transforamtionRes the updated qvt resource
	 * @throws EErrorParser 
	 * @throws EErrorTransform 
	 * @throws EErrorCore 
	 * @throws EErrorUnsupported 
	 * @throws EErrorAPI 
	 */
	private ETransformation reloadQVTConstraintAction(IResource transforamtionRes) throws EError {
		String qvtURI = transforamtionRes.getFullPath().toString();
		ETransformation trans = parser.getETransformation(transforamtionRes.getFullPath().toString(), true);
		runner.remTransformation(trans.ID);
		runner.addTransformation(trans);
		reporter.debug("QVT " + qvtURI + " reloaded.");
		return trans;
	}

	public ETransformation reloadQVTConstraint(IResource transformationRes) throws EError {
		ETransformation trans = reloadQVTConstraintAction(transformationRes);
		for (String c : runner.getConstraintsTransformation(trans.ID)) {
//             reporter.debug("Checking " + c);
             conformQVT(c);
		 }
		return trans;
	}
	
	public boolean isManagedQVT(IResource qvtRes) throws EError {
		String qvtURI = qvtRes.getFullPath().toString();
		if(!MDEManager.getInstance().hasETransformation(qvtURI)) return false;
		ETransformation transformation = MDEManager.getInstance().getETransformation(qvtRes.getFullPath().toString(), false);
		return runner.hasTransformation(transformation.ID);
	}
	
	/**
	 * Running tests
	 */
	
	/**
	 * Tests if a model resource conforms to the metamodel
	 * @param res the resource to be tested
	 * @throws EErrorCore
	 * @throws EErrorAPI 
	 */
	private void conformMeta(IResource res) throws EError {
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
	 * @throws EErrorCore
	 * @throws EErrorAPI
	 */
	private void conformQVT(String cID) throws EError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();

		if (!runner.check(cID))
			EchoMarker.createInterMarker(cID);
		else 
			EchoMarker.removeRelatedInterMarker(cID);
	}

	/**
	 * Tests all QVT constraints over a single model
	 * @param res the model over which QVT constraints are tested
	 * @throws EErrorCore
	 * @throws EErrorAPI 
	 */
	private void conformAllQVT(IResource res) throws EError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		EModel model = parser.getModel(res.getFullPath().toString(), false);
		for (String c : runner.getConstraintsModel(model.ID)) {
			if (runner.check(c))
				EchoMarker.removeRelatedInterMarker(c);	
			else
				EchoMarker.createInterMarker(c);
		}

	}

	/**
	 * Instructs the runner to generate a new model and presents the solution in the viewer.
	 * @param metamodelRes the meta-model of the model to be generated
	 * @param scopes extra scopes for model elements ((Package,Class),Scope)
	 * @param targetURI the URI of the model to be generated
	 * @throws EException 
	 */
	public void generate(IResource metamodelRes,
			Map<Entry<String, String>, Integer> scopes, String targetURI, String root)
			throws EException {
		String metamodelURI = metamodelRes.getFullPath().toString();
		
		EMetamodel metamodel = parser.getMetamodel(metamodelURI, false);
		
		if (!runner.hasMetaModel(metamodel.ID))
			runner.addMetamodel(metamodel);
		if (tracked.get(metamodelURI) == null)
			tracked.put(metamodelURI, new ArrayList<IResource>());
		
		runner.generate(metamodel.ID, scopes, targetURI,root);
		
		EModel m = MDEManager.getInstance().getModel(targetURI, false);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetID(m.ID, true, metamodelRes);
		amv.drawGraph();
	}

	public void addQVTgenerate(IResource resqvt, IResource ressource,
			String target, int newp) throws EError {

		if (!isManagedModel(ressource))
			addModelAction(ressource);
		
		ETransformation trans = parser.getETransformation(resqvt.getFullPath().toString(), false);

		String metamodelURI = trans.getModelParams().get(newp).getMetamodel().getURI();
		EMetamodel metamodel = parser.getMetamodel(metamodelURI, false);
		if (!runner.hasMetaModel(metamodel.ID))
			runner.addMetamodel(metamodel);
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
		runner.batch(resqvt.getFullPath().toString(), metamodelURI,
				modelIDs, target);

		EModel m = MDEManager.getInstance().getModel(target, false);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetID(m.ID, true, resmetamodel);
		amv.drawGraph();
	}

	/**
	 * Warns the manager that the new model was selected, creating the constraint over it.
	 * If resource already exists, removes it (may have different meta-model).
	 * @param resmodel
	 * @throws EErrorUnsupported
	 * @throws EErrorCore
	 * @throws EErrorTransform
	 * @throws EErrorParser
	 * @throws EErrorAPI
	 */
	public void modelGenerated(IResource resmodel) throws EError {
		if (isManagedModel(resmodel))
			remModel(resmodel);
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

	public void show(IFile res) throws EError {
		GraphView v = EchoPlugin.getInstance().getGraphView();
		if (v != null) v.clearGraph();
		String path = res.getFullPath().toString();
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(MDEManager.getInstance().getModel(path, false).ID);
		runner.show(list);
	}
	

}
