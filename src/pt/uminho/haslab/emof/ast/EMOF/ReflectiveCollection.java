/**
 */
package pt.uminho.haslab.emof.ast.EMOF;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reflective Collection</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getReflectiveCollection()
 * @model
 * @generated
 */
public interface ReflectiveCollection extends pt.uminho.haslab.emof.ast.EMOF.Object {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean add(pt.uminho.haslab.emof.ast.EMOF.Object object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean addAll(ReflectiveSequence objects);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void clear();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean remove(pt.uminho.haslab.emof.ast.EMOF.Object object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	Integer size();

} // ReflectiveCollection
