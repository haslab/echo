/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EssentialOCL.CollectionType;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Collection Template Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.CollectionTemplateExp#getMember <em>Member</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.CollectionTemplateExp#getReferredCollectionType <em>Referred Collection Type</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.CollectionTemplateExp#getRest <em>Rest</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getCollectionTemplateExp()
 * @model
 * @generated
 */
public interface CollectionTemplateExp extends TemplateExp {
	/**
	 * Returns the value of the '<em><b>Member</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Member</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Member</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getCollectionTemplateExp_Member()
	 * @model containment="true" ordered="false"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='listContainer'"
	 * @generated
	 */
	EList<OclExpression> getMember();

	/**
	 * Returns the value of the '<em><b>Referred Collection Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referred Collection Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referred Collection Type</em>' reference.
	 * @see #setReferredCollectionType(CollectionType)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getCollectionTemplateExp_ReferredCollectionType()
	 * @model required="true"
	 * @generated
	 */
	CollectionType getReferredCollectionType();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.CollectionTemplateExp#getReferredCollectionType <em>Referred Collection Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referred Collection Type</em>' reference.
	 * @see #getReferredCollectionType()
	 * @generated
	 */
	void setReferredCollectionType(CollectionType value);

	/**
	 * Returns the value of the '<em><b>Rest</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rest</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rest</em>' reference.
	 * @see #setRest(Variable)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getCollectionTemplateExp_Rest()
	 * @model annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='matchingExp'"
	 * @generated
	 */
	Variable getRest();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.CollectionTemplateExp#getRest <em>Rest</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rest</em>' reference.
	 * @see #getRest()
	 * @generated
	 */
	void setRest(Variable value);

} // CollectionTemplateExp
