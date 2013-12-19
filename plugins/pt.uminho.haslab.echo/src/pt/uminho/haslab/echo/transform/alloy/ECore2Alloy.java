package pt.uminho.haslab.echo.transform.alloy;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.ExpressionInOCL;
import org.eclipse.ocl.examples.pivot.OCL;
import org.eclipse.ocl.examples.pivot.ParserException;
import org.eclipse.ocl.examples.pivot.helper.OCLHelper;
import org.eclipse.ocl.examples.pivot.utilities.PivotEnvironmentFactory;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

class ECore2Alloy {

	/** the package being translated */
	final EPackage epackage;
	
	/** the Alloy signature representing this meta-model */
	final PrimSig sig_metamodel;
	
	/** the model parameter of the conformity expression
	 * constraint is defined over this variable */
	private final Decl model_var;
	
	/** the Alloy expression representing the conformity constraint
	 * should be defined over <code>model_var</code> */
	private Expr constraint_conforms = Sig.NONE.no();
	
	/** the Alloy expression representing the generation constraint 
	 * should be defined over <code>model_var</code> */
	private Expr constraint_generate = Sig.NONE.no();
	
	/** the Alloy signature representing the order over model elements
	 * should have order fact attached */
	private PrimSig sig_order;
	
	/** maps class names to the number of creations in operations
	 * each increment represents an <code>oclIsNew()</code> occurrence*/
	private Map<String,Integer> elem_creation_count = new HashMap<String,Integer>();

	/** maps class names into respective Alloy signatures */
	private Map<String,PrimSig> class2sig = new HashMap<String,PrimSig>();
	
	/** maps structural feature names into respective Alloy fields */
	private Map<String,Field> feature2field = new HashMap<String,Field>();
		
	/** maps literals into respective Alloy signatures */
	private BiMap<EEnumLiteral,PrimSig> literal2sig = HashBiMap.create();
	
	/** maps signatures into respective Alloy state fields */
	private Map<PrimSig,Field> sig2statefield = new HashMap<PrimSig,Field>();
	
	/** list of Alloy functions resulting from operation translation */
	private List<Func> operations = new ArrayList<Func>();
	
	
	/**
	 * Creates a translator from meta-models (represented by an EPackage) to Alloy artifacts
	 * @param pck the package to translate
	 * @param statesig the state signature representing the meta-model
	 * @throws EchoError
	 */
	ECore2Alloy(EPackage pck, PrimSig statesig) throws ErrorAlloy {
		this.sig_metamodel = statesig;
		this.epackage = pck;
		try {
			model_var = statesig.oneOf("s_");
		} catch (Err a) {
			throw new ErrorAlloy(
					ErrorAlloy.FAIL_CREATE_VAR,
					"Failed to create model variable at meta-model translation.",
					a,
					Task.TRANSLATE_METAMODEL);
		}
	}

	/** 
	 * Returns the {@link EClass} matching an Alloy {@link PrimSig}
	 * @param s the Alloy signature
 	 * @return the matching class
	 */
	EClassifier getEClassifierFromSig(PrimSig s) {
		for (String cla : class2sig.keySet())
			if (class2sig.get(cla).isSame(s)) return epackage.getEClassifier(AlloyUtil.getClassifierName(cla));
		return null;
	}

	/** 
	 * Returns the Alloy {@link PrimSig} matching a {@link EClass}
	 * @param c the class
 	 * @return the matching Alloy signature
	 */
	PrimSig getSigFromEClassifier(EClassifier c) {
		return class2sig.get(AlloyUtil.classifierKey(epackage, c));
	}	
	
	/** 
	 * Returns the state {@link Field} representing a {@link PrimSig}
	 * @param eclass the EClass
 	 * @return the state field
	 */
	Field getStateFieldFromClass(EClass eclass) {
		PrimSig sig = getSigFromEClassifier(eclass);
		return sig2statefield.get(sig);
	}	
	
	/** 
	 * Returns the state {@link Field} representing a {@link PrimSig}
	 * @param sig the signature
 	 * @return the state field
	 */
	Field getStateFieldFromSig(PrimSig sig) {
		return sig2statefield.get(sig);
	}	
	
	/**
	 * Returns all state fields of this meta-model
	 * @return the state fields
	 */
	Collection<Field> getStateFields() {
		return sig2statefield.values();
	}
	
	/** 
	 * Returns the Alloy {@link Field} matching a {@link EStructuralFeature}
	 * @param f the desired feature
	 * @return the matching Alloy field
	 */
	Field getFieldFromSFeature(EStructuralFeature f) {
		if (f == null) EchoReporter.getInstance().debug("getFieldFromSFeature: null argument.");
		EchoReporter.getInstance().debug("looking for "+AlloyUtil.featureKey(epackage, f) +" in "+feature2field.keySet());
		return feature2field.get(AlloyUtil.featureKey(epackage, f));
	}
	
	/** 
	 * Returns the {@link EStructuralFeature} matching an Alloy {@link Field}
	 * @param f the Alloy field
 	 * @return the matching feature
	 */
	EStructuralFeature getSFeatureFromField(Field f) {
		String refname = AlloyUtil.getFeatureName(f.label);
		String classname = AlloyUtil.getClassifierName(f.sig.label);
		EClass cla = (EClass) epackage.getEClassifier(classname);
		return cla.getEStructuralFeature(refname);
	}
		
	/**
	 * Returns all {@link EStructuralFeature} of this meta-model
	 * @return the features
	 */
	Collection<Field> getFields() {
		return feature2field.values();
	}

	/**
	 * Returns the Alloy {@link PrimSig} representing an {@link EEnumLiteral} 
	 * @return the matching signature
	 */
	PrimSig getSigFromEEnumLiteral(EEnumLiteral e) {
		return literal2sig.get(e);
	}
	
	/**
	 * Returns the {@link EEnumLiteral} represented by an Alloy {@link PrimSig}
	 * @return the matching enum literal
	 */
	EEnumLiteral getEEnumLiteralFromSig(PrimSig s) {
		return literal2sig.inverse().get(s);
	}
	
	/**
	 * Returns enum {@link PrimSig} of this meta-model
	 * @return the signatures
	 */
	List<PrimSig> getEnumSigs() {
		List<PrimSig> aux = new ArrayList<PrimSig>();
		for (String cname : class2sig.keySet())
			if (epackage.getEClassifier(AlloyUtil.getClassifierName(cname)) instanceof EEnum) 
				aux.add(class2sig.get(cname));
		aux.addAll(literal2sig.values());
		return aux;
	}

	/**
	 * Returns all {@link PrimSig} of this meta-model
	 * @return the signatures
	 */
	List<PrimSig> getAllSigs() {
		List<PrimSig> aux = new ArrayList<PrimSig>(class2sig.values());
		aux.addAll(literal2sig.values());
		if (EchoOptionsSetup.getInstance().isOperationBased()) aux.add(sig_order);
		return aux;
	}

	/**
	 * Returns the {@link Func} that tests well-formedness
	 * @return the predicate
	 * @throws ErrorAlloy 
	 */
	Func getConforms() throws ErrorAlloy {
		Func f;
		try {
			f = new Func(null, epackage.getName(), new ArrayList<Decl>(Arrays.asList(model_var)), null, constraint_conforms);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Failed to create conforming function.", e,
					Task.TRANSLATE_METAMODEL);
		}		
		return f;
	}

	/**
	 * Returns the {@link Func} that constraints the generation of models
	 * consists of the conform constraints and the generation constraints
	 * @return the predicate
	 * @throws ErrorAlloy 
	 */
	Func getGenerate() throws ErrorAlloy {
		Func f;
		try {
			f = new Func(null, epackage.getName(), new ArrayList<Decl>(Arrays.asList(model_var)), null, constraint_conforms.and(constraint_generate));
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Failed to create generation function.", e,
					Task.TRANSLATE_METAMODEL);
		}
		return f;
	}

	/**
	 * Translates the information from the this.epackage (classes, attributes, references, annotations, operations, eenums)
	 * @throws EchoError
	 */
	void translate() throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_METAMODEL,
				sig_metamodel.label);
		List<EClass> classList = new LinkedList<EClass>();
		List<EEnum> enumList = new ArrayList<EEnum>();

		for (EClassifier e : epackage.getEClassifiers()) {
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

		for (PrimSig s : class2sig.values())
			processHeritage(s);

		createOrder();

		EchoReporter.getInstance().result(Task.TRANSLATE_METAMODEL, true);
	}



    /**
	 * Translates an {@link EClass}
	 * New sigs: the signature representing the class
	 * New fields: the state field of the signature
	 * New facts: all elements must belong to the state field
	 * @param ec the EClass to translate
	 * @throws EchoError
	 */
	private void processClass(EClass ec) throws EchoError {
		PrimSig ecsig, parent = null;
		Field statefield;
		if (class2sig.get(AlloyUtil.classifierKey(epackage, ec)) != null)
			return;
		List<EClass> superTypes = ec.getESuperTypes();
		if (superTypes.size() > 1)
			throw new ErrorUnsupported(ErrorUnsupported.MULTIPLE_INHERITANCE,
					"Multiple inheritance not allowed: " + ec.getName() + ".",
					"", Task.TRANSLATE_METAMODEL);
		if (!superTypes.isEmpty()) {
			parent = class2sig.get(AlloyUtil.classifierKey(epackage,
					superTypes.get(0)));
			if (parent == null)
				processClass(superTypes.get(0));
		}
		String signame = AlloyUtil.classifierKey(epackage, ec);
		try {
			if (ec.isAbstract())
				ecsig = new PrimSig(signame, parent, Attr.ABSTRACT);
			else
				ecsig = new PrimSig(signame, parent);
			statefield = ecsig.addField(AlloyUtil.stateFieldName(epackage, ec),
					sig_metamodel.setOf());
			Expr stateatoms = ecsig.equal(statefield.join(sig_metamodel));
			ecsig.addFact(stateatoms);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
					"Failed to create class sig.", a, Task.TRANSLATE_METAMODEL);
		}
		sig2statefield.put(ecsig, statefield);
		class2sig.put(AlloyUtil.classifierKey(epackage, ec), ecsig);
	}
	
	/**
	 * Translates a list of {@link EAttribute}
	 * New fields: unary field if EBoolean
	 * New fields: binary field if EInt or EString
	 * New facts: multiplicity constraints for binary fields
	 * New facts: if Attribute is set to ID creates uniqueness constraint
	 * @param attributes the list of attributes to translate
	 * @throws ErrorUnsupported the attribute type is not supported
	 * @throws ErrorAlloy
	 */
	private void processAttributes(List<EAttribute> attributes)
			throws EchoError {
		Field field = null;
		for (EAttribute attr : attributes) {
			PrimSig classsig = class2sig.get(AlloyUtil.classifierKey(
					epackage, attr.getEContainingClass()));
			String fieldname = AlloyUtil.featureKey(epackage, attr);
			try {
				if (attr.getEType().getName().equals("EBoolean"))
					field = classsig.addField(
							AlloyUtil.featureKey(epackage, attr),
							sig_metamodel.setOf());
				else {
					PrimSig type = null;
					Expr fact = null;
					if (attr.getEType().getName().equals("EString"))
						type = Sig.STRING;
					else if (attr.getEType().getName().equals("EInt")) {
						type = Sig.SIGINT;
						EchoReporter.getInstance().warning(
								"Integer attributes require enough bitwidth to represent values", 
								Task.TRANSLATE_METAMODEL);
					}
					else if (attr.getEType() instanceof EEnum)
						type = class2sig.get(AlloyUtil.classifierKey(
								epackage, attr.getEType()));
					else
						throw new ErrorUnsupported(
								ErrorUnsupported.PRIMITIVE_TYPE,
								"Primitive type of attribute not supported: "
										+ attr.getName() + ".", "",
								Task.TRANSLATE_METAMODEL);

					field = classsig.addField(fieldname,
							type.product(sig_metamodel));
					fact = field.join(model_var.get());
					Expr bound;
					if (attr.isID())
						bound = sig2statefield.get(classsig)
								.join(model_var.get()).lone_arrow_one(type);
					else
						bound = sig2statefield.get(classsig)
								.join(model_var.get()).any_arrow_one(type);
					fact = fact.in(bound);
					constraint_conforms = constraint_conforms.and(fact);

				}
			} catch (Err err) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FIELD,
						"Could not create attribute field: " + attr.getName(),
						err, Task.TRANSLATE_METAMODEL);
			}

			feature2field.put(fieldname, field);

		}
	}
	
	/**
	 * Translates a list of {@link EReference}
	 * New fields: the field representing the reference
	 * New facts: field is converse of opposite reference (if exists)
	 * New facts: multiplicity constraints of new fields
	 * New facts: new field only relates elements present in the same states
	 * New facts: if reference is containment, no free elements outside field
	 * Optimizations: if opposite reference is container, do not translate to Alloy
	 * @param references the list of references to translate
	 * @throws EchoError
	 */
	private void processReferences(List<EReference> references)
			throws EchoError {
		for (EReference reference : references) {
			PrimSig classsig = class2sig.get(AlloyUtil.classifierKey(
					epackage, reference.getEContainingClass()));
			EReference op = reference.getEOpposite();

			if (op != null && EchoOptionsSetup.getInstance().isOptimize()) {
				if (op.isContainment())
					return;
//				if (!reference.isContainment() && op.getLowerBound() == 1
//						&& op.getUpperBound() == 1)
//					return; // ????
				if (getFieldFromSFeature(op) != null)
					return;
			}

			EClass cc = reference.getEReferenceType();
			if (cc == null)
				throw new ErrorParser(ErrorParser.METAMODEL,"Failed to find reference '"+reference.getName()+"' type.",
						"Check the meta-model specification.",Task.TRANSLATE_METAMODEL);
			PrimSig trgsig = class2sig.get(AlloyUtil.classifierKey(epackage,
					cc));
			Field field;
			String feature_key = AlloyUtil.featureKey(epackage, reference);

			try {
				field = classsig.addField(feature_key,
						trgsig.product(sig_metamodel));
			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FIELD,
						"Failed to create reference field: "
								+ reference.getName(), a,
						Task.TRANSLATE_METAMODEL);
			}

			feature2field.put(feature_key, field);

			Expr fact;
			if (op != null) {
				Field opField = getFieldFromSFeature(op);
				if (opField != null) {
					fact = field.join(model_var.get()).equal(
							opField.join(model_var.get()).transpose());
					EchoReporter.getInstance().debug("PROBLEMATIC: "+fact);
					
					constraint_conforms = constraint_conforms.and(fact);
				}
			}

			Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
			Integer max = (int) (Math.pow(2, bitwidth) / 2);
			if (reference.getLowerBound() >= max
					|| reference.getLowerBound() < -max)
				throw new ErrorTransform(ErrorTransform.BITWIDTH,
						"Bitwidth not enough to represent lower bound: "
								+ reference.getLowerBound() + ".", "",
						Task.TRANSLATE_METAMODEL);
			if (reference.getUpperBound() >= max
					|| reference.getUpperBound() < -max)
				throw new ErrorTransform(ErrorTransform.BITWIDTH,
						"Bitwidth not enough to represent upper bound: "
								+ reference.getUpperBound() + ".", "",
						Task.TRANSLATE_METAMODEL);

			try {
				Decl d = (sig2statefield.get(classsig).join(model_var.get()))
						.oneOf("src_");
				if (reference.getLowerBound() == 1
						&& reference.getUpperBound() == 1) {
					fact = (d.get()).join(field.join(model_var.get())).one()
							.forAll(d);
					EchoReporter.getInstance().debug("PROBLEMATIC: "+fact+" with src_::"+d.expr);

					constraint_conforms = constraint_conforms.and(fact);
				} else if (reference.getLowerBound() == 0
						&& reference.getUpperBound() == 1) {
					fact = (d.get()).join(field.join(model_var.get())).lone()
							.forAll(d);
					constraint_conforms = constraint_conforms.and(fact);
				} else if (reference.getLowerBound() == 1
						&& reference.getUpperBound() == -1) {
					fact = (d.get()).join(field.join(model_var.get())).some()
							.forAll(d);
					constraint_conforms = constraint_conforms.and(fact);
				} else if (reference.getUpperBound() == 0) {
					fact = (d.get()).join(field.join(model_var.get())).no()
							.forAll(d);
					constraint_conforms = constraint_conforms.and(fact);
				} else if (reference.getLowerBound() == 0
						&& reference.getUpperBound() == -1) {
				} else {
					if (reference.getLowerBound() > 1) {
						fact = (d.get())
								.join(field.join(model_var.get()))
								.cardinality()
								.gte(ExprConstant.makeNUMBER(reference
										.getLowerBound())).forAll(d);
						constraint_conforms = constraint_conforms.and(fact);
					}
					if (reference.getUpperBound() > 1) {
						fact = (d.get())
								.join(field.join(model_var.get()))
								.cardinality()
								.lte(ExprConstant.makeNUMBER(reference
										.getUpperBound())).forAll(d);
						constraint_conforms = constraint_conforms.and(fact);
					}
				}

				if (reference.isContainment()) {
					d = (sig2statefield.get(trgsig).join(model_var.get()))
							.oneOf("trg_");
					fact = ((field.join(model_var.get())).join(d.get())).one()
							.forAll(d);
					constraint_conforms = constraint_conforms.and(fact);
				}

				Expr parState = sig2statefield.get(classsig);
				Expr sTypeState = sig2statefield.get(trgsig);
				fact = field.join(model_var.get()).in(
						parState.join(model_var.get()).product(
								sTypeState.join(model_var.get())));
				constraint_conforms = constraint_conforms.and(fact);

			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
						"Failed to create reference constraints variable.", a,
						Task.TRANSLATE_METAMODEL);
			}
		}
	}

	/**
	 * Translates a list of {@link EAnnotation} representing OCL constraints
	 * New facts: constraints representing the OCL constraints
	 * @param annotations the list of annotations to translate
	 * @throws ErrorTransform the OCL translation failed
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported the OCL formulas contains unsupported operators
	 * @throws ErrorParser the OCL parser failed
	 */
	private void processAnnotations(List<EAnnotation> annotations)
			throws EchoError {
		OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
		for (EAnnotation annotation : annotations) {
			Decl self = null;
			OCLHelper helper = ocl.createOCLHelper(annotation.eContainer());
			Map<String, Entry<ExprHasName, String>> sd = new HashMap<String, Entry<ExprHasName, String>>();
			PrimSig classsig = class2sig.get(AlloyUtil.classifierKey(
					epackage, (EClassifier) annotation.eContainer()));
			Field statefield = sig2statefield.get(classsig);
			try {
				self = (statefield.join(model_var.get())).oneOf("self");
			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
						"Failed to create annotation variable.", a,
						Task.TRANSLATE_METAMODEL);
			}
			sd.put(self.get().label,
					new SimpleEntry<ExprHasName, String>(self.get(),
							sig_metamodel.label));
			sd.put(model_var.get().label, new SimpleEntry<ExprHasName, String>(
					model_var.get(), null));

			Map<String, ExprHasName> statevars = new HashMap<String, ExprHasName>();
			statevars.put(sig_metamodel.label, model_var.get());

			OCL2Alloy converter = new OCL2Alloy(sd, statevars, null);

			if (annotation.getSource() != null) {
				if (annotation.getSource().equals(
						"http://www.eclipse.org/emf/2002/Ecore/OCL")
						|| annotation.getSource().equals("Echo/Gen"))
					try {
						for (String sExpr : annotation.getDetails().values()) {
							ExpressionInOCL invariant = helper
									.createInvariant(sExpr);
							Expr oclalloy = converter.oclExprToAlloy(
									invariant.getBodyExpression()).forAll(self);
							AlloyOptimizations opt = new AlloyOptimizations();
							if (EchoOptionsSetup.getInstance().isOptimize()) {
								oclalloy = opt.trading(oclalloy);
								oclalloy = opt.onePoint(oclalloy);
							}
							if (annotation
									.getSource()
									.equals("http://www.eclipse.org/emf/2002/Ecore/OCL"))
								constraint_conforms = constraint_conforms
										.and(oclalloy);
							else
								constraint_generate = constraint_generate
										.and(oclalloy);
						}
					} catch (Err a) {
						throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
								"Failed to create annotation variable.", a,
								Task.TRANSLATE_METAMODEL);
					} catch (ParserException e) {
						throw new ErrorParser(ErrorParser.OCL,
								"Failed to parse OCL annotation.",
								e.getMessage(), Task.TRANSLATE_METAMODEL);
					}
			}
		}
	}
	
	/**
	 * Translates a list of {@link EOperation} specified by OCL formulas
	 * New function: the predicate representing the operation
	 * @param eoperations the operations to translate
	 * @throws ErrorTransform the OCL translation failed
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported the OCL formulas contains unsupported operators
	 * @throws ErrorParser the OCL parser failed
	 */
	private void processOperations(List<EOperation> eoperations)
			throws EchoError {
		OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
		for (EOperation operation : eoperations) {
			PrimSig classsig = class2sig.get(AlloyUtil.classifierKey(
					epackage, operation.getEContainingClass()));
			List<Decl> decls = new ArrayList<Decl>();
			Map<String, Entry<ExprHasName, String>> sd = new HashMap<String, Entry<ExprHasName, String>>();

			Decl pre, pos, self = null;
			try {
				self = classsig.oneOf("self");
				pre = sig_metamodel.oneOf("pre_");
				pos = sig_metamodel.oneOf("pos_");
				decls.add(self);
				sd.put(self.get().label, new SimpleEntry<ExprHasName, String>(
						self.get(), sig_metamodel.label));
				for (EParameter p : operation.getEParameters()) {
					PrimSig type = AlloyEchoTranslator.getInstance()
							.getClassifierFromSig(p.getEType());
					Decl d = type.oneOf(p.getName());
					decls.add(d);
					sd.put(d.get().label, new SimpleEntry<ExprHasName, String>(
							d.get(), sig_metamodel.label));
				}
				decls.add(pre);
				decls.add(pos);
				sd.put(pre.get().label, new SimpleEntry<ExprHasName, String>(
						pre.get(), sig_metamodel.label));
				sd.put(pos.get().label, new SimpleEntry<ExprHasName, String>(
						pos.get(), sig_metamodel.label));
			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
						"Failed to create operation variable.", a,
						Task.TRANSLATE_METAMODEL);
			}
			OCLHelper helper = ocl.createOCLHelper(operation);
			Map<String, ExprHasName> prestatevars = new HashMap<String, ExprHasName>();
			Map<String, ExprHasName> posstatevars = new HashMap<String, ExprHasName>();
			prestatevars.put(sig_metamodel.label, pre.get());
			posstatevars.put(sig_metamodel.label, pos.get());

			OCL2Alloy converter = new OCL2Alloy(sd, posstatevars, prestatevars);
			for (EAnnotation ea : operation.getEAnnotations())
				if (ea.getSource().equals(
						"http://www.eclipse.org/emf/2002/Ecore/OCL")) {
					Expr oclalloy = Sig.NONE.no();
					for (String sExpr : ea.getDetails().values()) {
						try {
							ExpressionInOCL invariant = helper
									.createPostcondition(sExpr);
							oclalloy = oclalloy.and(converter
									.oclExprToAlloy(invariant
											.getBodyExpression()));
						} catch (ParserException e) {
							throw new ErrorParser(ErrorParser.OCL,
									"Failed to parse OCL operation.",
									e.getMessage(), Task.TRANSLATE_METAMODEL);
						}
					}
					try {
						Func fun = new Func(null, operation.getName(), decls,
								null, oclalloy);
						operations.add(fun);
					} catch (Err a) {
						throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
								"Failed to create operation function.", a,
								Task.TRANSLATE_METAMODEL);
					}
				}

			for (String cl : converter.getOCLAreNews().keySet()) {
				Integer newi = elem_creation_count.get(cl);
				if (newi == null)
					elem_creation_count.put(cl,
							converter.getOCLAreNews().get(cl));
				else
					elem_creation_count.put(cl, newi
							+ converter.getOCLAreNews().get(cl));
			}

		}
	}

	/**
	 * Translates a list of {@link EEnum} and the respective {@link EEnumLiteral}
	 * New sigs: abstract sig representing the enum
	 * New sigs: child singleton sigs representing the enum literals
	 * @param enums the enums to translate
	 * @throws ErrorAlloy
	 */
	private void processEnums(List<EEnum> enums) throws ErrorAlloy {
		PrimSig enumSig = null;
		for (EEnum enu : enums) {
			try {
				enumSig = new PrimSig(AlloyUtil.classifierKey(epackage, enu),
						Attr.ABSTRACT);
			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
						"Failed to create enum sig.", a,
						Task.TRANSLATE_METAMODEL);
			}
			class2sig.put(AlloyUtil.classifierKey(epackage, enu), enumSig);
			PrimSig litSig = null;
			for (EEnumLiteral lit : enu.getELiterals()) {
				try {
					litSig = new PrimSig(AlloyUtil.literalKey(epackage, lit),
							enumSig, Attr.ONE);
				} catch (Err a) {
					throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
							"Failed to create enum literal sig.", a,
							Task.TRANSLATE_METAMODEL);
				}
				literal2sig.put(lit, litSig);
			}
		}
	}
	
	/**
	 * Creates the Alloy facts managing local state fields regarding hierarchy
	 * Annexed to <code>constraint_conforms</code>
	 * If abstract, child1 + ... + childn = parent
	 * If not, child1 + ... + childn in parent
	 * @param s the parent signature beeing processed
	 * @throws ErrorAlloy
	 */
	private void processHeritage(PrimSig s) throws ErrorAlloy {
		Expr childrenUnion = Sig.NONE;
		try {
			if (!(s.children().isEmpty())) {
				for (PrimSig child : s.children())
					childrenUnion = childrenUnion.plus(sig2statefield.get(
							child).join(model_var.get()));

				if (s.isAbstract != null)
					constraint_conforms = constraint_conforms.and(childrenUnion
							.equal(sig2statefield.get(s)
									.join(model_var.get())));
				else
					constraint_conforms = constraint_conforms.and(childrenUnion
							.in(sig2statefield.get(s).join(model_var.get())));
			}
		} catch (Err err) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_GET_CHILDREN,
					"Faild to find sub-sigs", err, Task.TRANSLATE_METAMODEL);
		}
	}
	
	/**
	 * Calculates the delta {@link Func} for model elements
	 * @return the delta function
	 * @throws ErrorAlloy
	 */
	Func getDeltaSetFunc() throws ErrorAlloy {
		Decl dm, dn;
		List<Decl> ds = new ArrayList<Decl>();
		try {
			dm = sig_metamodel.oneOf("m_");
			ds.add(dm);
			dn = sig_metamodel.oneOf("n_");
			ds.add(dn);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
					"Faild to create delta function variables.", e,
					Task.TRANSLATE_METAMODEL);
		}
		ExprHasName m = dm.get(), n = dn.get();
		Expr result = PrimSig.NONE;
		for (Expr e : sig2statefield.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e
					.join(m))));
			result = result.plus(aux);
		}

		Func f;
		try {
			f = new Func(null, sig_metamodel.label, ds, PrimSig.UNIV.setOf(),
					result);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Faild to create delta function.", e,
					Task.TRANSLATE_METAMODEL);
		}

		return f;
	}
	
	/**
	 * Calculates the delta {@link Func} for model associations
	 * Optimization: container opposites are not counted (made obsolete by optimization that removed opposites altogether)
	 * @return the delta expression
	 * @throws ErrorAlloy
	 */
	Func getDeltaRelFunc() throws ErrorAlloy {
		Decl dm, dn;
		List<Decl> ds = new ArrayList<Decl>();
		try {
			dm = sig_metamodel.oneOf("m_");
			ds.add(dm);
			dn = sig_metamodel.oneOf("n_");
			ds.add(dn);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
					"Faild to create delta function variables.", e,
					Task.TRANSLATE_METAMODEL);
		}
		ExprHasName m = dm.get(), n = dn.get();
		Expr result = ExprConstant.makeNUMBER(0);

		for (Field e : feature2field.values()) {
			EStructuralFeature ref = getSFeatureFromField(e);
			if (!(EchoOptionsSetup.getInstance().isOptimize()
					&& ref instanceof EReference
					&& ((EReference) ref).getEOpposite() != null && ((EReference) ref)
					.getEOpposite().isContainment())) {
				Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n))
						.minus(e.join(m)))).cardinality();
				result = result.iplus(aux);
			}
		}
		Func f;
		try {
			f = new Func(null, sig_metamodel.label, ds, PrimSig.SIGINT, result);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Faild to create delta function.", e,
					Task.TRANSLATE_METAMODEL);
		}
		return f;
	}
	
	/** 
	 * creates the total order over states defined by the defined operations
	 * @throws ErrorAlloy
	 */
	void createOrder() throws ErrorAlloy {
		PrimSig ord;
		Field next, first;
		try {
			ord = new PrimSig(AlloyUtil.ORDNAME, Attr.ONE);
			first = ord.addField("first", sig_metamodel.setOf());
			next = ord.addField("next", sig_metamodel.product(sig_metamodel));
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_SIG,
					"Faild to create order sig.", e, Task.TRANSLATE_METAMODEL);
		}
		Expr ops = Sig.NONE.some();
		try {
			Decl s1 = sig_metamodel.oneOf("m_"), s2 = (s1.get().join(ord
					.join(next))).oneOf("n_");
			for (Func fun : operations) {
				List<Decl> decls = new ArrayList<Decl>();
				List<ExprHasName> vars = new ArrayList<ExprHasName>();
				for (int i = 0; i < fun.decls.size() - 2; i++) {
					Decl d = fun.decls.get(i).get().type().toExpr()
							.oneOf(fun.decls.get(i).names.get(0).label);
					decls.add(d);
					vars.add(d.get());
				}
				vars.add(s1.get());
				vars.add(s2.get());

				Expr aux = fun.call(vars.toArray(new Expr[vars.size()]));
				Decl fst = decls.get(0);
				decls.remove(0);
				aux = aux.forSome(fst, decls.toArray(new Decl[decls.size()]));
				ops = ops.or(aux);
			}
			ops = ops.forAll(s1, s2);
		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
					"Faild to create order constraint variable.", e,
					Task.TRANSLATE_METAMODEL);
		}
		try {
			ord.addFact(ops);
			List<Expr> x = new ArrayList<Expr>();
			x.add(sig_metamodel);
			x.add(ord.join(first));
			x.add(ord.join(next));
			ord.addFact(ExprList.makeTOTALORDER(null, null, x));

		} catch (Err e) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FACT,
					"Faild to create order constraint.", e,
					Task.TRANSLATE_METAMODEL);
		}

		sig_order = ord;
	}
	
	public Map<String,Integer> getCreationCount() {
		return elem_creation_count;
	}
	
	/** calculates all possible root classes for this meta-model
	 * root classes are those classes not contained in any container reference
	 * @return the list of root classes
	 */
	List<EClass> getRootClass() {
		Map<Integer, EClass> classes = new HashMap<Integer, EClass>();
		for (EClassifier obj : epackage.getEClassifiers())
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

