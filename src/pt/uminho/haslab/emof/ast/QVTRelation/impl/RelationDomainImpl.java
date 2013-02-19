/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;



import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.QVTBase.impl.DomainImpl;
import pt.uminho.haslab.emof.ast.QVTRelation.DomainPattern;
import pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationDomain;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationDomainAssignment;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation Domain</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationDomainImpl#getDefaultAssignment <em>Default Assignment</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationDomainImpl#getPattern <em>Pattern</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationDomainImpl#getRootVariable <em>Root Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationDomainImpl extends DomainImpl implements RelationDomain {
	/**
	 * The cached value of the '{@link #getDefaultAssignment() <em>Default Assignment</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultAssignment()
	 * @generated
	 * @ordered
	 */
	protected EList<RelationDomainAssignment> defaultAssignment;

	/**
	 * The cached value of the '{@link #getPattern() <em>Pattern</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPattern()
	 * @generated
	 * @ordered
	 */
	protected DomainPattern pattern;

	/**
	 * The cached value of the '{@link #getRootVariable() <em>Root Variable</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRootVariable()
	 * @generated
	 * @ordered
	 */
	protected Variable rootVariable;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationDomainImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTRelationPackage.Literals.RELATION_DOMAIN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RelationDomainAssignment> getDefaultAssignment() {
		if (defaultAssignment == null) {
			defaultAssignment = new EObjectContainmentEList<RelationDomainAssignment>(RelationDomainAssignment.class, this, QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT);
		}
		return defaultAssignment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DomainPattern getPattern() {
		return pattern;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPattern(DomainPattern newPattern, NotificationChain msgs) {
		DomainPattern oldPattern = pattern;
		pattern = newPattern;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN__PATTERN, oldPattern, newPattern);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPattern(DomainPattern newPattern) {
		if (newPattern != pattern) {
			NotificationChain msgs = null;
			if (pattern != null)
				msgs = ((InternalEObject)pattern).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION_DOMAIN__PATTERN, null, msgs);
			if (newPattern != null)
				msgs = ((InternalEObject)newPattern).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION_DOMAIN__PATTERN, null, msgs);
			msgs = basicSetPattern(newPattern, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN__PATTERN, newPattern, newPattern));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable getRootVariable() {
		if (rootVariable != null && rootVariable.eIsProxy()) {
			InternalEObject oldRootVariable = (InternalEObject)rootVariable;
			rootVariable = (Variable)eResolveProxy(oldRootVariable);
			if (rootVariable != oldRootVariable) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE, oldRootVariable, rootVariable));
			}
		}
		return rootVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable basicGetRootVariable() {
		return rootVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRootVariable(Variable newRootVariable) {
		Variable oldRootVariable = rootVariable;
		rootVariable = newRootVariable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE, oldRootVariable, rootVariable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT:
				return ((InternalEList<?>)getDefaultAssignment()).basicRemove(otherEnd, msgs);
			case QVTRelationPackage.RELATION_DOMAIN__PATTERN:
				return basicSetPattern(null, msgs);
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
			case QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT:
				return getDefaultAssignment();
			case QVTRelationPackage.RELATION_DOMAIN__PATTERN:
				return getPattern();
			case QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE:
				if (resolve) return getRootVariable();
				return basicGetRootVariable();
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
			case QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT:
				getDefaultAssignment().clear();
				getDefaultAssignment().addAll((Collection<? extends RelationDomainAssignment>)newValue);
				return;
			case QVTRelationPackage.RELATION_DOMAIN__PATTERN:
				setPattern((DomainPattern)newValue);
				return;
			case QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE:
				setRootVariable((Variable)newValue);
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
			case QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT:
				getDefaultAssignment().clear();
				return;
			case QVTRelationPackage.RELATION_DOMAIN__PATTERN:
				setPattern((DomainPattern)null);
				return;
			case QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE:
				setRootVariable((Variable)null);
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
			case QVTRelationPackage.RELATION_DOMAIN__DEFAULT_ASSIGNMENT:
				return defaultAssignment != null && !defaultAssignment.isEmpty();
			case QVTRelationPackage.RELATION_DOMAIN__PATTERN:
				return pattern != null;
			case QVTRelationPackage.RELATION_DOMAIN__ROOT_VARIABLE:
				return rootVariable != null;
		}
		return super.eIsSet(featureID);
	}

} //RelationDomainImpl
