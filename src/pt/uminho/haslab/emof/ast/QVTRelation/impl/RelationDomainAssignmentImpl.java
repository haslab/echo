/**
 */
package pt.uminho.haslab.emof.ast.QVTRelation.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;



import pt.uminho.haslab.emof.ast.EMOF.impl.ElementImpl;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.QVTRelation.QVTRelationPackage;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationDomainAssignment;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation Domain Assignment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationDomainAssignmentImpl#getValueExp <em>Value Exp</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTRelation.impl.RelationDomainAssignmentImpl#getVariable <em>Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationDomainAssignmentImpl extends ElementImpl implements RelationDomainAssignment {
	/**
	 * The cached value of the '{@link #getValueExp() <em>Value Exp</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueExp()
	 * @generated
	 * @ordered
	 */
	protected OclExpression valueExp;

	/**
	 * The cached value of the '{@link #getVariable() <em>Variable</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariable()
	 * @generated
	 * @ordered
	 */
	protected Variable variable;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationDomainAssignmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTRelationPackage.Literals.RELATION_DOMAIN_ASSIGNMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OclExpression getValueExp() {
		return valueExp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValueExp(OclExpression newValueExp, NotificationChain msgs) {
		OclExpression oldValueExp = valueExp;
		valueExp = newValueExp;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP, oldValueExp, newValueExp);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValueExp(OclExpression newValueExp) {
		if (newValueExp != valueExp) {
			NotificationChain msgs = null;
			if (valueExp != null)
				msgs = ((InternalEObject)valueExp).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP, null, msgs);
			if (newValueExp != null)
				msgs = ((InternalEObject)newValueExp).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP, null, msgs);
			msgs = basicSetValueExp(newValueExp, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP, newValueExp, newValueExp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable getVariable() {
		if (variable != null && variable.eIsProxy()) {
			InternalEObject oldVariable = (InternalEObject)variable;
			variable = (Variable)eResolveProxy(oldVariable);
			if (variable != oldVariable) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE, oldVariable, variable));
			}
		}
		return variable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable basicGetVariable() {
		return variable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVariable(Variable newVariable) {
		Variable oldVariable = variable;
		variable = newVariable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE, oldVariable, variable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP:
				return basicSetValueExp(null, msgs);
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
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP:
				return getValueExp();
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE:
				if (resolve) return getVariable();
				return basicGetVariable();
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
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP:
				setValueExp((OclExpression)newValue);
				return;
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE:
				setVariable((Variable)newValue);
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
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP:
				setValueExp((OclExpression)null);
				return;
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE:
				setVariable((Variable)null);
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
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VALUE_EXP:
				return valueExp != null;
			case QVTRelationPackage.RELATION_DOMAIN_ASSIGNMENT__VARIABLE:
				return variable != null;
		}
		return super.eIsSet(featureID);
	}

} //RelationDomainAssignmentImpl
