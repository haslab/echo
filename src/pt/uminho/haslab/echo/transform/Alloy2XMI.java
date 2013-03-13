package pt.uminho.haslab.echo.transform;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;


import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;



public class Alloy2XMI {
	
	private Map<Expr,Object> mapExprObj = new HashMap<Expr,Object>();
	private final EObject result;
	private final A4Solution sol;
	private final Expr state;
	private final Map<EStructuralFeature,Field> mapSfField;
	private final EFactory eFactory;
	
	private Map<String,Expr> mapAtoms;
	
	
	
	public Alloy2XMI(A4Solution sol, XMI2Alloy modelInfo,ECore2Alloy metaInfo,Expr state) throws Err
	{
		mapSfField = metaInfo.getMapSfField();
		this.sol = sol;
		eFactory = metaInfo.pack.getEFactoryInstance();
		this.state = state;
		mapAtoms = buildMapAtoms();
		result = createObject(modelInfo.getMapObjSig().get(modelInfo.eObj),modelInfo.eObj.eClass());
	}
	
	
	public EObject getModel()
	{
		return result;
	}
	
	private Map<String, Expr> buildMapAtoms() {
		Map<String,Expr> res = new HashMap<String,Expr>();
		
		for(ExprVar e: sol.getAllAtoms())
			res.put(e.label, e);
		
		
		return res;
	}


	

	
	
	//TODO : Aridade das relações, não sei se esta será a maneira correcta.
	
	private EObject createObject(Expr ex, EClass ec) throws Err
	{
		EObject obj = eFactory.create(ec);
		mapExprObj.put(ex, obj);
		Expr field,itExpr;
		EObject itObj;
		A4TupleSet ts;
		EAttribute att;
		EReference ref;
		EList<EObject> itList;
		for(EStructuralFeature sf: ec.getEAllStructuralFeatures())
		{
			field = mapSfField.get(sf);
			if(sf instanceof EAttribute)
			{
				att = (EAttribute) sf;
				if(att.getEType().getName().equals("EBoolean"))
				{
					obj.eSet(sf,sol.eval(ex.in(field.join(state))));
				}
				else if(att.getEType().getName().equals("EString"))
				{
					ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
					obj.eSet(sf, ts.iterator().next().atom(0));
					
				}
				else if(att.getEType().getName().equals("EInt"))
					;
				else
					;
			}else if( sf instanceof EReference)
			{
				ref = (EReference) sf;
				ts = (A4TupleSet) sol.eval(ex.join(field.join(state)));
				if (ref.isMany())//ref.getUpperBound() == 1 )//&& ref.getLowerBound()==1)
				{
					itList = new BasicEList<EObject>();
					for(A4Tuple t : ts)
					{
						itExpr = mapAtoms.get(t.atom(0));
						itObj = (EObject) mapExprObj.get(itExpr);
						if(itObj == null)
							itObj = createObject(itExpr,ref.getEReferenceType());
						itList.add(itObj);
					}
					obj.eSet(sf, itList);
				}
				else if (ts.size()> 0)
				{
					itExpr = mapAtoms.get(ts.iterator().next().atom(0));
					itObj = (EObject) mapExprObj.get(itExpr);
					if(itObj == null)
						itObj = createObject(itExpr,ref.getEReferenceType());
					obj.eSet(sf, itObj);
				}
			}
			else
			{
				;
			}
		}
		
		
		
		return obj;
	}
}
