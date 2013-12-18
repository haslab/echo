package pt.uminho.haslab.echo.transform.kodkod;

import java.util.*;

import com.google.gson.internal.Pair;
import kodkod.ast.Relation;
import org.eclipse.emf.ecore.*;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;


class XMI2Kodkod {

	
	private EObject eObj;

    private Ecore2Kodkod translator;

    /*map of the objects in every class relation*/
    private Map<Relation,Set<Object>> bounds;
    private Set allAtoms;
	
	XMI2Kodkod(EObject obj,Ecore2Kodkod t) throws ErrorUnsupported, ErrorTransform {
		eObj =obj;
		translator = t;
        bounds = new HashMap<>();
        initBounds();
        makeAtomsList(eObj);

	}

    private void initBounds() {
        Collection<Relation> lRel = translator.getAllRelations();
        for(Relation rel : lRel)
            bounds.put(rel,new HashSet<>());
    }


    private void makeAtomsList(EObject it) throws ErrorUnsupported, ErrorTransform {
        //TODO
        EClass cc = translator.getEClassFromName(it.eClass().getName());
        Relation classRel = translator.getRelation(cc);

        //adding obj to corresponding class relation.
        Set<Object> auxSet = bounds.get(classRel);
        auxSet.add(it);
        bounds.put(classRel,auxSet);


        //iterate trough every child
        for(EStructuralFeature sf : cc.getEAllStructuralFeatures()){
            Object obj = it.eGet(sf);
            if(sf instanceof EAttribute)
                handleAttribute(obj,(EAttribute) sf,it);

            else if (sf instanceof EReference)

                handleReference(obj,(EReference) sf, it);

            else throw new ErrorUnsupported("Structural feature not supported: " + sf);
        }
	}

    private void handleReference(Object obj, EReference sf, EObject it) {
        //TODO
    }

    private void handleAttribute(Object obj, EAttribute attr, EObject it) throws ErrorTransform {
        //TODO
        Relation rel = translator.getRelation(attr);
        Set<Object> set = bounds.get(rel);
        if(obj instanceof Boolean)
            set.add(it);
        else
        {
            Pair<EObject,?> pair;
            if(obj instanceof Integer){
                Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
                Integer max = (int) (Math.pow(2, bitwidth) / 2);
                if ((Integer) obj >= max || (Integer) obj < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+obj+".");

                pair = new Pair<>(it,obj.toString());


            }else
                pair = new Pair<>(it,obj);
            set.add(pair);
        }
    }

}
