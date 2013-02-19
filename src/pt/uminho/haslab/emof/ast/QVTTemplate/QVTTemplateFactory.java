/**
 */
package pt.uminho.haslab.emof.ast.QVTTemplate;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see pt.uminho.haslab.emof.ast.QVTTemplate.QVTTemplatePackage
 * @generated
 */
public interface QVTTemplateFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	QVTTemplateFactory eINSTANCE = pt.uminho.haslab.emof.ast.QVTTemplate.impl.QVTTemplateFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Collection Template Exp</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Collection Template Exp</em>'.
	 * @generated
	 */
	CollectionTemplateExp createCollectionTemplateExp();

	/**
	 * Returns a new object of class '<em>Object Template Exp</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Object Template Exp</em>'.
	 * @generated
	 */
	ObjectTemplateExp createObjectTemplateExp();

	/**
	 * Returns a new object of class '<em>Property Template Item</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property Template Item</em>'.
	 * @generated
	 */
	PropertyTemplateItem createPropertyTemplateItem();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	QVTTemplatePackage getQVTTemplatePackage();

} //QVTTemplateFactory
