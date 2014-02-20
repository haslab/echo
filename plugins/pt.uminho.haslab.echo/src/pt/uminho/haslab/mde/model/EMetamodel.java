package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;

/**
 * The Echo representation of a metamodel (an Ecore EPackage).
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EMetamodel extends EArtifact {

	/** the corresponding EPackage */
	private EPackage epackage;

	/**
	 * Creates a new metamodel from an Ecore EPackage
	 * 
	 * @param epackage the corresponding EPackage
	 * @throws ErrorParser
	 * @throws ErrorUnsupported
	 */
	public EMetamodel(EPackage epackage) throws ErrorUnsupported, ErrorParser {
		super(epackage.getName(),epackage);
	}

	/** {@inheritDoc} */
	@Override
	protected void process(EObject artifact) {
		this.epackage = (EPackage) artifact;
	}

	/** {@inheritDoc} */
	@Override
	public EPackage getEObject() {
		return epackage;
	}


}
