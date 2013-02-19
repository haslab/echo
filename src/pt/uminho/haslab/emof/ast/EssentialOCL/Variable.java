/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;

import pt.uminho.haslab.emof.ast.EMOF.Parameter;
import pt.uminho.haslab.emof.ast.EMOF.TypedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Variable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getInitExpression <em>Init Expression</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getLetExp <em>Let Exp</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getRepresentedParameter <em>Represented Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getVariable()
 * @model
 * @generated
 */
public interface Variable extends TypedElement {
	/**
	 * Returns the value of the '<em><b>Init Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Init Expression</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Init Expression</em>' containment reference.
	 * @see #setInitExpression(OclExpression)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getVariable_InitExpression()
	 * @model containment="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='initializedElement'"
	 * @generated
	 */
	OclExpression getInitExpression();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getInitExpression <em>Init Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Init Expression</em>' containment reference.
	 * @see #getInitExpression()
	 * @generated
	 */
	void setInitExpression(OclExpression value);

	/**
	 * Returns the value of the '<em><b>Let Exp</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EssentialOCL.LetExp#getVariable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Let Exp</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Let Exp</em>' container reference.
	 * @see #setLetExp(LetExp)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getVariable_LetExp()
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.LetExp#getVariable
	 * @model opposite="variable" resolveProxies="false"
	 * @generated
	 */
	LetExp getLetExp();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getLetExp <em>Let Exp</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Let Exp</em>' container reference.
	 * @see #getLetExp()
	 * @generated
	 */
	void setLetExp(LetExp value);

	/**
	 * Returns the value of the '<em><b>Represented Parameter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Represented Parameter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Represented Parameter</em>' reference.
	 * @see #setRepresentedParameter(Parameter)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getVariable_RepresentedParameter()
	 * @model
	 * @generated
	 */
	Parameter getRepresentedParameter();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable#getRepresentedParameter <em>Represented Parameter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Represented Parameter</em>' reference.
	 * @see #getRepresentedParameter()
	 * @generated
	 */
	void setRepresentedParameter(Parameter value);

} // Variable
