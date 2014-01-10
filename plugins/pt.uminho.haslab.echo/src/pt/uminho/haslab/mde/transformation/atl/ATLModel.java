package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;
<<<<<<< HEAD:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/mde/transformation/atl/ATLModel.java

import pt.uminho.haslab.mde.transformation.EModelParameter;
=======
import pt.uminho.haslab.echo.consistency.EModelParameter;
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/echo/consistency/atl/ATLModel.java

import java.util.HashMap;
import java.util.Map;

public class ATLModel extends EModelParameter {

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
