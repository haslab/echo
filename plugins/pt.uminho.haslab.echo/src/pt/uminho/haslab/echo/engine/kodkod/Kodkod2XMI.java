package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tmg on 2/27/14.
 *
 */
class Kodkod2XMI {




    private EObject root;
    private Map<Relation,TupleSet> mapTuples;
    private EKodkodMetamodel e2k;
    private HashMap<Object,EObject> objects;

    Kodkod2XMI(Instance ins, EObject root, EKodkodMetamodel e2k){

        
        this.mapTuples = ins.relationTuples();
        this.e2k = e2k;
        objects = new HashMap<>();
        EClass rootType =(EClass) e2k.getEClass(e2k.getRelation(root.eClass()));
        this.root =  processAtom(root,rootType);
    }


    public EObject getModel() {
        return root;
    }

    private EObject processAtom(EObject obj,EClass type) {
        EObject eObj = objects.get(obj);

        if(eObj == null){

            eObj = type.getEPackage().getEFactoryInstance().create(type);

            objects.put(obj,eObj);


            for(EStructuralFeature sf : type.getEAllStructuralFeatures()){
                Relation rel = e2k.getRelation(sf);

                TupleSet ts = mapTuples.get(rel);
                if(ts!=null){
                    if(sf instanceof EReference)
                        processReference(obj,ts,sf);
                    else if(sf instanceof EAttribute)
                        processEAttribute(obj,ts,sf);
                }
            }

        }

        return eObj;
    }

    private void processEAttribute(Object obj, TupleSet ts, EStructuralFeature sf) {
        EObject eObj = objects.get(obj);


        EAttribute att = (EAttribute) sf;
        if(att.getEType() instanceof EEnum){ //TODO
        }
        else if(att.getEType().getName().equals("EBoolean")){
            Iterator<Tuple> it = ts.iterator();
            boolean found = false;
            while(it.hasNext() && !found){
                Tuple t = it.next();
                if(t.atom(0).equals(obj))
                    found = true;
            }
            eObj.eSet(att,found);
        }
        else if(att.getEType().getName().equals("EString")) {
            Iterator<Tuple> it = ts.iterator();
            boolean found = false;
            while(it.hasNext() && !found){
                Tuple t = it.next();
                if(t.atom(0).equals(obj)){
                    eObj.eSet(att,((String) t.atom(1)).substring(3));
                    found = true;
                }
            }
        }
        else if(att.getEType().getName().equals("EInt")) {
            Iterator<Tuple> it = ts.iterator();
            boolean found = false;
            while(it.hasNext() && !found){
                Tuple t = it.next();
                if(t.atom(0).equals(obj)){
                    eObj.eSet(att,(Integer.parseInt( (String) t.atom(1))));
                    found = true;
                }
            }
        }
    }

    private void processReference(Object obj, TupleSet ts, EStructuralFeature sf) {
        EList<EObject> list = new BasicEList<>();
        EObject eObj =  objects.get(obj);

        EClass type =(EClass) sf.getEType();

        for(Tuple t : ts){
            if(t.atom(0).equals(obj)){
                EObject o =  processAtom(t.atom(1),type);
                list.add(o);
            }
        }
        
        EReference ref =(EReference)  sf;
        
        if(ref.isMany())
        	eObj.eSet(sf,list);
        else if(list.size() > 0)
        	eObj.eSet(sf, list.get(0));
    }


    private EObject processAtom(Object o, EClass type)
    {
        if(o instanceof String)
            return processAtom((String) o, type);
        else
            return processAtom((EObject)o,type);


    }

    private EObject processAtom(String str,EClass type){
        if(objects.get(str) != null)
            return objects.get(str);
        else{
            EObject res = type.getEPackage().getEFactoryInstance().create(type);

            objects.put(str,res);

            for(EStructuralFeature sf : type.getEAllStructuralFeatures()){
                Relation rel = e2k.getRelation(sf);

                TupleSet ts = mapTuples.get(rel);
                if(ts!=null){
                    if(sf instanceof EReference)
                        processReference(str,ts,sf);
                    else if(sf instanceof EAttribute)
                        processEAttribute(str,ts,sf);
                }
            }


            return res;
        }
    }

}
