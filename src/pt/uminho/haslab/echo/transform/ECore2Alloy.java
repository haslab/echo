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
	private final String prefix;
	private final PrimSig state;
	private List<Sig> sigList;
	
	public ECore2Alloy(EPackage p) throws Err{
		state = new PrimSig("State");
		pack = p;
		prefix = "";
		sigList = makeSigList();
	}
	
	public ECore2Alloy(EPackage p, String pref) throws Err{
		state = new PrimSig("State");
		pack = p;
		prefix = pref;
		sigList = makeSigList();
	}
	
	public ECore2Alloy(EPackage p, String pref,String stateName) throws Err{
		state = new PrimSig(stateName);
		pack = p;
		prefix = pref;
		sigList = makeSigList();
	}

	public ECore2Alloy(EPackage p, String pref,PrimSig state) throws Err{
		this.state = state;
		this.pack = p;
		this.prefix = pref;
		this.sigList = makeSigList();
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
	private void processAttributes(List<EAttribute> attrList,PrimSig ec) throws Err
	{
		for(EAttribute attr : attrList)
			if (attr.getEType() instanceof EEnum)
			{
				PrimSig sigType = mapClassSig.get(attr.getEType());
				Expr field = ec.addField(prefix + attr.getName(),sigType.product(state));
				mapSfField.put(attr,field);
				Expr fact = field.join(state.decl.get());
				Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(sigType);
				fact = fact.in(bound);
				fact = fact.forAll(state.decl);
				ec.addFact(fact);
				
			}else if(attr.getEType().getName().equals("EBoolean"))
			{
				Expr field = ec.addField(prefix + attr.getName(),state.setOf());
				mapSfField.put(attr,field);
				
			}else if(attr.getEType().getName().equals("EString"))
			{
				Expr field = ec.addField(prefix + attr.getName(),Sig.STRING.product(state));
				mapSfField.put(attr,field);
				Expr fact = field.join(state.decl.get());
				Expr bound = mapSigState.get(ec).join(state.decl.get()).any_arrow_one(Sig.STRING);
				fact = fact.in(bound);
				fact = fact.forAll(state.decl);
				ec.addFact(fact);
			}
			else
				throw new Error("Primitive type for attribute not supported: "+attr.getEType().getName());
			
	}
	
	
	private Sig makeSig(EClass ec) throws Err
	{
		PrimSig res = mapClassSig.get(ec);
		if(res == null)
		{
			PrimSig parent = null;
			List<EClass> superTypes = null;
		
			superTypes = ec.getESuperTypes();
			if(superTypes.size() > 1) throw new Error("Multiple inheritance not supported.");
			if(!superTypes.isEmpty())
			{
				parent = mapClassSig.get(superTypes.get(0));
				if(parent == null) throw new Error("Parent class not found.");	
			}
			if(ec.isAbstract())
				res = new PrimSig(prefix + ec.getName(),parent,Attr.ABSTRACT);
			else res = new PrimSig(prefix + ec.getName(),parent);
			mapSigState.put(res,res.addField(prefix + ec.getName().toLowerCase(),state.setOf()));
			mapClassSig.put(ec, res);
			processAttributes(ec.getEAllAttributes(),res);
			sigList.add(res);
		}
		return res;
	}
	
	private void processReferences(List<EReference> eAllReferences, PrimSig parent) throws Err {
		for(EReference r : eAllReferences)
			processReference(r,parent);
	}

	private void processReference(EReference r, PrimSig parent) throws Err {
		EClass type = r.getEReferenceType();
		PrimSig sigType = mapClassSig.get(type);
		Expr field = parent.addField(prefix + r.getName(),sigType.product(state));
		mapSfField.put(r, field);
		// processing opposite references
		Expr opField = null;
		EReference op = r.getEOpposite();
		Expr s = state.decl.get();
		if(op!=null)
		{
			opField = mapSfField.get(op);
			if(opField != null)
				parent.addFact(field.join(s).equal(opField.join(s).transpose()).forAll(state.decl));
		}
		// processing multiplicities
		if(r.getLowerBound() > 0)
			parent.addFact(parent.decl.get().join(field).join(s).cardinality().gte(ExprConstant.makeNUMBER(r.getLowerBound())).forAll(state.decl));
		if(r.getUpperBound() != -1)
			parent.addFact(parent.decl.get().join(field).join(s).cardinality().lte(ExprConstant.makeNUMBER(r.getUpperBound())).forAll(state.decl));
		if(r.isContainment())
			sigType.addFact(field.join(s).join(sigType.decl.get()).one().forAll(state.decl));
		
		Expr parState = mapSigState.get(parent);
		Expr sTypeState = mapSigState.get(sigType);		
		parent.addFact(field.join(s).in(parState.join(s).product(sTypeState.join(s))).forAll(state.decl));
	}


	private List<Sig> makeSigList () throws Err
	{
		List<EClassifier> list = pack.getEClassifiers();
		List<EClass> classList = new LinkedList<EClass>();
		List<EDataType> dataList = new ArrayList<EDataType>();
		List<EEnum> enumList = new ArrayList<EEnum>();
		sigList = new ArrayList<Sig>();
		sigList.add(state);
		
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
	
	
	private void processEEnumLiterals(List<EEnumLiteral> el,PrimSig parent) throws Err
	{
		for(EEnumLiteral lit: el)
		{
			PrimSig litSig = new PrimSig(prefix + lit.getLiteral(),parent,Attr.ONE);
			mapLitSig.put(lit, litSig);
			sigList.add(litSig);
		}
	} 
	
	private List<Sig> processEEnum(List<EEnum> list) throws Err
	{
		PrimSig enumSig = null;
		for(EEnum en: list)
		{
			enumSig = new PrimSig(prefix + en.getName(),Attr.ABSTRACT);
			sigList.add(enumSig);
			mapClassSig.put(en, enumSig);
			//mapSigState.put(enumSig, enumSig.addField(prefix + en.getName().toLowerCase(),state.setOf()));
			processEEnumLiterals(en.getELiterals(),enumSig);
		}
		
		return sigList;
	}
	
	private List<Sig> processClass(List<EClass> classList) throws Err
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
	
	
}

