package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.EchoProjectPropertiesManager;
import pt.uminho.haslab.echo.plugin.views.GraphView;

/**
 * Manages the project resources being tracked by Echo
 * 
 * @author nmm
 * 
 */
public class ResourceManager {

	private static ResourceManager instance = new ResourceManager();

	public static ResourceManager getInstance() {
		return instance;
	}

	private EchoReporter reporter = EchoReporter.getInstance();
	private EchoRunner runner = EchoRunner.getInstance();
	private EchoParser parser = EchoParser.getInstance();

	/** The map o managed model resources: MetamodelURI -> ListModelResources **/
	private Map<String, List<IResource>> tracked = new HashMap<String, List<IResource>>();

	private IResource qvtwaiting;
	private IResource fstwaiting;
	private IResource sndwaiting;

	/**
	 * Model management
	 */

	/**
	 * Tracks a new model.
	 * If model is already being tracked, reloads it. 
	 * If the corresponding meta-model is not being tracked, it is loaded.
	 * 
	 * @param resmodel
	 *            the model resource to be tracked
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	public void addModel(IResource resmodel) throws ErrorUnsupported,
			ErrorAlloy, ErrorTransform, ErrorParser, ErrorAPI {

		if (hasModel(resmodel)) {
			reloadModel(resmodel);
			return;
		}

		String modeluri = resmodel.getFullPath().toString();

		EObject model = parser.loadModel(modeluri);
		reporter.debug("Model " + modeluri + " parsed.");

		String metamodeluri = URIUtil.resolveURI(model.eClass().getEPackage()
				.eResource());

		if (!runner.hasMetamodel(metamodeluri)) {
			reporter.debug("Model's metamodel still not tracked.");
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			reporter.debug("Metamodel " + metamodeluri + " parsed.");
			runner.addMetamodel(metamodel);
			reporter.debug("Metamodel " + metamodeluri + " processed.");
		}
		runner.addModel(model);
		reporter.debug("Model " + modeluri + " processed.");
		List<IResource> aux = tracked.get(metamodeluri);
		if (aux == null)
			aux = new ArrayList<IResource>();
		aux.add(resmodel);
		tracked.put(metamodeluri, aux);
		EchoProjectPropertiesManager.addModel(resmodel.getProject(), modeluri);
		conformMeta(resmodel);
	}

	/**
	 * Reloads a model resource.
	 * Assumes metamodel is already tracked.
	 * 
	 * @param resmodel
	 *            the model resource to be reloaded
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	public void reloadModel(IResource resmodel) throws ErrorUnsupported,
			ErrorAlloy, ErrorTransform, ErrorParser, ErrorAPI {

		String modeluri = resmodel.getFullPath().toString();
		EObject model = parser.loadModel(modeluri);
		reporter.debug("Model " + modeluri + " re-parsed.");
		runner.addModel(model);
		reporter.debug("Model " + modeluri + " re-processed.");
		conformMeta(resmodel);
		conformAllQVT(resmodel);
	}

	
	/**
	 * Untracks a model resource.
	 * Removes from the the Echo core, parser, plugin properties and all error markers.
	 * 
	 * @param resmodel
	 *            the model resource to be untracked
	 * @throws ErrorAPI 
	 */
	public void remModel(IResource resmodel) throws ErrorAPI {
		String modeluri = resmodel.getFullPath().toString();
		EchoProjectPropertiesManager.removeModel(resmodel.getProject(),
				modeluri);

		EObject model = parser.getModelFromUri(modeluri);
		String metamodeluri = parser.getMetamodelURI(model.eClass()
				.getEPackage().getName());

		runner.remModel(modeluri);
		tracked.get(metamodeluri).remove(resmodel);

		EchoMarker.removeIntraMarkers(resmodel);
		EchoMarker.removeInterMarkers(resmodel);

		reporter.debug("Model " + modeluri + " removed.");
	}

	/**
	 * Tests if a resource model is being tracked by the system
	 * 
	 * @param resmodel
	 *            the resource model to be tested
	 * @return if {@code resmodel} is being tracked
	 */
	public boolean hasModel(IResource resmodel) {
		return runner.hasModel(resmodel.getFullPath().toString());
	}

	/**
	 * Metamodel management
	 */

	/**
	 * Reload a metamodel Reloads all depending models
	 * 
	 * @param resmetamodel
	 *            the metamodel resource to be reloaded
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	public void reloadMetamodel(IResource resmetamodel) throws ErrorAlloy,
			ErrorUnsupported, ErrorTransform, ErrorParser, ErrorAPI {
		String metamodeluri = resmetamodel.getFullPath().toString();
		runner.remMetamodel(metamodeluri);
		EPackage metamodel = parser.loadMetamodel(metamodeluri);
		runner.addMetamodel(metamodel);

		for (IResource resmodel : tracked.get(metamodeluri))
			reloadModel(resmodel);

		reporter.debug("Metamodel " + metamodeluri + " reloaded.");

	}

	/**
	 * Untracks a metamodel.
	 * Also untracks all depending models.
	 * 
	 * @param resmetamodel
	 *            the metamodel to be untracked
	 * @throws ErrorAPI 
	 */
	public void remMetamodel(IResource resmetamodel) throws ErrorAPI {
		String metamodeluri = resmetamodel.getFullPath().toString();
		runner.remMetamodel(metamodeluri);

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
	public boolean hasMetamodel(IResource resmetamodel) {
		return runner.hasMetamodel(resmetamodel.getFullPath().toString());
	}

	/**
	 * QVT constraint management
	 */

	/**
	 * Adds a new QVT constraint to the system.
	 * If models are not tracked, adds them to the system.
	 * @param resqvt the qvt resource
	 * @param resmodelfst the first model to be related
	 * @param resmodelsnd the second model to be related
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 * @throws ErrorAPI 
	 */
	public void addQVTConstraint(IResource resqvt, IResource resmodelfst,
			IResource resmodelsnd) throws ErrorUnsupported, ErrorAlloy,
			ErrorTransform, ErrorParser, ErrorAPI {
		String qvturi = resqvt.getFullPath().toString();
		if (!hasModel(resmodelfst)) addModel(resmodelfst);
		if (!hasModel(resmodelsnd)) addModel(resmodelsnd);
		if (!runner.hasQVT(qvturi)) {
			RelationalTransformation qvt = parser.loadQVT(qvturi);
			reporter.debug("QVT-R "+qvturi+" parsed.");
			runner.addQVT(qvt);
			reporter.debug("QVT-R "+qvturi+" processed.");
		}
		List<String> modeluris = new ArrayList<String>();
		modeluris.add(resmodelfst.getFullPath().toString());
		modeluris.add(resmodelsnd.getFullPath().toString());
		EchoProjectPropertiesManager.addQVT(resqvt.getProject(), resqvt
				.getFullPath().toString(), modeluris);
		conformQVT(resqvt, resmodelfst, resmodelsnd);
	}

	
	/**
	 * Running tests
	 */
	
	/**
	 * Tests if a model resource conforms to the metamodel
	 * @param res the resource to be tested
	 * @throws ErrorAlloy
	 * @throws ErrorAPI 
	 */
	private void conformMeta(IResource res) throws ErrorAlloy, ErrorAPI {
		EchoPlugin.getInstance().getGraphView().clearGraph();
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
	 * @param resqvt the QVT specification
	 * @param resmodelfst the first related model
	 * @param resmodelsnd the second related model
	 * @throws ErrorAlloy
	 * @throws ErrorAPI
	 */
	private void conformQVT(IResource resqvt, IResource resmodelfst,
			IResource resmodelsnd) throws ErrorAlloy, ErrorAPI {
		List<String> modeluris = new ArrayList<String>(2);
		modeluris.add(resmodelfst.getFullPath().toString());
		modeluris.add(resmodelsnd.getFullPath().toString());

		if (!runner.check(resqvt.getFullPath().toString(), modeluris))
				EchoMarker.createInterMarker(resmodelfst, resmodelsnd, resqvt);
		else 
			EchoMarker.removeRelatedInterMarker(resmodelfst, resmodelsnd, resqvt);
	}

	/**
	 * Tests all QVT constraints over a single model
	 * @param res the model over which QVT constraints are tested
	 * @throws ErrorAlloy
	 * @throws ErrorAPI 
	 */
	private void conformAllQVT(IResource res) throws ErrorAlloy, ErrorAPI {
		EchoPlugin.getInstance().getGraphView().clearGraph();
		String modeluri = res.getFullPath().toString();
		Map<String, Set<String>> related = EchoProjectPropertiesManager.getQVTsModelFst(res.getProject(), modeluri);
		List<String> modeluris = new ArrayList<String>();
		modeluris.add(modeluri);
		for (String qvturi : related.keySet()) {
			for (String relateduri : related.get(qvturi)) {
				IResource partner = res.getWorkspace().getRoot().findMember(relateduri);
				IResource qvt = res.getWorkspace().getRoot().findMember(qvturi);
				modeluris.add(1, relateduri);
				if (runner.check(qvturi, modeluris))
					EchoMarker.removeRelatedInterMarker(res, partner, qvt);	
				else
					EchoMarker.createInterMarker(res, partner, qvt);
				modeluris.remove(1);
			}
		}

		related = EchoProjectPropertiesManager.getQVTsModelSnd(res.getProject(), modeluri);
		for (String qvturi : related.keySet()) {
			for (String relateduri : related.get(qvturi)) {
				IResource partner = res.getWorkspace().getRoot().findMember(relateduri);
				IResource qvt = res.getWorkspace().getRoot().findMember(qvturi);
				modeluris.add(0, relateduri);
				if (runner.check(qvturi, modeluris))
					EchoMarker.removeRelatedInterMarker(res, partner, qvt);	
				else
					EchoMarker.createInterMarker(res, partner, qvt);
				modeluris.remove(0);
			}
		}
	}

	public void generate(IResource resmetamodel,
			Map<Entry<String, String>, Integer> scopes, String target)
			throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform {
		String metamodeluri = resmetamodel.getFullPath().toString();
		if (!runner.hasMetamodel(metamodeluri)) {
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			runner.addMetamodel(metamodel);
		}
		runner.generate(metamodeluri, scopes);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target, true, resmetamodel);
		amv.drawGraph();
	}

	public void addQVTgenerate(IResource resqvt, IResource ressource,
			String target, int newp) throws ErrorParser, ErrorUnsupported,
			ErrorAlloy, ErrorTransform, ErrorAPI {

		if (!hasModel(ressource))
			addModel(ressource);
		RelationalTransformation trans;
		String metamodeluri = null;
		if (!runner.hasQVT(resqvt.getFullPath().toString())) {
			trans = parser.loadQVT(resqvt.getFullPath().toString());
		} else
			trans = parser.loadQVT(resqvt.getFullPath().toString());

		EPackage metamodel = trans.getModelParameter().get(newp)
				.getUsedPackage().get(0).getEPackage();
		metamodeluri = URIUtil.resolveURI(metamodel.eResource());
		metamodel = parser.loadMetamodel(metamodeluri);
		if (!runner.hasMetamodel(metamodeluri))
			runner.addMetamodel(metamodel);
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
		runner.generateqvt(resqvt.getFullPath().toString(), metamodeluri,
				modeluris, target);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target, true, resmetamodel);
		amv.drawGraph();
	}

	public void go(IResource resmodel) throws ErrorUnsupported, ErrorAlloy,
			ErrorTransform, ErrorParser, ErrorAPI {
		addModel(resmodel);
		if (qvtwaiting != null && fstwaiting != null)
			addQVTConstraint(qvtwaiting, fstwaiting, resmodel);
		if (qvtwaiting != null && sndwaiting != null)
			addQVTConstraint(qvtwaiting, resmodel, sndwaiting);
		qvtwaiting = null;
		fstwaiting = null;
		sndwaiting = null;
	}

}
