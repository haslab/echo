/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation;

import pt.uminho.haslab.emof.ast.EMOF.Element;
import pt.uminho.haslab.emof.ast.EMOF.Operation;

import pt.uminho.haslab.emof.ast.QVTBase.TypedModel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation Implementation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getImpl <em>Impl</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getInDirectionOf <em>In Direction Of</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getRelation <em>Relation</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationImplementation()
 * @model
 * @generated
 */
public interface RelationImplementation extends Element {
	/**
	 * Returns the value of the '<em><b>Impl</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Impl</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Impl</em>' reference.
	 * @see #setImpl(Operation)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationImplementation_Impl()
	 * @model required="true"
	 * @generated
	 */
	Operation getImpl();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getImpl <em>Impl</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Impl</em>' reference.
	 * @see #getImpl()
	 * @generated
	 */
	void setImpl(Operation value);

	/**
	 * Returns the value of the '<em><b>In Direction Of</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>In Direction Of</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In Direction Of</em>' reference.
	 * @see #setInDirectionOf(TypedModel)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationImplementation_InDirectionOf()
	 * @model required="true"
	 * @generated
	 */
	TypedModel getInDirectionOf();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getInDirectionOf <em>In Direction Of</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In Direction Of</em>' reference.
	 * @see #getInDirectionOf()
	 * @generated
	 */
	void setInDirectionOf(TypedModel value);

	/**
	 * Returns the value of the '<em><b>Relation</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTRelation.Relation#getOperationalImpl <em>Operational Impl</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relation</em>' container reference.
	 * @see #setRelation(Relation)
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationImplementation_Relation()
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.Relation#getOperationalImpl
	 * @model opposite="operationalImpl" resolveProxies="false"
	 * @generated
	 */
	Relation getRelation();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation#getRelation <em>Relation</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Relation</em>' container reference.
	 * @see #getRelation()
	 * @generated
	 */
	void setRelation(Relation value);

} // RelationImplementation
