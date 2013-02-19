/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Unlimited Natural Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.UnlimitedNaturalExp#getSymbol <em>Symbol</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getUnlimitedNaturalExp()
 * @model
 * @generated
 */
public interface UnlimitedNaturalExp extends NumericLiteralExp {
	/**
	 * Returns the value of the '<em><b>Symbol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Symbol</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Symbol</em>' attribute.
	 * @see #setSymbol(Integer)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getUnlimitedNaturalExp_Symbol()
	 * @model dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.UnlimitedNatural"
	 * @generated
	 */
	Integer getSymbol();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.UnlimitedNaturalExp#getSymbol <em>Symbol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Symbol</em>' attribute.
	 * @see #getSymbol()
	 * @generated
	 */
	void setSymbol(Integer value);

} // UnlimitedNaturalExp
