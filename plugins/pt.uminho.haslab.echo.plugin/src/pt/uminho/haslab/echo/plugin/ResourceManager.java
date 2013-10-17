package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.EchoProjectPropertiesManager;
import pt.uminho.haslab.echo.plugin.views.GraphView;

public class ResourceManager {

	private static ResourceManager instance = new ResourceManager();
	
	public static ResourceManager getInstance() {
		return instance;
	}
	
	private Map<String,List<IResource>> mms = new HashMap<String,List<IResource>>();
	
	private IResource qvtwaiting;
	private IResource fstwaiting;
	private IResource sndwaiting;
	
	public void addModel(IResource resmodel) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser, CoreException {
		if (hasModel(resmodel)) reloadModel(resmodel);
		EchoReporter reporter = EchoReporter.getInstance();
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		String modeluri = resmodel.getFullPath().toString();
			
		EObject model = parser.loadModel(modeluri);
		if (model == null) reporter.debug(modeluri + " failed to load.");
		else reporter.debug(modeluri + " loaded.");
		String metamodeluri = URIUtil.resolveURI(model.eClass().getEPackage().eResource());
		reporter.debug(metamodeluri + " "+runner.hasMetamodel(metamodeluri));
		if (!runner.hasMetamodel(metamodeluri)) {
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			reporter.debug(metamodeluri + " loaded.");
			runner.addMetamodel(metamodel);
			reporter.debug(metamodeluri + " processed.");		
		}
		runner.addModel(model);
		reporter.debug(modeluri + " processed.");		
		List<IResource> aux = mms.get(metamodeluri);
		if (aux == null) aux = new ArrayList<IResource>();
		aux.add(resmodel);
		mms.put(metamodeluri,aux);
		EchoProjectPropertiesManager.addModel(resmodel.getProject(), modeluri);
		conformMeta(resmodel);
	}
	
	public void reloadModel(IResource resmodel) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser, CoreException {
		EchoReporter.init(new EchoReporter());
		EchoReporter reporter = EchoReporter.getInstance();
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		String modeluri = resmodel.getFullPath().toString();
		EObject model = parser.loadModel(modeluri);
		reporter.debug(modeluri + " loaded.");
		runner.addModel(model);
		reporter.debug(modeluri + " processed.");	
		conformMeta(resmodel);
		conformAllQVT(resmodel);
	}

	public void remModel(IResource resmodel) throws CoreException {	
		EchoRunner runner = EchoRunner.getInstance();
		String modeluri = resmodel.getFullPath().toString();
		EchoProjectPropertiesManager.removeModel(resmodel.getProject(),resmodel.getFullPath().toString());
		resmodel.deleteMarkers(EchoMarker.INTRA_ERROR, false, 0);
		resmodel.deleteMarkers(EchoMarker.INTER_ERROR, false, 0);

	}
	
	public boolean hasModel(IResource resmodel) {
		return EchoRunner.getInstance().hasModel(resmodel.getFullPath().toString());
	}
	
	public void reloadMetamodel(IResource resmetamodel) throws ErrorAlloy, CoreException, ErrorUnsupported, ErrorTransform, ErrorParser  {
		EchoReporter reporter = EchoReporter.getInstance();
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		runner.remMetamodel(resmetamodel.getFullPath().toString());
		try {
			EPackage metamodel = parser.loadMetamodel(resmetamodel.getFullPath().toString());
			runner.addMetamodel(metamodel);
	
			for(IResource resmodel : mms.get(resmetamodel.getFullPath().toString())) {
				reloadModel(resmodel);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void remMetamodel(IResource resmetamodel)  {
		EchoReporter.init(new EchoReporter());
		EchoReporter reporter = EchoReporter.getInstance();
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		runner.remMetamodel(resmetamodel.getFullPath().toString());

		for(IResource resmodel : mms.get(resmetamodel.getFullPath().toString())) {
			runner.remModel(resmetamodel.getFullPath().toString());
			EchoProjectPropertiesManager.removeModel(resmodel.getProject(), resmetamodel.getFullPath().toString());
			//EchoReporter.getInstance().debug("Should delete "+EchoProjectPropertiesManager.getQVTsModelFst(resmodel.getProject(), resmetamodel.getFullPath().toString()));
		}
	}
	
	public boolean hasMetamodel(IResource resmetamodel) {
		return EchoRunner.getInstance().hasMetamodel(resmetamodel.getFullPath().toString());
	}

	public void addQVTConstraint(IResource resqvt, IResource resmodelfst, IResource resmodelsnd) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser, CoreException {
		if (!hasModel(resmodelfst)) addModel(resmodelfst);
		if (!hasModel(resmodelsnd)) addModel(resmodelsnd);
		if (!EchoRunner.getInstance().hasQVT(resqvt.getFullPath().toString())) {
			RelationalTransformation qvt = EchoParser.getInstance().loadQVT(resqvt.getFullPath().toString());
			EchoRunner.getInstance().addQVT(qvt);
		}
		List<String> modeluris = new ArrayList<String>();
		modeluris.add(resmodelfst.getFullPath().toString());
		modeluris.add(resmodelsnd.getFullPath().toString());
		EchoProjectPropertiesManager.addQVT(resqvt.getProject(), resqvt.getFullPath().toString(),modeluris);
		conformQVT(resqvt,resmodelfst,resmodelsnd);	

	}
	
	private void conformMeta(IResource res) throws ErrorAlloy, CoreException
	{
		EchoPlugin.getInstance().getGraphView().clearGraph();
		String path = res.getFullPath().toString();
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(path);
		if(EchoRunner.getInstance().conforms(list)) {
			res.deleteMarkers(EchoMarker.INTRA_ERROR, false, 0);
			//EchoReporter.getInstance().debug("conforms");
		}
		else
			EchoMarker.createIntraMarker(res);		
	}
	
	private void conformQVT(IResource resqvt, IResource resmodelfst, IResource resmodelsnd) throws ErrorAlloy, CoreException {
		List<String> modeluris = new ArrayList<String>(2);
		modeluris.add(resmodelfst.getFullPath().toString());
		modeluris.add(resmodelsnd.getFullPath().toString());

		if(!EchoRunner.getInstance().check(resqvt.getFullPath().toString(), modeluris))
			EchoMarker.createInterMarker(resmodelfst, resmodelsnd, resqvt);
	}
	
	private void conformAllQVT(IResource res) throws ErrorAlloy, CoreException
	{
		EchoPlugin.getInstance().getGraphView().clearGraph();
		Map<String,Set<String>> related =  EchoProjectPropertiesManager.getQVTsModelFst(res.getProject(),res.getFullPath().toString());
		List<String> modeluris = new ArrayList<String>();
		modeluris.add(res.getFullPath().toString());
		for(String qvturi : related.keySet()) {
			for (String relateduri : related.get(qvturi)) {
				IResource partner = res.getWorkspace().getRoot().findMember(relateduri);
				IResource qvt = res.getWorkspace().getRoot().findMember(qvturi);
				modeluris.add(1,relateduri);
				if(EchoRunner.getInstance().check(qvturi, modeluris))
				{	
					for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR, false, 0))
						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(relateduri) &&
								mk.getAttribute(EchoMarker.QVTR).equals(qvturi))
							mk.delete();
					for(IMarker mk : partner.findMarkers(EchoMarker.INTER_ERROR, false, 0))
						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(res.getFullPath().toString()) &&
								mk.getAttribute(EchoMarker.QVTR).equals(qvturi))
							mk.delete();
				}
				else {
					EchoMarker.createInterMarker(res,partner,qvt);
				}
				modeluris.remove(1);
			}
		}
		
		related =  EchoProjectPropertiesManager.getQVTsModelSnd(res.getProject(),res.getFullPath().toString());
		for(String qvturi : related.keySet()) {
			for (String relateduri : related.get(qvturi)) {
				IResource partner = res.getWorkspace().getRoot().findMember(relateduri);
				IResource qvt = res.getWorkspace().getRoot().findMember(qvturi);
				modeluris.add(0, relateduri);
				if(EchoRunner.getInstance().check(qvturi, modeluris))
				{	
					for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR, false, 0))
						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(relateduri) &&
								mk.getAttribute(EchoMarker.QVTR).equals(qvturi))
							mk.delete();
					for(IMarker mk : partner.findMarkers(EchoMarker.INTER_ERROR, false, 0))
						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(res.getFullPath().toString()) &&
								mk.getAttribute(EchoMarker.QVTR).equals(qvturi))
							mk.delete();
				}
				else {
					EchoMarker.createInterMarker(res,partner,qvt);
				}
				modeluris.remove(0);
			}
		}
	}
	
	public void generate(IResource resmetamodel, Map<Entry<String, String>, Integer> scopes, String target) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform {
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		String metamodeluri = resmetamodel.getFullPath().toString();
		if (!runner.hasMetamodel(metamodeluri)) {
			EPackage metamodel = parser.loadMetamodel(metamodeluri);
			runner.addMetamodel(metamodel);
		}
		runner.generate(metamodeluri, scopes);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target,true,resmetamodel);
		amv.drawGraph();
	}
	
	public void addQVTgenerate(IResource resqvt, IResource ressource, String target, int newp) throws ErrorParser, ErrorUnsupported, ErrorAlloy, ErrorTransform, CoreException {
		EchoRunner runner = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		if (!hasModel(ressource)) addModel(ressource);
		RelationalTransformation trans;
		String metamodeluri=null; 
		if (!runner.hasQVT(resqvt.getFullPath().toString())) {
			trans = EchoParser.getInstance().loadQVT(resqvt.getFullPath().toString());
		} else 
			trans = parser.loadQVT(resqvt.getFullPath().toString());
		
		EPackage metamodel = trans.getModelParameter().get(newp).getUsedPackage().get(0).getEPackage();
		metamodeluri = URIUtil.resolveURI(metamodel.eResource());
		metamodel = parser.loadMetamodel(metamodeluri);
		if (!runner.hasMetamodel(metamodeluri)) runner.addMetamodel(metamodel);
		IResource resmetamodel = ResourcesPlugin.getWorkspace().getRoot().findMember(metamodeluri);

		if (!runner.hasQVT(resqvt.getFullPath().toString())) {
			runner.addQVT(trans);
		}
		
		qvtwaiting = resqvt;
		List<String> modeluris = new ArrayList<String>();
		if (newp==0) {
			modeluris.add(target);
			modeluris.add(ressource.getFullPath().toString());
			sndwaiting = ressource;
		} else {
			modeluris.add(ressource.getFullPath().toString());
			modeluris.add(target);
			fstwaiting = ressource;
		}
		runner.generateqvt(resqvt.getFullPath().toString(),metamodeluri,modeluris,target);

		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.setTargetPath(target,true,resmetamodel);
		amv.drawGraph();
	}
	
	public void go (IResource resmodel) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser, CoreException {
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
