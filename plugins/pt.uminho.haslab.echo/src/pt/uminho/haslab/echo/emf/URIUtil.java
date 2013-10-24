package pt.uminho.haslab.echo.emf;

import org.eclipse.emf.ecore.resource.Resource;



public class URIUtil {
	
	public static String resolveURI(Resource res) {
		if (res.getURI().isPlatform())
			return res.getURI().toPlatformString(true).replace(".oclas", "");
		else {
			String aux = res.getURI().path().replace("/null", "");
			aux = aux.replace("/platform/resource", "");
			aux = aux.replace(".oclas", "");
			return aux;
		}
	}
	
}
