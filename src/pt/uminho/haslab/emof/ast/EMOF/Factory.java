/**
 */
package pt.uminho.haslab.emof.ast.EMOF;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Factory</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.Factory#getPackage <em>Package</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getFactory()
 * @model
 * @generated
 */
public interface Factory extends Element {
	/**
	 * Returns the value of the '<em><b>Package</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package</em>' reference.
	 * @see #setPackage(pt.uminho.haslab.emof.ast.EMOF.Package)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getFactory_Package()
	 * @model required="true"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Package getPackage();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.Factory#getPackage <em>Package</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package</em>' reference.
	 * @see #getPackage()
	 * @generated
	 */
	void setPackage(pt.uminho.haslab.emof.ast.EMOF.Package value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.String"
	 * @generated
	 */
	String convertToString(DataType dataType, pt.uminho.haslab.emof.ast.EMOF.Object object);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Element create(pt.uminho.haslab.emof.ast.EMOF.Class metaClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model stringDataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.String"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Object createFromString(DataType dataType, String string);

} // Factory
