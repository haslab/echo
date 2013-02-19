/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;


import pt.uminho.haslab.emof.ast.EMOF.Operation;
import pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.OperationCallExp;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation Call Exp</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.OperationCallExpImpl#getArgument <em>Argument</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.OperationCallExpImpl#getReferredOperation <em>Referred Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OperationCallExpImpl extends FeatureCallExpImpl implements OperationCallExp {
	/**
	 * The cached value of the '{@link #getArgument() <em>Argument</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArgument()
	 * @generated
	 * @ordered
	 */
	protected EList<OclExpression> argument;

	/**
	 * The cached value of the '{@link #getReferredOperation() <em>Referred Operation</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferredOperation()
	 * @generated
	 * @ordered
	 */
	protected Operation referredOperation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OperationCallExpImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EssentialOCLPackage.Literals.OPERATION_CALL_EXP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OclExpression> getArgument() {
		if (argument == null) {
			argument = new EObjectContainmentEList<OclExpression>(OclExpression.class, this, EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT);
		}
		return argument;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operation getReferredOperation() {
		if (referredOperation != null && referredOperation.eIsProxy()) {
			InternalEObject oldReferredOperation = (InternalEObject)referredOperation;
			referredOperation = (Operation)eResolveProxy(oldReferredOperation);
			if (referredOperation != oldReferredOperation) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION, oldReferredOperation, referredOperation));
			}
		}
		return referredOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operation basicGetReferredOperation() {
		return referredOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReferredOperation(Operation newReferredOperation) {
		Operation oldReferredOperation = referredOperation;
		referredOperation = newReferredOperation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION, oldReferredOperation, referredOperation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT:
				return ((InternalEList<?>)getArgument()).basicRemove(otherEnd, msgs);
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
			case EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT:
				return getArgument();
			case EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION:
				if (resolve) return getReferredOperation();
				return basicGetReferredOperation();
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
			case EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT:
				getArgument().clear();
				getArgument().addAll((Collection<? extends OclExpression>)newValue);
				return;
			case EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION:
				setReferredOperation((Operation)newValue);
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
			case EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT:
				getArgument().clear();
				return;
			case EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION:
				setReferredOperation((Operation)null);
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
			case EssentialOCLPackage.OPERATION_CALL_EXP__ARGUMENT:
				return argument != null && !argument.isEmpty();
			case EssentialOCLPackage.OPERATION_CALL_EXP__REFERRED_OPERATION:
				return referredOperation != null;
		}
		return super.eIsSet(featureID);
	}

} //OperationCallExpImpl
