/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tuple Literal Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.TupleLiteralExp#getPart <em>Part</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getTupleLiteralExp()
 * @model
 * @generated
 */
public interface TupleLiteralExp extends LiteralExp {
	/**
	 * Returns the value of the '<em><b>Part</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.TupleLiteralPart}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EssentialOCL.TupleLiteralPart#getTupleLiteralExp <em>Tuple Literal Exp</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Part</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Part</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getTupleLiteralExp_Part()
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.TupleLiteralPart#getTupleLiteralExp
	 * @model opposite="tupleLiteralExp" containment="true" ordered="false"
	 * @generated
	 */
	EList<TupleLiteralPart> getPart();

} // TupleLiteralExp
