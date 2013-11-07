package pt.uminho.haslab.echo.consistency.atl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.consistency.Model;

public class ATLModel implements Model {

	private static Map<EObject,ATLModel> list = new HashMap<EObject,ATLModel>();
	private EObject mdl;
	private ATLTransformation module;
	
	public ATLModel(EObject mdl2,ATLTransformation module) {
		this.module = module;
		mdl = mdl2.eCrossReferences().get(0);
		list.put(mdl,this);
		System.out.println(getName());
	}

	public String getMetamodelURI() {
		return ATLTransformation.metamodeluris.get(getName());
	}
	
	public String getName() {
		return (String) mdl.eGet(mdl.eClass().getEStructuralFeature("name"));
	}

	public static ATLModel get(EObject typedModel) {
		return list.get(typedModel);
	}
	
}
