/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EMOF.Element;
import pt.uminho.haslab.emof.ast.EMOF.Property;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Key</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getIdentifies <em>Identifies</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getOppositePart <em>Opposite Part</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getPart <em>Part</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getTransformation <em>Transformation</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getKey()
 * @model
 * @generated
 */
public interface Key extends Element {
	/**
	 * Returns the value of the '<em><b>Identifies</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Identifies</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identifies</em>' reference.
	 * @see #setIdentifies(pt.uminho.haslab.emof.ast.EMOF.Class)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getKey_Identifies()
	 * @model required="true"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Class getIdentifies();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getIdentifies <em>Identifies</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Identifies</em>' reference.
	 * @see #getIdentifies()
	 * @generated
	 */
	void setIdentifies(pt.uminho.haslab.emof.ast.EMOF.Class value);

	/**
	 * Returns the value of the '<em><b>Opposite Part</b></em>' reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Property}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Opposite Part</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Opposite Part</em>' reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getKey_OppositePart()
	 * @model ordered="false"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='oppositeKey'"
	 * @generated
	 */
	EList<Property> getOppositePart();

	/**
	 * Returns the value of the '<em><b>Part</b></em>' reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Property}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Part</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Part</em>' reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getKey_Part()
	 * @model ordered="false"
	 * @generated
	 */
	EList<Property> getPart();

	/**
	 * Returns the value of the '<em><b>Transformation</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationalTransformation#getOwnedKey <em>Owned Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transformation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transformation</em>' container reference.
	 * @see #setTransformation(RelationalTransformation)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getKey_Transformation()
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.RelationalTransformation#getOwnedKey
	 * @model opposite="ownedKey" resolveProxies="false"
	 * @generated
	 */
	RelationalTransformation getTransformation();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getTransformation <em>Transformation</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transformation</em>' container reference.
	 * @see #getTransformation()
	 * @generated
	 */
	void setTransformation(RelationalTransformation value);

} // Key
