package pt.uminho.haslab.mde.transformation.atl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;
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

	private static Map<String,EATLModelParameter> list = new HashMap<>();
	private EObject mdl;
	private EATLTransformation module;
	private EMetamodel metamodel;

	public EATLModelParameter(EObject mdl2,EATLTransformation module) throws EErrorParser, EErrorUnsupported {
		this.module = module;
		mdl = mdl2;
		
		EObject x = (EObject) mdl2.eGet(mdl2.eClass().getEStructuralFeature("metamodel"));
		String y = (String) x.eGet(x.eClass().getEStructuralFeature("name"));		
		list.put((String) mdl.eGet(mdl.eClass().getEStructuralFeature("name")),this);
		metamodel = MDEManager.getInstance().getMetamodel(EATLTransformation.metamodeluris.get(y),false);
	}

	@Override
	public String getName() {
		return (String) mdl.eGet(mdl.eClass().getEStructuralFeature("name"));
	}

	public static EATLModelParameter get(String modelName) {
//		EchoReporter.getInstance().debug("ATL domain metamodel: "+modelName+" over "+list.keySet());
		return list.get(modelName);
	}

	@Override
	public EMetamodel getMetamodel() {
		return metamodel;
	}

}
