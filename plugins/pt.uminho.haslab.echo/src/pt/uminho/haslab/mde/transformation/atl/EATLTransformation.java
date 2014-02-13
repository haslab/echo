package pt.uminho.haslab.mde.transformation.atl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * An implementation of a model transformation in ATL
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLTransformation extends ETransformation {

	private List<EModelParameter> models = new ArrayList<EModelParameter>();
	private List<ERelation> relations = new ArrayList<ERelation>();
	private EObject transformation;
	public final EObject mdl1;
	public final EObject mdl2;
	public static Map<String,String> metamodeluris = new HashMap<String,String>();

	public EATLTransformation(EObject module, EObject mdl1, EObject mdl2) throws EchoError {
		super("",module);
		this.transformation = module;
		this.mdl1 = mdl1;
		this.mdl2 = mdl2;
		if (!module.eClass().getName().equals("Module")) throw new ErrorParser("Bad atl");

		EStructuralFeature cms = module.eClass().getEStructuralFeature("commentsBefore");
		EList<String> comments = (EList<String>) module.eGet(cms);
		for (String comment : comments) {
			if (comment.split(" ")[1].equals("@nsURI"))
				metamodeluris.put(comment.split(" ")[2].split("=")[0],comment.split(" ")[2].split("=")[1]);
		}

		EStructuralFeature elements = module.eClass().getEStructuralFeature("elements");
		EStructuralFeature inmdls = module.eClass().getEStructuralFeature("inModels");
		EStructuralFeature outmdls = module.eClass().getEStructuralFeature("outModels");
		EList<EObject> objs = (EList<EObject>) module.eGet(elements);
		for (EObject x : objs)
			relations.add(new EATLRelation(x));
		objs = (EList<EObject>) module.eGet(inmdls);
		for (EObject x : objs)
			models.add(new EATLModel(x,this));
		objs = (EList<EObject>) module.eGet(outmdls);
		for (EObject x : objs)
			models.add(new EATLModel(x,this));
	}

	public EATLTransformation(EObject loadATL) throws ErrorUnsupported, ErrorParser {
		super("",loadATL);
		mdl1 = null;
		mdl2 = null;
		// TODO Auto-generated constructor stub
	}

	@Override
	public EObject getEObject() {
		return transformation;
	}

	@Override
	protected void process(EObject artifact) throws ErrorUnsupported {
		// TODO Auto-generated method stub
	}


	@Override
	public List<EModelParameter> getModels() {
		return models;
	}

	@Override
	public List<ERelation> getRelations() {
		return relations;
	}

	@Override
	public String getName() {
		EStructuralFeature name = transformation.eClass().getEStructuralFeature("name");
		return (String) transformation.eGet(name);
	}


}
