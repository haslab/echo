package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Alloy2XMI {
	
	private Map<ExprVar,Object> mapExprObj = new HashMap<ExprVar,Object>();
	private final EObject result;
	private final A4Solution sol;
	private final Expr state;
	private final EAlloyMetamodel metamodel;
	private final List<PrimSig> instancesigs;
	private Map<String,ExprVar> mapAtoms;
	
	Alloy2XMI(A4Solution sol, PrimSig rootatom, EAlloyMetamodel metamodel, PrimSig state, List<PrimSig> instsigs) throws EchoError 
	{
		if (sol.eval(rootatom).size() != 1) throw new ErrorTransform("Could not resolve top atom: "+rootatom);
		instancesigs = (instsigs==null)?new ArrayList<PrimSig>():instsigs;
		this.metamodel = metamodel;
		this.sol = sol;
		this.state = state;	
		mapAtoms = buildMapAtoms();
		result = createObject(mapAtoms.get(sol.eval(rootatom).iterator().next().atom(0)));
	}
	
	
	EObject getModel()
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
	
	private EObject createObject(ExprVar ex) throws EchoError {
		EClass ec = null;
		PrimSig type = null;
		try {
			type = (PrimSig)ex.type().toExpr();
			if (instancesigs.contains(type)) {
				ec = (EClass) metamodel.getEClassifierFromSig(type.parent);
			}
			else {
				ec = (EClass) metamodel.getEClassifierFromSig(type);
				if (ec == null)
					ec = (EClass) metamodel.getEClassifierFromSig(type.parent);
			}
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }

		EObject obj = ec.getEPackage().getEFactoryInstance().create(ec);

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
			field = metamodel.getFieldFromSFeature(sf);
			//EchoReporter.getInstance().debug(ex+", "+field+", "+state);
			if(sf instanceof EAttribute) {

				att = (EAttribute) sf;
				if(att.getEType() instanceof EEnum){
					try {
						ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
						Expr e = mapAtoms.get(ts.iterator().next().atom(0));
						EEnumLiteral lit = metamodel.getEEnumLiteralFromSig((PrimSig)e.type().toExpr());
						obj.eSet(sf, lit);
					} catch (Err a) {throw new ErrorAlloy (a.getMessage());}

				}
				else if(att.getEType().getName().equals("EBoolean"))
					try {
						obj.eSet(sf,sol.eval(ex.in(field.join(state))));
					} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
				else if(att.getEType().getName().equals("EString")) {
					try {
						ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
						obj.eSet(sf, ts.iterator().next().atom(0));
					} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
				}
				else if(att.getEType().getName().equals("EInt")) {
					try {
						ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
						obj.eSet(sf,Integer.parseInt(ts.iterator().next().atom(0)));
					} catch (Err a) {throw new ErrorAlloy (a.getMessage());}

				}
			}
			else if(sf instanceof EReference) {
				ref = (EReference) sf;
				if (EchoOptionsSetup.getInstance().isOptimize() && ref.getEOpposite() != null && field == null) {}
				else {
					try {
						ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
					} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
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
		}
		
		
		
		return obj;
	}
}
