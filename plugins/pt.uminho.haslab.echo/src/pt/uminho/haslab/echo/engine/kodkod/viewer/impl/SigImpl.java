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
import pt.uminho.haslab.echo.engine.kodkod.viewer.Atom;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Sig;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Type;
import pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sig</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getParentID <em>Parent ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getAtom <em>Atom</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getBuiltin <em>Builtin</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getID <em>ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getAbstract <em>Abstract</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SigImpl extends MinimalEObjectImpl.Container implements Sig {

    /**
     * Copy constructor, already defines all fields, assumes parent to be univ and built-in false
     */
    SigImpl(int ID, String label){
        this.id = ID;
        this.label = label;
        this.parentID = 2;
        this.builtin = null;

    }

    /**
     * Copy constructor, already defines all fields, assumes built-in false
     */
    SigImpl(int ID, int parentID, String label){
        this.id = ID;
        this.label = label;
        this.parentID = parentID;
        this.builtin = null;
    }

    /**
     * Copy constructor
     */
    SigImpl(int ID, int parentID, String label, String builtin){
        this.id = ID;
        this.label = label;
        this.parentID = parentID;
        this.builtin = builtin;
    }


    /**
	 * The default value of the '{@link #getParentID() <em>Parent ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentID()
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
	 * The cached value of the '{@link #getAtom() <em>Atom</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAtom()
	 * @generated
	 * @ordered
	 */
	protected EList<Atom> atom;

	/**
	 * The default value of the '{@link #getBuiltin() <em>Builtin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuiltin()
	 * @generated
	 * @ordered
	 */
	protected static final String BUILTIN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBuiltin() <em>Builtin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuiltin()
	 * @generated
	 * @ordered
	 */
	protected String builtin = BUILTIN_EDEFAULT;

	/**
	 * The default value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getID()
	 * @ordered
	 */
	protected static final int ID_EDEFAULT = -1;

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
	 * The default value of the '{@link #getAbstract() <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstract()
	 * @generated
	 * @ordered
	 */
	protected static final String ABSTRACT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAbstract() <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstract()
	 * @generated
	 * @ordered
	 */
	protected String abstract_ = ABSTRACT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected EList<Type> type;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ViewerPackage.Literals.SIG;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.SIG__PARENT_ID, oldParentID, parentID));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.SIG__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Atom> getAtom() {
		if (atom == null) {
			atom = new EObjectContainmentEList<Atom>(Atom.class, this, ViewerPackage.SIG__ATOM);
		}
		return atom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBuiltin() {
		return builtin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBuiltin(String newBuiltin) {
		String oldBuiltin = builtin;
		builtin = newBuiltin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.SIG__BUILTIN, oldBuiltin, builtin));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.SIG__ID, oldID, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAbstract() {
		return abstract_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAbstract(String newAbstract) {
		String oldAbstract = abstract_;
		abstract_ = newAbstract;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.SIG__ABSTRACT, oldAbstract, abstract_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Type> getType() {
		if (type == null) {
			type = new EObjectContainmentEList<Type>(Type.class, this, ViewerPackage.SIG__TYPE);
		}
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ViewerPackage.SIG__ATOM:
				return ((InternalEList<?>)getAtom()).basicRemove(otherEnd, msgs);
			case ViewerPackage.SIG__TYPE:
				return ((InternalEList<?>)getType()).basicRemove(otherEnd, msgs);
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
			case ViewerPackage.SIG__PARENT_ID:
				return getParentID();
			case ViewerPackage.SIG__LABEL:
				return getLabel();
			case ViewerPackage.SIG__ATOM:
				return getAtom();
			case ViewerPackage.SIG__BUILTIN:
				return getBuiltin();
			case ViewerPackage.SIG__ID:
				return getID();
			case ViewerPackage.SIG__ABSTRACT:
				return getAbstract();
			case ViewerPackage.SIG__TYPE:
				return getType();
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
			case ViewerPackage.SIG__PARENT_ID:
				setParentID((Integer)newValue);
				return;
			case ViewerPackage.SIG__LABEL:
				setLabel((String)newValue);
				return;
			case ViewerPackage.SIG__ATOM:
				getAtom().clear();
				getAtom().addAll((Collection<? extends Atom>)newValue);
				return;
			case ViewerPackage.SIG__BUILTIN:
				setBuiltin((String)newValue);
				return;
			case ViewerPackage.SIG__ID:
				setID((Integer)newValue);
				return;
			case ViewerPackage.SIG__ABSTRACT:
				setAbstract((String)newValue);
				return;
			case ViewerPackage.SIG__TYPE:
				getType().clear();
				getType().addAll((Collection<? extends Type>)newValue);
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
			case ViewerPackage.SIG__PARENT_ID:
				setParentID(PARENT_ID_EDEFAULT);
				return;
			case ViewerPackage.SIG__LABEL:
				setLabel(LABEL_EDEFAULT);
				return;
			case ViewerPackage.SIG__ATOM:
				getAtom().clear();
				return;
			case ViewerPackage.SIG__BUILTIN:
				setBuiltin(BUILTIN_EDEFAULT);
				return;
			case ViewerPackage.SIG__ID:
				setID(ID_EDEFAULT);
				return;
			case ViewerPackage.SIG__ABSTRACT:
				setAbstract(ABSTRACT_EDEFAULT);
				return;
			case ViewerPackage.SIG__TYPE:
				getType().clear();
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
			case ViewerPackage.SIG__PARENT_ID:
				return parentID != PARENT_ID_EDEFAULT;
			case ViewerPackage.SIG__LABEL:
				return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
			case ViewerPackage.SIG__ATOM:
				return atom != null && !atom.isEmpty();
			case ViewerPackage.SIG__BUILTIN:
				return BUILTIN_EDEFAULT == null ? builtin != null : !BUILTIN_EDEFAULT.equals(builtin);
			case ViewerPackage.SIG__ID:
				return id != ID_EDEFAULT;
			case ViewerPackage.SIG__ABSTRACT:
				return ABSTRACT_EDEFAULT == null ? abstract_ != null : !ABSTRACT_EDEFAULT.equals(abstract_);
			case ViewerPackage.SIG__TYPE:
				return type != null && !type.isEmpty();
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
		result.append(" (parentID: ");
		result.append(parentID);
		result.append(", label: ");
		result.append(label);
		result.append(", builtin: ");
		result.append(builtin);
		result.append(", ID: ");
		result.append(id);
		result.append(", abstract: ");
		result.append(abstract_);
		result.append(')');
		return result.toString();
	}

} //SigImpl
