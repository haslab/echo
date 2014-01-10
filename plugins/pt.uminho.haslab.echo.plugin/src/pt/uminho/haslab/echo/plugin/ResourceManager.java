package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
<<<<<<< HEAD

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
=======
import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.plugin.ConstraintManager.Constraint;
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.views.GraphView;
import pt.uminho.haslab.mde.emf.EMFParser;
import pt.uminho.haslab.mde.emf.URIUtil;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Manages the project resources being tracked by Echo
 * 
 * @author nmm
 * 
 */
public class ResourceManager {

	private EchoReporter reporter = EchoReporter.getInstance();
	private EchoRunner runner = EchoPlugin.getInstance().getRunner();
	private EMFParser parser = EMFParser.getInstance();

	/** The map of managed model resources: MetamodelURI -> ListModelResources **/
	private Map<String, List<IResource>> tracked = new HashMap<String, List<IResource>>();
	private Map<String, List<IResource>> ctracked = new HashMap<String, List<IResource>>();
	private Map<IResource, String> metas = new HashMap<IResource, String>();
	/** The map of managed qvtr constraints: QVTRURI -> ListModelResources **/
	public EConstraintManager constraints = new EConstraintManager();

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
	 * @param resmodel
	 *            the model resource to be tracked
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	private EObject addModelAction(IResource resmodel) throws EchoError {

		if (isManagedModel(resmodel)) {
			return reloadModel(resmodel);
		}

		String modeluri = resmodel.getFullPath().toString();

		EObject model = parser.loadModel(modeluri);
		reporter.debug("Model " + modeluri + " parsed.");
		
		String metamodeluri = URIUtil.resolveURI(model.eClass().getEPackage().eResource());

		if (!runner.hasMetaModel(metamodeluri)) {
			reporter.debug("Model's metamodel "+metamodeluri+"still not tracked.");
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			reporter.debug("Metamodel " + metamodeluri + " parsed.");
			runner.addMetaModel(metamodel);
			reporter.debug("Metamodel " + metamodeluri + " processed.");
		}
		runner.addModel(model);
		reporter.debug("Model " + modeluri + " processed.");
		List<IResource> aux = tracked.get(metamodeluri);
		if (aux == null)
			aux = new ArrayList<IResource>();
		aux.add(resmodel);
		tracked.put(metamodeluri, aux);
		metas.put(resmodel, metamodeluri);
		
		return model;
	}
	
	public EObject addModel(IResource resmodel) throws EchoError {
		EObject o = addModelAction( resmodel);
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
	private EObject reloadModelAction(IResource resmodel) throws EchoError {
		String modeluri = resmodel.getFullPath().toString();
		runner.remModel(modeluri);
		EObject model = parser.loadModel(modeluri);
		reporter.debug("Model " + modeluri + " re-parsed.");
		runner.addModel(model);
		reporter.debug("Model " + modeluri + " re-processed.");
		return model;
	}
	
	public EObject reloadModel(IResource resmodel) throws EchoError {
		EObject o = reloadModelAction(resmodel);
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

		EObject model = parser.getModelFromUri(modelUri);
		String metaModelUri = parser.getMetamodelURI(model.eClass()
				.getEPackage().getName());
		
		
		runner.remModel(modelUri);
		tracked.get(metaModelUri).remove(resModel);
		
		for (EConstraint c : constraints.getAllConstraintsModel(resModel)) {
			reporter.debug("Will remove  " + c.constraint);
			this.removeQVTConstraint(c);
		}

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
	 */
	public boolean isManagedModel(IResource resmodel) {
		return runner.hasModel(resmodel.getFullPath().toString());
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
		EPackage metamodel = parser.loadMetamodel(metamodeluri);
		runner.addMetaModel(metamodel);

		for (IResource resqvt : ctracked.get(metamodeluri))
			reloadQVTConstraintAction( resqvt);

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
	 */
	public boolean isManagedMetamodel(IResource resmetamodel) {
		return runner.hasMetaModel(resmetamodel.getFullPath().toString());
	}

	/**
	 * QVT constraint management
	 */

	/**
	 * Adds a new QVT constraint to the system.
	 * If models are not tracked, adds them to the system.
	 * @param resqvt the qvt resource
<<<<<<< HEAD
	 * @param deps 
	 * @param resmodelfst the first model to be related
	 * @param resmodelsnd the second model to be related
=======
	 * @param resmodels the first model to be related
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	private EConstraint addQVTConstraintAction(IResource resqvt, List<IResource> resmodels) throws EchoError {
		String qvturi = resqvt.getFullPath().toString();
		List<EObject> models = new ArrayList<EObject>();
		List<String> metamodels = new ArrayList<String>();

		for (IResource resmodel : resmodels){
			if (!isManagedModel(resmodel)) addModel(resmodel);
			EObject obj = parser.getModelFromUri(resmodel.getFullPath().toString());
			models.add(obj);
			metamodels.add(parser.getMetamodelURI(obj.eClass().getEPackage().getName()));
		}
			
		for (String mm : metamodels) {
			List<IResource> l = ctracked.get(mm);
			if (l == null) l = new ArrayList<IResource>();
			l.add(resqvt);
			ctracked.put(mm, l);
		}
		
		RelationalTransformation qvt;
		if (!runner.hasQVT(qvturi)) {
			qvt = parser.loadQVT(qvturi);
			reporter.debug("QVT-R "+qvturi+" parsed.");
		} else {
			qvt = parser.getTransformation(qvturi);
		}

		for (int i=0;i<qvt.getModelParameter().size();i++) {
			if (!qvt.getModelParameter().get(i).getUsedPackage().get(0).getName().equals(models.get(i).eClass().getEPackage().getName()))
				throw new ErrorAPI("Model does not type-check.");
		}
		
		
		if (!runner.hasQVT(qvturi)) {
			runner.addQVT(qvt);
			reporter.debug("QVT-R "+qvturi+" processed.");
		}
		
		
		EConstraint c = constraints.addConstraint(resqvt,resmodels);
		return c;
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
		constraints.removeConstraint(c);
		if (constraints.getAllConstraintsConstraint(c.constraint) == null ||
				constraints.getAllConstraintsConstraint(c.constraint).size() == 0)
			runner.remQVT(c.constraint.getFullPath().toString());


		EchoMarker.removeRelatedInterMarker(c);
	}
	
	public void removeAllQVTConstraint(IResource r) throws EchoError {
		for (EConstraint c : constraints.getAllConstraintsConstraint(r))
			this.removeQVTConstraint(c);
	}

	
	public List<EConstraint> getConstraints() {
		return constraints.getAllConstraints();
	}
	
	public ETransformation getTransformation(IResource constraint) {
		if (runner.hasQVT(constraint.getFullPath().toString())) {
			return runner.getQVT(constraint.getFullPath().toString());
		}
		return null;
		
	}
	
	public List<EConstraint> getConstraints(IResource constraint) {
		List<EConstraint> res = constraints.getAllConstraintsConstraint(constraint);
		if (res == null) res = new ArrayList<EConstraint>();
		return res;
	}
	
	
	/**
	 * Reloads a QVT constraint
	 * Assumes QVT constraint was previously in the system
	 * Does not reload related models nor meta-models
	 * Launches inter-model checks
	 * @param res the updated qvt resource
	 * @throws ErrorParser 
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine 
	 * @throws ErrorUnsupported 
	 * @throws ErrorAPI 
	 */
	private void reloadQVTConstraintAction(IResource res) throws EchoError {
		String uri = res.getFullPath().toString();
		runner.remQVT(uri);
		RelationalTransformation qvt = parser.loadQVT(uri);
		runner.addQVT(qvt);
		reporter.debug("QVT " + uri + " reloaded.");
	}

	public void reloadQVTConstraint(IResource res) throws EchoError {
		reloadQVTConstraintAction(res);
		for (EConstraint c : constraints.getAllConstraintsConstraint(res)) {
             reporter.debug("Checking " + c);
             conformQVT(c);
		 }
	}
	
	public boolean isManagedQVT(IResource qvtres) {
		return runner.hasQVT(qvtres.getFullPath().toString());
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
		list.add(path);
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
		List<String> modeluris = new ArrayList<String>(2);
		for (IResource model : c.models)
			modeluris.add(model.getFullPath().toString());

		if (!runner.check(c.constraint.getFullPath().toString(), modeluris))
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
		List<EConstraint> cs = constraints.getAllConstraintsModel(res);
		for (EConstraint c : cs) {
			List<String> modeluris = new ArrayList<String>();
			for (IResource r : c.models)
				modeluris.add(r.getFullPath().toString());
			if (runner.check(c.constraint.getFullPath().toString(), modeluris))
				EchoMarker.removeRelatedInterMarker(c);	
			else
				EchoMarker.createInterMarker(c);
		}

	}

	public void generate(IResource resMetaModel,
			Map<Entry<String, String>, Integer> scopes, String target)
			throws EchoError {
		String metamodeluri = resMetaModel.getFullPath().toString();
		if (!isManagedMetamodel(resMetaModel)) {
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			runner.addMetaModel(metamodel);
		}
			
		runner.generate(metamodeluri, scopes, target);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target, true, resMetaModel);
		amv.drawGraph();
	}

	public void addQVTgenerate(IResource resqvt, IResource ressource,
			String target, int newp) throws EchoError {

		if (!isManagedModel(ressource))
			addModelAction(ressource);
		RelationalTransformation trans;
		String metamodeluri = null;
		
		if (!isManagedQVT(resqvt))
			trans = parser.loadQVT(resqvt.getFullPath().toString());
		else
			trans = parser.loadQVT(resqvt.getFullPath().toString());

		EPackage metamodel = trans.getModelParameter().get(newp)
				.getUsedPackage().get(0).getEPackage();
		
		metamodeluri = URIUtil.resolveURI(metamodel.eResource());
		metamodel = parser.loadMetamodel(metamodeluri);
		if (!runner.hasMetaModel(metamodeluri))
			runner.addMetaModel(metamodel);
		IResource resmetamodel = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(metamodeluri);

		if (!runner.hasQVT(resqvt.getFullPath().toString())) {
			runner.addQVT(trans);
		}

		qvtwaiting = resqvt;
		List<String> modeluris = new ArrayList<String>();
		if (newp == 0) {
			modeluris.add(target);
			modeluris.add(ressource.getFullPath().toString());
			sndwaiting = ressource;
		} else {
			modeluris.add(ressource.getFullPath().toString());
			modeluris.add(target);
			fstwaiting = ressource;
		}
		runner.generateQvt(resqvt.getFullPath().toString(), metamodeluri,
                modeluris, target);

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
		list.add(path);
		runner.show(list);
	}
	

}
