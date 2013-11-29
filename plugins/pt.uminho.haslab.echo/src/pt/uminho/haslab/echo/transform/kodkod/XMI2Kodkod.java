package pt.uminho.haslab.echo.transform.kodkod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Relation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;


public class XMI2Kodkod {
	
	private List<?> atomList;
	
	private EObject eObj;
	
	private Ecore2Kodkod translator;

    /*map of the objects in every relation*/
    private Map<Relation,Set<Object>> bounds;
	
	XMI2Kodkod(EObject obj,Ecore2Kodkod t){
		eObj =obj;
		translator = t;
        bounds = new HashMap<>();
        makeAtomsList(eObj);
	}
	
	
	private void makeAtomsList(EObject it)
	{
        EClass cc = translator.getEClassFromName(it.eClass().getName());


	}

}
