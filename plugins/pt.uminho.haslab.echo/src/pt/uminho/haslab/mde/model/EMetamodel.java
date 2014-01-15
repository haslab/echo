package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * The Echo representation of a metamodel (an Ecore EPackage).
 * 
 * @author nmm
 */
public class EMetamodel {
	
	/** the metamodel unique ID (may not change) */
	public final String ID;
	/** the corresponding EPackage */
	private EPackage epackage;
	
	/**
	 * Creates a new metamodel from an Ecore EPackage
	 * @param epackage the corresponding EPackage
	 */
	public EMetamodel(EPackage epackage) {
		this.epackage = epackage;
		ID = epackage.getName() + this.hashCode()+"";
	}

	/** returns the corresponding EPackage */
	public EPackage getEPackage() {
		return epackage;
	}

	/**
	 * Updates the metamodel with a new EPackage
	 * @param epackage the updates package
	 */
	public void update(EPackage epackage) {
		this.epackage = epackage;		
	}
	
	public String getURI() {
		return EcoreUtil.getURI(epackage).path();
	}
}
