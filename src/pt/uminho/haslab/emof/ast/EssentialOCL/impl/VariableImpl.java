/**
 */
package pt.uminho.haslab.emof.ast.EssentialOCL.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;



import pt.uminho.haslab.emof.ast.EMOF.Parameter;
import pt.uminho.haslab.emof.ast.EMOF.impl.TypedElementImpl;
import pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage;
import pt.uminho.haslab.emof.ast.EssentialOCL.LetExp;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Variable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.VariableImpl#getInitExpression <em>Init Expression</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.VariableImpl#getLetExp <em>Let Exp</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.VariableImpl#getRepresentedParameter <em>Represented Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VariableImpl extends TypedElementImpl implements Variable {
	/**
	 * The cached value of the '{@link #getInitExpression() <em>Init Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitExpression()
	 * @generated
	 * @ordered
	 */
	protected OclExpression initExpression;

	/**
	 * The cached value of the '{@link #getRepresentedParameter() <em>Represented Parameter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRepresentedParameter()
	 * @generated
	 * @ordered
	 */
	protected Parameter representedParameter;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VariableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EssentialOCLPackage.Literals.VARIABLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OclExpression getInitExpression() {
		return initExpression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInitExpression(OclExpression newInitExpression, NotificationChain msgs) {
		OclExpression oldInitExpression = initExpression;
		initExpression = newInitExpression;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.VARIABLE__INIT_EXPRESSION, oldInitExpression, newInitExpression);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitExpression(OclExpression newInitExpression) {
		if (newInitExpression != initExpression) {
			NotificationChain msgs = null;
			if (initExpression != null)
				msgs = ((InternalEObject)initExpression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.VARIABLE__INIT_EXPRESSION, null, msgs);
			if (newInitExpression != null)
				msgs = ((InternalEObject)newInitExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.VARIABLE__INIT_EXPRESSION, null, msgs);
			msgs = basicSetInitExpression(newInitExpression, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.VARIABLE__INIT_EXPRESSION, newInitExpression, newInitExpression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LetExp getLetExp() {
		if (eContainerFeatureID() != EssentialOCLPackage.VARIABLE__LET_EXP) return null;
		return (LetExp)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLetExp(LetExp newLetExp, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newLetExp, EssentialOCLPackage.VARIABLE__LET_EXP, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLetExp(LetExp newLetExp) {
		if (newLetExp != eInternalContainer() || (eContainerFeatureID() != EssentialOCLPackage.VARIABLE__LET_EXP && newLetExp != null)) {
			if (EcoreUtil.isAncestor(this, newLetExp))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newLetExp != null)
				msgs = ((InternalEObject)newLetExp).eInverseAdd(this, EssentialOCLPackage.LET_EXP__VARIABLE, LetExp.class, msgs);
			msgs = basicSetLetExp(newLetExp, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.VARIABLE__LET_EXP, newLetExp, newLetExp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getRepresentedParameter() {
		if (representedParameter != null && representedParameter.eIsProxy()) {
			InternalEObject oldRepresentedParameter = (InternalEObject)representedParameter;
			representedParameter = (Parameter)eResolveProxy(oldRepresentedParameter);
			if (representedParameter != oldRepresentedParameter) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER, oldRepresentedParameter, representedParameter));
			}
		}
		return representedParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter basicGetRepresentedParameter() {
		return representedParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRepresentedParameter(Parameter newRepresentedParameter) {
		Parameter oldRepresentedParameter = representedParameter;
		representedParameter = newRepresentedParameter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER, oldRepresentedParameter, representedParameter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetLetExp((LetExp)otherEnd, msgs);
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
			case EssentialOCLPackage.VARIABLE__INIT_EXPRESSION:
				return basicSetInitExpression(null, msgs);
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				return basicSetLetExp(null, msgs);
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
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				return eInternalContainer().eInverseRemove(this, EssentialOCLPackage.LET_EXP__VARIABLE, LetExp.class, msgs);
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
			case EssentialOCLPackage.VARIABLE__INIT_EXPRESSION:
				return getInitExpression();
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				return getLetExp();
			case EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER:
				if (resolve) return getRepresentedParameter();
				return basicGetRepresentedParameter();
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
			case EssentialOCLPackage.VARIABLE__INIT_EXPRESSION:
				setInitExpression((OclExpression)newValue);
				return;
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				setLetExp((LetExp)newValue);
				return;
			case EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER:
				setRepresentedParameter((Parameter)newValue);
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
			case EssentialOCLPackage.VARIABLE__INIT_EXPRESSION:
				setInitExpression((OclExpression)null);
				return;
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				setLetExp((LetExp)null);
				return;
			case EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER:
				setRepresentedParameter((Parameter)null);
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
			case EssentialOCLPackage.VARIABLE__INIT_EXPRESSION:
				return initExpression != null;
			case EssentialOCLPackage.VARIABLE__LET_EXP:
				return getLetExp() != null;
			case EssentialOCLPackage.VARIABLE__REPRESENTED_PARAMETER:
				return representedParameter != null;
		}
		return super.eIsSet(featureID);
	}

} //VariableImpl
