package pt.uminho.haslab.echo.engine.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.mde.model.EMetamodel;

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
		Map<Integer, EClass> classes = new HashMap<Integer, EClass>();
		for (EClassifier obj : metamodel.getEObject().getEClassifiers())
			if (obj instanceof EClass)
				classes.put(obj.getClassifierID(), (EClass) obj);
		Map<Integer, EClass> candidates = new HashMap<Integer, EClass>(classes);

		for (EClass obj : classes.values()) {
			for (EReference ref : obj.getEReferences())
				if (ref.isContainment())
					candidates
							.remove(ref.getEReferenceType().getClassifierID());
			List<EClass> sups = obj.getESuperTypes();
			if (sups != null && sups.size() != 0)
				if (!candidates.keySet()
						.contains(sups.get(0).getClassifierID()))
					candidates.remove(obj.getClassifierID());
		}
		return new ArrayList<EClass>(candidates.values());
	}
	
}
