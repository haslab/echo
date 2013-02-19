/**
 */
package pt.uminho.haslab.emof.ast.QVTBase;

import org.eclipse.emf.common.util.EList;


import pt.uminho.haslab.emof.ast.EMOF.Element;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Pattern#getBindsTo <em>Binds To</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Pattern#getPredicate <em>Predicate</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPattern()
 * @model
 * @generated
 */
public interface Pattern extends Element {
	/**
	 * Returns the value of the '<em><b>Binds To</b></em>' reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.Variable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binds To</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Binds To</em>' reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPattern_BindsTo()
	 * @model ordered="false"
	 * @generated
	 */
	EList<Variable> getBindsTo();

	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTBase.Predicate}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Predicate#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Predicate</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predicate</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPattern_Predicate()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Predicate#getPattern
	 * @model opposite="pattern" containment="true" ordered="false"
	 * @generated
	 */
	EList<Predicate> getPredicate();

} // Pattern
