package pt.uminho.haslab.echo.engine.kodkod;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Relation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ast.EEngineModel;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.model.EModel;

class EKodkodModel implements EEngineModel {

	private EModel emodel;

    private EKodkodMetamodel translator;

    public Map<Relation, Set<Object>> getBounds() {
        return bounds;
    }

	@Override
	public KodkodFormula getModelConstraint() {
		// TODO calculo que isto em Kodkod sejam os bounds?
		return null;
	}
	
    public Set<Object> getUniverse() {
        return universe;
    }

    /**
     * map of the objects in every class relation
     * Objects in the set are:
     * ->EObjects in case of Boolean Fields
     * ->Pair  EObject->Type.
     * */
    private Map<Relation,Set<Object>> bounds;

    private Set<Object> universe;

    public Set<String> getStrings() {
        return strings;
    }

    private Set<String> strings;
	
	EKodkodModel(EModel obj,EKodkodMetamodel t) throws EchoError {
		emodel = obj;
		translator = t;
        bounds = new HashMap<>();
        universe = new HashSet<>();
        strings = new HashSet<>();
        initBounds();
        makeAtomsList(emodel.getEObject());

	}

    private void initBounds() {
        Collection<Relation> lRel = translator.getAllRelations();
        for(Relation rel : lRel)
            bounds.put(rel,new HashSet<>());
    }


    private void makeAtomsList(EObject it) throws EchoError {
        //TODO
        Relation classRel = translator.getRelation(it.eClass());

        //adding obj to corresponding class relation.
        Set<Object> auxSet = bounds.get(classRel);
        auxSet.add(it);
        universe.add(it);
        //bounds.put(classRel,auxSet);

        //iterate trough every child
        for(EStructuralFeature sf : it.eClass().getEAllStructuralFeatures()){
            Object value = it.eGet(sf);
            Relation relation = translator.getRelation(sf);
            Set<Object> set = bounds.get(relation);


            if(sf instanceof EAttribute)
                processAttribute(value, set, it);

            else if (sf instanceof EReference){

                if (value instanceof EList<?>) {
                    if (!((EList<?>) value).isEmpty()) {
                        EReference op = ((EReference) sf).getEOpposite();
                        if (op != null && translator.getRelation(op) != null) {}
                        else {
                            processReference((EList<?>) value, set, it);
                        }
                    }
                } else if (value instanceof EObject) {
                    EReference op = ((EReference) sf).getEOpposite();
                    if (op != null && translator.getRelation(op) != null) {}
                    else {
                        processReference((EObject) value, set, it);
                    }
                }
            }
            else 
            	throw new ErrorUnsupported("Structural feature not supported: " + sf.getClass());
        }
	}

    private void processReference(EObject value, Set<Object> set, EObject it) throws EchoError {
        set.add(new Pair<>(it,value));
        if(!universe.contains(value))
            makeAtomsList(value);
    }

    private void processReference(EList<?> value, Set<Object> set, EObject it) throws EchoError{
    	for(Object obj : value)
        {
            if(obj instanceof EObject)
                processReference((EObject) obj,set,it);
            else
                throw new ErrorUnsupported(ErrorUnsupported.ECORE,
                "EReference type not supported: "
                        + obj.getClass().getName(), "",
                EchoRunner.Task.TRANSLATE_MODEL);
        }
    }


    private void processAttribute(Object obj, Set<Object> set, EObject it) throws EchoError {
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
                set.add(pair);


            }else if(obj instanceof String ){
                obj = "str" + obj;       //Putting a prefix to strings, to make creation of new strings easier
                pair = new Pair<>(it,obj);
                universe.add(obj);
                set.add(pair);
                strings.add((String) obj);
            }else if(obj instanceof EEnumLiteral){
                pair = new Pair<>(it,obj);
                universe.add(obj);
                set.add(pair);
            }
            else
                throw new ErrorUnsupported(ErrorUnsupported.PRIMITIVE_TYPE,
                        "Primitive type not supported: "
                                + obj.getClass().getName(), "",
                        EchoRunner.Task.TRANSLATE_MODEL);
            
        }
    }
    
	@Override
	public EKodkodMetamodel getMetamodel() {
		return translator;
	}

	@Override
	public EModel getModel() {
		return emodel;
	}

}
