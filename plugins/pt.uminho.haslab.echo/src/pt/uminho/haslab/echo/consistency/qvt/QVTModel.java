package pt.uminho.haslab.echo.consistency.qvt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.qvtd.pivot.qvtbase.TypedModel;


import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.emf.URIUtil;

public class QVTModel implements Model {

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
