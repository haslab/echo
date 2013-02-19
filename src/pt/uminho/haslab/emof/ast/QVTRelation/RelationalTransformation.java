/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.QVTBase.Transformation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relational Transformation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.RelationalTransformation#getOwnedKey <em>Owned Key</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationalTransformation()
 * @model
 * @generated
 */
public interface RelationalTransformation extends Transformation {
	/**
	 * Returns the value of the '<em><b>Owned Key</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTRelation.Key}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTRelation.Key#getTransformation <em>Transformation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owned Key</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owned Key</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage#getRelationalTransformation_OwnedKey()
	 * @see pt.uminho.haslab.emof.ast.QVTRelation.Key#getTransformation
	 * @model opposite="transformation" containment="true" ordered="false"
	 * @generated
	 */
	EList<Key> getOwnedKey();

} // RelationalTransformation
