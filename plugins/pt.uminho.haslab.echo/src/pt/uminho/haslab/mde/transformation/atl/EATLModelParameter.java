package pt.uminho.haslab.mde.transformation.atl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;

/**
 * An embedding of an EMF ATL model parameter in Echo.
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLModelParameter extends EModelParameter {

	private static Map<EObject,EATLModelParameter> list = new HashMap<EObject,EATLModelParameter>();
	private EObject mdl;
	private EATLTransformation module;

	public EATLModelParameter(EObject mdl2,EATLTransformation module) {
		this.module = module;
		mdl = mdl2.eCrossReferences().get(0);
		list.put(mdl,this);
		System.out.println(getName());
	}

	public String getMetamodelURI() {
		return EATLTransformation.metamodeluris.get(getName());
	}

	@Override
	public String getName() {
		return (String) mdl.eGet(mdl.eClass().getEStructuralFeature("name"));
	}

	public static EATLModelParameter get(EObject typedModel) {
		return list.get(typedModel);
	}

	@Override
	public EMetamodel getMetamodel() {
		// TODO Auto-generated method stub
		return null;
	}

}
