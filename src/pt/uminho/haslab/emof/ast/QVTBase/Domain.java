/**
 */
package pt.uminho.haslab.emof.ast.QVTBase;

import pt.uminho.haslab.emof.ast.EMOF.NamedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Domain</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getIsCheckable <em>Is Checkable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getIsEnforceable <em>Is Enforceable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getRule <em>Rule</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getTypedModel <em>Typed Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getDomain()
 * @model abstract="true"
 * @generated
 */
public interface Domain extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Is Checkable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Checkable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Checkable</em>' attribute.
	 * @see #setIsCheckable(Boolean)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getDomain_IsCheckable()
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsCheckable();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getIsCheckable <em>Is Checkable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Checkable</em>' attribute.
	 * @see #getIsCheckable()
	 * @generated
	 */
	void setIsCheckable(Boolean value);

	/**
	 * Returns the value of the '<em><b>Is Enforceable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Enforceable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Enforceable</em>' attribute.
	 * @see #setIsEnforceable(Boolean)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getDomain_IsEnforceable()
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsEnforceable();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getIsEnforceable <em>Is Enforceable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Enforceable</em>' attribute.
	 * @see #getIsEnforceable()
	 * @generated
	 */
	void setIsEnforceable(Boolean value);

	/**
	 * Returns the value of the '<em><b>Rule</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getDomain <em>Domain</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule</em>' container reference.
	 * @see #setRule(Rule)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getDomain_Rule()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Rule#getDomain
	 * @model opposite="domain" resolveProxies="false" required="true"
	 * @generated
	 */
	Rule getRule();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getRule <em>Rule</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rule</em>' container reference.
	 * @see #getRule()
	 * @generated
	 */
	void setRule(Rule value);

	/**
	 * Returns the value of the '<em><b>Typed Model</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Typed Model</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Typed Model</em>' reference.
	 * @see #setTypedModel(TypedModel)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getDomain_TypedModel()
	 * @model
	 * @generated
	 */
	TypedModel getTypedModel();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getTypedModel <em>Typed Model</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Typed Model</em>' reference.
	 * @see #getTypedModel()
	 * @generated
	 */
	void setTypedModel(TypedModel value);

} // Domain
