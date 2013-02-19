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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;

import pt.uminho.haslab.emof.ast.QVTBase.Pattern;

import pt.uminho.haslab.emof.ast.QVTBase.impl.RuleImpl;

import pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage;
import pt.uminho.haslab.emof.ast.QVTRelation.Relation;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationImplementation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationImpl#getIsTopLevel <em>Is Top Level</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationImpl#getOperationalImpl <em>Operational Impl</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationImpl#getVariable <em>Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationImpl#getWhen <em>When</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationImpl#getWhere <em>Where</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationImpl extends RuleImpl implements Relation {
	/**
	 * The default value of the '{@link #getIsTopLevel() <em>Is Top Level</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsTopLevel()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_TOP_LEVEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIsTopLevel() <em>Is Top Level</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsTopLevel()
	 * @generated
	 * @ordered
	 */
	protected Boolean isTopLevel = IS_TOP_LEVEL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOperationalImpl() <em>Operational Impl</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperationalImpl()
	 * @generated
	 * @ordered
	 */
	protected EList<RelationImplementation> operationalImpl;

	/**
	 * The cached value of the '{@link #getVariable() <em>Variable</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariable()
	 * @generated
	 * @ordered
	 */
	protected EList<Variable> variable;

	/**
	 * The cached value of the '{@link #getWhen() <em>When</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWhen()
	 * @generated
	 * @ordered
	 */
	protected Pattern when;

	/**
	 * The cached value of the '{@link #getWhere() <em>Where</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWhere()
	 * @generated
	 * @ordered
	 */
	protected Pattern where;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTRelationPackage.Literals.RELATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsTopLevel() {
		return isTopLevel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsTopLevel(Boolean newIsTopLevel) {
		Boolean oldIsTopLevel = isTopLevel;
		isTopLevel = newIsTopLevel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION__IS_TOP_LEVEL, oldIsTopLevel, isTopLevel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RelationImplementation> getOperationalImpl() {
		if (operationalImpl == null) {
			operationalImpl = new EObjectContainmentWithInverseEList<RelationImplementation>(RelationImplementation.class, this, QVTRelationPackage.RELATION__OPERATIONAL_IMPL, QVTRelationPackage.RELATION_IMPLEMENTATION__RELATION);
		}
		return operationalImpl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Variable> getVariable() {
		if (variable == null) {
			variable = new EObjectContainmentEList<Variable>(Variable.class, this, QVTRelationPackage.RELATION__VARIABLE);
		}
		return variable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pattern getWhen() {
		return when;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWhen(Pattern newWhen, NotificationChain msgs) {
		Pattern oldWhen = when;
		when = newWhen;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION__WHEN, oldWhen, newWhen);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWhen(Pattern newWhen) {
		if (newWhen != when) {
			NotificationChain msgs = null;
			if (when != null)
				msgs = ((InternalEObject)when).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION__WHEN, null, msgs);
			if (newWhen != null)
				msgs = ((InternalEObject)newWhen).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION__WHEN, null, msgs);
			msgs = basicSetWhen(newWhen, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION__WHEN, newWhen, newWhen));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pattern getWhere() {
		return where;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWhere(Pattern newWhere, NotificationChain msgs) {
		Pattern oldWhere = where;
		where = newWhere;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION__WHERE, oldWhere, newWhere);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWhere(Pattern newWhere) {
		if (newWhere != where) {
			NotificationChain msgs = null;
			if (where != null)
				msgs = ((InternalEObject)where).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION__WHERE, null, msgs);
			if (newWhere != null)
				msgs = ((InternalEObject)newWhere).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION__WHERE, null, msgs);
			msgs = basicSetWhere(newWhere, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION__WHERE, newWhere, newWhere));
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
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOperationalImpl()).basicAdd(otherEnd, msgs);
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
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				return ((InternalEList<?>)getOperationalImpl()).basicRemove(otherEnd, msgs);
			case QVTRelationPackage.RELATION__VARIABLE:
				return ((InternalEList<?>)getVariable()).basicRemove(otherEnd, msgs);
			case QVTRelationPackage.RELATION__WHEN:
				return basicSetWhen(null, msgs);
			case QVTRelationPackage.RELATION__WHERE:
				return basicSetWhere(null, msgs);
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
			case QVTRelationPackage.RELATION__IS_TOP_LEVEL:
				return getIsTopLevel();
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				return getOperationalImpl();
			case QVTRelationPackage.RELATION__VARIABLE:
				return getVariable();
			case QVTRelationPackage.RELATION__WHEN:
				return getWhen();
			case QVTRelationPackage.RELATION__WHERE:
				return getWhere();
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
			case QVTRelationPackage.RELATION__IS_TOP_LEVEL:
				setIsTopLevel((Boolean)newValue);
				return;
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				getOperationalImpl().clear();
				getOperationalImpl().addAll((Collection<? extends RelationImplementation>)newValue);
				return;
			case QVTRelationPackage.RELATION__VARIABLE:
				getVariable().clear();
				getVariable().addAll((Collection<? extends Variable>)newValue);
				return;
			case QVTRelationPackage.RELATION__WHEN:
				setWhen((Pattern)newValue);
				return;
			case QVTRelationPackage.RELATION__WHERE:
				setWhere((Pattern)newValue);
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
			case QVTRelationPackage.RELATION__IS_TOP_LEVEL:
				setIsTopLevel(IS_TOP_LEVEL_EDEFAULT);
				return;
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				getOperationalImpl().clear();
				return;
			case QVTRelationPackage.RELATION__VARIABLE:
				getVariable().clear();
				return;
			case QVTRelationPackage.RELATION__WHEN:
				setWhen((Pattern)null);
				return;
			case QVTRelationPackage.RELATION__WHERE:
				setWhere((Pattern)null);
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
			case QVTRelationPackage.RELATION__IS_TOP_LEVEL:
				return IS_TOP_LEVEL_EDEFAULT == null ? isTopLevel != null : !IS_TOP_LEVEL_EDEFAULT.equals(isTopLevel);
			case QVTRelationPackage.RELATION__OPERATIONAL_IMPL:
				return operationalImpl != null && !operationalImpl.isEmpty();
			case QVTRelationPackage.RELATION__VARIABLE:
				return variable != null && !variable.isEmpty();
			case QVTRelationPackage.RELATION__WHEN:
				return when != null;
			case QVTRelationPackage.RELATION__WHERE:
				return where != null;
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
		result.append(" (isTopLevel: ");
		result.append(isTopLevel);
		result.append(')');
		return result.toString();
	}

} //RelationImpl
