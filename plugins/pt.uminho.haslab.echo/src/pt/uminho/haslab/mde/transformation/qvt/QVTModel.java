package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
<<<<<<< HEAD:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/mde/transformation/qvt/QVTModel.java

import pt.uminho.haslab.mde.emf.URIUtil;
import pt.uminho.haslab.mde.transformation.EModelParameter;
=======
import pt.uminho.haslab.echo.consistency.EModelParameter;
import pt.uminho.haslab.echo.emf.URIUtil;
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/echo/consistency/qvt/QVTModel.java

import java.util.HashMap;
import java.util.Map;

public class QVTModel extends EModelParameter {

	private static Map<TypedModel,QVTModel> list = new HashMap<TypedModel,QVTModel>();
	private TypedModel mdl;
	
	public QVTModel(TypedModel mdl2) {
		mdl = mdl2;

		list.put(mdl2,this);
	}

	public String getMetamodelURI() {

		return URIUtil.resolveURI(mdl.getUsedPackage().get(0).getEPackage().eResource());
	}
	
	public String getName() {
		return mdl.getName();
	}

	public static QVTModel get(TypedModel typedModel) {
		return list.get(typedModel);
		
	}

}
