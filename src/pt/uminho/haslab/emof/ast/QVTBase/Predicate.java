/**
 */
package pt.uminho.haslab.emof.ast.QVTBase;


import pt.uminho.haslab.emof.ast.EMOF.Element;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Predicate#getConditionExpression <em>Condition Expression</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.Predicate#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPredicate()
 * @model
 * @generated
 */
public interface Predicate extends Element {
	/**
	 * Returns the value of the '<em><b>Condition Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Condition Expression</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Condition Expression</em>' containment reference.
	 * @see #setConditionExpression(OclExpression)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPredicate_ConditionExpression()
	 * @model containment="true" required="true"
	 * @generated
	 */
	OclExpression getConditionExpression();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Predicate#getConditionExpression <em>Condition Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Condition Expression</em>' containment reference.
	 * @see #getConditionExpression()
	 * @generated
	 */
	void setConditionExpression(OclExpression value);

	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTBase.Pattern#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pattern</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern</em>' container reference.
	 * @see #setPattern(Pattern)
	 * @see pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage#getPredicate_Pattern()
	 * @see pt.uminho.haslab.emof.ast.QVTBase.Pattern#getPredicate
	 * @model opposite="predicate" resolveProxies="false" required="true"
	 * @generated
	 */
	Pattern getPattern();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTBase.Predicate#getPattern <em>Pattern</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' container reference.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(Pattern value);

} // Predicate
