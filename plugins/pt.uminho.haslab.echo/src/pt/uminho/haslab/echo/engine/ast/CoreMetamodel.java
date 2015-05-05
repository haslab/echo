package pt.uminho.haslab.echo.engine.ast;

import org.eclipse.emf.ecore.*;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.mde.model.EMetamodel;

import java.util.*;

public abstract class CoreMetamodel {

	/** the package being translated */
	public final EMetamodel metamodel;

	protected CoreMetamodel(EMetamodel metamodel) throws EError {
		this.metamodel = metamodel;
	}
	
	/**
	 * Translates the information from the this.epackage (classes, attributes, references, annotations, operations, eenums)
	 * @throws EError
	 */
	public void translate() throws EError {
		List<EClass> classList = new LinkedList<EClass>();
		List<EEnum> enumList = new ArrayList<EEnum>();

		for (EClassifier e : metamodel.getEObject().getEClassifiers()) {
			if (e instanceof EClass)
				classList.add((EClass) e);
			else if (e instanceof EEnum)
				enumList.add((EEnum) e);
			else if (e instanceof EDataType)
				throw new EErrorUnsupported(EErrorUnsupported.ECORE,
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
	}
	
	abstract protected void processOperations(List<EOperation> eOperations) throws EError;
	abstract protected void processAnnotations(List<EAnnotation> eAnnotations) throws EError;
	abstract protected void processReferences(List<EReference> eReferences) throws EError;
	abstract protected void processAttributes(List<EAttribute> attributes) throws EError;
	abstract protected void processEnums(List<EEnum> enumList) throws EError;
	abstract protected void processClass(EClass eclass) throws EError;


	
	protected abstract IFormula getConforms(String modelID) throws EErrorCore;
	
}
