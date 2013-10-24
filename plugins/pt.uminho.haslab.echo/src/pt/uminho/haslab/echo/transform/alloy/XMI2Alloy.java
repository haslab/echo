package pt.uminho.haslab.echo.transform.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

class XMI2Alloy {
	
	private static int counter = 0;
	
	private final EObject eObj;
	private final PrimSig state;

	private Map<Expr,Expr> mapContent = new HashMap<Expr,Expr>();	
	
	private Map<EObject,PrimSig> mapObjSig = new HashMap<EObject,PrimSig>();
	private Map<String,List<PrimSig>> sigList = new HashMap<String,List<PrimSig>>();
	private Expr factExpr = null; 
	
	final ECore2Alloy translator;
	
	XMI2Alloy(EObject obj,ECore2Alloy t, PrimSig stateSig) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		eObj = obj;
		translator = t;
		state = stateSig;
		initContent();
		makeSigList(eObj);
		makeFactExpr();
	}
	
	PrimSig getSigFromEObject(EObject o) {
		return mapObjSig.get(o);
	}

	List<PrimSig> getSigsFromSig(String s) {
		return sigList.get(s);
	}

	EObject getRootEObject(){
		return eObj;
	}
	
	// initializes relations to n-ary none
	private void initContent()
	{
		for(PrimSig s: translator.getAllSigs())
			if (translator.getStateFieldFromSig(s) != null)
				mapContent.put(translator.getStateFieldFromSig(s),Sig.NONE);
		for(Field field: translator.getFields()){
			//EchoReporter.getInstance().debug("FIELDS at XMI2Alloy: "+field);
			EStructuralFeature sfeature = translator.getSFeatureFromField(field);
			if (sfeature instanceof EReference && ((EReference) sfeature).getEOpposite() != null &&((EReference) sfeature).getEOpposite().isContainment()) {}
			else if(sfeature.getEType().getName().equals("EBoolean"))
				mapContent.put(field,Sig.NONE);
			else
				mapContent.put(field,Sig.NONE.product(Sig.NONE));
		}
	}

	
	List<PrimSig> getSigList()
	{
		List<PrimSig> res = new ArrayList<PrimSig>();
		for (List<PrimSig> sigs : sigList.values())
			res.addAll(sigs);
		return res;
	}

	Map<String,List<PrimSig>> getSigMap()
	{
		return new HashMap<String,List<PrimSig>>(sigList);
	}

	Expr getFact()
	{
		return factExpr;
	}
	
	
	private void makeFactExpr()
	{
		factExpr = Sig.NONE.no();
		for(Expr f: mapContent.keySet()) {
			if (!f.toString().equals("String"))
				factExpr = factExpr.and(f.join(state).equal(mapContent.get(f)));
		}
	}
	
	private PrimSig handleRef(EObject obj) throws ErrorUnsupported, ErrorAlloy, ErrorTransform
	{
		PrimSig ref;
		
		ref = mapObjSig.get(obj);
		
		if(ref == null) ref = makeSigList(obj);

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
			}else throw new ErrorTransform("Invalid reference.: "+o);
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
		EClass cc = translator.getEClassFromName(it.eClass().getName());
		PrimSig parent = translator.getSigFromEClass(cc);
		PrimSig res;
		//System.out.println(parent + ", " + counter);
		try {
			res = new PrimSig(parent.label +"_"+ counter++ +"_", parent, Attr.ONE);
		}
		catch (Err a) {throw new ErrorAlloy(a.getMessage());}
		
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
		if (sigList.get(parent.label) == null) sigList.put(parent.label, new ArrayList<PrimSig>());
		sigList.get(parent.label).add(res);
		Expr mappedExpr;
		List<EStructuralFeature> sfList = it.eClass().getEAllStructuralFeatures();
		for(EStructuralFeature sf: sfList)
		{
			field = translator.getFieldFromSFeature(sf);
			eG = it.eGet(sf);
			if (sf instanceof EReference) {
				//System.out.println(sf +" : " +eG);
				if(eG instanceof EList<?>) {
					if(!((EList<?>) eG).isEmpty()) {
						EReference op = ((EReference) sf).getEOpposite();
						if (op != null && field == null){}
						else{
							aux = handleRef((EList<?>) eG);
							mappedExpr = mapContent.get(field);
							mappedExpr = mappedExpr.plus(res.product(aux));
							mapContent.put(field, mappedExpr);	
						}	
					}
				} else if(eG instanceof EObject) {
					EReference op = ((EReference) sf).getEOpposite();
					if (op == null || (op != null && !op.isContainment())){				
						aux = handleRef((EObject) eG);
						mappedExpr = mapContent.get(field);
						mappedExpr = mappedExpr.plus(res.product(aux));
						mapContent.put(field, mappedExpr);	}	
				} else if (eG == null) {} 
				else throw new ErrorUnsupported("EReference type not supported: "+eG);
			} 
			else if(sf instanceof EAttribute)
				handleAttr(eG,res,field);
			else throw new ErrorUnsupported("Structural feature not supported: "+sf);
		}
		return res;
	}
	
	
	private void handleAttr(Object obj, Sig it, Expr field) throws ErrorUnsupported, ErrorTransform
	{
		Expr manos = mapContent.get(field);
		if(obj instanceof Boolean)
		{
			Boolean b = (Boolean) obj;
			
			if(b)
			{
				manos = manos.plus(it);
				mapContent.put(field, manos);
			}
		}
		else if(obj instanceof EEnumLiteral)
		{
			manos = manos.plus(it.product(translator.getSigFromEEnumLiteral((EEnumLiteral)obj)));
			mapContent.put(field, manos);
		}
		else if(obj instanceof String)
		{
			Expr str = ExprConstant.Op.STRING.make(null,(String) obj);
			
			manos = manos.plus(it.product(str));
			mapContent.put(field, manos);
		}else if(obj instanceof Integer)
		{
			Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
			Integer max = (int) (Math.pow(2, bitwidth) / 2);
			if ((Integer) obj >= max || (Integer) obj < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+obj+".");
			Expr str = ExprConstant.makeNUMBER((Integer) obj);
			
			manos = manos.plus(it.product(str));
			mapContent.put(field, manos);
		}else throw new ErrorUnsupported("Primitive type for attribute not supported: "+obj+".");
	}
	


	
}
