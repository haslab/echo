/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EMOF.Type;
import pt.uminho.haslab.emof.ast.EMOF.TypedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression In Ocl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getBodyExpression <em>Body Expression</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getContextVariable <em>Context Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getGeneratedType <em>Generated Type</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getParameterVariable <em>Parameter Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getResultVariable <em>Result Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl()
 * @model
 * @generated
 */
public interface ExpressionInOcl extends TypedElement {
	/**
	 * Returns the value of the '<em><b>Body Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Body Expression</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Body Expression</em>' containment reference.
	 * @see #setBodyExpression(OclExpression)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl_BodyExpression()
	 * @model containment="true" required="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='topExpression'"
	 * @generated
	 */
	OclExpression getBodyExpression();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getBodyExpression <em>Body Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Body Expression</em>' containment reference.
	 * @see #getBodyExpression()
	 * @generated
	 */
	void setBodyExpression(OclExpression value);

	/**
	 * Returns the value of the '<em><b>Context Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Context Variable</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context Variable</em>' containment reference.
	 * @see #setContextVariable(Variable)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl_ContextVariable()
	 * @model containment="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='selfOwner'"
	 * @generated
	 */
	Variable getContextVariable();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getContextVariable <em>Context Variable</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context Variable</em>' containment reference.
	 * @see #getContextVariable()
	 * @generated
	 */
	void setContextVariable(Variable value);

	/**
	 * Returns the value of the '<em><b>Generated Type</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generated Type</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generated Type</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl_GeneratedType()
	 * @model containment="true" ordered="false"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='owningExpression'"
	 * @generated
	 */
	EList<Type> getGeneratedType();

	/**
	 * Returns the value of the '<em><b>Parameter Variable</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Variable</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Variable</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl_ParameterVariable()
	 * @model containment="true" ordered="false"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='varOwner'"
	 * @generated
	 */
	EList<Variable> getParameterVariable();

	/**
	 * Returns the value of the '<em><b>Result Variable</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result Variable</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Variable</em>' containment reference.
	 * @see #setResultVariable(Variable)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getExpressionInOcl_ResultVariable()
	 * @model containment="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='resultOwner'"
	 * @generated
	 */
	Variable getResultVariable();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl#getResultVariable <em>Result Variable</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result Variable</em>' containment reference.
	 * @see #getResultVariable()
	 * @generated
	 */
	void setResultVariable(Variable value);

} // ExpressionInOcl
