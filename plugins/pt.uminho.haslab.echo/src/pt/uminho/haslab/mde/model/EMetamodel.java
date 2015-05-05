package pt.uminho.haslab.mde.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;

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
	 * @throws EErrorParser
	 * @throws EErrorUnsupported
	 */
	public EMetamodel(EPackage epackage) throws EErrorUnsupported, EErrorParser {
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
	
	/** calculates all possible root classes for this meta-model
	 * root classes are non-abstract classes not contained in any container reference
	 * @return the list of root classes
	 */
	public List<EClass> getRootClass() {
		List<EClass> isContained = new ArrayList<>();
		List<EClass> classes = new ArrayList<>();
		for (EClassifier obj : getEObject().getEClassifiers())
			if (obj instanceof EClass) {
				EClass eclass = (EClass) obj;
				for (EReference ref : eclass.getEReferences())
					if (ref.isContainment())
						isContained.add(ref.getEReferenceType());
			}

		for (EClassifier obj : getEObject().getEClassifiers())
			if (obj instanceof EClass) {
				EClass eclass = (EClass) obj;
				boolean add = true;
				if (eclass.isAbstract()) add = false;
				else if (isContained.contains(eclass)) add = false;
				else for (EClass s : eclass.getEAllSuperTypes())
					if (isContained.contains(s)) add = false;
				if (add) classes.add(eclass);
			}

		return new ArrayList<EClass>(classes);
	}


}
