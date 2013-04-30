package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class ECore2Alloy {

	/** the package being translated */
	public final EPackage epackage;
	/** the signature matching this metamodel */
	public final PrimSig statesig;
	/** the parent EMF translator */
	public final EMF2Alloy translator;
	
	/** maps classes into respective Alloy signature */
	private BiMap<EClass,PrimSig> mapClassSig;
	/** maps structural features into the respective Alloy field */
	private BiMap<EStructuralFeature,Field> mapSfField;
	/** maps a signature name into its respective class */
	private Map<String,EClass> mapClassClass = new HashMap<String,EClass>();
	/** maps a literal into its Alloy signature */
	/* private Map<EEnumLiteral,PrimSig> mapLitSig = new HashMap<EEnumLiteral,PrimSig>(); */
	/** maps a signature into its state field */
	private Map<PrimSig,Field> mapSigState = new HashMap<PrimSig,Field>();
	/** list of Alloy functions resulting from operation translation */
	private List<Func> functions = new ArrayList<Func>();
	
	
	/**
	 * Creates a translator from meta-models (represented by an EPackage) to Alloy artifacts
	 * @param pck the package to translate
	 * @param statesig the state signature representing the metamodel
	 * @param translator the parent translator
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public ECore2Alloy(EPackage pck, PrimSig statesig, EMF2Alloy translator) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		mapSfField = HashBiMap.create();
		mapClassSig = HashBiMap.create();
		this.translator = translator;
		this.statesig = statesig;
		epackage = pck;
	}

	/**
	 * Translates the information from the this.epackage (classes, attributes, references, annotations, operations)
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void translate() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser{
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
		
		//processEEnum(enumList);
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
		if(superTypes.size() > 1) throw new ErrorTransform("Multiple inheritance not allowed: "+ec.getName()+".","ECore2Alloy");
		if(!superTypes.isEmpty()) {
			parent = mapClassSig.get(superTypes.get(0));
			if(parent == null) throw new ErrorTransform("Parent class not found: "+superTypes.get(0).getName()+".","ECore2Alloy");	
		}
		String signame = AlloyUtil.pckPrefix(epackage.getName(),ec.getName());
		try {
			if(ec.isAbstract()) ecsig = new PrimSig(signame,parent,Attr.ABSTRACT);
			else ecsig = new PrimSig(signame,parent);
			statefield = ecsig.addField(AlloyUtil.stateFieldName(epackage,ec),statesig.setOf());
			Expr stateatoms = ecsig.equal(statefield.join(statesig));
			ecsig.addFact(stateatoms);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy");}	
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
				} catch (Err a) { throw new ErrorAlloy(a.getMessage(),"ECore2Alloy"); }
				mapSfField.put(attr,field);					
			} else if(attr.getEType().getName().equals("EString")) {
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),Sig.STRING.product(statesig));
					fact = field.join(statesig.decl.get());
					Expr bound = mapSigState.get(classsig).join(statesig.decl.get()).any_arrow_one(Sig.STRING);
					fact = fact.in(bound);
					fact = fact.forAll(statesig.decl);
					classsig.addFact(fact);
				} catch (Err a) { throw new ErrorAlloy(a.getMessage(),"ECore2Alloy"); }
				mapSfField.put(attr,field);
			} else if(attr.getEType().getName().equals("EInt")) {
				try {
					field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),Sig.SIGINT.product(statesig));
					fact = field.join(statesig.decl.get());
					Expr bound = mapSigState.get(classsig).join(statesig.decl.get()).any_arrow_one(Sig.SIGINT);
					fact = fact.in(bound);
					fact = fact.forAll(statesig.decl);
					classsig.addFact(fact);
				} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
				mapSfField.put(attr,field);
			} 
			/*else if (attr.getEType() instanceof EEnum) {
			PrimSig sigType = mapClassSig.get(attr.getEType());
			try {
				field = classsig.addField(AlloyUtil.pckPrefix(epackage.getName(),attr.getName()),sigType.product(statesig));
				fact = field.join(statesig.decl.get());
				Expr bound = mapSigState.get(classsig).join(statesig.decl.get()).any_arrow_one(sigType);
				fact = fact.in(bound);
				fact = fact.forAll(statesig.decl);
				classsig.addFact(fact);				
			} catch (Err a) { throw new ErrorAlloy(a.getMessage(),"ECore2Alloy"); }
			mapSfField.put(attr,field);
			} */
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
				Decl s = statesig.decl;

				EReference op = reference.getEOpposite();
				if(op!=null) {
					Field opField = getFieldFromSFeature(op);
					if(opField != null)
						try{classsig.addFact(field.join(s.get()).equal(opField.join(s.get()).transpose()).forAll(statesig.decl));}
						catch (Err a) {throw new ErrorAlloy (a.getMessage());}
				}

				Expr fact;
				try{
					Decl d = AlloyUtil.localStateSig(classsig,s.get()).oneOf("src_");
					if (reference.getLowerBound() == 1 && reference.getUpperBound() == 1) {
						fact = (d.get()).join(field.join(s.get())).one().forAll(s,d);
						classsig.addFact(fact);	
					} else if (reference.getLowerBound() == 0 && reference.getUpperBound() == 1) {
						fact = (d.get()).join(field.join(s.get())).lone().forAll(s,d);
						classsig.addFact(fact);	
					} else if (reference.getLowerBound() == 1 && reference.getUpperBound() == -1) {
						fact = (d.get()).join(field.join(s.get())).some().forAll(s,d);
						classsig.addFact(fact);	
					} else if (reference.getUpperBound() == 0) {
						fact = (d.get()).join(field.join(s.get())).no().forAll(s,d);
						classsig.addFact(fact);	
					} else if (reference.getLowerBound() == 0 && reference.getUpperBound() == -1) {}
					if(reference.getLowerBound() > 1) {
						fact = (d.get()).join(field.join(s.get())).cardinality().gte(ExprConstant.makeNUMBER(reference.getLowerBound())).forAll(s,d);
						classsig.addFact(fact);
					}
					if(reference.getUpperBound() != -1){
						fact = (d.get()).join(field.join(s.get())).cardinality().lte(ExprConstant.makeNUMBER(reference.getUpperBound())).forAll(s,d);
						classsig.addFact(fact);
					}
					
					if(reference.isContainment()){
						d = AlloyUtil.localStateSig(trgsig,s.get()).oneOf("trg_");
						fact = ((field.join(s.get())).join(d.get())).one().forAll(s,d);
						trgsig.addFact(fact);
					}
					
					Expr parState = mapSigState.get(classsig);
					Expr sTypeState = mapSigState.get(trgsig);
					classsig.addFact(field.join(s.get()).in(parState.join(s.get()).product(sTypeState.join(s.get()))).forAll(statesig.decl));
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
			sd.add(statesig.decl);

			Map<String,ExprHasName> statevars = new HashMap<String, ExprHasName>();
			statevars.put(statesig.label,statesig.decl.get());
			OCL2Alloy converter = new OCL2Alloy(translator,sd,statevars,null);
			
			if(annotation.getSource().equals("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot"))
				try{
					for(String sExpr: annotation.getDetails().values()) {
						ExpressionInOCL invariant = helper.createInvariant(sExpr);
						Expr oclalloy = converter.oclExprToAlloy(invariant.getBodyExpression()).forAll(self, statesig.decl);
						classsig.addFact(oclalloy);
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
				pre = statesig.oneOf("pre");
				pos = statesig.oneOf("pos");
				decls.add(self);
				for (EParameter p : operation.getEParameters()) {
					PrimSig type = translator.getClassifierFromSig(p.getEType());
					Decl d = type.oneOf(p.getName());
					decls.add(d);
				}
				decls.add(pre);
				decls.add(pos);
			} catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",classsig);}
			OCLHelper helper = ocl.createOCLHelper(operation);
			Map<String,ExprHasName> prestatevars = new HashMap<String, ExprHasName>();
			Map<String,ExprHasName> posstatevars = new HashMap<String, ExprHasName>();
			prestatevars.put(statesig.label,pre.get());
			posstatevars.put(statesig.label,pos.get());
			OCL2Alloy converter = new OCL2Alloy(translator,new HashSet<Decl>(decls),posstatevars,prestatevars);
			for (EAnnotation ea : operation.getEAnnotations())
				if(ea.getSource().equals("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot"))
					try{
						for(String sExpr: ea.getDetails().values()) {
							System.out.println("Operation: "+sExpr);
							ExpressionInOCL invariant = helper.createPostcondition(sExpr);
							Expr oclalloy = converter.oclExprToAlloy(invariant.getBodyExpression()).forAll(self);
							decls.remove(self);
							Func fun = new Func(null,operation.getName(),decls,null,oclalloy);
							functions.add(fun);
						}
					} catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",classsig);} 
					  catch (ParserException e) { throw new ErrorParser(e.getMessage(),"ECore2Alloy");}
		}
	}
		
	/*private void processEEnumLiterals(List<EEnumLiteral> el,PrimSig parent) throws ErrorAlloy 
	{
		PrimSig litSig = null;
		for(EEnumLiteral lit: el)
		{
			try { litSig = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),lit.getLiteral()),parent,Attr.ONE); }
			catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",litSig);}
			mapLitSig.put(lit, litSig);
			sigList.add(litSig);
		}
	}

	private void processEEnum(List<EEnum> list) throws ErrorAlloy 
	{
		PrimSig enumSig = null;
		for(EEnum en: list)
		{
			try{ enumSig = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),en.getName()),Attr.ABSTRACT);}
			catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",enumSig);}
			sigList.add(enumSig);
			mapClassSig.put(en, enumSig);
			//mapSigState.put(enumSig, enumSig.addField(prefix + en.getName().toLowerCase(),state.setOf()));
			processEEnumLiterals(en.getELiterals(),enumSig);
		}
	}*/
	
	/**
	 * Calculates the delta {@link Expr} for particular state {@link PrimSig}
	 * @param m the pre state signature
	 * @param n the post state signature
	 * @return the delta expression
	 * @throws ErrorAlloy
	 */
	public Expr getDeltaExpr(PrimSig m, PrimSig n) throws ErrorAlloy{
		Expr result = ExprConstant.makeNUMBER(0);
		for (Expr e : mapSigState.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
			result = result.iplus(aux);
		}
		for (Field e : mapSfField.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
			result = result.iplus(aux);
		}
		return result;
	}
	
	/** 
	 * Returns the Alloy {@link Field} matching a {@link EStructuralFeature}
	 * @param f the desired feature
	 * @return the matching Alloy field
	 */
	public Field getFieldFromSFeature(EStructuralFeature f) {
		return mapSfField.get(f);
	}
	
	/** 
	 * Returns the {@link EStructuralFeature} matching an Alloy {@link Field}
	 * @param f the Alloy field
 	 * @return the matching feature
	 */
	public EStructuralFeature getSFeatureFromField(Field f)	{
		return mapSfField.inverse().get(f);
	}
	
	/**
	 * Returns the {@link EStructuralFeature} matching the class and feature names
	 * @param ref the name of the feature
	 * @param cla the name of the class
 	 * @return the matching feature
	 */
	public EStructuralFeature getSFeatureFromName(String ref, String cla) {
		EClass eclass = mapClassClass.get(cla);
		return eclass.getEStructuralFeature(ref);
	}
	
	/**
	 * Returns all {@link EStructuralFeature} of this metamodel
	 * @return the features
	 */
	public Set<EStructuralFeature> getSFeatures() {
		return mapSfField.keySet();
	}

	/**
	 * Returns the {@link EClass} matching the class  name
	 * @param s the class name
	 * @return the matching class
	 */
	public EClass getEClassFromName(String s) {
		return mapClassClass.get(s);
	}

	/** 
	 * Returns the {@link EClass} matching an Alloy {@link PrimSig}
	 * @param s the Alloy signature
 	 * @return the matching class
	 */
	public EClass getEClassFromSig(PrimSig s) {
		return mapClassSig.inverse().get(s);
	}

	/** 
	 * Returns the Alloy {@link PrimSig} matching a {@link EClass}
	 * @param c the class
 	 * @return the matching Alloy signature
	 */
	public PrimSig getSigFromEClass(EClass c) {
		return mapClassSig.get(c);
	}

	/** 
	 * Returns the state {@link Field} representing a {@link PrimSig}
	 * @param s the signature
 	 * @return the state field
	 */
	public Field getStateFieldFromSig(PrimSig s) {
		return mapSigState.get(s);
	}
	
	/**
	 * Returns all {@link PrimSig} of this metamodel
	 * @return the signatures
	 */
	public List<PrimSig> getSigList() {
		return new ArrayList<PrimSig>(mapClassSig.values());
	}

	/*public PrimSig getSigFromEEnumLiteral(EEnumLiteral e)
	{
		return mapLitSig.get(e);
	}*/
	
}

