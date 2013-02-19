/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation;

import org.eclipse.emf.common.util.EList;


import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.QVTBase.Pattern;
import pt.uminho.haslab.emof.ast.QVTBase.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getIsTopLevel <em>Is Top Level</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getOperationalImpl <em>Operational Impl</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getVariable <em>Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getWhen <em>When</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getWhere <em>Where</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation()
 * @model
 * @generated
 */
public interface Relation extends Rule {
	/**
	 * Returns the value of the '<em><b>Is Top Level</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Top Level</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Top Level</em>' attribute.
	 * @see #setIsTopLevel(Boolean)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation_IsTopLevel()
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsTopLevel();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getIsTopLevel <em>Is Top Level</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Top Level</em>' attribute.
	 * @see #getIsTopLevel()
	 * @generated
	 */
	void setIsTopLevel(Boolean value);

	/**
	 * Returns the value of the '<em><b>Operational Impl</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getRelation <em>Relation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operational Impl</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operational Impl</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation_OperationalImpl()
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getRelation
	 * @model opposite="relation" containment="true" ordered="false"
	 * @generated
	 */
	EList<RelationImplementation> getOperationalImpl();

	/**
	 * Returns the value of the '<em><b>Variable</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variable</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variable</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation_Variable()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<Variable> getVariable();

	/**
	 * Returns the value of the '<em><b>When</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>When</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>When</em>' containment reference.
	 * @see #setWhen(Pattern)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation_When()
	 * @model containment="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='whenOwner'"
	 * @generated
	 */
	Pattern getWhen();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getWhen <em>When</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>When</em>' containment reference.
	 * @see #getWhen()
	 * @generated
	 */
	void setWhen(Pattern value);

	/**
	 * Returns the value of the '<em><b>Where</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Where</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Where</em>' containment reference.
	 * @see #setWhere(Pattern)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelation_Where()
	 * @model containment="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='whereOwner'"
	 * @generated
	 */
	Pattern getWhere();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getWhere <em>Where</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Where</em>' containment reference.
	 * @see #getWhere()
	 * @generated
	 */
	void setWhere(Pattern value);

} // Relation
