/**
 */
package pt.uminho.haslab.emof.ast.QVTBase.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;


import pt.uminho.haslab.emof.ast.EMOF.impl.NamedElementImpl;
import pt.uminho.haslab.emof.ast.QVTBase.Domain;
import pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage;
import pt.uminho.haslab.emof.ast.QVTBase.Rule;
import pt.uminho.haslab.emof.ast.QVTBase.Transformation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.RuleImpl#getDomain <em>Domain</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.RuleImpl#getOverrides <em>Overrides</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.RuleImpl#getTransformation <em>Transformation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class RuleImpl extends NamedElementImpl implements Rule {
	/**
	 * The cached value of the '{@link #getDomain() <em>Domain</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDomain()
	 * @generated
	 * @ordered
	 */
	protected EList<Domain> domain;

	/**
	 * The cached value of the '{@link #getOverrides() <em>Overrides</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOverrides()
	 * @generated
	 * @ordered
	 */
	protected Rule overrides;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTBasePackage.Literals.RULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Domain> getDomain() {
		if (domain == null) {
			domain = new EObjectContainmentWithInverseEList<Domain>(Domain.class, this, QVTBasePackage.RULE__DOMAIN, QVTBasePackage.DOMAIN__RULE);
		}
		return domain;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rule getOverrides() {
		if (overrides != null && overrides.eIsProxy()) {
			InternalEObject oldOverrides = (InternalEObject)overrides;
			overrides = (Rule)eResolveProxy(oldOverrides);
			if (overrides != oldOverrides) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QVTBasePackage.RULE__OVERRIDES, oldOverrides, overrides));
			}
		}
		return overrides;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rule basicGetOverrides() {
		return overrides;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOverrides(Rule newOverrides) {
		Rule oldOverrides = overrides;
		overrides = newOverrides;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.RULE__OVERRIDES, oldOverrides, overrides));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Transformation getTransformation() {
		if (eContainerFeatureID() != QVTBasePackage.RULE__TRANSFORMATION) return null;
		return (Transformation)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTransformation(Transformation newTransformation, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newTransformation, QVTBasePackage.RULE__TRANSFORMATION, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransformation(Transformation newTransformation) {
		if (newTransformation != eInternalContainer() || (eContainerFeatureID() != QVTBasePackage.RULE__TRANSFORMATION && newTransformation != null)) {
			if (EcoreUtil.isAncestor(this, newTransformation))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newTransformation != null)
				msgs = ((InternalEObject)newTransformation).eInverseAdd(this, QVTBasePackage.TRANSFORMATION__RULE, Transformation.class, msgs);
			msgs = basicSetTransformation(newTransformation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.RULE__TRANSFORMATION, newTransformation, newTransformation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTBasePackage.RULE__DOMAIN:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getDomain()).basicAdd(otherEnd, msgs);
			case QVTBasePackage.RULE__TRANSFORMATION:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetTransformation((Transformation)otherEnd, msgs);
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
			case QVTBasePackage.RULE__DOMAIN:
				return ((InternalEList<?>)getDomain()).basicRemove(otherEnd, msgs);
			case QVTBasePackage.RULE__TRANSFORMATION:
				return basicSetTransformation(null, msgs);
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
			case QVTBasePackage.RULE__TRANSFORMATION:
				return eInternalContainer().eInverseRemove(this, QVTBasePackage.TRANSFORMATION__RULE, Transformation.class, msgs);
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
			case QVTBasePackage.RULE__DOMAIN:
				return getDomain();
			case QVTBasePackage.RULE__OVERRIDES:
				if (resolve) return getOverrides();
				return basicGetOverrides();
			case QVTBasePackage.RULE__TRANSFORMATION:
				return getTransformation();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case QVTBasePackage.RULE__DOMAIN:
				getDomain().clear();
				getDomain().addAll((Collection<? extends Domain>)newValue);
				return;
			case QVTBasePackage.RULE__OVERRIDES:
				setOverrides((Rule)newValue);
				return;
			case QVTBasePackage.RULE__TRANSFORMATION:
				setTransformation((Transformation)newValue);
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
			case QVTBasePackage.RULE__DOMAIN:
				getDomain().clear();
				return;
			case QVTBasePackage.RULE__OVERRIDES:
				setOverrides((Rule)null);
				return;
			case QVTBasePackage.RULE__TRANSFORMATION:
				setTransformation((Transformation)null);
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
			case QVTBasePackage.RULE__DOMAIN:
				return domain != null && !domain.isEmpty();
			case QVTBasePackage.RULE__OVERRIDES:
				return overrides != null;
			case QVTBasePackage.RULE__TRANSFORMATION:
				return getTransformation() != null;
		}
		return super.eIsSet(featureID);
	}

} //RuleImpl
