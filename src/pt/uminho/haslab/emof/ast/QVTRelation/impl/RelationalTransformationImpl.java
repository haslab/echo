/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import pt.uminho.haslab.emof.ast.QVTBase.impl.TransformationImpl;

import pt.uminho.haslab.emof.ast.QVTRelation.Key;
import pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationalTransformation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relational Transformation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationalTransformationImpl#getOwnedKey <em>Owned Key</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationalTransformationImpl extends TransformationImpl implements RelationalTransformation {
	/**
	 * The cached value of the '{@link #getOwnedKey() <em>Owned Key</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnedKey()
	 * @generated
	 * @ordered
	 */
	protected EList<Key> ownedKey;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationalTransformationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTRelationPackage.Literals.RELATIONAL_TRANSFORMATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Key> getOwnedKey() {
		if (ownedKey == null) {
			ownedKey = new EObjectContainmentWithInverseEList<Key>(Key.class, this, QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY, QVTRelationPackage.KEY__TRANSFORMATION);
		}
		return ownedKey;
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
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOwnedKey()).basicAdd(otherEnd, msgs);
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
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				return ((InternalEList<?>)getOwnedKey()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				return getOwnedKey();
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
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				getOwnedKey().clear();
				getOwnedKey().addAll((Collection<? extends Key>)newValue);
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
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				getOwnedKey().clear();
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
			case QVTRelationPackage.RELATIONAL_TRANSFORMATION__OWNED_KEY:
				return ownedKey != null && !ownedKey.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //RelationalTransformationImpl
