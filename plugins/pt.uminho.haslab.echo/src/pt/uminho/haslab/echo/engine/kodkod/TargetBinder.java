package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import kodkod.util.ints.IndexedEntry;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tmg on 2/11/14
 */
class TargetBinder extends AbstractBinder implements Binder{

    private Set<String> extras;

    TargetBinder(EKodkodModel x2k){
        createExtras();
        Set<Object> uni = numberCollection();
        uni.addAll(x2k.getUniverse());
        uni.addAll(extras);
        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        initNumbers();
        makeStringBounds(x2k);

        Map<Relation,Set<Object>> map = x2k.getBounds();

        for(Relation rel : x2k.getMetamodel().getClassRelations())
            bindClassRelation(rel,map.get(rel));

        for(Relation rel : x2k.getMetamodel().getSfRelations())
            bindSfRelation(rel,map.get(rel),x2k.getMetamodel());

    }



    TargetBinder(Set<EKodkodModel> models, Set<EKodkodModel> targets){
        createExtras();
        Set<Object> uni = numberCollection();
        for(EKodkodModel x2k: models)
            uni.addAll(x2k.getUniverse());
        uni.addAll(extras);
        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        initNumbers();
        makeStringBounds(models);


        models.removeAll(targets);
        for(EKodkodModel x2k : models){
            makeExactlyBounds(x2k);
        }

        for(EKodkodModel x2k : targets){
            Map<Relation,Set<Object>> map = x2k.getBounds();

            for(Relation rel : x2k.getMetamodel().getClassRelations())
                bindClassRelation(rel,map.get(rel));

            for(Relation rel : x2k.getMetamodel().getSfRelations())
                bindSfRelation(rel,map.get(rel),x2k.getMetamodel());
        }
    }

    private void bindSfRelation(Relation rel, Set<Object> atoms, EKodkodMetamodel e2k) {


        Set<Tuple> targets = new HashSet<>();



        if(rel.arity()==2){
            Pair<Set<Relation>,Set<Relation>> type = e2k.getRefTypes(rel);
            EStructuralFeature sf = e2k.getSf(rel);
            if(type!=null){
                TupleSet leftTuples = factory.noneOf(1);
        	    for (Relation relation : type.left)
        		    leftTuples.addAll(bounds.upperBound(relation));

        	    TupleSet rightTuples = factory.noneOf(1);
        	    for(Relation relation :type.right)
        		    rightTuples.addAll(bounds.upperBound(relation));

        	    bounds.bound(rel,leftTuples.product(rightTuples));
            }else if (sf.getEType().getName().equals("EInt")){
                Set<Relation> newType = e2k.getType(rel);

                TupleSet leftTuples = factory.noneOf(1);
                for (Relation relation : newType)
                    leftTuples.addAll(bounds.upperBound(relation));

                TupleSet rightTuples = factory.noneOf(1);
                for(IndexedEntry<TupleSet> ts :bounds.intBounds()){
                    rightTuples.addAll(ts.value());
                }

                bounds.bound(rel,leftTuples.product(rightTuples));
            }else if (sf.getEType().getName().equals("EString")){
                Set<Relation> newType = e2k.getType(rel);

                TupleSet leftTuples = factory.noneOf(1);
                for (Relation relation : newType)
                    leftTuples.addAll(bounds.upperBound(relation));

                TupleSet rightTuples = factory.noneOf(1);

                rightTuples.addAll(bounds.upperBound(KodkodUtil.stringRel));


                bounds.bound(rel,leftTuples.product(rightTuples));
            }else{       //EENUM
                Set<Relation> newType = e2k.getType(rel);

                TupleSet leftTuples = factory.noneOf(1);
                for (Relation relation : newType)
                    leftTuples.addAll(bounds.upperBound(relation));

                EEnum eenum = (EEnum) sf.getEType();

                Relation r = e2k.getRelation(eenum);

                TupleSet rightTuples = bounds.upperBound(r);

                bounds.bound(rel,leftTuples.product(rightTuples));

            }

        }
        else{
            Set<Relation> type = e2k.getBoolType(rel);
            TupleSet tuples = factory.noneOf(1);
            for(Relation relation : type)
            {
                tuples.addAll(bounds.upperBound(relation));
            }
            bounds.bound(rel,tuples);
        }

        for(Object obj: atoms)
        {
            if( obj instanceof Pair){
                Pair<?, ?> p = (Pair<?, ?>) obj;
                targets.add(factory.tuple(p.left, p.right));
            }else
                targets.add(factory.tuple(obj));
        }

        if(!targets.isEmpty())
            bounds.setTarget(rel,factory.setOf(targets));
        else
            bounds.setTarget(rel, factory.noneOf(rel.arity()));

    }

    private void bindClassRelation(Relation rel, Set<Object> atoms) {
        Set<Tuple> targets = new HashSet<>();
        Set<Tuple> total = new HashSet<>();

        for(Object obj: atoms)
        {
            targets.add(factory.tuple(obj));
            total.add(factory.tuple(obj));
        }

        for(String s: extras)
            total.add(factory.tuple(s));


        bounds.bound(rel, factory.setOf(total));

        if(!targets.isEmpty())
            bounds.setTarget(rel,factory.setOf(targets));
        else
            bounds.setTarget(rel,factory.noneOf(rel.arity()));
    }


    private void createExtras(){
        extras = new HashSet<>();
        int max = EchoOptionsSetup.getInstance().getMaxDelta();
        for(int i = 0; i<max; i++)
            extras.add("new"+i);
    }


    private void makeExactlyBounds (EKodkodModel x2k)
    {
        Map<Relation,Set<Object>> map = x2k.getBounds();

        for(Relation rel : map.keySet())
        {
            Set<Tuple> tuples = new HashSet<>();
            for(Object obj: map.get(rel))
            {
                if( obj instanceof Pair){
                    Pair<?, ?> p = (Pair<?, ?>) obj;
                    tuples.add(factory.tuple(p.left,p.right));
                }else
                    tuples.add(factory.tuple(obj));
            }
            if(!tuples.isEmpty())
                bounds.boundExactly(rel,factory.setOf(tuples));
            else
                bounds.boundExactly(rel, factory.noneOf(rel.arity()));
        }
    }
}
