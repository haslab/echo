package pt.uminho.haslab.echo.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;



public class Alloy2XMI {
	
	private Map<Sig,EObject> mapSigObj;
	private Map<EObject,Sig> mapObjSig;
	private final EObject root;
	private final A4Solution sol;
	
	
	public Alloy2XMI(A4Solution sol, XMI2Alloy oldInst, List<Sig> states)
	{
		this.sol = sol;
		root = oldInst.eObj;
		mapObjSig = oldInst.getMapObjSig();
		mapSigObj = invertMap(mapObjSig);
	}

	
	private <V, K> Map<V,K> invertMap(Map<K,V> map)
	{
		Map<V,K> res = new HashMap<V,K>();
		for(K key: map.keySet())
			res.put(map.get(key),key);
			
		return res;
	}
}
