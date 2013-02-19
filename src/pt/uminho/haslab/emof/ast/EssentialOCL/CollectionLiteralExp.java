/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Collection Literal Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralExp#getKind <em>Kind</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralExp#getPart <em>Part</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getCollectionLiteralExp()
 * @model
 * @generated
 */
public interface CollectionLiteralExp extends LiteralExp {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionKind}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.CollectionKind
	 * @see #setKind(CollectionKind)
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getCollectionLiteralExp_Kind()
	 * @model
	 * @generated
	 */
	CollectionKind getKind();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralExp#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.CollectionKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(CollectionKind value);

	/**
	 * Returns the value of the '<em><b>Part</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralPart}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralPart#getCollectionLiteralExp <em>Collection Literal Exp</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Part</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Part</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage#getCollectionLiteralExp_Part()
	 * @see pt.uminho.haslab.emof.ast.EssentialOCL.CollectionLiteralPart#getCollectionLiteralExp
	 * @model opposite="collectionLiteralExp" containment="true" ordered="false"
	 * @generated
	 */
	EList<CollectionLiteralPart> getPart();

} // CollectionLiteralExp
