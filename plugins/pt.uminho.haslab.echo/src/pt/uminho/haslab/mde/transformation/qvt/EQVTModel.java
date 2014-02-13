package pt.uminho.haslab.mde.transformation.qvt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;

/**
 * An implementation of a model parameter in QVT-R
 *
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EQVTModel extends EModelParameter {

	private static Map<TypedModel,EQVTModel> list = new HashMap<TypedModel,EQVTModel>();
	private TypedModel mdl;
	private EMetamodel metamodel;

	public EQVTModel(TypedModel mdl2) {
		mdl = mdl2;
		String metamodelURI = EcoreUtil.getURI(mdl.getUsedPackage().get(0).getEPackage()).path().replace("/resource", "");
		try {
			metamodel = MDEManager.getInstance().getMetamodel(metamodelURI, false);
		} catch (EchoError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.put(mdl2,this);
	}

	@Override
	public EMetamodel getMetamodel() {
		return metamodel;
	}

	@Override
	public String getName() {
		return mdl.getName();
	}

	public static EQVTModel get(TypedModel typedModel) {
		return list.get(typedModel);

	}

}
