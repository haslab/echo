/**
 */
package pt.uminho.haslab.echo.engine.kodkod.viewer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/OCL/Import ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface ViewerPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "viewer";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "pt.uminho.haslab.echo.engine.kodkod.viewer";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ViewerPackage eINSTANCE = pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl.init();

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl <em>Sig</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getSig()
	 * @generated
	 */
	int SIG = 0;

	/**
	 * The feature id for the '<em><b>Parent ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__PARENT_ID = 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__LABEL = 1;

	/**
	 * The feature id for the '<em><b>Atom</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__ATOM = 2;

	/**
	 * The feature id for the '<em><b>Builtin</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__BUILTIN = 3;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__ID = 4;

	/**
	 * The feature id for the '<em><b>Abstract</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__ABSTRACT = 5;

	/**
	 * The feature id for the '<em><b>Type</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG__TYPE = 6;

	/**
	 * The number of structural features of the '<em>Sig</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Sig</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl <em>Field</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getField()
	 * @generated
	 */
	int FIELD = 1;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD__ID = 0;

	/**
	 * The feature id for the '<em><b>Parent ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD__PARENT_ID = 1;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD__LABEL = 2;

	/**
	 * The feature id for the '<em><b>Tuple</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD__TUPLE = 3;

	/**
	 * The feature id for the '<em><b>Types</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD__TYPES = 4;

	/**
	 * The number of structural features of the '<em>Field</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Field</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIELD_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl <em>Instance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getInstance()
	 * @generated
	 */
	int INSTANCE = 2;

	/**
	 * The feature id for the '<em><b>Bitwidth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__BITWIDTH = 0;

	/**
	 * The feature id for the '<em><b>Maxseq</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__MAXSEQ = 1;

	/**
	 * The feature id for the '<em><b>Command</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__COMMAND = 2;

	/**
	 * The feature id for the '<em><b>Filename</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__FILENAME = 3;

	/**
	 * The feature id for the '<em><b>Sig</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__SIG = 4;

	/**
	 * The feature id for the '<em><b>Field</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE__FIELD = 5;

	/**
	 * The number of structural features of the '<em>Instance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Instance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AtomImpl <em>Atom</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AtomImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getAtom()
	 * @generated
	 */
	int ATOM = 3;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATOM__LABEL = 0;

	/**
	 * The number of structural features of the '<em>Atom</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATOM_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Atom</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATOM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AlloyTupleImpl <em>Alloy Tuple</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AlloyTupleImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getAlloyTuple()
	 * @generated
	 */
	int ALLOY_TUPLE = 4;

	/**
	 * The feature id for the '<em><b>Atom</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY_TUPLE__ATOM = 0;

	/**
	 * The number of structural features of the '<em>Alloy Tuple</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY_TUPLE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Alloy Tuple</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY_TUPLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl <em>alloy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getalloy()
	 * @generated
	 */
	int ALLOY = 5;

	/**
	 * The feature id for the '<em><b>Builddate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY__BUILDDATE = 0;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY__INSTANCE = 1;

	/**
	 * The number of structural features of the '<em>alloy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>alloy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALLOY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypesImpl <em>Types</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypesImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getTypes()
	 * @generated
	 */
	int TYPES = 6;

	/**
	 * The feature id for the '<em><b>Type</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPES__TYPE = 0;

	/**
	 * The number of structural features of the '<em>Types</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPES_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Types</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypeImpl <em>Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypeImpl
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 7;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__ID = 0;

	/**
	 * The number of structural features of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig <em>Sig</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sig</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig
	 * @generated
	 */
	EClass getSig();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getParentID <em>Parent ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent ID</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getParentID()
	 * @see #getSig()
	 * @generated
	 */
	EAttribute getSig_ParentID();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getLabel()
	 * @see #getSig()
	 * @generated
	 */
	EAttribute getSig_Label();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAtom <em>Atom</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Atom</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAtom()
	 * @see #getSig()
	 * @generated
	 */
	EReference getSig_Atom();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getBuiltin <em>Builtin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Builtin</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getBuiltin()
	 * @see #getSig()
	 * @generated
	 */
	EAttribute getSig_Builtin();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getID <em>ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ID</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getID()
	 * @see #getSig()
	 * @generated
	 */
	EAttribute getSig_ID();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAbstract <em>Abstract</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Abstract</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getAbstract()
	 * @see #getSig()
	 * @generated
	 */
	EAttribute getSig_Abstract();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Type</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Sig#getType()
	 * @see #getSig()
	 * @generated
	 */
	EReference getSig_Type();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field <em>Field</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Field</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field
	 * @generated
	 */
	EClass getField();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getID <em>ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ID</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getID()
	 * @see #getField()
	 * @generated
	 */
	EAttribute getField_ID();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getParentID <em>Parent ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent ID</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getParentID()
	 * @see #getField()
	 * @generated
	 */
	EAttribute getField_ParentID();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getLabel()
	 * @see #getField()
	 * @generated
	 */
	EAttribute getField_Label();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTuple <em>Tuple</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tuple</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTuple()
	 * @see #getField()
	 * @generated
	 */
	EReference getField_Tuple();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTypes <em>Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Types</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Field#getTypes()
	 * @see #getField()
	 * @generated
	 */
	EReference getField_Types();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance <em>Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Instance</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance
	 * @generated
	 */
	EClass getInstance();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getBitwidth <em>Bitwidth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bitwidth</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getBitwidth()
	 * @see #getInstance()
	 * @generated
	 */
	EAttribute getInstance_Bitwidth();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getMaxseq <em>Maxseq</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maxseq</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getMaxseq()
	 * @see #getInstance()
	 * @generated
	 */
	EAttribute getInstance_Maxseq();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getCommand <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Command</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getCommand()
	 * @see #getInstance()
	 * @generated
	 */
	EAttribute getInstance_Command();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getFilename <em>Filename</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Filename</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getFilename()
	 * @see #getInstance()
	 * @generated
	 */
	EAttribute getInstance_Filename();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getSig <em>Sig</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sig</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getSig()
	 * @see #getInstance()
	 * @generated
	 */
	EReference getInstance_Sig();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getField <em>Field</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Field</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Instance#getField()
	 * @see #getInstance()
	 * @generated
	 */
	EReference getInstance_Field();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Atom <em>Atom</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Atom</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Atom
	 * @generated
	 */
	EClass getAtom();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Atom#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Atom#getLabel()
	 * @see #getAtom()
	 * @generated
	 */
	EAttribute getAtom_Label();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple <em>Alloy Tuple</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alloy Tuple</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple
	 * @generated
	 */
	EClass getAlloyTuple();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple#getAtom <em>Atom</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Atom</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.AlloyTuple#getAtom()
	 * @see #getAlloyTuple()
	 * @generated
	 */
	EReference getAlloyTuple_Atom();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy <em>alloy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>alloy</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.alloy
	 * @generated
	 */
	EClass getalloy();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getBuilddate <em>Builddate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Builddate</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getBuilddate()
	 * @see #getalloy()
	 * @generated
	 */
	EAttribute getalloy_Builddate();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getInstance <em>Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Instance</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.alloy#getInstance()
	 * @see #getalloy()
	 * @generated
	 */
	EReference getalloy_Instance();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Types <em>Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Types</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Types
	 * @generated
	 */
	EClass getTypes();

	/**
	 * Returns the meta object for the containment reference list '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Types#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Type</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Types#getType()
	 * @see #getTypes()
	 * @generated
	 */
	EReference getTypes_Type();

	/**
	 * Returns the meta object for class '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Type
	 * @generated
	 */
	EClass getType();

	/**
	 * Returns the meta object for the attribute '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.Type#getID <em>ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ID</em>'.
	 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.Type#getID()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_ID();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ViewerFactory getViewerFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl <em>Sig</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.SigImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getSig()
		 * @generated
		 */
		EClass SIG = eINSTANCE.getSig();

		/**
		 * The meta object literal for the '<em><b>Parent ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIG__PARENT_ID = eINSTANCE.getSig_ParentID();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIG__LABEL = eINSTANCE.getSig_Label();

		/**
		 * The meta object literal for the '<em><b>Atom</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SIG__ATOM = eINSTANCE.getSig_Atom();

		/**
		 * The meta object literal for the '<em><b>Builtin</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIG__BUILTIN = eINSTANCE.getSig_Builtin();

		/**
		 * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIG__ID = eINSTANCE.getSig_ID();

		/**
		 * The meta object literal for the '<em><b>Abstract</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIG__ABSTRACT = eINSTANCE.getSig_Abstract();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SIG__TYPE = eINSTANCE.getSig_Type();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl <em>Field</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.FieldImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getField()
		 * @generated
		 */
		EClass FIELD = eINSTANCE.getField();

		/**
		 * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIELD__ID = eINSTANCE.getField_ID();

		/**
		 * The meta object literal for the '<em><b>Parent ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIELD__PARENT_ID = eINSTANCE.getField_ParentID();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIELD__LABEL = eINSTANCE.getField_Label();

		/**
		 * The meta object literal for the '<em><b>Tuple</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FIELD__TUPLE = eINSTANCE.getField_Tuple();

		/**
		 * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FIELD__TYPES = eINSTANCE.getField_Types();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl <em>Instance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.InstanceImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getInstance()
		 * @generated
		 */
		EClass INSTANCE = eINSTANCE.getInstance();

		/**
		 * The meta object literal for the '<em><b>Bitwidth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INSTANCE__BITWIDTH = eINSTANCE.getInstance_Bitwidth();

		/**
		 * The meta object literal for the '<em><b>Maxseq</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INSTANCE__MAXSEQ = eINSTANCE.getInstance_Maxseq();

		/**
		 * The meta object literal for the '<em><b>Command</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INSTANCE__COMMAND = eINSTANCE.getInstance_Command();

		/**
		 * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INSTANCE__FILENAME = eINSTANCE.getInstance_Filename();

		/**
		 * The meta object literal for the '<em><b>Sig</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTANCE__SIG = eINSTANCE.getInstance_Sig();

		/**
		 * The meta object literal for the '<em><b>Field</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTANCE__FIELD = eINSTANCE.getInstance_Field();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AtomImpl <em>Atom</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AtomImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getAtom()
		 * @generated
		 */
		EClass ATOM = eINSTANCE.getAtom();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATOM__LABEL = eINSTANCE.getAtom_Label();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AlloyTupleImpl <em>Alloy Tuple</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.AlloyTupleImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getAlloyTuple()
		 * @generated
		 */
		EClass ALLOY_TUPLE = eINSTANCE.getAlloyTuple();

		/**
		 * The meta object literal for the '<em><b>Atom</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALLOY_TUPLE__ATOM = eINSTANCE.getAlloyTuple_Atom();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl <em>alloy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.alloyImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getalloy()
		 * @generated
		 */
		EClass ALLOY = eINSTANCE.getalloy();

		/**
		 * The meta object literal for the '<em><b>Builddate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ALLOY__BUILDDATE = eINSTANCE.getalloy_Builddate();

		/**
		 * The meta object literal for the '<em><b>Instance</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALLOY__INSTANCE = eINSTANCE.getalloy_Instance();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypesImpl <em>Types</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypesImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getTypes()
		 * @generated
		 */
		EClass TYPES = eINSTANCE.getTypes();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPES__TYPE = eINSTANCE.getTypes_Type();

		/**
		 * The meta object literal for the '{@link pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypeImpl <em>Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.TypeImpl
		 * @see pt.uminho.haslab.echo.engine.kodkod.viewer.impl.ViewerPackageImpl#getType()
		 * @generated
		 */
		EClass TYPE = eINSTANCE.getType();

		/**
		 * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__ID = eINSTANCE.getType_ID();

	}

} //ViewerPackage
