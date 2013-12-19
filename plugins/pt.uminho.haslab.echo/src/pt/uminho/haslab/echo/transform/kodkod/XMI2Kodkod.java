package pt.uminho.haslab.echo.transform.kodkod;

import java.util.*;


import kodkod.ast.Relation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.util.Pair;

class XMI2Kodkod {

	
	private EObject eObj;

    private Ecore2Kodkod translator;

    /**
     * map of the objects in every class relation
     * Objects in the set are:
     * ->EObjects in case of Boolean Fields
     * ->Pair  EObject->Type.
     * */
    private Map<Relation,Set<Object>> bounds;
	
	XMI2Kodkod(EObject obj,Ecore2Kodkod t) throws EchoError {
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


    private void makeAtomsList(EObject it) throws EchoError {
        //TODO
        EClass cc = translator.getEClass(it.eClass().getName());
        Relation classRel = translator.getRelation(cc);

        //adding obj to corresponding class relation.
        Set<Object> auxSet = bounds.get(classRel);
        auxSet.add(it);
        //bounds.put(classRel,auxSet);


        //iterate trough every child
        for(EStructuralFeature sf : cc.getEAllStructuralFeatures()){

            Object value = it.eGet(sf);
            Relation relation = translator.getRelation(sf);
            Set<Object> set = bounds.get(relation);


            if(sf instanceof EAttribute)
                processAttribute(value, set, it);

            else if (sf instanceof EReference)

                if (value instanceof EList<?>) {
                    if (!((EList<?>) value).isEmpty()) {
                        EReference op = ((EReference) sf).getEOpposite();
                        if (op != null && translator.getRelation(op) == null) {}
                        else {
                            processReference((EList<?>) value, set, it);
                        }
                    }
                } else if (value instanceof EObject) {
                    EReference op = ((EReference) sf).getEOpposite();
                    if (op != null && translator.getRelation(op) == null) {}
                    else {
                        processReference((EObject) value, set, it);
                    }
                }

            else throw new ErrorUnsupported("Structural feature not supported: " + sf);
        }
	}

    private void processReference(EObject value, Set<Object> set, EObject it) {
                   set.add(new Pair<>(it,value));

    }

    private void processReference(EList<?> value, Set<Object> set, EObject it) throws EchoError{
        for(Object obj : value)
        {
            if(obj instanceof EObject)
                set.add(new Pair<>(it,obj));
            else
                throw new ErrorUnsupported(ErrorUnsupported.ECORE,
                "EReference type not supported: "
                        + obj.getClass().getName(), "",
                EchoRunner.Task.TRANSLATE_MODEL);
        }
    }


    private void processAttribute(Object obj, Set<Object> set, EObject it) throws EchoError {
        //TODO : check integer stuff, and add throw when type unsupported
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


            }else if(obj instanceof String || obj instanceof EEnumLiteral )
                pair = new Pair<>(it,obj);
            else
                throw new ErrorUnsupported(ErrorUnsupported.PRIMITIVE_TYPE,
                        "Primitive type not supported: "
                                + obj.getClass().getName(), "",
                        EchoRunner.Task.TRANSLATE_MODEL);
            set.add(pair);
        }
    }

}
