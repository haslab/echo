package pt.uminho.haslab.echo.transform;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;

public class Alloy2XMI {
	
	private Map<ExprVar,Object> mapExprObj = new HashMap<ExprVar,Object>();
	private final EObject result;
	private final A4Solution sol;
	private final Expr state;
	private final ECore2Alloy e2a;
	private final EchoOptions options;
	private final List<PrimSig> instancesigs;
	private Map<String,ExprVar> mapAtoms;
	
	public Alloy2XMI(A4Solution sol, PrimSig rootatom, ECore2Alloy metaInfo,PrimSig state, EchoOptions options,List<PrimSig> instsigs) throws ErrorAlloy, ErrorTransform 
	{
		if (sol.eval(rootatom).size() != 1) throw new ErrorTransform("Could not resolve top atom", "Alloy2XMI");
		instancesigs = (instsigs==null)?new ArrayList<PrimSig>():instsigs;
		e2a = metaInfo;
		this.sol = sol;
		this.state = state;
		mapAtoms = buildMapAtoms();
		this.options = options;
		result = createObject(mapAtoms.get(sol.eval(rootatom).iterator().next().atom(0)));
	}
	
	
	public EObject getModel()
	{
		return result;
	}
	
	private Map<String, ExprVar> buildMapAtoms() {
		Map<String,ExprVar> res = new HashMap<String,ExprVar>();
		
		for(ExprVar e: sol.getAllAtoms())
			res.put(e.label, e);
		
		return res;
	}
	
	//TODO : Recheck relation arity
	
	private EObject createObject(ExprVar ex) throws ErrorAlloy
	{
		EClass ec = null;
		try {
			PrimSig type = (PrimSig)ex.type().toExpr();
			if (instancesigs.contains(type)) ec = (EClass) e2a.getEClassFromSig(type.parent);
			else ec = (EClass) e2a.getEClassFromSig(type);
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
		EObject obj = e2a.epackage.getEFactoryInstance().create(ec);
		mapExprObj.put(ex, obj);
		Field field;
		ExprVar itExpr;
		EObject itObj;
		A4TupleSet ts;
		EAttribute att;
		EReference ref;
		EList<EObject> itList;
		for(EStructuralFeature sf: ec.getEAllStructuralFeatures())
		{	
			try {
				field = e2a.getFieldFromSFeature(sf);
				ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));

				if(sf instanceof EAttribute)
				{
					att = (EAttribute) sf;
					if(att.getEType() instanceof EEnum){
						Expr e = mapAtoms.get(ts.iterator().next().atom(0));
						EEnumLiteral lit = e2a.getEEnumLiteralFromSig((PrimSig)e.type().toExpr());
						obj.eSet(sf, lit);
					}
					else if(att.getEType().getName().equals("EBoolean"))
						obj.eSet(sf,sol.eval(ex.in(field.join(state))));
					else if(att.getEType().getName().equals("EString"))
						obj.eSet(sf, ts.iterator().next().atom(0));
					else if(att.getEType().getName().equals("EInt"))
						obj.eSet(sf,Integer.parseInt(ts.iterator().next().atom(0)));
					else
						;
				}else if(sf instanceof EReference)
				{
					if (!options.isOptimize() || 
							!(((EReference) sf).getEOpposite() != null && ((EReference) sf).getEOpposite().isContainment())) {
						ref = (EReference) sf;
		
						if (ref.isMany())//ref.getUpperBound() == 1 )//&& ref.getLowerBound()==1)
						{
							itList = new BasicEList<EObject>();
							for(A4Tuple t : ts)
							{
								itExpr = mapAtoms.get(t.atom(0));
								itObj = (EObject) mapExprObj.get(itExpr);
								if(itObj == null)
									itObj = createObject(itExpr);
								itList.add(itObj);
							}
							obj.eSet(sf, itList);
						}
						else if (ts.size()> 0)
						{
							itExpr = mapAtoms.get(ts.iterator().next().atom(0));
							itObj = (EObject) mapExprObj.get(itExpr);
							if(itObj == null)
								itObj = createObject(itExpr);
							obj.eSet(sf, itObj);
						}
					}
				}
				else
				{
					;
				}
			} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		}
		
		
		
		return obj;
	}
}
