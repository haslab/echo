package pt.uminho.haslab.echo.consistency.atl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Relation;
import pt.uminho.haslab.echo.consistency.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ATLTransformation implements Transformation {

	private static Map<EObject,ATLTransformation> list = new HashMap<EObject,ATLTransformation>();

	private List<Model> models = new ArrayList<Model>();
	private List<Relation> relations = new ArrayList<Relation>();
	private EObject transformation;
	public final EObject mdl1;
	public final EObject mdl2;
	public static Map<String,String> metamodeluris = new HashMap<String,String>();
	
	public ATLTransformation(EObject module, EObject mdl1, EObject mdl2) throws ErrorParser {
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
        	relations.add(new ATLRelation(x));
        objs = (EList<EObject>) module.eGet(inmdls);
        for (EObject x : objs)
        	models.add(new ATLModel(x,this)); 
        objs = (EList<EObject>) module.eGet(outmdls);
        for (EObject x : objs)
        	models.add(new ATLModel(x,this));
        list.put(module, this);
	}

	@Override
	public List<Model> getModels() {
		return models;
	}

	@Override
	public List<Relation> getRelations() {
		return relations;
	}

	@Override
	public String getName() {
		EStructuralFeature name = transformation.eClass().getEStructuralFeature("name");
		return (String) transformation.eGet(name);
	}
	
	public static ATLTransformation get(EObject eObject) {
		return list.get(eObject);
	}
	
	

}
