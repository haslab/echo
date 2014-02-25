/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getBitwidth <em>Bitwidth</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getMaxseq <em>Maxseq</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getCommand <em>Command</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getFilename <em>Filename</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getSig <em>Sig</em>}</li>
 *   <li>{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getField <em>Field</em>}</li>
 * </ul>
 * </p>
 *
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance()
 * @model
 * @generated
 */
public interface Instance extends EObject {
	/**
	 * Returns the value of the '<em><b>Bitwidth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bitwidth</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bitwidth</em>' attribute.
	 * @see #setBitwidth(int)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Bitwidth()
	 * @model
	 * @generated
	 */
	int getBitwidth();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getBitwidth <em>Bitwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bitwidth</em>' attribute.
	 * @see #getBitwidth()
	 * @generated
	 */
	void setBitwidth(int value);

	/**
	 * Returns the value of the '<em><b>Maxseq</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Maxseq</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maxseq</em>' attribute.
	 * @see #setMaxseq(int)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Maxseq()
	 * @model
	 * @generated
	 */
	int getMaxseq();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getMaxseq <em>Maxseq</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maxseq</em>' attribute.
	 * @see #getMaxseq()
	 * @generated
	 */
	void setMaxseq(int value);

	/**
	 * Returns the value of the '<em><b>Command</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Command</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command</em>' attribute.
	 * @see #setCommand(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Command()
	 * @model
	 * @generated
	 */
	String getCommand();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getCommand <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Command</em>' attribute.
	 * @see #getCommand()
	 * @generated
	 */
	void setCommand(String value);

	/**
	 * Returns the value of the '<em><b>Filename</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Filename</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Filename</em>' attribute.
	 * @see #setFilename(String)
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Filename()
	 * @model
	 * @generated
	 */
	String getFilename();

	/**
	 * Sets the value of the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getFilename <em>Filename</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Filename</em>' attribute.
	 * @see #getFilename()
	 * @generated
	 */
	void setFilename(String value);

	/**
	 * Returns the value of the '<em><b>Sig</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sig</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sig</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Sig()
	 * @model containment="true"
	 * @generated
	 */
	EList<Sig> getSig();

	/**
	 * Returns the value of the '<em><b>Field</b></em>' containment reference list.
	 * The list contents are of type {@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Field</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Field</em>' containment reference list.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerPackage#getInstance_Field()
	 * @model containment="true"
	 * @generated
	 */
	EList<Field> getField();

} // Instance
