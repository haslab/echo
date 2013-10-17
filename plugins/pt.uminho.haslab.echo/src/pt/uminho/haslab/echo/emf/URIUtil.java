package pt.uminho.haslab.echo.emf;

import org.eclipse.emf.ecore.resource.Resource;

import pt.uminho.haslab.echo.EchoReporter;

public class URIUtil {
	
	public static String resolveURI(Resource res) {
		if (res.getURI().isPlatform())
			return res.getURI().toPlatformString(true);
		else {
			String aux = res.getURI().path().replace("/null", "");
			aux = aux.replace("/platform/resource", "");
			return aux;
		}
	}
	
}
