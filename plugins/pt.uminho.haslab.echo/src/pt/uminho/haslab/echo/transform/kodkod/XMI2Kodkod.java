package pt.uminho.haslab.echo.transform.kodkod;

import java.util.*;

import kodkod.ast.Relation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import pt.uminho.haslab.echo.ErrorUnsupported;


public class XMI2Kodkod {

	
	private EObject eObj;

    private Ecore2Kodkod translator;

    /*map of the objects in every relation*/
    private Map<Relation,Set<Object>> bounds;
	
	XMI2Kodkod(EObject obj,Ecore2Kodkod t) throws ErrorUnsupported {
		eObj =obj;
		translator = t;
        bounds = new HashMap<>();
        initBounds();
        makeAtomsList(eObj);

	}

    private void initBounds() {
        Collection<Relation> lRel = translator.getClassRelations().values();
        for(Relation rel : lRel)
            bounds.put(rel,new TreeSet<>());
    }


    private void makeAtomsList(EObject it) throws ErrorUnsupported {
        //TODO
        EClass cc = translator.getEClassFromName(it.eClass().getName());
        Relation classRel = translator.getClassRelations().get(cc);

        //adding obj to corresponding class relation.
        Set<Object> auxSet = bounds.get(classRel);
        auxSet.add(it);
        bounds.put(classRel,auxSet);


        //iterate trough every child
        for(EStructuralFeature sf : cc.getEAllStructuralFeatures()){
            Object obj = it.eGet(sf);
            if(sf instanceof EAttribute)
                handleAttribute(obj,sf,it);

            else if (sf instanceof EReference)

                handleReference(obj,(EReference) sf, it);

            else throw new ErrorUnsupported("Structural feature not supported: " + sf);
        }
	}

    private void handleReference(Object obj, EReference sf, EObject it) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void handleAttribute(Object obj, EStructuralFeature sf, EObject it) {

    }

}
