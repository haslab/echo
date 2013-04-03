package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.ExpressionInOCL;
import org.eclipse.ocl.examples.pivot.OCL;
import org.eclipse.ocl.examples.pivot.ParserException;
import org.eclipse.ocl.examples.pivot.helper.OCLHelper;
import org.eclipse.ocl.examples.pivot.utilities.PivotEnvironmentFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class ECore2Alloy {

	/** maps class names into respective Alloy signature */
	private Map<String,PrimSig> mapClassSig = new HashMap<String,PrimSig>();
	/** maps a structural feature into the respective Alloy field */
	private BiMap<EStructuralFeature,Field> mapSfField;
	/** maps a signature name into its respective class */
	private Map<String,EClass> mapClassClass = new HashMap<String,EClass>();
	/** maps a literal into its Alloy signature */
	private Map<EEnumLiteral,PrimSig> mapLitSig = new HashMap<EEnumLiteral,PrimSig>();
	/** maps a signature into its state field */
	private Map<PrimSig,Field> mapSigState = new HashMap<PrimSig,Field>();

	private final EPackage pack;
	private final PrimSig state;
	private List<PrimSig> sigList;
	
	private EMF2Alloy translator;
	
	public ECore2Alloy(EPackage p, PrimSig statesig, EMF2Alloy translator) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		mapSfField = HashBiMap.create();
		this.translator = translator;
		state = statesig;
		pack = p;
	}
	
	public void translate() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser{
		sigList = makeSigList();
	}
	
	public PrimSig getState()
	{
		return state;
	}
	
	public EPackage getEPackage(){
		return pack;
	}
	/** Given a StructuralFeature, returns the respective Alloy field created by the translator
	 * 
	 * @param f the desired StructuralFeature
	 * @return the matching Alloy field
	 */
	public Field getFieldFromSFeature(EStructuralFeature f)
	{
		return mapSfField.get(f);
	}
	
	public EStructuralFeature getSFeatureFromField(Field f)
	{
		return mapSfField.inverse().get(f);
	}
	
	public Set<EStructuralFeature> getSFeatures()
	{
		return mapSfField.keySet();
	}

	public EClass getEClassFromSig(PrimSig s)
	{
		return mapClassClass.get(s.label);
	}

	public PrimSig getSigFromEClass(EClass c)
	{
		return mapClassSig.get(c.getName());
	}

	public Field getStateFieldFromSig(PrimSig s)
	{
		return mapSigState.get(s);
	}

	public Collection<Field> getStateFields()
	{
		return mapSigState.values();
	}

	public PrimSig getSigFromEEnumLiteral(EEnumLiteral e)
	{
		return mapLitSig.get(e);
	}

	
	public List<PrimSig> getSigList()
	{
		return new ArrayList<PrimSig>(sigList);
	}
	
	
	/*
	private void processAttribute(EAttribute attr,PrimSig ec) throws Err
	{
		EClassifier type = attr.getEType();
		PrimSig sigType = mapClassSig.get(type);
		if(sigType == null)
		{
			sigType =  new PrimSig(prefix + type.getName());
			mapClassSig.put(type, sigType);
			sigList.add(sigType);
			mapSigState.put(sigType,sigType.addField(prefix+type.getName().toLowerCase(), state.setOf()));
		}
		Expr field = ec.addField(prefix + attr.getName(),sigType.product(state));
		mapSfField.put(attr,field);
		Expr fact= field.join(state.decl.get());
		Expr bound = mapSigState.get(ec).join(state.decl.get());
		bound = bound.any_arrow_one(mapSigState.get(sigType).join(state.decl.get()));
		fact = fact.in(bound);
		fact = fact.forAll(state.decl);
		ec.addFact(fact);
	}
	*/
	private void processAttributes(List<EAttribute> attrList,PrimSig ec) throws ErrorUnsupported, ErrorAlloy
	{
		Field field = null;
		Expr fact = null;
		for(EAttribute attr : attrList)
			try{
				if (attr.getEType() instanceof EEnum)
				{
					PrimSig sigType = mapClassSig.get(attr.getEType());
					field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),sigType.product(state));
					mapSfField.put(attr,field);
					fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(sigType);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);				
				}else if(attr.getEType().getName().equals("EBoolean"))
				{
					field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),state.setOf());
					mapSfField.put(attr,field);					
				}else if(attr.getEType().getName().equals("EString"))
				{
					field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),Sig.STRING.product(state));
					mapSfField.put(attr,field);
					fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(Sig.STRING);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);
				}else if(attr.getEType().getName().equals("EInt"))
				{
					field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),Sig.SIGINT.product(state));
					mapSfField.put(attr,field);
					fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(Sig.SIGINT);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);
				}else
					throw new ErrorUnsupported("Primitive type for attribute not supported.","ECore2Alloy",attr.getEType());
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",ec);}			
			

	}
	
	
	private PrimSig makeSig(EClass ec) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		PrimSig res = mapClassSig.get(ec.getName());
		if(res == null) {
			try {
				PrimSig parent = null;
				List<EClass> superTypes = null;
			
				superTypes = ec.getESuperTypes();
				if(superTypes.size() > 1) throw new ErrorTransform("Multiple inheritance not allowed.","ECore2Alloy",ec);
				if(!superTypes.isEmpty())
				{
					parent = mapClassSig.get(superTypes.get(0).getName());
					if(parent == null) throw new ErrorTransform("Parent class not found.","ECore2Alloy",superTypes);	
				}
				String signame = AlloyUtil.pckPrefix(pack.getName(),ec.getName());
				if(ec.isAbstract())
					res = new PrimSig(signame,parent,Attr.ABSTRACT);
				else res = new PrimSig(signame,parent);
				mapClassClass.put(signame,ec);

				Field statefield = res.addField(AlloyUtil.stateFieldName(pack,ec),state.setOf());
				mapSigState.put(res,statefield);
				mapClassSig.put(ec.getName(), res);
				// all atoms must belong to a state
				Expr stateatoms = res.equal(statefield.join(state));
				res.addFact(stateatoms);
				processAttributes(ec.getEAttributes(),res);
				sigList.add(res);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",res);}	
		}
		return res;
	}
	
	private void processReferences(List<EReference> eAllReferences, PrimSig parent) throws ErrorAlloy, ErrorTransform, ErrorUnsupported {
		for(EReference r : eAllReferences)
			processReference(r,parent);
	}

	private void processReference(EReference r, PrimSig srcsig) throws ErrorAlloy, ErrorTransform, ErrorUnsupported {
		EClass type = r.getEReferenceType();
		PrimSig trgsig = mapClassSig.get(type.getName());
		Field field;
		try{field = srcsig.addField(AlloyUtil.pckPrefix(pack.getName(),r.getName()),trgsig.product(state));}
		catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",srcsig);}
		mapSfField.put(r,field);
		// processing opposite references
		EReference op = r.getEOpposite();
		Decl s = state.decl;
		if(op!=null) {
			Field opField = getFieldFromSFeature(op);
			if(opField != null)
				try{srcsig.addFact(field.join(s.get()).equal(opField.join(s.get()).transpose()).forAll(state.decl));}
				catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy");}
		}
		// processing multiplicities
		Expr fact;
		try{
			Decl d = AlloyUtil.localStateSig(srcsig,s.get()).oneOf("src_");
			if (r.getLowerBound() == 1 && r.getUpperBound() == 1) {
				fact = (d.get()).join(field.join(s.get())).one().forAll(s,d);
				srcsig.addFact(fact);	
			} else if (r.getLowerBound() == 0 && r.getUpperBound() == 1) {
				fact = (d.get()).join(field.join(s.get())).lone().forAll(s,d);
				srcsig.addFact(fact);	
			} else if (r.getLowerBound() == 1 && r.getUpperBound() == -1) {
				fact = (d.get()).join(field.join(s.get())).some().forAll(s,d);
				srcsig.addFact(fact);	
			} else if (r.getUpperBound() == 0) {
				fact = (d.get()).join(field.join(s.get())).no().forAll(s,d);
				srcsig.addFact(fact);	
			} else if (r.getLowerBound() == 0 && r.getUpperBound() == -1) {}
			if(r.getLowerBound() > 1) {
				fact = (d.get()).join(field.join(s.get())).cardinality().gte(ExprConstant.makeNUMBER(r.getLowerBound())).forAll(s,d);
				srcsig.addFact(fact);
			}
			if(r.getUpperBound() != -1){
				fact = (d.get()).join(field.join(s.get())).cardinality().lte(ExprConstant.makeNUMBER(r.getUpperBound())).forAll(s,d);
				srcsig.addFact(fact);
			}
			
			d = AlloyUtil.localStateSig(trgsig,s.get()).oneOf("trg_");
			if(r.isContainment()){
				fact = ((field.join(s.get())).join(d.get())).one().forAll(s,d);
				trgsig.addFact(fact);
			}
			Expr parState = mapSigState.get(srcsig);
			Expr sTypeState = mapSigState.get(trgsig);
			// BUG IS HERE
			srcsig.addFact(field.join(s.get()).in(parState.join(s.get()).product(sTypeState.join(s.get()))).forAll(state.decl));
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",srcsig);}
		
		
	}


	private void processEAnnotations(List<EAnnotation> lAnn, EObject obj,PrimSig sig) throws ErrorTransform, ErrorAlloy, ErrorUnsupported, ErrorParser {
		Set<Decl> sd = new HashSet<Decl>();
		Decl self = null;
		try{
			self = sig.oneOf("self");
			sd.add(self);
			sd.add(state.decl);
		} catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",sig);}

		OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
		OCLHelper helper = ocl.createOCLHelper(obj);
		ExpressionInOCL invariant;
		OCL2Alloy converter = new OCL2Alloy(translator,sd);
		for(EAnnotation ea : lAnn)
			if(ea.getSource().equals("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot"))
				try{
					for(String sExpr: ea.getDetails().values())
					{
						invariant = helper.createInvariant(sExpr);
						Expr oclalloy = converter.oclExprToAlloy(invariant.getBodyExpression()).forAll(self, state.decl);
						sig.addFact(oclalloy);
					}
				} catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",sig);} 
				  catch (ParserException e) { throw new ErrorParser(e.getMessage(),"ECore2Alloy");}

	}
	
	private List<PrimSig> makeSigList () throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser
	{
		List<EClassifier> list = pack.getEClassifiers();
		List<EClass> classList = new LinkedList<EClass>();
		List<EDataType> dataList = new ArrayList<EDataType>();
		List<EEnum> enumList = new ArrayList<EEnum>();
		sigList = new ArrayList<PrimSig>();
		
		for(EClassifier e: list)
		{
			if (e instanceof EClass)
				classList.add((EClass)e);
			else if (e instanceof EEnum)
				enumList.add((EEnum) e);
			else if (e instanceof EDataType)
				dataList.add((EDataType) e);
		}
		
		processEEnum(enumList);
		processClass(classList);
		
		return sigList;
	}
	
	
	private void processEEnumLiterals(List<EEnumLiteral> el,PrimSig parent) throws ErrorAlloy 
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
	
	private List<PrimSig> processEEnum(List<EEnum> list) throws ErrorAlloy 
	{
		PrimSig enumSig = null;
		for(EEnum en: list)
		{
			try{ enumSig = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),en.getName()),Attr.ABSTRACT);}
			catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",enumSig);}
			sigList.add(enumSig);
			mapClassSig.put(en.getName(), enumSig);
			//mapSigState.put(enumSig, enumSig.addField(prefix + en.getName().toLowerCase(),state.setOf()));
			processEEnumLiterals(en.getELiterals(),enumSig);
		}
		return sigList;
	}
	
	private List<PrimSig> processClass(List<EClass> classList) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser
	{
		LinkedList<EClass> list = new LinkedList<EClass>(classList);
		EClass ec = list.poll();
		Sig itSig = null;
		while(ec !=null)
		{
			itSig = makeSig(ec);
			if(itSig == null)
				list.offer(ec);
			ec = list.poll();			
		}
		
		for(EClass e: classList)
		{
			processReferences(e.getEReferences(),mapClassSig.get(e.getName()));
			processEAnnotations(e.getEAnnotations(),e,mapClassSig.get(e.getName()));
		}
		return sigList;
	}
	
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

}

