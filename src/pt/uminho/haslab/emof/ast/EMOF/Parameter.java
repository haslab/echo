/**
 */
package pt.uminho.haslab.emof.ast.EMOF;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.Parameter#getOperation <em>Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends TypedElement, MultiplicityElement {
	/**
	 * Returns the value of the '<em><b>Operation</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EMOF.Operation#getOwnedParameter <em>Owned Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation</em>' container reference.
	 * @see #setOperation(Operation)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getParameter_Operation()
	 * @see pt.uminho.haslab.emof.ast.EMOF.Operation#getOwnedParameter
	 * @model opposite="ownedParameter" resolveProxies="false"
	 * @generated
	 */
	Operation getOperation();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.Parameter#getOperation <em>Operation</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation</em>' container reference.
	 * @see #getOperation()
	 * @generated
	 */
	void setOperation(Operation value);

} // Parameter
