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
import pt.uminho.haslab.echo.engine.kodkod.viewer.Instance;
import pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage;
import pt.uminho.haslab.echo.engine.kodkod.viewer.alloy;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>alloy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl#getBuilddate <em>Builddate</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl#getInstance <em>Instance</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class alloyImpl extends MinimalEObjectImpl.Container implements alloy {
	/**
	 * The default value of the '{@link #getBuilddate() <em>Builddate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuilddate()
	 * @generated
	 * @ordered
	 */
	protected static final String BUILDDATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBuilddate() <em>Builddate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuilddate()
	 * @generated
	 * @ordered
	 */
	protected String builddate = BUILDDATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInstance() <em>Instance</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInstance()
	 * @generated
	 * @ordered
	 */
	protected EList<Instance> instance;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected alloyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ViewerPackage.Literals.ALLOY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBuilddate() {
		return builddate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBuilddate(String newBuilddate) {
		String oldBuilddate = builddate;
		builddate = newBuilddate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ViewerPackage.ALLOY__BUILDDATE, oldBuilddate, builddate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Instance> getInstance() {
		if (instance == null) {
			instance = new EObjectContainmentEList<Instance>(Instance.class, this, ViewerPackage.ALLOY__INSTANCE);
		}
		return instance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ViewerPackage.ALLOY__INSTANCE:
				return ((InternalEList<?>)getInstance()).basicRemove(otherEnd, msgs);
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
			case ViewerPackage.ALLOY__BUILDDATE:
				return getBuilddate();
			case ViewerPackage.ALLOY__INSTANCE:
				return getInstance();
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
			case ViewerPackage.ALLOY__BUILDDATE:
				setBuilddate((String)newValue);
				return;
			case ViewerPackage.ALLOY__INSTANCE:
				getInstance().clear();
				getInstance().addAll((Collection<? extends Instance>)newValue);
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
			case ViewerPackage.ALLOY__BUILDDATE:
				setBuilddate(BUILDDATE_EDEFAULT);
				return;
			case ViewerPackage.ALLOY__INSTANCE:
				getInstance().clear();
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
			case ViewerPackage.ALLOY__BUILDDATE:
				return BUILDDATE_EDEFAULT == null ? builddate != null : !BUILDDATE_EDEFAULT.equals(builddate);
			case ViewerPackage.ALLOY__INSTANCE:
				return instance != null && !instance.isEmpty();
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
		result.append(" (builddate: ");
		result.append(builddate);
		result.append(')');
		return result.toString();
	}

} //alloyImpl
