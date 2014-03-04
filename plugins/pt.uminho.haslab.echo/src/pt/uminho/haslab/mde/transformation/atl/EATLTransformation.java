package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.atl.emftvm.ModelDeclaration;
import org.eclipse.m2m.atl.emftvm.Module;
import org.eclipse.m2m.atl.emftvm.Rule;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
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
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLTransformation extends ETransformation {

	private Map<String,EATLModelParameter> modelParams;
	private List<EATLRelation> relations = new ArrayList<>();
	private Module transformation;

	public static Map<String,String> metamodeluris = new HashMap<>();

	public EATLTransformation(Module module) throws EchoError {
		super(module.getName(),module);
	}

	@Override
	public EObject getEObject() {
		return transformation;
	}

	@Override
	protected void process(EObject module) throws ErrorUnsupported, ErrorParser {
		this.transformation = (Module) module;

		if (modelParams == null) modelParams = new HashMap<>();
		if (relations == null) relations = new ArrayList<>();
	
		for (Rule x : transformation.getRules())
			relations.add(new EATLRelation(x));
		for (ModelDeclaration x : transformation.getInputModels())
			modelParams.put(x.getModelName(), new EATLModelParameter(x,this));
		for (ModelDeclaration x : transformation.getOutputModels())
			modelParams.put(x.getModelName(), new EATLModelParameter(x,this));
	}


	@Override
	public List<EATLModelParameter> getModelParams() {
		return new ArrayList<EATLModelParameter>(modelParams.values());
	}

	@Override
	public List<EATLRelation> getRelations() {
		return relations;
	}

	@Override
	public String getName() {
		EStructuralFeature name = transformation.eClass().getEStructuralFeature("name");
		return (String) transformation.eGet(name);
	}

	@Override
	public EModelParameter getModelParameter(String paramName) {
		// TODO Auto-generated method stub
		return null;
	}


}
