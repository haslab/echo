/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;



import pt.uminho.haslab.emof.ast.EssentialOCL.OclExpression;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.EssentialOCL.impl.LiteralExpImpl;
import pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage;
import pt.uminho.haslab.emof.ast.QVTTemplate.TemplateExp;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Template Exp</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.TemplateExpImpl#getBindsTo <em>Binds To</em>}</li>
 *   <li>{@link pt.uminho.haslab.emof.ast.QVTTemplate.impl.TemplateExpImpl#getWhere <em>Where</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TemplateExpImpl extends LiteralExpImpl implements TemplateExp {
	/**
	 * The cached value of the '{@link #getBindsTo() <em>Binds To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBindsTo()
	 * @generated
	 * @ordered
	 */
	protected Variable bindsTo;

	/**
	 * The cached value of the '{@link #getWhere() <em>Where</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWhere()
	 * @generated
	 * @ordered
	 */
	protected OclExpression where;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TemplateExpImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return QVTTemplatePackage.Literals.TEMPLATE_EXP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable getBindsTo() {
		if (bindsTo != null && bindsTo.eIsProxy()) {
			InternalEObject oldBindsTo = (InternalEObject)bindsTo;
			bindsTo = (Variable)eResolveProxy(oldBindsTo);
			if (bindsTo != oldBindsTo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO, oldBindsTo, bindsTo));
			}
		}
		return bindsTo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable basicGetBindsTo() {
		return bindsTo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBindsTo(Variable newBindsTo) {
		Variable oldBindsTo = bindsTo;
		bindsTo = newBindsTo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO, oldBindsTo, bindsTo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OclExpression getWhere() {
		return where;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWhere(OclExpression newWhere, NotificationChain msgs) {
		OclExpression oldWhere = where;
		where = newWhere;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.TEMPLATE_EXP__WHERE, oldWhere, newWhere);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWhere(OclExpression newWhere) {
		if (newWhere != where) {
			NotificationChain msgs = null;
			if (where != null)
				msgs = ((InternalEObject)where).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - QVTTemplatePackage.TEMPLATE_EXP__WHERE, null, msgs);
			if (newWhere != null)
				msgs = ((InternalEObject)newWhere).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - QVTTemplatePackage.TEMPLATE_EXP__WHERE, null, msgs);
			msgs = basicSetWhere(newWhere, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, QVTTemplatePackage.TEMPLATE_EXP__WHERE, newWhere, newWhere));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case QVTTemplatePackage.TEMPLATE_EXP__WHERE:
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
			case QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO:
				if (resolve) return getBindsTo();
				return basicGetBindsTo();
			case QVTTemplatePackage.TEMPLATE_EXP__WHERE:
				return getWhere();
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
			case QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO:
				setBindsTo((Variable)newValue);
				return;
			case QVTTemplatePackage.TEMPLATE_EXP__WHERE:
				setWhere((OclExpression)newValue);
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
			case QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO:
				setBindsTo((Variable)null);
				return;
			case QVTTemplatePackage.TEMPLATE_EXP__WHERE:
				setWhere((OclExpression)null);
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
			case QVTTemplatePackage.TEMPLATE_EXP__BINDS_TO:
				return bindsTo != null;
			case QVTTemplatePackage.TEMPLATE_EXP__WHERE:
				return where != null;
		}
		return super.eIsSet(featureID);
	}

} //TemplateExpImpl
