/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sig</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getParentID <em>Parent ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getLabel <em>Label</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAtom <em>Atom</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getBuiltin <em>Builtin</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getID <em>ID</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAbstract <em>Abstract</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig()
 * @model
 * @generated
 */
public interface Sig extends EObject {
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
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_ParentID()
	 * @model
	 * @generated
	 */
	int getParentID();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getParentID <em>Parent ID</em>}' attribute.
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
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns the value of the '<em><b>Atom</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Atom}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Atom</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Atom</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_Atom()
	 * @model containment="true"
	 * @generated
	 */
	EList<Atom> getAtom();

	/**
	 * Returns the value of the '<em><b>Builtin</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Builtin</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Builtin</em>' attribute.
	 * @see #setBuiltin(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_Builtin()
	 * @model
	 * @generated
	 */
	String getBuiltin();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getBuiltin <em>Builtin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Builtin</em>' attribute.
	 * @see #getBuiltin()
	 * @generated
	 */
	void setBuiltin(String value);

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
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_ID()
	 * @model
	 * @generated
	 */
	int getID();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getID <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ID</em>' attribute.
	 * @see #getID()
	 * @generated
	 */
	void setID(int value);

	/**
	 * Returns the value of the '<em><b>Abstract</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Abstract</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Abstract</em>' attribute.
	 * @see #setAbstract(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_Abstract()
	 * @model
	 * @generated
	 */
	String getAbstract();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAbstract <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Abstract</em>' attribute.
	 * @see #getAbstract()
	 * @generated
	 */
	void setAbstract(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getSig_Type()
	 * @model containment="true"
	 * @generated
	 */
	EList<Type> getType();

} // Sig
