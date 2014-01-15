package pt.uminho.haslab.mde.transformation.qvt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;

public class QVTModel extends EModelParameter {

	private static Map<TypedModel,QVTModel> list = new HashMap<TypedModel,QVTModel>();
	private TypedModel mdl;
	private EMetamodel metamodel;
	
	public QVTModel(TypedModel mdl2) {
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

	public EMetamodel getMetamodel() {
		return metamodel;
	}
	
	public String getName() {
		return mdl.getName();
	}

	public static QVTModel get(TypedModel typedModel) {
		return list.get(typedModel);
		
	}

}
