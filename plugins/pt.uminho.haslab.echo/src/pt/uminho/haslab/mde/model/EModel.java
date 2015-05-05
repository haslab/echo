package pt.uminho.haslab.mde.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.EEnumLiteral;
import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Echo representation of a model instance (an XMI object).
 * It is basically a pointer to a root element
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EModel extends EArtifact {

	/** the root element */
	private EElement root;

	/** the model's meta-model */
	private EMetamodel metamodel;

	/**
	 * Creates a model instance from a XMI EObject
	 * An EModel is basically a pointer to a root EElement
	 * 
	 * @param name the model name
	 * @param eobject the XMI input object
	 * @throws EErrorParser
	 * @throws EError
	 */
	public EModel(EObject eobject) throws EErrorUnsupported, EErrorParser {
		super(eobject.eClass().getName(),eobject);
	}

	public EModel(EObject eobject, String uri) throws EErrorUnsupported, EErrorParser {
		super(eobject.eClass().getName(),eobject,uri);
	}

	/** {@inheritDoc} */
	@Override
	protected void process(EObject eobject) throws EErrorUnsupported, EErrorParser {
		root = new XMI2EModel(eobject).eelement;
		String metamodelURI = eobject.eClass().getEPackage().eResource().getURI().path();
		metamodel = MDEManager.getInstance().getMetamodel(metamodelURI, false);
	}

	/** {@inheritDoc} */
	@Override
	public EObject getEObject() {
		return root.getEObject();
	}



	/**
	 * Returns the model's metamodel
	 * 
	 * @return the metamodel
	 */
	public EMetamodel getMetamodel() {
		return metamodel;
	}

	/**
	 * Returns the model's root element
	 * 
	 * @return the model's root element
	 */
	public EElement getRootEElement() {
		return root;
	}

	/**
	 * EObject to EModel translator class.
	 * 
	 * @author nmm
	 */
	class XMI2EModel {

		/** the EObject XMI model being translated */
		final EObject eobject;

		/** the resulting EElement */
		final EElement eelement;

		/** maps EClasses to all occurring elements of that type */
		Map<String, List<EElement>> eclass2elements = new HashMap<String, List<EElement>>();

		/** maps EObjects to the respective EElement */
		Map<EObject, EElement> object2element = new HashMap<EObject, EElement>();

		/**
		 * Creates a new XMI to EModel translator
		 * Assumes that <code>obj</code> is the root of the model and recursively
		 * processes contained objects
		 * Unconnected objects are ignored
		 * 
		 * @param obj
		 *            the root XMI object
		 * @throws EErrorUnsupported
		 */
		XMI2EModel(EObject obj) throws EErrorUnsupported {
			eobject = obj;
			eelement = translateEObject(eobject);
		}

		/**
		 * Return the EElement representing the <code>obj</code> object
		 * 
		 * @param o
		 *            the EObject
		 * @return the respective EElement
		 */
		EElement getEElementFromEObject(EObject o) {
			return object2element.get(o);
		}

		/**
		 * Returns all EElements defined in this object
		 * 
		 * @return all EElements
		 */
		List<EElement> getAllEElements() {
			List<EElement> res = new ArrayList<EElement>();
			for (List<EElement> elems : eclass2elements.values())
				res.addAll(elems);
			return res;
		}

		/**
		 * Returns all EElements defined in the object belonging to a given
		 * class
		 * 
		 * @param eclass
		 *            the class
		 * @return the EElements
		 */
		List<EElement> getEClassEElements(EClass eclass) {
			List<EElement> elems = eclass2elements.get(eclass.getName());
			if (elems == null)
				elems = new ArrayList<EElement>();
			return new ArrayList<EElement>(elems);
		}

		/**
		 * Translates a particular EObject to an EElement
		 * Will recursively create objects contained in references if not
		 * already translated
		 * 
		 * @param eobj
		 *            the object to be translated
		 * @return the EElement representing the object
		 * @throws EErrorUnsupported
		 */
		private EElement translateEObject(EObject eobj) throws EErrorUnsupported {
			EElement eelement = new EElement(eobj);

			if (eclass2elements.get(eelement.type.getName()) == null)
				eclass2elements
				.put(eelement.type.getName(), new ArrayList<EElement>());
			eclass2elements.get(eelement.type.getName()).add(eelement);

			for (EClass superclass : eelement.type.getEAllSuperTypes()) {
				if (eclass2elements.get(superclass.getName()) == null)
					eclass2elements.put(superclass.getName(),
							new ArrayList<EElement>());
				eclass2elements.get(superclass.getName()).add(eelement);
			}

			object2element.put(eobj, eelement);

			List<EStructuralFeature> sfList = eobj.eClass()
					.getEAllStructuralFeatures();
			for (EStructuralFeature sf : sfList) {
				Object value = eobj.eGet(sf);
				if (sf instanceof EReference) {
					if (value instanceof EList<?>) {
						if (!((EList<?>) value).isEmpty()) {
							processReference((EList<?>) value, eelement,
									(EReference) sf);
						}
					} else if (value instanceof EObject) {
						processReference((EObject) value, eelement,
								(EReference) sf);
					} else if (value == null) {
					} else
						throw new EErrorUnsupported(EErrorUnsupported.ECORE,
								"EReference type not supported: "
										+ value.getClass().getName(), "",
										Task.TRANSLATE_MODEL);
				} else if (sf instanceof EAttribute)
					processAttribute(value, eelement, (EAttribute) sf);
			}
			return eelement;
		}

		/**
		 * Calculates the EElement representing the reference object processes
		 * it if still unprocessed
		 * 
		 * @param obj
		 *            the reference value
		 * @param eelement
		 *            the parent EElement
		 * @param ref
		 *            the reference
		 * @return the representing sig
		 * @throws EErrorUnsupported
		 */
		private EElement processReference(EObject obj, EElement eelement,
				EReference ref) throws EErrorUnsupported {
			EElement value = object2element.get(obj);

			if (value == null)
				value = translateEObject(obj);

			eelement.addValue(ref, value);

			return value;
		}

		/**
		 * Calculates the list of EElements representing the value of an n-ary
		 * reference
		 * Calls <code>processReference(EObject)</code> to process each value
		 * 
		 * @param values
		 *            the values of the reference
		 * @param eelement
		 *            the parent EElement
		 * @param ref
		 *            the reference
		 * @return the EElement values
		 * @throws EErrorUnsupported
		 */
		private List<EElement> processReference(EList<?> values,
				EElement eelement, EReference ref) throws EErrorUnsupported {
			List<EElement> res = new ArrayList<EElement>();
			EElement value;

			for (Object o : values)
				if (o instanceof EObject) {
					value = processReference((EObject) o, eelement, ref);
					res.add(value);
				} else
					throw new EErrorUnsupported(EErrorUnsupported.ECORE,
							"EReference type not supported: "
									+ o.getClass().getName(), "",
									Task.TRANSLATE_MODEL);

			return res;
		}

		/**
		 * Calculates the value of an attribute
		 * 
		 * @param value
		 *            the attribute value
		 * @param eelement
		 *            the parent EElement
		 * @param att
		 *            the reference
		 * @throws EErrorUnsupported
		 */
		private void processAttribute(Object value, EElement eelement,
				EAttribute att) throws EErrorUnsupported {
			if (value == null) {}
			else if (value instanceof Boolean)
				eelement.addValue(att, (Boolean) value);
			else if (value instanceof EEnumLiteral)
				eelement.addValue(att, (EEnumLiteral) value);
			else if (value instanceof String)
				eelement.addValue(att, (String) value);
			else if (value instanceof Integer)
				eelement.addValue(att, (Integer) value);
			else
				throw new EErrorUnsupported(EErrorUnsupported.PRIMITIVE_TYPE,
						"Primitive type not supported: "
								+ value.getClass().getName(), "",
								Task.TRANSLATE_MODEL);
		}

	}

	@Override
	public String toString() {
		return root.toString();
	}

}
