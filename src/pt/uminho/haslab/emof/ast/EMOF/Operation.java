/**
 */
package pt.uminho.haslab.emof.ast.EMOF;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.Operation#getClass_ <em>Class</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.Operation#getOwnedParameter <em>Owned Parameter</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.Operation#getRaisedException <em>Raised Exception</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends TypedElement, MultiplicityElement {
	/**
	 * Returns the value of the '<em><b>Class</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EMOF.Class#getOwnedOperation <em>Owned Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' container reference.
	 * @see #setClass(pt.uminho.haslab.emof.ast.EMOF.Class)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getOperation_Class()
	 * @see pt.uminho.haslab.emof.ast.EMOF.Class#getOwnedOperation
	 * @model opposite="ownedOperation" resolveProxies="false"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Class getClass_();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.Operation#getClass_ <em>Class</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' container reference.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(pt.uminho.haslab.emof.ast.EMOF.Class value);

	/**
	 * Returns the value of the '<em><b>Owned Parameter</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Parameter}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EMOF.Parameter#getOperation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owned Parameter</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owned Parameter</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getOperation_OwnedParameter()
	 * @see pt.uminho.haslab.emof.ast.EMOF.Parameter#getOperation
	 * @model opposite="operation" containment="true"
	 * @generated
	 */
	EList<Parameter> getOwnedParameter();

	/**
	 * Returns the value of the '<em><b>Raised Exception</b></em>' reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Raised Exception</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Raised Exception</em>' reference list.
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getOperation_RaisedException()
	 * @model ordered="false"
	 * @generated
	 */
	EList<Type> getRaisedException();

} // Operation
