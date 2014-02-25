/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Field;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Types;
import pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Field</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl#getID <em>ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl#getParentID <em>Parent ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl#getTuple <em>Tuple</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FieldImpl extends MinimalEObjectImpl.Container implements Field {
	/**
	 * The default value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getID()
	 * @generated
	 * @ordered
	 */
	protected static final int ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getID()
	 * @generated
	 * @ordered
	 */
	protected int id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getParentID() <em>Parent ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentID()
	 * @generated
	 * @ordered
	 */
	protected static final int PARENT_ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getParentID() <em>Parent ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentID()
	 * @generated
	 * @ordered
	 */
	protected int parentID = PARENT_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTuple() <em>Tuple</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTuple()
	 * @generated
	 * @ordered
	 */
	protected EList<AlloyTuple> tuple;

	/**
	 * The cached value of the '{@link #getTypes() <em>Types</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypes()
	 * @generated
	 * @ordered
	 */
	protected EList<Types> types;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FieldImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ViewerPackage.Literals.FIELD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getID() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setID(int newID) {
		int oldID = id;
		id = newID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.FIELD__ID, oldID, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getParentID() {
		return parentID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentID(int newParentID) {
		int oldParentID = parentID;
		parentID = newParentID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.FIELD__PARENT_ID, oldParentID, parentID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.FIELD__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AlloyTuple> getTuple() {
		if (tuple == null) {
			tuple = new EObjectContainmentEList<AlloyTuple>(AlloyTuple.class, this, ViewerPackage.FIELD__TUPLE);
		}
		return tuple;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Types> getTypes() {
		if (types == null) {
			types = new EObjectContainmentEList<Types>(Types.class, this, ViewerPackage.FIELD__TYPES);
		}
		return types;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ViewerPackage.FIELD__TUPLE:
				return ((InternalEList<?>)getTuple()).basicRemove(otherEnd, msgs);
			case ViewerPackage.FIELD__TYPES:
				return ((InternalEList<?>)getTypes()).basicRemove(otherEnd, msgs);
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
			case ViewerPackage.FIELD__ID:
				return getID();
			case ViewerPackage.FIELD__PARENT_ID:
				return getParentID();
			case ViewerPackage.FIELD__LABEL:
				return getLabel();
			case ViewerPackage.FIELD__TUPLE:
				return getTuple();
			case ViewerPackage.FIELD__TYPES:
				return getTypes();
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
			case ViewerPackage.FIELD__ID:
				setID((Integer)newValue);
				return;
			case ViewerPackage.FIELD__PARENT_ID:
				setParentID((Integer)newValue);
				return;
			case ViewerPackage.FIELD__LABEL:
				setLabel((String)newValue);
				return;
			case ViewerPackage.FIELD__TUPLE:
				getTuple().clear();
				getTuple().addAll((Collection<? extends AlloyTuple>)newValue);
				return;
			case ViewerPackage.FIELD__TYPES:
				getTypes().clear();
				getTypes().addAll((Collection<? extends Types>)newValue);
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
			case ViewerPackage.FIELD__ID:
				setID(ID_EDEFAULT);
				return;
			case ViewerPackage.FIELD__PARENT_ID:
				setParentID(PARENT_ID_EDEFAULT);
				return;
			case ViewerPackage.FIELD__LABEL:
				setLabel(LABEL_EDEFAULT);
				return;
			case ViewerPackage.FIELD__TUPLE:
				getTuple().clear();
				return;
			case ViewerPackage.FIELD__TYPES:
				getTypes().clear();
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
			case ViewerPackage.FIELD__ID:
				return id != ID_EDEFAULT;
			case ViewerPackage.FIELD__PARENT_ID:
				return parentID != PARENT_ID_EDEFAULT;
			case ViewerPackage.FIELD__LABEL:
				return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
			case ViewerPackage.FIELD__TUPLE:
				return tuple != null && !tuple.isEmpty();
			case ViewerPackage.FIELD__TYPES:
				return types != null && !types.isEmpty();
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
		result.append(" (ID: ");
		result.append(id);
		result.append(", parentID: ");
		result.append(parentID);
		result.append(", label: ");
		result.append(label);
		result.append(')');
		return result.toString();
	}

} //FieldImpl
