package pt.uminho.haslab.mde.transformation.atl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * An embedding of an EMF ATL model transformation in Echo.
 * 
 * @author nmm
 * @version 0.4 05/03/2014
 */
public class EATLTransformation extends ETransformation {

	private Map<String,EATLModelParameter> modelParams;
	private HashMap<String,EATLRelation> relations;
	private EObject transformation;
	private List<EDependency> dependencies;

	public static Map<String,String> metamodeluris = new HashMap<>();

	public EATLTransformation(EObject module) throws EError {
		super((String) module.eGet(module.eClass().getEStructuralFeature("name")),module);
	}

	@Override
	public EObject getEObject() {
		return transformation;
	}

	@Override
	protected void process(EObject module) throws EErrorUnsupported, EErrorParser {
		this.transformation = module;

		if (modelParams == null) modelParams = new HashMap<>();
		if (relations == null) relations = new HashMap<>();
	
		if (!module.eClass().getName().equals("Module")) throw new EErrorParser(EErrorParser.ATL,"Bad atl",Task.TRANSLATE_TRANSFORMATION);

		EStructuralFeature cms = module.eClass().getEStructuralFeature("commentsBefore");
		EList<String> comments = (EList<String>) module.eGet(cms);
		for (String comment : comments) {
			if (comment.split(" ")[1].equals("@nsURI")) {
				String uri = comment.split(" ")[2].split("=")[1].replace("\'", "");
				EchoReporter.getInstance().debug("URI: "+uri);
				EchoReporter.getInstance().debug("URI: "+org.eclipse.emf.common.util.URI.createURI(uri));
//				EchoReporter.getInstance().debug("URI: "+new Path(uri).makeAbsolute());
//				EchoReporter.getInstance().debug("URI: "+new Path(uri).makeRelative());
				metamodeluris.put(comment.split(" ")[2].split("=")[0],uri);
			}
		}

		EStructuralFeature elements = module.eClass().getEStructuralFeature("elements");
		EStructuralFeature inmdls = module.eClass().getEStructuralFeature("inModels");
		EStructuralFeature outmdls = module.eClass().getEStructuralFeature("outModels");
		EList<EObject> objs = (EList<EObject>) module.eGet(elements);
		for (EObject x : objs)
			relations.put((String) x.eGet(x.eClass().getEStructuralFeature("name")),new EATLRelation(this,x));
		objs = (EList<EObject>) module.eGet(inmdls);
		for (EObject x : objs)
			modelParams.put((String) x.eGet(x.eClass().getEStructuralFeature("name")), new EATLModelParameter(x,this));
		objs = (EList<EObject>) module.eGet(outmdls);
		for (EObject x : objs)
			modelParams.put((String) x.eGet(x.eClass().getEStructuralFeature("name")), new EATLModelParameter(x,this));
	}


	@Override
	public List<EATLModelParameter> getModelParams() {
		return new ArrayList<EATLModelParameter>(modelParams.values());
	}

	@Override
	public List<EATLRelation> getRelations() {
		return new ArrayList<EATLRelation>(relations.values());
	}

	public EATLRelation getRelation(String name) {
		return relations.get(name);
	}

	@Override
	public String getName() {
		EStructuralFeature name = transformation.eClass().getEStructuralFeature("name");
		return (String) transformation.eGet(name);
	}

	@Override
	public EATLModelParameter getModelParameter(String paramName) {
		return modelParams.get(paramName);
	}

	@Override
	public List<EDependency> getDependencies() {
		return dependencies;
	}



}
