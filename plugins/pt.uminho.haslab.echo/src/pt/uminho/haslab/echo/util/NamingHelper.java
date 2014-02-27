package pt.uminho.haslab.echo.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tmg on 2/25/14.
 *
 */
public class NamingHelper {

    private static Map<EClass,Integer> mapCounter = new HashMap<>();

    public static String nameAtom(EClass ec){
        if(!mapCounter.containsKey(ec))
            mapCounter.put(ec,0);

        int i = mapCounter.get(ec);
        String res =ec.getName() + "$" + i;

        mapCounter.put(ec,++i);

        return res;
    }

    public static String nameField(EStructuralFeature sf) {       
    	return sf.getEContainingClass().getName() + "." + sf.getName();
    }
}
