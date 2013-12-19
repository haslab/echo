package pt.uminho.haslab.echo.transform.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

class XMI2Alloy {
		
	/** the EObject model being translated */
	final EObject eobject;
	
	/** the Alloy signature representing this model */
	final PrimSig model_sig;

	/** the elements belonging to each Alloy field
	 *  includes state fields, defining existing elements */
	private Map<Field,Expr> field2elements = new HashMap<Field,Expr>();	
	
	/** maps EObjects to the respective Alloy signature */
	private Map<EObject,PrimSig> object2sig = new HashMap<EObject,PrimSig>();

	/** maps class names to set os object signatures */
	private Map<String,List<PrimSig>> classsig2sigs = new HashMap<String,List<PrimSig>>();

	/** the Alloy expression defining this model object */
	private Expr model_constraint = null; 
	
	final ECore2Alloy translator;
	
	/**
	 * Creates a new XMI to Alloy translator
	 * Assumes that <code>obj</code> is the root of the model
	 * and recursively processes contained objects
	 * Unconnected objects are ignored
	 * @param obj
	 * @param t
	 * @param stateSig
	 * @throws EchoError
	 */
	XMI2Alloy(EObject obj, ECore2Alloy t, PrimSig stateSig) throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_MODEL, stateSig.label);
		eobject = obj;
		translator = t;
		model_sig = stateSig;
		initContent();
		makeSigList(eobject);
		makeFactExpr();
		
		EchoReporter.getInstance().result(Task.TRANSLATE_MODEL, true);
	}
	
	/**
	 * Return the signature representing the <code>obj</code> object
	 * @param o the EObject
	 * @return the respective Sig
	 */
	PrimSig getSigFromEObject(EObject o) {
		return object2sig.get(o);
	}

	/**
	 * Returns all signatures defined in this object
	 * @return all signatures defined in the object
	 */
	List<PrimSig> getAllSigs() {
		List<PrimSig> res = new ArrayList<PrimSig>();
		for (List<PrimSig> sigs : classsig2sigs.values())
			res.addAll(sigs);
		return res;
	}

	/**
	 * Returns all signatures defined in the object belonging to a given class
	 * @param sig the class signature
	 * @return all signatures defined in the object
	 */
	List<PrimSig> getClassSigs(PrimSig sig) {
		List<PrimSig> sigs = classsig2sigs.get(sig.label);
		if (sigs == null) sigs = new ArrayList<PrimSig>();
		return new ArrayList<PrimSig>(sigs);
	}

	/** the Alloy constraint defining this model */
	Expr getModelConstraint() {
		return model_constraint;
	}
	
	/**
	 * Initializes <code>field2elements</code> for every field without any elements
	 */
	private void initContent() {
		for(Field f: translator.getStateFields())
			field2elements.put(f,Sig.NONE);
		for(Field field: translator.getFields()){
			EStructuralFeature sfeature = translator.getSFeatureFromField(field);
			if (field != null)
				if(sfeature.getEType().getName().equals("EBoolean"))
					field2elements.put(field,Sig.NONE);
				else
					field2elements.put(field,Sig.NONE.product(Sig.NONE));
		}
	}

	/**
	 * Creates the EObject signatures and respective structural features
	 * Will recursively create objects contained in references
	 * @param eobj the object to be translated
	 * @return the sig representing the object
	 * @throws EchoError
	 */
	private PrimSig makeSigList(EObject eobj) throws EchoError {
		PrimSig classsig = translator.getSigFromEClassifier(eobj.eClass());
		PrimSig objectsig;
		try {
			objectsig = new PrimSig(AlloyUtil.elementName(classsig), classsig,
					Attr.ONE);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
					"Failed to create object sig.", a, Task.TRANSLATE_MODEL);
		}

		Field statefield = translator.getStateFieldFromSig(classsig);
		Expr siblings = field2elements.get(statefield);
		siblings = siblings.plus(objectsig);
		field2elements.put(statefield, siblings);

		PrimSig supersig = classsig.parent;
		while (supersig != Sig.UNIV && supersig != null) {
			Field superstatefield = translator.getStateFieldFromSig(supersig);
			Expr siblingsup = field2elements.get(superstatefield);
			siblingsup = siblingsup.plus(objectsig);
			field2elements.put(superstatefield, siblingsup);
			supersig = supersig.parent;
		}

		object2sig.put(eobj, objectsig);
		if (classsig2sigs.get(classsig.label) == null)
			classsig2sigs.put(classsig.label, new ArrayList<PrimSig>());

		classsig2sigs.get(classsig.label).add(objectsig);

		List<EStructuralFeature> sfList = eobj.eClass()
				.getEAllStructuralFeatures();
		for (EStructuralFeature sf : sfList) {
			Field field = translator.getFieldFromSFeature(sf);
			Object value = eobj.eGet(sf);
			if (sf instanceof EReference) {
				if (value instanceof EList<?>) {
					if (!((EList<?>) value).isEmpty()) {
						EReference op = ((EReference) sf).getEOpposite();
						if (field != null) {
							processReference((EList<?>) value, field, objectsig);
						}
					}
				} else if (value instanceof EObject) {
					EReference op = ((EReference) sf).getEOpposite();
					if (field != null) {
						processReference((EObject) value, field, objectsig);
					}
				} else if (value == null) {
				} else throw new ErrorUnsupported(ErrorUnsupported.ECORE,
							"EReference type not supported: "
									+ value.getClass().getName(), "",
							Task.TRANSLATE_MODEL);
			} else if (sf instanceof EAttribute)
				processAttribute(value, objectsig, field);
		}
		return objectsig;
	}
	
	/**
	 * calculates the PrimSig representing the reference object
	 * processes it if still unprocessed
	 * @param obj the reference value
	 * @return the representing sig
	 * @throws EchoError
	 */
	private PrimSig processReference(EObject obj, Field field, PrimSig objectsig) throws EchoError {
		PrimSig ref = object2sig.get(obj);

		if (ref == null)
			ref = makeSigList(obj);

		Expr mappedExpr = field2elements.get(field);
		mappedExpr = mappedExpr.plus(objectsig.product(ref));
		field2elements.put(field, mappedExpr);
		
		return ref;
	}

	/**
	 * Calculates the Alloy expression representing the value of an n-ary reference
	 * Calls <code>processReference(EObject)</code> to process each value
	 * @param values the values of the reference
	 * @return the Alloy expression representing the values
	 * @throws EchoError
	 */
	private Expr processReference(EList<?> values, Field field, PrimSig objectsig) throws EchoError {
		Expr res = null;
		PrimSig ref;

		EchoReporter.getInstance().debug("Reference list of "+field);

		for (Object o : values)
			if (o instanceof EObject) {
				ref = processReference((EObject) o, field, objectsig);
				if (res == null) res = ref;
				else res = res.plus(ref);
			} 
			else throw new ErrorUnsupported(ErrorUnsupported.ECORE,
						"EReference type not supported: "
								+ o.getClass().getName(), "",
						Task.TRANSLATE_MODEL);
		
		return res;
	}

	/** 
	 * Adds the attribute value to the field expression 
	 * @param value the attribute value
	 * @param obj the owning object
	 * @param field the field representing the attribute
	 * @throws EchoError
	 */
	private void processAttribute(Object value, Sig obj, Field field)
			throws EchoError {
		Expr siblings = field2elements.get(field);
		if (value instanceof Boolean) {
			if ((Boolean) value)
				siblings = siblings.plus(obj);
		} else if (value instanceof EEnumLiteral) {
			siblings = siblings.plus(obj.product(translator
					.getSigFromEEnumLiteral((EEnumLiteral) value)));
		} else if (value instanceof String) {
			Expr str = ExprConstant.Op.STRING.make(null, (String) value);
			siblings = siblings.plus(obj.product(str));
		} else if (value instanceof Integer) {
			Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
			Integer max = (int) (Math.pow(2, bitwidth) / 2);
			if ((Integer) value >= max || (Integer) value < -max)
				throw new ErrorTransform(ErrorTransform.BITWIDTH,
						"Bitwidth not enough to represent attribute value: "
								+ value + ".", "", Task.TRANSLATE_MODEL);
			Expr str = ExprConstant.makeNUMBER((Integer) value);

			siblings = siblings.plus(obj.product(str));

		} else
			throw new ErrorUnsupported(ErrorUnsupported.PRIMITIVE_TYPE,
					"Primitive type not supported: "
							+ value.getClass().getName(), "",
					Task.TRANSLATE_MODEL);

		field2elements.put(field, siblings);
	}
	
	private void makeFactExpr() {
		model_constraint = Sig.NONE.no();
		for(Expr f: field2elements.keySet()) {
			if (!f.toString().equals("String"))
				model_constraint = model_constraint.and(f.join(model_sig).equal(field2elements.get(f)));
		}
	}
	
	
	


	
}
