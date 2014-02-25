/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Alloy Tuple</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple#getAtom <em>Atom</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getAlloyTuple()
 * @model
 * @generated
 */
public interface AlloyTuple extends EObject {
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
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getAlloyTuple_Atom()
	 * @model containment="true"
	 * @generated
	 */
	EList<Atom> getAtom();

} // AlloyTuple
