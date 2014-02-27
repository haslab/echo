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
import pt.uminho.haslab.echo.engine.kodkod.viewer.Field;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Instance;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Sig;
import pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Instance</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getBitwidth <em>Bitwidth</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getMaxseq <em>Maxseq</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getFilename <em>Filename</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getSig <em>Sig</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl#getField <em>Field</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InstanceImpl extends MinimalEObjectImpl.Container implements Instance {
	/**
	 * The default value of the '{@link #getBitwidth() <em>Bitwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBitwidth()
	 * @generated
	 * @ordered
	 */
	protected static final int BITWIDTH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBitwidth() <em>Bitwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBitwidth()
	 * @ordered
	 */
	protected int bitwidth = BITWIDTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxseq() <em>Maxseq</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxseq()
	 * @ordered
	 */
	protected static final int MAXSEQ_EDEFAULT = -1;

	/**
	 * The cached value of the '{@link #getMaxseq() <em>Maxseq</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxseq()
	 * @generated
	 * @ordered
	 */
	protected int maxseq = MAXSEQ_EDEFAULT;

	/**
	 * The default value of the '{@link #getCommand() <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommand()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMAND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCommand() <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommand()
	 * @generated
	 * @ordered
	 */
	protected String command = COMMAND_EDEFAULT;

	/**
	 * The default value of the '{@link #getFilename() <em>Filename</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilename()
	 * @generated
	 * @ordered
	 */
	protected static final String FILENAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFilename() <em>Filename</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilename()
	 * @generated
	 * @ordered
	 */
	protected String filename = FILENAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSig() <em>Sig</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSig()
	 * @generated
	 * @ordered
	 */
	protected EList<Sig> sig;

	/**
	 * The cached value of the '{@link #getField() <em>Field</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getField()
	 * @generated
	 * @ordered
	 */
	protected EList<Field> field;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InstanceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ViewerPackage.Literals.INSTANCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getBitwidth() {
		return bitwidth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBitwidth(int newBitwidth) {
		int oldBitwidth = bitwidth;
		bitwidth = newBitwidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.INSTANCE__BITWIDTH, oldBitwidth, bitwidth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMaxseq() {
		return maxseq;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaxseq(int newMaxseq) {
		int oldMaxseq = maxseq;
		maxseq = newMaxseq;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.INSTANCE__MAXSEQ, oldMaxseq, maxseq));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCommand(String newCommand) {
		String oldCommand = command;
		command = newCommand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.INSTANCE__COMMAND, oldCommand, command));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFilename(String newFilename) {
		String oldFilename = filename;
		filename = newFilename;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.INSTANCE__FILENAME, oldFilename, filename));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Sig> getSig() {
		if (sig == null) {
			sig = new EObjectContainmentEList<Sig>(Sig.class, this, ViewerPackage.INSTANCE__SIG);
		}
		return sig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Field> getField() {
		if (field == null) {
			field = new EObjectContainmentEList<Field>(Field.class, this, ViewerPackage.INSTANCE__FIELD);
		}
		return field;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ViewerPackage.INSTANCE__SIG:
				return ((InternalEList<?>)getSig()).basicRemove(otherEnd, msgs);
			case ViewerPackage.INSTANCE__FIELD:
				return ((InternalEList<?>)getField()).basicRemove(otherEnd, msgs);
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
			case ViewerPackage.INSTANCE__BITWIDTH:
				return getBitwidth();
			case ViewerPackage.INSTANCE__MAXSEQ:
				return getMaxseq();
			case ViewerPackage.INSTANCE__COMMAND:
				return getCommand();
			case ViewerPackage.INSTANCE__FILENAME:
				return getFilename();
			case ViewerPackage.INSTANCE__SIG:
				return getSig();
			case ViewerPackage.INSTANCE__FIELD:
				return getField();
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
			case ViewerPackage.INSTANCE__BITWIDTH:
				setBitwidth((Integer)newValue);
				return;
			case ViewerPackage.INSTANCE__MAXSEQ:
				setMaxseq((Integer)newValue);
				return;
			case ViewerPackage.INSTANCE__COMMAND:
				setCommand((String)newValue);
				return;
			case ViewerPackage.INSTANCE__FILENAME:
				setFilename((String)newValue);
				return;
			case ViewerPackage.INSTANCE__SIG:
				getSig().clear();
				getSig().addAll((Collection<? extends Sig>)newValue);
				return;
			case ViewerPackage.INSTANCE__FIELD:
				getField().clear();
				getField().addAll((Collection<? extends Field>)newValue);
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
			case ViewerPackage.INSTANCE__BITWIDTH:
				setBitwidth(BITWIDTH_EDEFAULT);
				return;
			case ViewerPackage.INSTANCE__MAXSEQ:
				setMaxseq(MAXSEQ_EDEFAULT);
				return;
			case ViewerPackage.INSTANCE__COMMAND:
				setCommand(COMMAND_EDEFAULT);
				return;
			case ViewerPackage.INSTANCE__FILENAME:
				setFilename(FILENAME_EDEFAULT);
				return;
			case ViewerPackage.INSTANCE__SIG:
				getSig().clear();
				return;
			case ViewerPackage.INSTANCE__FIELD:
				getField().clear();
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
			case ViewerPackage.INSTANCE__BITWIDTH:
				return bitwidth != BITWIDTH_EDEFAULT;
			case ViewerPackage.INSTANCE__MAXSEQ:
				return maxseq != MAXSEQ_EDEFAULT;
			case ViewerPackage.INSTANCE__COMMAND:
				return COMMAND_EDEFAULT == null ? command != null : !COMMAND_EDEFAULT.equals(command);
			case ViewerPackage.INSTANCE__FILENAME:
				return FILENAME_EDEFAULT == null ? filename != null : !FILENAME_EDEFAULT.equals(filename);
			case ViewerPackage.INSTANCE__SIG:
				return sig != null && !sig.isEmpty();
			case ViewerPackage.INSTANCE__FIELD:
				return field != null && !field.isEmpty();
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
		result.append(" (bitwidth: ");
		result.append(bitwidth);
		result.append(", maxseq: ");
		result.append(maxseq);
		result.append(", command: ");
		result.append(command);
		result.append(", filename: ");
		result.append(filename);
		result.append(')');
		return result.toString();
	}

} //InstanceImpl
