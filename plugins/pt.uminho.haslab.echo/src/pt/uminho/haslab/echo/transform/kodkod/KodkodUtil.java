package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Relation;

import org.eclipse.emf.ecore.EPackage;

import pt.uminho.haslab.mde.emf.URIUtil;

class KodkodUtil {
	
	public static final Relation stringRel = Relation.unary("Strings");
	
	/*TODO: This method is the same as the AlloyUtil one. 
	*Maybe this should be generic or be a part of URIUtil.
	*/
	public static String pckPrefix(EPackage pck, String name){
		return (URIUtil.resolveURI(pck.eResource()) + "@" + name);
	}
	

}
