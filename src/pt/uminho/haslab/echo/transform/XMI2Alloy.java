package pt.uminho.haslab.echo.transform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

public class XMI2Alloy {
	
	private static int counter = 0;
	
	private final EObject eObj;
	private final PrimSig state;

	private Map<Expr,Expr> mapContent = new HashMap<Expr,Expr>();	
	
	private Map<EObject,PrimSig> mapObjSig = new HashMap<EObject,PrimSig>();
	private List<PrimSig> sigList = new ArrayList<PrimSig>();
	private Expr factExpr = null; 
	
	public final ECore2Alloy translator;
	
	public XMI2Alloy(EObject obj,ECore2Alloy t, PrimSig stateSig) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		eObj = obj;
		translator = t;
		state = stateSig;
		initContent();
		makeSigList(eObj);
		makeFactExpr();
	}
	
	public PrimSig getSigFromEObject(EObject o) {
		return mapObjSig.get(o);
	}

	public EObject getRootEObject(){
		return eObj;
	}
	
	// initializes relations to n-ary none
	private void initContent()
	{
		for(Expr f: translator.getStateFields()) {
			mapContent.put(f,Sig.NONE);}
		for(EStructuralFeature sf: translator.getSFeatures()){
			if (sf instanceof EReference && ((EReference) sf).getEOpposite() != null &&((EReference) sf).getEOpposite().isContainment()) {}
			else if(sf.getEType().getName().equals("EBoolean"))
				mapContent.put(translator.getFieldFromSFeature(sf),Sig.NONE);
			else
				mapContent.put(translator.getFieldFromSFeature(sf),Sig.NONE.product(Sig.NONE));}
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
			if (!f.toString().equals("String"))
				factExpr = factExpr.and(f.join(state).equal(mapContent.get(f)));
		}
	}
	
	private PrimSig handleRef(EObject obj) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		PrimSig ref;
		
		ref = mapObjSig.get(obj);
		
		if(ref == null) ref=makeSigList(obj);

		return ref;
	}
	
	private Expr handleRef(EList<?> eG) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		Expr res=null;
		PrimSig ref;
		
		for(Object o: eG)
			if(o instanceof EObject){
				ref = handleRef((EObject) o);
				if(res==null) res = ref;
				else res = res.plus(ref);
			}else throw new ErrorTransform("Invalid reference.","XMI2Alloy",o);
		return res;
	}
	
	private PrimSig makeSigList(EObject it) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		Field field;
		Expr fieldState;
		Expr siblings;
		//List<Sig> listSiblings;
		Expr aux = null;
		Object eG;
		PrimSig parent = translator.getSigFromEClass(it.eClass());
		PrimSig res;
		try {res = new PrimSig(parent.label +"_"+ counter++ +"_", parent, Attr.ONE);}
		catch (Err a) {throw new ErrorAlloy(a.getMessage(),"XMI2Alloy",parent);}
		
		/*listSiblings = mapContents.get(parent);
		listSiblings.add(res);*/
		
		fieldState = translator.getStateFieldFromSig(parent);
		siblings = mapContent.get(fieldState);

		siblings = siblings.plus(res);
		mapContent.put(fieldState,siblings);
		PrimSig up = parent.parent;
		while (up != Sig.UNIV && up != null){
			Expr fieldStateup = translator.getStateFieldFromSig(up);
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
			field = translator.getFieldFromSFeature(sf);
			eG = it.eGet(sf);
			if (sf instanceof EReference) {
				if(eG instanceof EList<?>) {
					if(!((EList<?>) eG).isEmpty()) {
						EReference op = ((EReference) sf).getEOpposite();
						if (op == null || (op != null && !op.isContainment())){				
							aux = handleRef((EList<?>) eG);
							mappedExpr = mapContent.get(field);
							mappedExpr = mappedExpr.plus(res.product(aux));
							mapContent.put(field, mappedExpr);	}	
					}
				} else if(eG instanceof EObject) {
					EReference op = ((EReference) sf).getEOpposite();
					if (op == null || (op != null && !op.isContainment())){				
						aux = handleRef((EObject) eG);
						mappedExpr = mapContent.get(field);
						mappedExpr = mappedExpr.plus(res.product(aux));
						mapContent.put(field, mappedExpr);	}	
				} else if (eG == null) {} 
				else throw new ErrorUnsupported("EReference type not supported: "+eG, "XMI2Alloy");
			} else if(sf instanceof EAttribute && eG != null)
				handleAttr(eG,res,field);
			else throw new ErrorUnsupported("Structural feature not supported: "+sf, "XMI2Alloy");
		}
		return res;
	}
	
	
	private void handleAttr(Object obj, Sig it, Expr field) throws ErrorUnsupported
	{
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
			manos = manos.plus(it.product(translator.getSigFromEEnumLiteral((EEnumLiteral)obj)));
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
	


	
}
