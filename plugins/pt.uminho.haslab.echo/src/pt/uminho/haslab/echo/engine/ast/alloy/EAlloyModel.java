package pt.uminho.haslab.echo.engine.ast.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.alloy.AlloyUtil;
import pt.uminho.haslab.echo.engine.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.engine.ast.EEngineModel;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EBoolean;
import pt.uminho.haslab.mde.model.EElement;
import pt.uminho.haslab.mde.model.EInteger;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.model.EProperty;
import pt.uminho.haslab.mde.model.EString;
import pt.uminho.haslab.mde.model.EValue;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class EAlloyModel implements EEngineModel {
		
	/** the EObject model being translated */
	public final EModel emodel;
	
	/** the Alloy signature representing this model */
	private final PrimSig model_sig;

	private PrimSig trg_model_sig;
	private boolean isTarget;
	
	/** the elements belonging to each Alloy field
	 *  includes state fields, defining existing elements */
	private Map<Field,Expr> field2elements = new HashMap<Field,Expr>();	
	
	/** maps EObjects to the respective Alloy signature */
	private Map<EElement,PrimSig> object2sig = new HashMap<EElement,PrimSig>();

	/** maps class names to set os object signatures */
	private Map<String,List<PrimSig>> classsig2sigs = new HashMap<String,List<PrimSig>>();

	/** the Alloy expression defining this model object */
	private Expr model_constraint = null; 
	
	public final EAlloyMetamodel metamodel;
	
	/**
	 * Creates a new XMI to Alloy translator
	 * Assumes that <code>obj</code> is the root of the model
	 * and recursively processes contained objects
	 * Unconnected objects are ignored
	 * @param emodel
	 * @param t
	 * @param stateSig
	 * @throws EchoError
	 */
	public EAlloyModel(EModel emodel, EAlloyMetamodel t) throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_MODEL, emodel.ID);
		this.emodel = emodel;
		metamodel = t;
		
		try {
			model_sig = new PrimSig(emodel.ID,(PrimSig) t.sig_metamodel,Attr.ONE);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()); }
		
		initContent();
		translateElement(emodel.getRootEElement());
		makeFactExpr();
		
		EchoReporter.getInstance().result(Task.TRANSLATE_MODEL, "", true);
	}
	
	/**
	 * Return the signature representing the <code>obj</code> object
	 * @param o the EObject
	 * @return the respective Sig
	 */
	public PrimSig getSigFromEObject(EElement o) {
		return object2sig.get(o);
	}

	/**
	 * Returns all signatures defined in this object
	 * @return all signatures defined in the object
	 */
	public List<PrimSig> getAllSigs() {
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
	public List<PrimSig> getClassSigs(PrimSig sig) {
		List<PrimSig> sigs = classsig2sigs.get(sig.label);
		if (sigs == null) sigs = new ArrayList<PrimSig>();
		return new ArrayList<PrimSig>(sigs);
	}

	/** the Alloy constraint defining this model */
	public AlloyFormula getModelConstraint() {
		return new AlloyFormula(model_constraint);
	}
	
	/**
	 * Initializes <code>field2elements</code> for every field without any elements
	 */
	private void initContent() {
		for(Field f: metamodel.getStateFields())
			field2elements.put(f,Sig.NONE);
		for(Field field: metamodel.getFields()){
			EStructuralFeature sfeature = metamodel.getSFeatureFromField(field);
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
	 * @param eelement the object to be translated
	 * @return the sig representing the object
	 * @throws EchoError
	 */
	private PrimSig translateElement(EElement eelement) throws EchoError {
		PrimSig classsig = metamodel.getSigFromEClassifier(eelement.type);
		PrimSig elementsig;
		try {
			elementsig = new PrimSig(AlloyUtil.elementName(classsig), classsig,
					Attr.ONE);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
					"Failed to create object sig.", a, Task.TRANSLATE_MODEL);
		}

		Field statefield = metamodel.getStateFieldFromSig(classsig);
		Expr siblings = field2elements.get(statefield).plus(elementsig);
		field2elements.put(statefield, siblings);

		//EchoReporter.getInstance().debug("Translate element: "+classsig+" : "+classsig.parent);
		PrimSig supersig = classsig.parent;
		while (supersig != Sig.UNIV && supersig != null) {
			Field superstatefield = metamodel.getStateFieldFromSig(supersig);
			Expr siblingsup = field2elements.get(superstatefield).plus(elementsig);
			field2elements.put(superstatefield, siblingsup);
			supersig = supersig.parent;
		}

		object2sig.put(eelement, elementsig);
		if (classsig2sigs.get(classsig.label) == null)
			classsig2sigs.put(classsig.label, new ArrayList<PrimSig>());

		classsig2sigs.get(classsig.label).add(elementsig);

		
		for (EProperty eprop : eelement.getProperties()) {
			Field field = metamodel.getFieldFromSFeature(eprop.feature);
			processValues(eprop.getValues(),field,elementsig);
		}

		return elementsig;
	}

	/**
	 * Calculates the Alloy expression representing the value of an n-ary reference
	 * Calls <code>processReference(EObject)</code> to process each value
	 * @param values the values of the reference
	 * @return the Alloy expression representing the values
	 * @throws EchoError
	 */
	private void processValues(List<EValue> values, Field field, PrimSig elementsig) throws EchoError {
		for (EValue value : values)
			processValue(value, field, elementsig);
	}

	/** 
	 * Adds the attribute value to the field expression 
	 * @param value the attribute value
	 * @param field the field representing the attribute
	 * @param elementsig the owning object
	 * @throws EchoError
	 */
	private void processValue(EValue value, Field field, Sig elementsig)
			throws EchoError {
		Expr siblings = field2elements.get(field);
		if (siblings == null) return;
		
		if (value instanceof EElement) {
			PrimSig ref = object2sig.get(value);
			if (ref == null) {
				ref = translateElement((EElement) value);
				siblings = field2elements.get(field);
			}
			siblings = siblings.plus(elementsig.product(ref));
			//EchoReporter.getInstance().debug(field+" : "+siblings+" : "+value);
		} else if (value instanceof EBoolean) {
			if (((EBoolean) value).getValue())
				siblings = siblings.plus(elementsig);
		} else if (value instanceof EEnumLiteral) {
			siblings = siblings.plus(elementsig.product(metamodel
					.getSigFromEEnumLiteral((EEnumLiteral) value)));
		} else if (value instanceof EString) {
			Expr str = ExprConstant.Op.STRING.make(null, ((EString) value).getValue());
			siblings = siblings.plus(elementsig.product(str));
		} else if (value instanceof EInteger) {
			Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
			Integer max = (int) (Math.pow(2, bitwidth) / 2);
			Integer ival = ((EInteger) value).getValue();
			if (ival >= max || ival < -max)
				throw new ErrorTransform(ErrorTransform.BITWIDTH,
						"Bitwidth not enough to represent attribute value: "
								+ value + ".", "", Task.TRANSLATE_MODEL);
			Expr eint = ExprConstant.makeNUMBER(ival);
			siblings = siblings.plus(elementsig.product(eint));
		} else
			throw new ErrorUnsupported(ErrorUnsupported.PRIMITIVE_TYPE,
					"Primitive type not supported: "
							+ value.getClass().getName(), "",
					Task.TRANSLATE_MODEL);
		field2elements.put(field, siblings);
		//EchoReporter.getInstance().debug(field2elements.keySet()+"");
	}
	
	private void makeFactExpr() {
		model_constraint = Sig.NONE.no();
		for(Expr f: field2elements.keySet()) {
			if (!f.toString().equals("String"))
				model_constraint = model_constraint.and(f.join(model_sig).equal(field2elements.get(f)));
		}
	}

	@Override
	public EAlloyMetamodel getMetamodel() {
		return metamodel;
	}

	@Override
	public EModel getModel() {
		return emodel;
	}
	
	public PrimSig setTarget() throws ErrorAlloy {
		isTarget = true;
		try {
			trg_model_sig = new PrimSig(AlloyUtil.targetName(model_sig), metamodel.sig_metamodel, Attr.ONE);
		} catch (Err e) {
			throw new ErrorAlloy("", "Failed to create target sig.", e, Task.ALLOY_RUN);
		}
		return trg_model_sig;
	}
	
	public PrimSig getModelSig() {
		return isTarget?trg_model_sig:model_sig;
	}

	public void unsetTarget() {
		isTarget = false;	
	}
	
}
