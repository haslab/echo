package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTModelParameter;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedding of an EMF ATL model transformation in Echo.
 * 
 * @author nmm
 * @version 0.4 05/03/2014
 */
public class EATLTransformation extends ETransformation {

	private Map<String,EATLModelParameter> modelParams;
	private List<EATLRelation> relations = new ArrayList<>();
	private EObject transformation;

	public static Map<String,String> metamodeluris = new HashMap<>();

	public EATLTransformation(EObject module) throws EchoError {
		super((String) module.eGet(module.eClass().getEStructuralFeature("name")),module);
	}

	@Override
	public EObject getEObject() {
		return transformation;
	}

	@Override
	protected void process(EObject module) throws ErrorUnsupported, ErrorParser {
		this.transformation = module;

		if (modelParams == null) modelParams = new HashMap<>();
		if (relations == null) relations = new ArrayList<>();
	
		if (!module.eClass().getName().equals("Module")) throw new ErrorParser("Bad atl");

		EStructuralFeature cms = module.eClass().getEStructuralFeature("commentsBefore");
		EList<String> comments = (EList<String>) module.eGet(cms);
		for (String comment : comments) {
			if (comment.split(" ")[1].equals("@nsURI")) {
				String uri = comment.split(" ")[2].split("=")[1].replace("\'", "");
				EchoReporter.getInstance().debug("URI: "+uri);
				EchoReporter.getInstance().debug("URI: "+org.eclipse.emf.common.util.URI.createURI(uri));
				EchoReporter.getInstance().debug("URI: "+new Path(uri).makeAbsolute());
				EchoReporter.getInstance().debug("URI: "+new Path(uri).makeRelative());
				metamodeluris.put(comment.split(" ")[2].split("=")[0],uri);
			}
		}

		EStructuralFeature elements = module.eClass().getEStructuralFeature("elements");
		EStructuralFeature inmdls = module.eClass().getEStructuralFeature("inModels");
		EStructuralFeature outmdls = module.eClass().getEStructuralFeature("outModels");
		EList<EObject> objs = (EList<EObject>) module.eGet(elements);
		for (EObject x : objs) {
			relations.add(new EATLRelation(x));
			System.out.println("add here: "+relations);
		}
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
		System.out.println("been here: "+relations);
		return relations;
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



}
