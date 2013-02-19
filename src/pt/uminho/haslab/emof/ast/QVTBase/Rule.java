/**
 */
package pt.uminho.haslab.emof.ast.QVTBase;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EMOF.NamedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getDomain <em>Domain</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getOverrides <em>Overrides</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getTransformation <em>Transformation</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getRule()
 * @model abstract="true"
 * @generated
 */
public interface Rule extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Domain</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTBase.Domain}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Domain#getRule <em>Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getRule_Domain()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Domain#getRule
	 * @model opposite="rule" containment="true"
	 * @generated
	 */
	EList<Domain> getDomain();

	/**
	 * Returns the value of the '<em><b>Overrides</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Overrides</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Overrides</em>' reference.
	 * @see #setOverrides(Rule)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getRule_Overrides()
	 * @model annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='overriden'"
	 * @generated
	 */
	Rule getOverrides();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getOverrides <em>Overrides</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Overrides</em>' reference.
	 * @see #getOverrides()
	 * @generated
	 */
	void setOverrides(Rule value);

	/**
	 * Returns the value of the '<em><b>Transformation</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getRule <em>Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transformation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transformation</em>' container reference.
	 * @see #setTransformation(Transformation)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getRule_Transformation()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Transformation#getRule
	 * @model opposite="rule" resolveProxies="false"
	 * @generated
	 */
	Transformation getTransformation();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getTransformation <em>Transformation</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transformation</em>' container reference.
	 * @see #getTransformation()
	 * @generated
	 */
	void setTransformation(Transformation value);

} // Rule
