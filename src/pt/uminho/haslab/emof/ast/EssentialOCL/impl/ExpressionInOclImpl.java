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



import pt.uminho.haslab.emof.ast.EMOF.Type;
import pt.uminho.haslab.emof.ast.EMOF.impl.TypedElementImpl;
import pt.uminho.haslab.emof.ast.EssentialOCL.EssentialOCLPackage;
import pt.uminho.haslab.emof.ast.EssentialOCL.ExpressionInOcl;
import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression In Ocl</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.ExpressionInOclImpl#getBodyExpression <em>Body Expression</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.ExpressionInOclImpl#getContextVariable <em>Context Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.ExpressionInOclImpl#getGeneratedType <em>Generated Type</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.ExpressionInOclImpl#getParameterVariable <em>Parameter Variable</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.EssentialOCL.impl.ExpressionInOclImpl#getResultVariable <em>Result Variable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExpressionInOclImpl extends TypedElementImpl implements ExpressionInOcl {
	/**
	 * The cached value of the '{@link #getBodyExpression() <em>Body Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBodyExpression()
	 * @generated
	 * @ordered
	 */
	protected OclExpression bodyExpression;

	/**
	 * The cached value of the '{@link #getContextVariable() <em>Context Variable</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextVariable()
	 * @generated
	 * @ordered
	 */
	protected Variable contextVariable;

	/**
	 * The cached value of the '{@link #getGeneratedType() <em>Generated Type</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratedType()
	 * @generated
	 * @ordered
	 */
	protected EList<Type> generatedType;

	/**
	 * The cached value of the '{@link #getParameterVariable() <em>Parameter Variable</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterVariable()
	 * @generated
	 * @ordered
	 */
	protected EList<Variable> parameterVariable;

	/**
	 * The cached value of the '{@link #getResultVariable() <em>Result Variable</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultVariable()
	 * @generated
	 * @ordered
	 */
	protected Variable resultVariable;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExpressionInOclImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EssentialOCLPackage.Literals.EXPRESSION_IN_OCL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OclExpression getBodyExpression() {
		return bodyExpression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetBodyExpression(OclExpression newBodyExpression, NotificationChain msgs) {
		OclExpression oldBodyExpression = bodyExpression;
		bodyExpression = newBodyExpression;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION, oldBodyExpression, newBodyExpression);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBodyExpression(OclExpression newBodyExpression) {
		if (newBodyExpression != bodyExpression) {
			NotificationChain msgs = null;
			if (bodyExpression != null)
				msgs = ((InternalEObject)bodyExpression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION, null, msgs);
			if (newBodyExpression != null)
				msgs = ((InternalEObject)newBodyExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION, null, msgs);
			msgs = basicSetBodyExpression(newBodyExpression, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION, newBodyExpression, newBodyExpression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable getContextVariable() {
		return contextVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContextVariable(Variable newContextVariable, NotificationChain msgs) {
		Variable oldContextVariable = contextVariable;
		contextVariable = newContextVariable;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE, oldContextVariable, newContextVariable);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContextVariable(Variable newContextVariable) {
		if (newContextVariable != contextVariable) {
			NotificationChain msgs = null;
			if (contextVariable != null)
				msgs = ((InternalEObject)contextVariable).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE, null, msgs);
			if (newContextVariable != null)
				msgs = ((InternalEObject)newContextVariable).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE, null, msgs);
			msgs = basicSetContextVariable(newContextVariable, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE, newContextVariable, newContextVariable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Type> getGeneratedType() {
		if (generatedType == null) {
			generatedType = new EObjectContainmentEList<Type>(Type.class, this, EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE);
		}
		return generatedType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Variable> getParameterVariable() {
		if (parameterVariable == null) {
			parameterVariable = new EObjectContainmentEList<Variable>(Variable.class, this, EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE);
		}
		return parameterVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable getResultVariable() {
		return resultVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetResultVariable(Variable newResultVariable, NotificationChain msgs) {
		Variable oldResultVariable = resultVariable;
		resultVariable = newResultVariable;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE, oldResultVariable, newResultVariable);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResultVariable(Variable newResultVariable) {
		if (newResultVariable != resultVariable) {
			NotificationChain msgs = null;
			if (resultVariable != null)
				msgs = ((InternalEObject)resultVariable).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE, null, msgs);
			if (newResultVariable != null)
				msgs = ((InternalEObject)newResultVariable).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE, null, msgs);
			msgs = basicSetResultVariable(newResultVariable, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE, newResultVariable, newResultVariable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION:
				return basicSetBodyExpression(null, msgs);
			case EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE:
				return basicSetContextVariable(null, msgs);
			case EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE:
				return ((InternalEList<?>)getGeneratedType()).basicRemove(otherEnd, msgs);
			case EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE:
				return ((InternalEList<?>)getParameterVariable()).basicRemove(otherEnd, msgs);
			case EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE:
				return basicSetResultVariable(null, msgs);
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
			case EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION:
				return getBodyExpression();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE:
				return getContextVariable();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE:
				return getGeneratedType();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE:
				return getParameterVariable();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE:
				return getResultVariable();
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
			case EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION:
				setBodyExpression((OclExpression)newValue);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE:
				setContextVariable((Variable)newValue);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE:
				getGeneratedType().clear();
				getGeneratedType().addAll((Collection<? extends Type>)newValue);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE:
				getParameterVariable().clear();
				getParameterVariable().addAll((Collection<? extends Variable>)newValue);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE:
				setResultVariable((Variable)newValue);
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
			case EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION:
				setBodyExpression((OclExpression)null);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE:
				setContextVariable((Variable)null);
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE:
				getGeneratedType().clear();
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE:
				getParameterVariable().clear();
				return;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE:
				setResultVariable((Variable)null);
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
			case EssentialOCLPackage.EXPRESSION_IN_OCL__BODY_EXPRESSION:
				return bodyExpression != null;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__CONTEXT_VARIABLE:
				return contextVariable != null;
			case EssentialOCLPackage.EXPRESSION_IN_OCL__GENERATED_TYPE:
				return generatedType != null && !generatedType.isEmpty();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__PARAMETER_VARIABLE:
				return parameterVariable != null && !parameterVariable.isEmpty();
			case EssentialOCLPackage.EXPRESSION_IN_OCL__RESULT_VARIABLE:
				return resultVariable != null;
		}
		return super.eIsSet(featureID);
	}

} //ExpressionInOclImpl
