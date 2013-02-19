/**
 */
package pt.uminho.haslab.emof.ast.QVTBase.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;



import pt.uminho.haslab.emof.ast.EMOF.EMOFPackage;
import pt.uminho.haslab.emof.ast.EMOF.MultiplicityElement;
import pt.uminho.haslab.emof.ast.EMOF.Operation;
import pt.uminho.haslab.emof.ast.EMOF.Parameter;
import pt.uminho.haslab.emof.ast.EssentialOCL.impl.VariableImpl;
import pt.uminho.haslab.emof.ast.QVTBase.FunctionParameter;
import pt.uminho.haslab.emof.ast.QVTBase.QVTBasePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Function Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.FunctionParameterImpl#getIsOrdered <em>Is Ordered</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.FunctionParameterImpl#getIsUnique <em>Is Unique</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.FunctionParameterImpl#getLower <em>Lower</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.FunctionParameterImpl#getUpper <em>Upper</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTBase.impl.FunctionParameterImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FunctionParameterImpl extends VariableImpl implements FunctionParameter {
	/**
	 * The default value of the '{@link #getIsOrdered() <em>Is Ordered</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsOrdered()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_ORDERED_EDEFAULT = Boolean.FALSE;

	/**
	 * The cached value of the '{@link #getIsOrdered() <em>Is Ordered</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsOrdered()
	 * @generated
	 * @ordered
	 */
	protected Boolean isOrdered = IS_ORDERED_EDEFAULT;

	/**
	 * The default value of the '{@link #getIsUnique() <em>Is Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsUnique()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_UNIQUE_EDEFAULT = Boolean.TRUE;

	/**
	 * The cached value of the '{@link #getIsUnique() <em>Is Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsUnique()
	 * @generated
	 * @ordered
	 */
	protected Boolean isUnique = IS_UNIQUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getLower() <em>Lower</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLower()
	 * @generated
	 * @ordered
	 */
	protected static final Integer LOWER_EDEFAULT = new Integer(1);

	/**
	 * The cached value of the '{@link #getLower() <em>Lower</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLower()
	 * @generated
	 * @ordered
	 */
	protected Integer lower = LOWER_EDEFAULT;

	/**
	 * The default value of the '{@link #getUpper() <em>Upper</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUpper()
	 * @generated
	 * @ordered
	 */
	protected static final Integer UPPER_EDEFAULT = new Integer(1);

	/**
	 * The cached value of the '{@link #getUpper() <em>Upper</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUpper()
	 * @generated
	 * @ordered
	 */
	protected Integer upper = UPPER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FunctionParameterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTBasePackage.Literals.FUNCTION_PARAMETER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsOrdered() {
		return isOrdered;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsOrdered(Boolean newIsOrdered) {
		Boolean oldIsOrdered = isOrdered;
		isOrdered = newIsOrdered;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED, oldIsOrdered, isOrdered));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsUnique() {
		return isUnique;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsUnique(Boolean newIsUnique) {
		Boolean oldIsUnique = isUnique;
		isUnique = newIsUnique;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE, oldIsUnique, isUnique));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Integer getLower() {
		return lower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLower(Integer newLower) {
		Integer oldLower = lower;
		lower = newLower;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.FUNCTION_PARAMETER__LOWER, oldLower, lower));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Integer getUpper() {
		return upper;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUpper(Integer newUpper) {
		Integer oldUpper = upper;
		upper = newUpper;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.FUNCTION_PARAMETER__UPPER, oldUpper, upper));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Operation getOperation() {
		if (eContainerFeatureID() != QVTBasePackage.FUNCTION_PARAMETER__OPERATION) return null;
		return (Operation)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOperation(Operation newOperation, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newOperation, QVTBasePackage.FUNCTION_PARAMETER__OPERATION, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOperation(Operation newOperation) {
		if (newOperation != eInternalContainer() || (eContainerFeatureID() != QVTBasePackage.FUNCTION_PARAMETER__OPERATION && newOperation != null)) {
			if (EcoreUtil.isAncestor(this, newOperation))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newOperation != null)
				msgs = ((InternalEObject)newOperation).eInverseAdd(this, EMOFPackage.OPERATION__OWNED_PARAMETER, Operation.class, msgs);
			msgs = basicSetOperation(newOperation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTBasePackage.FUNCTION_PARAMETER__OPERATION, newOperation, newOperation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetOperation((Operation)otherEnd, msgs);
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
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				return basicSetOperation(null, msgs);
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
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				return eInternalContainer().eInverseRemove(this, EMOFPackage.OPERATION__OWNED_PARAMETER, Operation.class, msgs);
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
			case QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED:
				return getIsOrdered();
			case QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE:
				return getIsUnique();
			case QVTBasePackage.FUNCTION_PARAMETER__LOWER:
				return getLower();
			case QVTBasePackage.FUNCTION_PARAMETER__UPPER:
				return getUpper();
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				return getOperation();
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
			case QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED:
				setIsOrdered((Boolean)newValue);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE:
				setIsUnique((Boolean)newValue);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__LOWER:
				setLower((Integer)newValue);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__UPPER:
				setUpper((Integer)newValue);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				setOperation((Operation)newValue);
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
			case QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED:
				setIsOrdered(IS_ORDERED_EDEFAULT);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE:
				setIsUnique(IS_UNIQUE_EDEFAULT);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__LOWER:
				setLower(LOWER_EDEFAULT);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__UPPER:
				setUpper(UPPER_EDEFAULT);
				return;
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				setOperation((Operation)null);
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
			case QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED:
				return IS_ORDERED_EDEFAULT == null ? isOrdered != null : !IS_ORDERED_EDEFAULT.equals(isOrdered);
			case QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE:
				return IS_UNIQUE_EDEFAULT == null ? isUnique != null : !IS_UNIQUE_EDEFAULT.equals(isUnique);
			case QVTBasePackage.FUNCTION_PARAMETER__LOWER:
				return LOWER_EDEFAULT == null ? lower != null : !LOWER_EDEFAULT.equals(lower);
			case QVTBasePackage.FUNCTION_PARAMETER__UPPER:
				return UPPER_EDEFAULT == null ? upper != null : !UPPER_EDEFAULT.equals(upper);
			case QVTBasePackage.FUNCTION_PARAMETER__OPERATION:
				return getOperation() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == MultiplicityElement.class) {
			switch (derivedFeatureID) {
				case QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED: return EMOFPackage.MULTIPLICITY_ELEMENT__IS_ORDERED;
				case QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE: return EMOFPackage.MULTIPLICITY_ELEMENT__IS_UNIQUE;
				case QVTBasePackage.FUNCTION_PARAMETER__LOWER: return EMOFPackage.MULTIPLICITY_ELEMENT__LOWER;
				case QVTBasePackage.FUNCTION_PARAMETER__UPPER: return EMOFPackage.MULTIPLICITY_ELEMENT__UPPER;
				default: return -1;
			}
		}
		if (baseClass == Parameter.class) {
			switch (derivedFeatureID) {
				case QVTBasePackage.FUNCTION_PARAMETER__OPERATION: return EMOFPackage.PARAMETER__OPERATION;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == MultiplicityElement.class) {
			switch (baseFeatureID) {
				case EMOFPackage.MULTIPLICITY_ELEMENT__IS_ORDERED: return QVTBasePackage.FUNCTION_PARAMETER__IS_ORDERED;
				case EMOFPackage.MULTIPLICITY_ELEMENT__IS_UNIQUE: return QVTBasePackage.FUNCTION_PARAMETER__IS_UNIQUE;
				case EMOFPackage.MULTIPLICITY_ELEMENT__LOWER: return QVTBasePackage.FUNCTION_PARAMETER__LOWER;
				case EMOFPackage.MULTIPLICITY_ELEMENT__UPPER: return QVTBasePackage.FUNCTION_PARAMETER__UPPER;
				default: return -1;
			}
		}
		if (baseClass == Parameter.class) {
			switch (baseFeatureID) {
				case EMOFPackage.PARAMETER__OPERATION: return QVTBasePackage.FUNCTION_PARAMETER__OPERATION;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(" (isOrdered: ");
		result.append(isOrdered);
		result.append(", isUnique: ");
		result.append(isUnique);
		result.append(", lower: ");
		result.append(lower);
		result.append(", upper: ");
		result.append(upper);
		result.append(')');
		return result.toString();
	}

} //FunctionParameterImpl
