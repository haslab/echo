package pt.uminho.haslab.echo.transform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EReference;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;


import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class XMI2Alloy {
	
	private static int counter = 0;
	
	private final String pre;
	public final EObject eObj;
	private final PrimSig state;
	private final Map<EClassifier,PrimSig> mapClassSig;
	private final Map<EStructuralFeature,Expr> mapSfField;
	private final Map<EEnumLiteral,PrimSig> mapLitSig;
	private final Map<PrimSig, Expr> mapSigState;

	private Map<Expr,Expr> mapContent = new HashMap<Expr,Expr>();	
	
	private Map<EObject,PrimSig> mapObjSig = new HashMap<EObject,PrimSig>();
	private List<PrimSig> sigList = new ArrayList<PrimSig>();
	private Expr factExpr = null; 
	
	
	public Map<EObject,Sig> getMapObjSig()
	{
		return new HashMap<EObject,Sig>(mapObjSig);
	}
	
	public XMI2Alloy(EObject obj,ECore2Alloy t,String prefix, PrimSig stateSig) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		pre = prefix;
		eObj = obj;
		mapClassSig = t.getMapClassSig();
		mapSfField = t.getMapSfField();
		mapSigState = t.getMapSigState();
		mapLitSig = t.getMapLitSig();
		state = stateSig;
		initContent();
		makeSigList(eObj);
		makeFactExpr();
		//System.out.println("Singleton sigs: " + sigList.toString());
	}
	

	
	// initializes relations to n-ary none
	private void initContent()
	{
		for(Expr f: mapSigState.values()) {
			mapContent.put(f,Sig.NONE);}
		for(EStructuralFeature sf: mapSfField.keySet()){
			if (sf instanceof EReference && ((EReference) sf).getEOpposite() != null &&((EReference) sf).getEOpposite().isContainment()) {}
			else if(sf.getEType().getName().equals("EBoolean"))
				mapContent.put(mapSfField.get(sf),Sig.NONE);
			else
				mapContent.put(mapSfField.get(sf),Sig.NONE.product(Sig.NONE));}
	}

	
	public List<PrimSig> getSigList()
	{
		return sigList;
	}
	
	public Expr getFact()
	{
		return factExpr;
	}
	
	
	private void makeFactExpr()
	{
		factExpr = Sig.NONE.no();
		
		for(Expr f: mapContent.keySet())
		{
			/*System.out.println("fe: " + factExpr);
			System.out.println("f: " + f);
			System.out.println("mcf: " + mapContent.get(f));*/
			if (!f.toString().equals("String"))
				factExpr = factExpr.and(f.join(state).equal(mapContent.get(f)));
		}
	}
	
	private Expr handleRef(EList<?> eG) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		Expr res=null;
		PrimSig ref;
		
		for(Object o: eG)
			if(o instanceof EObject)
			{
				EObject obj = (EObject) o;
				ref = mapObjSig.get(obj);
				
				if(ref == null) ref=makeSigList(obj);
				
				if(res==null) res = ref;
				else res = res.plus(ref);
			}else throw new ErrorTransform("Invalid reference.","XMI2Alloy",o);
		return res;
	}
	
	private PrimSig makeSigList(EObject it) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		Expr field;
		Expr fieldState;
		Expr siblings;
		//List<Sig> listSiblings;
		Expr aux = null;
		Object eG;
		PrimSig parent = mapClassSig.get(it.eClass());
		//System.out.println("Object instances of "+parent);
		PrimSig res;
		try {res = new PrimSig(pre + counter++, parent, Attr.ONE);}
		catch (Err a) {throw new ErrorAlloy(a.getMessage(),"XMI2Alloy",parent);}
		
		/*listSiblings = mapContents.get(parent);
		listSiblings.add(res);*/
		
		fieldState = mapSigState.get(parent);
		siblings = mapContent.get(fieldState);
		
		siblings = siblings.plus(res);
		mapContent.put(fieldState,siblings);
		PrimSig up = parent.parent;
		while (up != Sig.UNIV && up != null){
			Expr fieldStateup = mapSigState.get(up);
			Expr siblingsup = mapContent.get(fieldStateup);			
			siblingsup = siblingsup.plus(res);
			mapContent.put(fieldStateup,siblingsup);
			up = up.parent;
		}
		mapObjSig.put(it, res);
		sigList.add(res);
		Expr mappedExpr;
		List<EStructuralFeature> sfList = it.eClass().getEAllStructuralFeatures();
		for(EStructuralFeature sf: sfList)
		{
			field = mapSfField.get(sf);
			eG = it.eGet(sf);
			if(eG instanceof EList<?>)
			{
				//System.out.println("Handling reference " + sf.getName());
				if(!((EList<?>) eG).isEmpty())
				{
					if (sf instanceof EReference) {
						EReference op = ((EReference) sf).getEOpposite();
						if (op == null || (op != null && !op.isContainment())){				
							aux = handleRef((EList<?>) eG);
							mappedExpr = mapContent.get(field);
							mappedExpr = mappedExpr.plus(res.product(aux));
							mapContent.put(field, mappedExpr);	}	
					}
				}
			}
			else if(sf instanceof EAttribute && eG != null)
				handleAttr(eG,res,field);
		}
		return res;
	}
	
	
	private void handleAttr(Object obj, Sig it, Expr field) throws ErrorUnsupported
	{
		//System.out.println("Object instance attribute: "+ obj);
		Expr manos = mapContent.get(field);
		if(obj instanceof Boolean)
		{
			Boolean b = (Boolean) obj;
			
			if(b.booleanValue())
			{
				manos = manos.plus(it);
				mapContent.put(field, manos);
			}
		}else if(obj instanceof EEnumLiteral)
		{
			manos = manos.plus(it.product(mapLitSig.get((EEnumLiteral)obj)));
			mapContent.put(field, manos);
		}else if(obj instanceof String)
		{
			Expr str = ExprConstant.Op.STRING.make(null,(String) obj);
			
			manos = manos.plus(it.product(str));
			mapContent.put(field, manos);
		}else if(obj instanceof Integer)
		{
			Expr str = ExprConstant.makeNUMBER((Integer) obj);
			
			manos = manos.plus(it.product(str));
			mapContent.put(field, manos);
		}else throw new ErrorUnsupported("Primitive type for attribute not supported.","XMI2Alloy",obj.toString());
	}
	
	
	
	public void print()
	{
		System.out.println(eObj);
		System.out.println("TODO");
	}
}
