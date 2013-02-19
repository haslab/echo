/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate;

import pt.uminho.haslab.emof.ast.EMOF.Element;
import pt.uminho.haslab.emof.ast.EMOF.Property;

import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Template Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getIsOpposite <em>Is Opposite</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getObjContainer <em>Obj Container</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getReferredProperty <em>Referred Property</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getPropertyTemplateItem()
 * @model
 * @generated
 */
public interface PropertyTemplateItem extends Element {
	/**
	 * Returns the value of the '<em><b>Is Opposite</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Opposite</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Opposite</em>' attribute.
	 * @see #setIsOpposite(Boolean)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getPropertyTemplateItem_IsOpposite()
	 * @model default="false" dataType="pt.uminho.haslab.emof.ast.PrimitiveTypes.Boolean"
	 * @generated
	 */
	Boolean getIsOpposite();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getIsOpposite <em>Is Opposite</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Opposite</em>' attribute.
	 * @see #getIsOpposite()
	 * @generated
	 */
	void setIsOpposite(Boolean value);

	/**
	 * Returns the value of the '<em><b>Obj Container</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp#getPart <em>Part</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Obj Container</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Obj Container</em>' container reference.
	 * @see #setObjContainer(ObjectTemplateExp)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getPropertyTemplateItem_ObjContainer()
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp#getPart
	 * @model opposite="part" resolveProxies="false" required="true"
	 * @generated
	 */
	ObjectTemplateExp getObjContainer();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getObjContainer <em>Obj Container</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Obj Container</em>' container reference.
	 * @see #getObjContainer()
	 * @generated
	 */
	void setObjContainer(ObjectTemplateExp value);

	/**
	 * Returns the value of the '<em><b>Referred Property</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referred Property</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referred Property</em>' reference.
	 * @see #setReferredProperty(Property)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getPropertyTemplateItem_ReferredProperty()
	 * @model required="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='propertyItem'"
	 * @generated
	 */
	Property getReferredProperty();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getReferredProperty <em>Referred Property</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referred Property</em>' reference.
	 * @see #getReferredProperty()
	 * @generated
	 */
	void setReferredProperty(Property value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' containment reference.
	 * @see #setValue(OclExpression)
	 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage#getPropertyTemplateItem_Value()
	 * @model containment="true" required="true"
	 *        annotation="http://schema.omg.org/spec/MOF/2.0/emof.xml#Property.oppositeRoleName body='propertyItem'"
	 * @generated
	 */
	OclExpression getValue();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem#getValue <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' containment reference.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(OclExpression value);

} // PropertyTemplateItem
