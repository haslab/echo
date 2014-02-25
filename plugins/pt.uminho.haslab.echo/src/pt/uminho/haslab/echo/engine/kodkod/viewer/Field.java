/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Field</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getID <em>ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getParentID <em>Parent ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getLabel <em>Label</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTuple <em>Tuple</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField()
 * @model
 * @generated
 */
public interface Field extends EObject {
	/**
	 * Returns the value of the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>ID</em>' attribute.
	 * @see #setID(int)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField_ID()
	 * @model
	 * @generated
	 */
	int getID();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getID <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ID</em>' attribute.
	 * @see #getID()
	 * @generated
	 */
	void setID(int value);

	/**
	 * Returns the value of the '<em><b>Parent ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent ID</em>' attribute.
	 * @see #setParentID(int)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField_ParentID()
	 * @model
	 * @generated
	 */
	int getParentID();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getParentID <em>Parent ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent ID</em>' attribute.
	 * @see #getParentID()
	 * @generated
	 */
	void setParentID(int value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns the value of the '<em><b>Tuple</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tuple</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tuple</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField_Tuple()
	 * @model containment="true"
	 * @generated
	 */
	EList<AlloyTuple> getTuple();

	/**
	 * Returns the value of the '<em><b>Types</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Types}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Types</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getField_Types()
	 * @model containment="true"
	 * @generated
	 */
	EList<Types> getTypes();

} // Field
