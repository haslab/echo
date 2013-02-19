/**
 */
package pt.uminho.haslab.emof.ast.EMOF;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reflective Sequence</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getReflectiveSequence()
 * @model
 * @generated
 */
public interface ReflectiveSequence extends ReflectiveCollection {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model indexDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	void add(Integer index, pt.uminho.haslab.emof.ast.EMOF.Object object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model indexDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Object get(Integer index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model indexDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Object remove(Integer index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model indexDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Object set(Integer index, pt.uminho.haslab.emof.ast.EMOF.Object object);

} // ReflectiveSequence
