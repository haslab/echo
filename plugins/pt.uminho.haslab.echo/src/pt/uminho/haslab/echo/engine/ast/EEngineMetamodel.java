package pt.uminho.haslab.echo.engine.ast;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.util.EcoreUtil;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EMetamodel;

import java.util.*;

public abstract class EEngineMetamodel {

	/** the package being translated */
	public final EMetamodel metamodel;

	protected EEngineMetamodel(EMetamodel metamodel) throws EchoError {
		this.metamodel = metamodel;
	}
	
	/**
	 * Translates the information from the this.epackage (classes, attributes, references, annotations, operations, eenums)
	 * @throws EchoError
	 */
	public void translate() throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_METAMODEL,
				metamodel.ID);
		List<EClass> classList = new LinkedList<EClass>();
		List<EEnum> enumList = new ArrayList<EEnum>();

		for (EClassifier e : metamodel.getEObject().getEClassifiers()) {
			if (e instanceof EClass)
				classList.add((EClass) e);
			else if (e instanceof EEnum)
				enumList.add((EEnum) e);
			else if (e instanceof EDataType)
				throw new ErrorUnsupported(ErrorUnsupported.ECORE,
						"'EDataTypes' are not supported.", "",
						Task.TRANSLATE_METAMODEL);
		}		
		
		processEnums(enumList);

		for (EClass c : classList)
			processClass(c);
		for (EClass c : classList)
			processAttributes(c.getEAttributes());
		for (EClass c : classList)
			processReferences(c.getEReferences());
		for (EClass c : classList)
			processAnnotations(c.getEAnnotations());
		for (EClass c : classList)
			processOperations(c.getEOperations());

		EchoReporter.getInstance().result(Task.TRANSLATE_METAMODEL, "", true);
	}
	
	abstract protected void processOperations(List<EOperation> eOperations) throws EchoError;
	abstract protected void processAnnotations(List<EAnnotation> eAnnotations) throws EchoError;
	abstract protected void processReferences(List<EReference> eReferences) throws EchoError;
	abstract protected void processAttributes(List<EAttribute> attributes) throws EchoError;
	abstract protected void processEnums(List<EEnum> enumList) throws EchoError;
	abstract protected void processClass(EClass eclass) throws EchoError;

	/** calculates all possible root classes for this meta-model
	 * root classes are those classes not contained in any container reference
	 * @return the list of root classes
	 */
	public List<EClass> getRootClass() {
		List<EClass> isContained = new ArrayList<>();
		List<EClass> classes = new ArrayList<>();
		for (EClassifier obj : metamodel.getEObject().getEClassifiers())
			if (obj instanceof EClass) {
				EClass eclass = (EClass) obj;
				for (EReference ref : eclass.getEReferences())
					if (ref.isContainment())
						isContained.add(ref.getEReferenceType());
			}

		for (EClassifier obj : metamodel.getEObject().getEClassifiers())
			if (obj instanceof EClass) {
				EClass eclass = (EClass) obj;
				boolean add = true;
				if (isContained.contains(eclass)) add = false;
				for (EClass s : eclass.getEAllSuperTypes())
					if (isContained.contains(s)) add = false;
				if (add) classes.add(eclass);
			}
			
//			if (obj instanceof EClass && !((EClass) obj).isAbstract())
//				classes.put(obj.getClassifierID(), (EClass) obj);
//		Map<Integer, EClass> candidates = new HashMap<Integer, EClass>(classes);
//		for (EClass obj : classes.values()) {
//			EchoReporter.getInstance().debug("Candidates before "+obj.getName()+": "+candidates);
//			for (EReference ref : obj.getEAllReferences())
//				if (ref.isContainer())
//					candidates.remove(obj.getClassifierID());
//			EchoReporter.getInstance().debug("Candidates after refs "+obj.getName()+": "+candidates);
//			List<EClass> sups = obj.getESuperTypes();
//			if (sups != null && sups.size() != 0)
//				if (!candidates.keySet()
//						.contains(sups.get(0).getClassifierID()) && !sups.get(0).isAbstract())
//					candidates.remove(obj.getClassifierID());
//			EchoReporter.getInstance().debug("Candidates after sups "+obj.getName()+": "+candidates);
//		}
		return new ArrayList<EClass>(classes);
	}
	
	protected abstract IFormula getConforms(String modelID) throws ErrorInternalEngine;
	
}
