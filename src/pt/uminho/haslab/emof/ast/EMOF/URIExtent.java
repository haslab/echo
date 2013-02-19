/**
 */
package pt.uminho.haslab.emof.ast.EMOF;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>URI Extent</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getURIExtent()
 * @model
 * @generated
 */
public interface URIExtent extends Extent {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.String"
	 * @generated
	 */
	String contextURI();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model uriDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.String"
	 * @generated
	 */
	Element element(String uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.String"
	 * @generated
	 */
	String uri(Element element);

} // URIExtent
