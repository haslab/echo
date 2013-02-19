/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;




import pt.uminho.haslab.emof.ast.EMOF.Property;
import pt.uminho.haslab.emof.ast.EMOF.impl.ElementImpl;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.QVTTemplate.ObjectTemplateExp;
import pt.uminho.haslab.emof.ast.QVTTemplate.PropertyTemplateItem;
import pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Template Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.PropertyTemplateItemImpl#getIsOpposite <em>Is Opposite</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.PropertyTemplateItemImpl#getObjContainer <em>Obj Container</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.PropertyTemplateItemImpl#getReferredProperty <em>Referred Property</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.PropertyTemplateItemImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropertyTemplateItemImpl extends ElementImpl implements PropertyTemplateItem {
	/**
	 * The default value of the '{@link #getIsOpposite() <em>Is Opposite</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsOpposite()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_OPPOSITE_EDEFAULT = Boolean.FALSE;

	/**
	 * The cached value of the '{@link #getIsOpposite() <em>Is Opposite</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsOpposite()
	 * @generated
	 * @ordered
	 */
	protected Boolean isOpposite = IS_OPPOSITE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getReferredProperty() <em>Referred Property</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferredProperty()
	 * @generated
	 * @ordered
	 */
	protected Property referredProperty;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected OclExpression value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropertyTemplateItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTTemplatePackage.Literals.PROPERTY_TEMPLATE_ITEM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsOpposite() {
		return isOpposite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsOpposite(Boolean newIsOpposite) {
		Boolean oldIsOpposite = isOpposite;
		isOpposite = newIsOpposite;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__IS_OPPOSITE, oldIsOpposite, isOpposite));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ObjectTemplateExp getObjContainer() {
		if (eContainerFeatureID() != QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER) return null;
		return (ObjectTemplateExp)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetObjContainer(ObjectTemplateExp newObjContainer, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newObjContainer, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setObjContainer(ObjectTemplateExp newObjContainer) {
		if (newObjContainer != eInternalContainer() || (eContainerFeatureID() != QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER && newObjContainer != null)) {
			if (EcoreUtil.isAncestor(this, newObjContainer))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newObjContainer != null)
				msgs = ((InternalEObject)newObjContainer).eInverseAdd(this, QVTTemplatePackage.OBJECT_TEMPLATE_EXP__PART, ObjectTemplateExp.class, msgs);
			msgs = basicSetObjContainer(newObjContainer, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER, newObjContainer, newObjContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Property getReferredProperty() {
		if (referredProperty != null && referredProperty.eIsProxy()) {
			InternalEObject oldReferredProperty = (InternalEObject)referredProperty;
			referredProperty = (Property)eResolveProxy(oldReferredProperty);
			if (referredProperty != oldReferredProperty) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY, oldReferredProperty, referredProperty));
			}
		}
		return referredProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Property basicGetReferredProperty() {
		return referredProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReferredProperty(Property newReferredProperty) {
		Property oldReferredProperty = referredProperty;
		referredProperty = newReferredProperty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY, oldReferredProperty, referredProperty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OclExpression getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValue(OclExpression newValue, NotificationChain msgs) {
		OclExpression oldValue = value;
		value = newValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE, oldValue, newValue);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(OclExpression newValue) {
		if (newValue != value) {
			NotificationChain msgs = null;
			if (value != null)
				msgs = ((InternalEObject)value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE, null, msgs);
			if (newValue != null)
				msgs = ((InternalEObject)newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE, null, msgs);
			msgs = basicSetValue(newValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE, newValue, newValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetObjContainer((ObjectTemplateExp)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				return basicSetObjContainer(null, msgs);
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE:
				return basicSetValue(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				return eInternalContainer().eInverseRemove(this, QVTTemplatePackage.OBJECT_TEMPLATE_EXP__PART, ObjectTemplateExp.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__IS_OPPOSITE:
				return getIsOpposite();
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				return getObjContainer();
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY:
				if (resolve) return getReferredProperty();
				return basicGetReferredProperty();
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE:
				return getValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__IS_OPPOSITE:
				setIsOpposite((Boolean)newValue);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				setObjContainer((ObjectTemplateExp)newValue);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY:
				setReferredProperty((Property)newValue);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE:
				setValue((OclExpression)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__IS_OPPOSITE:
				setIsOpposite(IS_OPPOSITE_EDEFAULT);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				setObjContainer((ObjectTemplateExp)null);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY:
				setReferredProperty((Property)null);
				return;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE:
				setValue((OclExpression)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__IS_OPPOSITE:
				return IS_OPPOSITE_EDEFAULT == null ? isOpposite != null : !IS_OPPOSITE_EDEFAULT.equals(isOpposite);
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__OBJ_CONTAINER:
				return getObjContainer() != null;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__REFERRED_PROPERTY:
				return referredProperty != null;
			case QVTTemplatePackage.PROPERTY_TEMPLATE_ITEM__VALUE:
				return value != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (isOpposite: ");
		result.append(isOpposite);
		result.append(')');
		return result.toString();
	}

} //PropertyTemplateItemImpl
