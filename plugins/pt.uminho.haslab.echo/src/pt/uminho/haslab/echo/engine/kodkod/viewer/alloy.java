/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>alloy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getBuilddate <em>Builddate</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getInstance <em>Instance</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getalloy()
 * @model
 * @generated
 */
public interface alloy extends EObject {
	/**
	 * Returns the value of the '<em><b>Builddate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Builddate</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Builddate</em>' attribute.
	 * @see #setBuilddate(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getalloy_Builddate()
	 * @model
	 * @generated
	 */
	String getBuilddate();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getBuilddate <em>Builddate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Builddate</em>' attribute.
	 * @see #getBuilddate()
	 * @generated
	 */
	void setBuilddate(String value);

	/**
	 * Returns the value of the '<em><b>Instance</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Instance</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Instance</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getalloy_Instance()
	 * @model containment="true"
	 * @generated
	 */
	EList<Instance> getInstance();

} // alloy
