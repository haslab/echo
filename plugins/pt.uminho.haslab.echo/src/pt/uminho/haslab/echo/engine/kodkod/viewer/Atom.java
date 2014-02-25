/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Atom</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Atom#getLabel <em>Label</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getAtom()
 * @model
 * @generated
 */
public interface Atom extends EObject {
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
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getAtom_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Atom#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

} // Atom
