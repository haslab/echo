/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Loop Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.LoopExp#getBody <em>Body</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.LoopExp#getIterator <em>Iterator</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getLoopExp()
 * @model abstract="true"
 * @generated
 */
public interface LoopExp extends CallExp, OclExpression {
	/**
	 * Returns the value of the '<em><b>Body</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Body</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Body</em>' containment reference.
	 * @see #setBody(OclExpression)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getLoopExp_Body()
	 * @model containment="true" required="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='loopBodyOwner'"
	 * @generated
	 */
	OclExpression getBody();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.LoopExp#getBody <em>Body</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Body</em>' containment reference.
	 * @see #getBody()
	 * @generated
	 */
	void setBody(OclExpression value);

	/**
	 * Returns the value of the '<em><b>Iterator</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Iterator</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Iterator</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getLoopExp_Iterator()
	 * @model containment="true"
	 * @generated
	 */
	EList<Variable> getIterator();

} // LoopExp
