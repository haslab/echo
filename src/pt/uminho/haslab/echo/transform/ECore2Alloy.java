package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
//import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class ECore2Alloy {

	private HashMap<EClassifier,PrimSig> mapClassSig = new HashMap<EClassifier,PrimSig>();
	private HashMap<EEnumLiteral,PrimSig> mapLitSig = new HashMap<EEnumLiteral,PrimSig>();
	private HashMap<EStructuralFeature,Expr> mapSfField = new HashMap<EStructuralFeature,Expr>();
	private HashMap<PrimSig,Expr> mapSigState = new HashMap<PrimSig,Expr>();
	private final EPackage pack;
	private final PrimSig state;
	private List<Sig> sigList;
	
	public ECore2Alloy(EPackage p, PrimSig statesig) throws ErrorUnsupported, ErrorAlloy, ErrorTransform{
		state = statesig;
		pack = p;
		sigList = makeSigList();
	}
	
	public HashMap<PrimSig,Expr> getMapSigState()
	{
		//return new HashMap<PrimSig,Expr>(mapSigState);
		return mapSigState;
	}
	
	public HashMap<EEnumLiteral,PrimSig> getMapLitSig()
	{
		return mapLitSig;
	}
	
	public PrimSig getState()
	{
		return state;
	}
	
	public HashMap<EStructuralFeature,Expr> getMapSfField()
	{
		//return new HashMap<EStructuralFeature,Expr>(mapSfField);
		return mapSfField;
	}
	
	public HashMap<EClassifier,PrimSig> getMapClassSig()
	{
		//return new HashMap<EClassifier,PrimSig>(mapClassSig);
		return mapClassSig;
	}
	
	public List<Sig> getSigList()
	{
		return new ArrayList<Sig>(sigList);
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
		for(EAttribute attr : attrList)
			try{
				if (attr.getEType() instanceof EEnum)
				{
					PrimSig sigType = mapClassSig.get(attr.getEType());
					Expr field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),sigType.product(state));
					mapSfField.put(attr,field);
					Expr fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(sigType);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);
					
				}else if(attr.getEType().getName().equals("EBoolean"))
				{
					Expr field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),state.setOf());
					mapSfField.put(attr,field);
					
				}else if(attr.getEType().getName().equals("EString"))
				{
					Expr field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),Sig.STRING.product(state));
					mapSfField.put(attr,field);
					Expr fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(Sig.STRING);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);
				}else if(attr.getEType().getName().equals("EInt"))
				{
					Expr field = ec.addField(AlloyUtil.pckPrefix(pack.getName(),attr.getName()),Sig.SIGINT.product(state));
					mapSfField.put(attr,field);
					Expr fact = field.join(state.decl.get());
					Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(Sig.SIGINT);
					fact = fact.in(bound);
					fact = fact.forAll(state.decl);
					ec.addFact(fact);
				}else
					throw new ErrorUnsupported("Primitive type for attribute not supported.","ECore2Alloy",attr.getEType());
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",attr);}			
	}
	
	
	private Sig makeSig(EClass ec) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		PrimSig res = mapClassSig.get(ec);
		if(res == null) {
			try {
				PrimSig parent = null;
				List<EClass> superTypes = null;
			
				superTypes = ec.getESuperTypes();
				if(superTypes.size() > 1) throw new ErrorTransform("Multiple inheritance not allowed.","ECore2Alloy",ec);
				if(!superTypes.isEmpty())
				{
					parent = mapClassSig.get(superTypes.get(0));
					if(parent == null) throw new ErrorTransform("Parent class not found.","ECore2Alloy",superTypes);	
				}
				if(ec.isAbstract())
					res = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),ec.getName()),parent,Attr.ABSTRACT);
				else res = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),ec.getName()),parent);
				mapSigState.put(res,res.addField(AlloyUtil.pckPrefix(pack.getName(),ec.getName()).toLowerCase(),state.setOf()));
				mapClassSig.put(ec, res);
				processAttributes(ec.getEAllAttributes(),res);
				sigList.add(res);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",ec);}	
		}
		return res;
	}
	
	private void processReferences(List<EReference> eAllReferences, PrimSig parent) throws ErrorAlloy {
		for(EReference r : eAllReferences)
			processReference(r,parent);
	}

	private void processReference(EReference r, PrimSig parent) throws ErrorAlloy {
		EClass type = r.getEReferenceType();
		PrimSig sigType = mapClassSig.get(type);
		Expr field;
		try{field = parent.addField(AlloyUtil.pckPrefix(pack.getName(),r.getName()),sigType.product(state));}
		catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",r);}
		mapSfField.put(r, field);
		// processing opposite references
		Expr opField = null;
		EReference op = r.getEOpposite();
		Expr s = state.decl.get();
		if(op!=null)
		{
			opField = mapSfField.get(op);
			if(opField != null)
				try{parent.addFact(field.join(s).equal(opField.join(s).transpose()).forAll(state.decl));}
				catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",r);}

		}
		// processing multiplicities
		try{
			if(r.getLowerBound() > 0)
				parent.addFact(parent.decl.get().join(field).join(s).cardinality().gte(ExprConstant.makeNUMBER(r.getLowerBound())).forAll(state.decl));
			if(r.getUpperBound() != -1)
				parent.addFact(parent.decl.get().join(field).join(s).cardinality().lte(ExprConstant.makeNUMBER(r.getUpperBound())).forAll(state.decl));
			if(r.isContainment())
				sigType.addFact(field.join(s).join(sigType.decl.get()).one().forAll(state.decl));
			Expr parState = mapSigState.get(parent);
			Expr sTypeState = mapSigState.get(sigType);		
			parent.addFact(field.join(s).in(parState.join(s).product(sTypeState.join(s))).forAll(state.decl));
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",r);}
	}


	private List<Sig> makeSigList () throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		List<EClassifier> list = pack.getEClassifiers();
		List<EClass> classList = new LinkedList<EClass>();
		List<EDataType> dataList = new ArrayList<EDataType>();
		List<EEnum> enumList = new ArrayList<EEnum>();
		sigList = new ArrayList<Sig>();
		
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
			catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",lit);}
			mapLitSig.put(lit, litSig);
			sigList.add(litSig);
		}
	} 
	
	private List<Sig> processEEnum(List<EEnum> list) throws ErrorAlloy 
	{
		PrimSig enumSig = null;
		for(EEnum en: list)
		{
			try{ enumSig = new PrimSig(AlloyUtil.pckPrefix(pack.getName(),en.getName()),Attr.ABSTRACT);}
			catch (Err a) {throw new ErrorAlloy(a.getMessage(),"ECore2Alloy",en);}
			sigList.add(enumSig);
			mapClassSig.put(en, enumSig);
			//mapSigState.put(enumSig, enumSig.addField(prefix + en.getName().toLowerCase(),state.setOf()));
			processEEnumLiterals(en.getELiterals(),enumSig);
		}
		
		return sigList;
	}
	
	private List<Sig> processClass(List<EClass> classList) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
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
			processReferences(e.getEAllReferences(),mapClassSig.get(e));
		
		return sigList;
	}
	
	public Expr getDeltaExpr(PrimSig m, PrimSig n) throws ErrorAlloy{
		Expr result = ExprConstant.makeNUMBER(0);
		for (Expr e : mapSigState.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
			result = result.iplus(aux);
		}
		for (Expr e : mapSfField.values()) {
			Expr aux = (((e.join(m)).minus(e.join(n))).plus((e.join(n)).minus(e.join(m)))).cardinality();
			result = result.iplus(aux);
		}
		return result;
	}
	
	
}

