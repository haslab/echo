package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;

import java.util.HashMap;
import java.util.Map;

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
	private EMetamodel metamodel;

	public EATLModelParameter(EObject mdl2,EATLTransformation module) throws ErrorParser, ErrorUnsupported {
		this.module = module;
		mdl = mdl2.eCrossReferences().get(0);
		list.put(mdl,this);
		EObject x = (EObject) mdl2.eGet(mdl2.eClass().getEStructuralFeature("metamodel"));
		String y = (String) x.eGet(x.eClass().getEStructuralFeature("name"));		
		metamodel = MDEManager.getInstance().getMetamodel(EATLTransformation.metamodeluris.get(y),false);
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
		return metamodel;
	}

}
