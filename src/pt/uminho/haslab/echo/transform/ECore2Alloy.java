package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;

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
	/** the signature matching this meta-model */
	final PrimSig statesig;
	/** the parent EMF translator */
	final EMF2Alloy translator;
	
	/** maps classes into respective Alloy signatures */
	private BiMap<EClassifier,PrimSig> mapClassSig;
	/** maps structural features into respective Alloy fields */
	private BiMap<EStructuralFeature,Field> mapSfField;
	/** maps signature names into respective classes */
	private Map<String,EClass> mapClassClass = new HashMap<String,EClass>();
	/** maps literals into respective Alloy signature */
	private BiMap<EEnumLiteral,PrimSig> mapLitSig;
	/** maps signatures into respective state fields */
	private Map<PrimSig,Field> mapSigState = new HashMap<PrimSig,Field>();
	/** list of Alloy functions resulting from operation translation */
	private List<Func> functions = new ArrayList<Func>();
	/** the constraint representing the conformity test */
	private Expr constraint = Sig.NONE.no();
	/** the variable declaration for the conformity test */
	private Decl constraintdecl;
	
	private PrimSig order;
	private Map<String,Integer> news = new HashMap<String,Integer>();

	
	/**
	 * Creates a translator from meta-models (represented by an EPackage) to Alloy artifacts
	 * @param pck the package to translate
	 * @param statesig the state signature representing the meta-model
	 * @param translator the parent translator
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	ECore2Alloy(EPackage pck, PrimSig statesig, EMF2Alloy translator) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		mapSfField = HashBiMap.create();
		mapClassSig = HashBiMap.create();
		mapLitSig = HashBiMap.create();
		this.translator = translator;
		this.statesig = statesig;
		epackage = pck;
		try{
			constraintdecl = statesig.oneOf("s_");
		} catch (Err a) { throw new ErrorAlloy (a.getMessage());}
	}

	/**
	 * Translates the information from the this.epackage (classes, attributes, references, annotations, operations, eenums)
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	void translate() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser{
		List<EClass> classList = new LinkedList<EClass>();
		List<EDataType> dataList = new ArrayList<EDataType>();
		List<EEnum> enumList = new ArrayList<EEnum>();
		
		for(EClassifier e: epackage.getEClassifiers()) {
			if (e instanceof EClass)
				classList.add((EClass)e);
			else if (e instanceof EEnum)
				enumList.add((EEnum) e);
			else if (e instanceof EDataType)
				dataList.add((EDataType) e);
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
		
		if (translator.options.isOperationBased()) createOrder(statesig);
		
	}
	
	/**
	 * Translates an {@link EClass}
	 * New sigs: the signature representing the class
	 * New fields: the state field of the signature
	 * New facts: all elements must belong to the state field
	 * @param ec the EClass to translate
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 */
	private void processClass(EClass ec) throws  ErrorAlloy, ErrorTransform {
		PrimSig ecsig,parent = null;
		Field statefield;
		List<EClass> superTypes = ec.getESuperTypes();
		if(superTypes.size() > 1) throw new ErrorTransform("Multiple inheritance not allowed: "+ec.getName()+".");
		if(!superTypes.isEmpty()) {
			parent = mapClassSig.get(superTypes.get(0));
			if(parent == null) throw new ErrorTransform("Parent class not found: "+superTypes.get(0).getName()+".");	
		}
		String signame = AlloyUtil.pckPrefix(epackage.getName(),ec.getName());
		try {
			if(ec.isAbstract()) ecsig = new PrimSig(signame,parent,Attr.ABSTRACT);
			else ecsig = new PrimSig(signame,parent);
			statefield = ecsig.addField(AlloyUtil.stateFieldName(epackage,ec),statesig.setOf());
			Expr stateatoms = ecsig.equal(statefield.join(statesig));
			ecsig.addFact(stateatoms);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}	
		mapSigState.put(ecsig,statefield);
		mapClassSig.put(ec, ecsig);
		mapClassClass.put(ec.getName(), ec);
	}
	
	/**
	 * Translates a list of {@link EAttribute}
	 * New fields: unary field if EBoolean
	 * New fields: binary field if EInt or EString
	 * New facts: multiplicity constraints for binary fields
	 * @param attributes the list of attributes to translate
	 * @throws ErrorUnsupported the attribute type is not supported
	 * @throws ErrorAlloy
	 */
	private void processAttributes(List<EAttribute> attributes) throws ErrorUnsupported, ErrorAlloy {
		Field field = null;
		Expr fact = null;
		for(EAttribute attr : attributes) {
			PrimSig classsig = mapClassSig.get(attr.getEContainingClass());

			if(attr.getEType().getName().equals("EBoolean")) {
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),statesig.setOf());
				} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
				mapSfField.put(attr,field);					
			} else if(attr.getEType().getName().equals("EString")) {
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),Sig.STRING.product(statesig));
					fact = field.join(constraintdecl.get());
					Expr bound = mapSigState.get(classsig).join(constraintdecl.get()).any_arrow_one(Sig.STRING);
					fact = fact.in(bound);
					constraint = constraint.and(fact);
				} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
				mapSfField.put(attr,field);
			} else if(attr.getEType().getName().equals("EInt")) {
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),Sig.SIGINT.product(statesig));
					fact = field.join(constraintdecl.get());
					Expr bound = mapSigState.get(classsig).join(constraintdecl.get()).any_arrow_one(Sig.SIGINT);
					fact = fact.in(bound);
					constraint = constraint.and(fact);
				} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
				mapSfField.put(attr,field);
			} 
			else if (attr.getEType() instanceof EEnum) {
				PrimSig sigType = mapClassSig.get(attr.getEType());
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),sigType.product(statesig));
					fact = field.join(constraintdecl.get());
					Expr bound = mapSigState.get(classsig).join(constraintdecl.get()).any_arrow_one(sigType);
					fact = fact.in(bound);
					constraint = constraint.and(fact);
				} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
				mapSfField.put(attr,field);
			} 
			else throw new ErrorUnsupported("Primitive type for attribute not supported: "+attr+".");
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
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 */
	private void processReferences(List<EReference> references) throws ErrorAlloy, ErrorTransform {
		for(EReference reference : references) {
			PrimSig classsig = mapClassSig.get(reference.getEContainingClass());
			if(!(reference.getEOpposite() != null && reference.getEOpposite().isContainment() && translator.options.isOptimize())) {
				PrimSig trgsig = mapClassSig.get(reference.getEReferenceType());
				Field field;
				try{field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),reference.getName()),trgsig.product(statesig));}
				catch (Err a) {throw new ErrorAlloy (a.getMessage());}
				mapSfField.put(reference,field);

				EReference op = reference.getEOpposite();
				Expr fact;
				if(op!=null) {
					Field opField = getFieldFromSFeature(op);
					if(opField != null) {
						try { 
							Decl d = statesig.oneOf("s_");
							fact = (field.join(d.get()).equal(opField.join(d.get()).transpose())).forAll(d);
							classsig.addFact(fact); 
						} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
					}
				}

				Integer bitwidth = translator.options.getBitwidth();
				Integer max = (int) (Math.pow(2, bitwidth) / 2);
				if (reference.getLowerBound() >= max || reference.getLowerBound() < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+reference.getLowerBound()+".");
				if (reference.getUpperBound() >= max || reference.getUpperBound() < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+reference.getUpperBound()+".");
				
				try{
					Decl d = AlloyUtil.localStateSig(classsig,constraintdecl.get()).oneOf("src_");
					if (reference.getLowerBound() == 1 && reference.getUpperBound() == 1) {
						fact = (d.get()).join(field.join(constraintdecl.get())).one().forAll(d);
						constraint = constraint.and(fact);
					} else if (reference.getLowerBound() == 0 && reference.getUpperBound() == 1) {
						fact = (d.get()).join(field.join(constraintdecl.get())).lone().forAll(d);
						constraint = constraint.and(fact);
					} else if (reference.getLowerBound() == 1 && reference.getUpperBound() == -1) {
						fact = (d.get()).join(field.join(constraintdecl.get())).some().forAll(d);
						constraint = constraint.and(fact);
					} else if (reference.getUpperBound() == 0) {
						fact = (d.get()).join(field.join(constraintdecl.get())).no().forAll(d);
						constraint = constraint.and(fact);
					} else if (reference.getLowerBound() == 0 && reference.getUpperBound() == -1) {}
					else {
						if(reference.getLowerBound() > 1) {
							fact = (d.get()).join(field.join(constraintdecl.get())).cardinality().gte(ExprConstant.makeNUMBER(reference.getLowerBound())).forAll(d);
							constraint = constraint.and(fact);
						}
						if(reference.getUpperBound() > 1){
							fact = (d.get()).join(field.join(constraintdecl.get())).cardinality().lte(ExprConstant.makeNUMBER(reference.getUpperBound())).forAll(d);
							constraint = constraint.and(fact);
						}
					}
					
					if(reference.isContainment()){
						d = AlloyUtil.localStateSig(trgsig,constraintdecl.get()).oneOf("trg_");
						fact = ((field.join(constraintdecl.get())).join(d.get())).one().forAll(d);
						constraint = constraint.and(fact);
					}
					
					Expr parState = mapSigState.get(classsig);
					Expr sTypeState = mapSigState.get(trgsig);
					fact = field.join(constraintdecl.get()).in(parState.join(constraintdecl.get()).product(sTypeState.join(constraintdecl.get())));
					constraint = constraint.and(fact);

				} catch (Err a) {throw new ErrorAlloy (a.getMessage());}		
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
	private void processAnnotations(List<EAnnotation> annotations) throws ErrorTransform, ErrorAlloy, ErrorUnsupported, ErrorParser {
		OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
		for(EAnnotation annotation : annotations) {
			Decl self = null;
			OCLHelper helper = ocl.createOCLHelper(annotation.eContainer());
			Set<Decl> sd = new HashSet<Decl>();
			PrimSig classsig = mapClassSig.get(annotation.eContainer());
			try{
				self = classsig.oneOf("self");
			} catch (Err a) {throw new ErrorAlloy(a.getMessage());}
			sd.add(self);
			sd.add(constraintdecl);

			Map<String,List<ExprHasName>> statevars = new HashMap<String,List<ExprHasName>>();
			statevars.put(statesig.label,new ArrayList<ExprHasName>());
			statevars.get(statesig.label).add(constraintdecl.get());
			
			OCL2Alloy converter = new OCL2Alloy(translator,sd,statevars,null);
			
			if(annotation.getSource().equals("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot"))
				try{
					for(String sExpr: annotation.getDetails().values()) {
						ExpressionInOCL invariant = helper.createInvariant(sExpr);
						Expr oclalloy = converter.oclExprToAlloy(invariant.getBodyExpression()).forAll(self);
						constraint = constraint.and(oclalloy);
					}
				} catch (Err a) {throw new ErrorAlloy(a.getMessage());} 
				  catch (ParserException e) { throw new ErrorParser(e.getMessage());}
		}
	}
	
	/**
	 * Translates a list of {@link EOperation} specified by OCL formulas
	 * New function: the predicate representing the operation
	 * @param operations the operations to translate
	 * @throws ErrorTransform the OCL translation failed
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported the OCL formulas contains unsupported operators
	 * @throws ErrorParser the OCL parser failed
	 */
	private void processOperations(List<EOperation> operations) throws ErrorTransform, ErrorAlloy, ErrorUnsupported, ErrorParser {
		OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
		for(EOperation operation : operations) {
			PrimSig classsig = mapClassSig.get(operation.getEContainingClass());
			List<Decl> decls = new ArrayList<Decl>();
			Decl pre,pos,self = null;
			try{
				self = classsig.oneOf("self");
				pre = statesig.oneOf("pre_");
				pos = statesig.oneOf("pos_");
				decls.add(self);
				for (EParameter p : operation.getEParameters()) {
					PrimSig type = translator.getClassifierFromSig(p.getEType());
					Decl d = type.oneOf(p.getName());
					decls.add(d);
				}
				decls.add(pre);
				decls.add(pos);
			} catch (Err a) {throw new ErrorAlloy(a.getMessage());}
			OCLHelper helper = ocl.createOCLHelper(operation);
			Map<String,List<ExprHasName>> prestatevars = new HashMap<String,List<ExprHasName>>();
			Map<String,List<ExprHasName>> posstatevars = new HashMap<String,List<ExprHasName>>();
			prestatevars.put(statesig.label,new ArrayList<ExprHasName>());
			posstatevars.put(statesig.label,new ArrayList<ExprHasName>());
			prestatevars.get(statesig.label).add(pre.get());
			posstatevars.get(statesig.label).add(pos.get());
			OCL2Alloy converter = new OCL2Alloy(translator,new HashSet<Decl>(decls),posstatevars,prestatevars);
			for (EAnnotation ea : operation.getEAnnotations())
				if(ea.getSource().equals("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot")) {
					Expr oclalloy = Sig.NONE.no();
					for(String sExpr: ea.getDetails().values()) {
						try{
							ExpressionInOCL invariant = helper.createPostcondition(sExpr);
							oclalloy = oclalloy.and(converter.oclExprToAlloy(invariant.getBodyExpression()));
						} catch (ParserException e) { throw new ErrorParser("Error parsing OCL formula: "+sExpr);}
					}
					try{
						System.out.println(oclalloy);
						Func fun = new Func(null,operation.getName(),decls,null,oclalloy);
						functions.add(fun);
					} catch (Err a) {throw new ErrorAlloy(a.getMessage());} 
				}
			
			for (String cl : converter.getOCLAreNews().keySet()) {
				Integer newi = news.get(cl);
				if (newi == null) news.put(cl,converter.getOCLAreNews().get(cl));
				else news.put(cl,newi+converter.getOCLAreNews().get(cl));
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
		for(EEnum enu: enums) {
			try{ 
				enumSig = new PrimSig(AlloyUtil.pckPrefix(epackage.getName(),enu.getName()),Attr.ABSTRACT);
			} catch (Err a) {throw new ErrorAlloy(a.getMessage());}
			mapClassSig.put(enu, enumSig);
			PrimSig litSig = null;
			for(EEnumLiteral lit : enu.getELiterals()) {
				try { 
					litSig = new PrimSig(AlloyUtil.pckPrefix(epackage.getName(),lit.getLiteral()),enumSig,Attr.ONE); 
				} catch (Err a) {throw new ErrorAlloy(a.getMessage());}
				mapLitSig.put(lit, litSig);
			}		
		}
	}
	
	/**
	 * Calculates the delta {@link Expr} for particular state {@link PrimSig}
	 * Optimization: container opposites are not counted (made obsolete by optimization that removed opposites altogether)
	 * @param m the pre state signature
	 * @param n the post state signature
	 * @return the delta expression
	 * @throws ErrorAlloy
	 */
	Expr getDeltaExpr(PrimSig m, PrimSig n) throws ErrorAlloy{
		Expr result = ExprConstant.makeNUMBER(0);
		for (Expr e : mapSigState.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
			result = result.iplus(aux);
		}
		for (Field e : mapSfField.values()) {
			EStructuralFeature ref = mapSfField.inverse().get(e);
			if (!(translator.options.isOptimize() && mapSfField.inverse().get(e) instanceof EReference &&
				((EReference) ref).getEOpposite() != null && ((EReference) ref).getEOpposite().isContainment())) {
				Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
				result = result.iplus(aux);
			}
		}
		return result;
	}
	
	/** 
	 * Returns the Alloy {@link Field} matching a {@link EStructuralFeature}
	 * @param f the desired feature
	 * @return the matching Alloy field
	 */
	Field getFieldFromSFeature(EStructuralFeature f) {
		return mapSfField.get(f);
	}
	
	/** 
	 * Returns the {@link EStructuralFeature} matching an Alloy {@link Field}
	 * @param f the Alloy field
 	 * @return the matching feature
	 */
	EStructuralFeature getSFeatureFromField(Field f)	{
		return mapSfField.inverse().get(f);
	}
	
	/**
	 * Returns the {@link EStructuralFeature} matching the class and feature names
	 * @param ref the name of the feature
	 * @param cla the name of the class
 	 * @return the matching feature
	 */
	EStructuralFeature getSFeatureFromName(String ref, String cla) {
		EClass eclass = mapClassClass.get(cla);
		return eclass.getEStructuralFeature(ref);
	}
	
	/**
	 * Returns all {@link EStructuralFeature} of this meta-model
	 * @return the features
	 */
	Set<EStructuralFeature> getSFeatures() {
		return mapSfField.keySet();
	}

	/**
	 * Returns the {@link EClass} matching the class  name
	 * @param s the class name
	 * @return the matching class
	 */
	EClass getEClassFromName(String s) {
		return mapClassClass.get(s);
	}

	/** 
	 * Returns the {@link EClass} matching an Alloy {@link PrimSig}
	 * @param s the Alloy signature
 	 * @return the matching class
	 */
	EClassifier getEClassFromSig(PrimSig s) {
		return mapClassSig.inverse().get(s);
	}

	/** 
	 * Returns the Alloy {@link PrimSig} matching a {@link EClass}
	 * @param c the class
 	 * @return the matching Alloy signature
	 */
	PrimSig getSigFromEClass(EClass c) {
		return mapClassSig.get(c);
	}

	/** 
	 * Returns the state {@link Field} representing a {@link PrimSig}
	 * @param s the signature
 	 * @return the state field
	 */
	Field getStateFieldFromSig(PrimSig s) {
		return mapSigState.get(s);
	}
	
	/**
	 * Returns class {@link PrimSig} of this meta-model
	 * @return the signatures
	 */
	List<PrimSig> getClassSigs() {
		List<PrimSig> aux = new ArrayList<PrimSig>();
		for (EClassifier c : mapClassSig.keySet())
			if (c instanceof EClass) aux.add(mapClassSig.get(c));
		return aux;
	}
	
	/**
	 * Returns enum {@link PrimSig} of this meta-model
	 * @return the signatures
	 */
	List<PrimSig> getEnumSigs() {
		List<PrimSig> aux = new ArrayList<PrimSig>();
		for (EClassifier c : mapClassSig.keySet())
			if (c instanceof EEnum) aux.add(mapClassSig.get(c));
		aux.addAll(mapLitSig.values());
		return aux;
	}

	/**
	 * Returns all {@link PrimSig} of this meta-model
	 * @return the signatures
	 * @throws ErrorAlloy 
	 */
	List<PrimSig> getAllSigs() throws ErrorAlloy {
		List<PrimSig> aux = new ArrayList<PrimSig>(mapClassSig.values());
		aux.addAll(mapLitSig.values());
		if (translator.options.isOperationBased()) aux.add(order);
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
			f = new Func(null, epackage.getName(), new ArrayList<Decl>(Arrays.asList(constraintdecl)), null, constraint);
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
		return f;
	}

	/**
	 * Returns the Alloy {@link PrimSig} representing an {@link EEnumLiteral} 
	 * @return the matching signature
	 */
	PrimSig getSigFromEEnumLiteral(EEnumLiteral e) {
		return mapLitSig.get(e);
	}
	
	/**
	 * Returns the {@link EEnumLiteral} represented by an Alloy {@link PrimSig}
	 * @return the matching enum literal
	 */
	EEnumLiteral getEEnumLiteralFromSig(PrimSig s) {
		return mapLitSig.inverse().get(s);
	}
	
	void createOrder(PrimSig sig) throws ErrorAlloy {
		PrimSig ord; Field next,first;
		try {
			ord = new PrimSig("ord", Attr.ONE);
			first = ord.addField("first", sig.setOf());
			next = ord.addField("next", sig.product(sig));
		} catch (Err e) { throw new ErrorAlloy(e.getMessage());	}
		try {
			Decl s1 = sig.oneOf("m_"),s2 = (s1.get().join(ord.join(next))).oneOf("n_");
			Expr ops = Sig.NONE.some();
			for (Func fun : functions) {
				List<Decl> decls = new ArrayList<Decl>();
				List<ExprHasName> vars = new ArrayList<ExprHasName>();
				for (int i = 0; i < fun.decls.size() -2; i ++) {
					Decl d = fun.decls.get(i).get().type().toExpr().oneOf(fun.decls.get(i).names.get(0).label);
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
			//System.out.println("ORD: "+ops);
			ord.addFact(ops);
			List<Expr> x = new ArrayList<Expr>();
			x.add(sig); x.add(ord.join(first)); x.add(ord.join(next));
			ord.addFact(ExprList.makeTOTALORDER(null, null, x));
			
		} catch (Err e) { throw new ErrorAlloy(e.getMessage());	}

		order = ord;
	}
	
	public Map<String,Integer> getOCLAreNews() {
		return news;
	}
	
}

