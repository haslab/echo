/**
 */
package pt.uminho.haslab.emof.ast.QVTBase;

import org.eclipse.emf.common.util.EList;

import pt.uminho.haslab.emof.ast.EMOF.Tag;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transformation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getExtends <em>Extends</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getModelParameter <em>Model Parameter</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getOwnedTag <em>Owned Tag</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getRule <em>Rule</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getTransformation()
 * @model
 * @generated
 */
public interface Transformation extends pt.uminho.haslab.emof.ast.EMOF.Class, pt.uminho.haslab.emof.ast.EMOF.Package {
	/**
	 * Returns the value of the '<em><b>Extends</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extends</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extends</em>' reference.
	 * @see #setExtends(Transformation)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getTransformation_Extends()
	 * @model annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='extendedBy'"
	 * @generated
	 */
	Transformation getExtends();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Transformation#getExtends <em>Extends</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Extends</em>' reference.
	 * @see #getExtends()
	 * @generated
	 */
	void setExtends(Transformation value);

	/**
	 * Returns the value of the '<em><b>Model Parameter</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTBase.TypedModel}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.TypedModel#getTransformation <em>Transformation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model Parameter</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Parameter</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getTransformation_ModelParameter()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.TypedModel#getTransformation
	 * @model opposite="transformation" containment="true"
	 * @generated
	 */
	EList<TypedModel> getModelParameter();

	/**
	 * Returns the value of the '<em><b>Owned Tag</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EMOF.Tag}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owned Tag</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owned Tag</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getTransformation_OwnedTag()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<Tag> getOwnedTag();

	/**
	 * Returns the value of the '<em><b>Rule</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTBase.Rule}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Rule#getTransformation <em>Transformation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getTransformation_Rule()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Rule#getTransformation
	 * @model opposite="transformation" containment="true" ordered="false"
	 * @generated
	 */
	EList<Rule> getRule();

} // Transformation
