package pt.uminho.haslab.echo;
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

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class Instance {
	
	private static int counter = 0;
	
	private final String pre;
	private final EObject eObj;
	private final PrimSig state;
	private final Map<EClassifier,PrimSig> mapClassSig;
	private final Map<EStructuralFeature,Expr> mapSfField;
	private final Map<EEnumLiteral,PrimSig> mapLitSig;
	private final Map<PrimSig, Expr> mapSigState;
	

	
	
	private Map<Expr,Expr> mapContent = new HashMap<Expr,Expr>();
	
	
	private Map<EObject,PrimSig> mapObjSig = new HashMap<EObject,PrimSig>();
	private List<Sig> sigList =new ArrayList<Sig>();
	private Expr factExpr = null; 
	
	
	
	public Instance(EObject obj,Transformer t,String prefix) throws Err
	{
		pre = prefix;
		eObj = obj;
		mapClassSig = t.getMapClassSig();
		mapSfField = t.getMapSfField();
		mapSigState = t.getMapSigState();
		mapLitSig = t.getMapLitSig();
		state =new PrimSig(pre + counter++,t.getState(),Attr.ONE);
		sigList.add(state);
		initContent();
		makeSigList(eObj);
		makeFactExpr();	
		
	}
	

	

	private void initContent()
	{
		for(Expr f: mapSigState.values())
			mapContent.put(f,Sig.NONE);
		for(EStructuralFeature sf: mapSfField.keySet())
			if(sf.getEType().getName().equals("EBoolean"))
				mapContent.put(mapSfField.get(sf),Sig.NONE);
			else
				mapContent.put(mapSfField.get(sf),Sig.NONE.product(Sig.NONE));
	}

	
	public List<Sig> getSigList()
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
			factExpr = factExpr.and(f.join(state).equal(mapContent.get(f)));
		}
	}
	
	private Expr handleRef(EList<?> eG) throws Err
	{
		Expr res=null;
		PrimSig ref;
		
		for(Object o: eG)
			if(o instanceof EObject)
			{
				EObject obj = (EObject) o;
				ref = mapObjSig.get(obj);
				
				if(ref == null)
					ref=makeSigList(obj);
				
				if(res==null) res = ref;
				else res = res.plus(ref);
			}
		return res;
	}
	
	
	
	
	private PrimSig makeSigList(EObject it) throws Err
	{
		Expr field;
		Expr fieldState;
		Expr siblings;
		//List<Sig> listSiblings;
		Expr aux = null;
		Object eG;
		PrimSig parent = mapClassSig.get(it.eClass());
		System.out.println(parent);
		PrimSig res = new PrimSig(pre + counter++,parent ,Attr.ONE);
		
		
		/*listSiblings = mapContents.get(parent);
		listSiblings.add(res);*/
		
		fieldState = mapSigState.get(parent);
		siblings = mapContent.get(fieldState);
		
		
		siblings = siblings.plus(res);
		mapContent.put(fieldState,siblings);
		
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
				if(!((EList<?>) eG).isEmpty())
				{
					aux = handleRef((EList<?>) eG);
					
					mappedExpr = mapContent.get(field);
					mappedExpr = mappedExpr.plus(res.product(aux));
					mapContent.put(field, mappedExpr);		
				}
			}
			else if(sf instanceof EAttribute && eG != null)
				handleAttr(eG,res,field);
		}
		
		return res;
	}
	
	
	private void handleAttr(Object obj, Sig it, Expr field) throws Err
	{
		System.out.println("obj = "+ obj);
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
		}
			
			

		
	}
	
	
	
	public void print()
	{
		System.out.println(eObj);
		System.out.println("TODO");
	}
}
