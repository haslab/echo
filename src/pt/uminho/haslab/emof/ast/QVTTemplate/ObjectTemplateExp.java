/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Object Template Exp</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp#getPart <em>Part</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp#getReferredClass <em>Referred Class</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getObjectTemplateExp()
 * @model
 * @generated
 */
public interface ObjectTemplateExp extends TemplateExp {
	/**
	 * Returns the value of the '<em><b>Part</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem}.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getObjContainer <em>Obj Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Part</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Part</em>' containment reference list.
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getObjectTemplateExp_Part()
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getObjContainer
	 * @model opposite="objContainer" containment="true" ordered="false"
	 * @generated
	 */
	EList<PropertyTemplateItem> getPart();

	/**
	 * Returns the value of the '<em><b>Referred Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referred Class</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referred Class</em>' reference.
	 * @see #setReferredClass(pt.uminho.haslab.emof.ast.EMOF.Class)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getObjectTemplateExp_ReferredClass()
	 * @model required="true"
	 * @generated
	 */
	pt.uminho.haslab.emof.ast.EMOF.Class getReferredClass();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp#getReferredClass <em>Referred Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referred Class</em>' reference.
	 * @see #getReferredClass()
	 * @generated
	 */
	void setReferredClass(pt.uminho.haslab.emof.ast.EMOF.Class value);

} // ObjectTemplateExp
