/**
 */
package pt.uminho.haslab.emof.ast.EMOF;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multiplicity Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getIsOrdered <em>Is Ordered</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getIsUnique <em>Is Unique</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getLower <em>Lower</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getUpper <em>Upper</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getMultiplicityElement()
 * @model abstract="true"
 * @generated
 */
public interface MultiplicityElement extends EObject {
	/**
	 * Returns the value of the '<em><b>Is Ordered</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Ordered</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Ordered</em>' attribute.
	 * @see #setIsOrdered(Boolean)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getMultiplicityElement_IsOrdered()
	 * @model default="false" dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsOrdered();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getIsOrdered <em>Is Ordered</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Ordered</em>' attribute.
	 * @see #getIsOrdered()
	 * @generated
	 */
	void setIsOrdered(Boolean value);

	/**
	 * Returns the value of the '<em><b>Is Unique</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Unique</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Unique</em>' attribute.
	 * @see #setIsUnique(Boolean)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getMultiplicityElement_IsUnique()
	 * @model default="true" dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsUnique();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getIsUnique <em>Is Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Unique</em>' attribute.
	 * @see #getIsUnique()
	 * @generated
	 */
	void setIsUnique(Boolean value);

	/**
	 * Returns the value of the '<em><b>Lower</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Lower</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lower</em>' attribute.
	 * @see #setLower(Integer)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getMultiplicityElement_Lower()
	 * @model default="1" dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Integer"
	 * @generated
	 */
	Integer getLower();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getLower <em>Lower</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lower</em>' attribute.
	 * @see #getLower()
	 * @generated
	 */
	void setLower(Integer value);

	/**
	 * Returns the value of the '<em><b>Upper</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Upper</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Upper</em>' attribute.
	 * @see #setUpper(Integer)
	 * @see pt.uminho.haslab.emof.ast.EMOF.EMOFPackage#getMultiplicityElement_Upper()
	 * @model default="1" dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.UnlimitedNatural"
	 * @generated
	 */
	Integer getUpper();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement#getUpper <em>Upper</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Upper</em>' attribute.
	 * @see #getUpper()
	 * @generated
	 */
	void setUpper(Integer value);

} // MultiplicityElement
