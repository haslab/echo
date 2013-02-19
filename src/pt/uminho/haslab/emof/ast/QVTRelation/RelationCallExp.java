/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation Call Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationCallExp#getArgument <em>Argument</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationCallExp#getReferredRelation <em>Referred Relation</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationCallExp()
 * @model
 * @generated
 */
public interface RelationCallExp extends OclExpression {
	/**
	 * Returns the value of the '<em><b>Argument</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Argument</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Argument</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationCallExp_Argument()
	 * @model containment="true" lower="2"
	 * @generated
	 */
	EList<OclExpression> getArgument();

	/**
	 * Returns the value of the '<em><b>Referred Relation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referred Relation</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referred Relation</em>' reference.
	 * @see #setReferredRelation(Relation)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationCallExp_ReferredRelation()
	 * @model required="true"
	 * @generated
	 */
	Relation getReferredRelation();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationCallExp#getReferredRelation <em>Referred Relation</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referred Relation</em>' reference.
	 * @see #getReferredRelation()
	 * @generated
	 */
	void setReferredRelation(Relation value);

} // RelationCallExp
