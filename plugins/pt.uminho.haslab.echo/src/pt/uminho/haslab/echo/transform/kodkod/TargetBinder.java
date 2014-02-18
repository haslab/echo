package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
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
    private final XMI2Kodkod x2k;

    TargetBinder(XMI2Kodkod x2k){
        this.x2k = x2k;
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

        for(Relation rel : x2k.getMetaTranslator().getClassRelations())
            bindClassRelation(rel,map.get(rel));



        for(Relation rel : x2k.getMetaTranslator().getSfRelations())
            bindSfRelation(rel,map.get(rel));

    }

    private void bindSfRelation(Relation rel, Set<Object> atoms) {


        Set<Tuple> targets = new HashSet<>();

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




        if(rel.arity()==2){
            Pair<Set<Relation>,Set<Relation>> type = x2k.getMetaTranslator().getRefTypes(rel);
        	if(type!=null){
                TupleSet leftTuples = factory.noneOf(1);
        	    for (Relation relation : type.left)
        		    leftTuples.addAll(bounds.upperBound(relation));

        	    TupleSet rightTuples = factory.noneOf(1);
        	    for(Relation relation :type.right)
        		    rightTuples.addAll(bounds.upperBound(relation));

        	    bounds.bound(rel,leftTuples.product(rightTuples));
            }else{

            }
        }
        else{
        	
        }

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

    @Override
    public Bounds getBounds() {
        return null;
    }
}
