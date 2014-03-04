package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.Universe;
import pt.uminho.haslab.echo.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Binder to use on Conforms and Checks.
 * Might be updated in the future to use Kodkod ?? l?? Echo
 */
class SATBinder extends AbstractBinder implements Binder {

	SATBinder(EKodkodModel x2k)
    {
        Set<Object> uni = numberCollection();
        uni.addAll(x2k.getUniverse());
        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        initNumbers();
        makeStringBounds(x2k);
        makeBounds(x2k);
    }




    SATBinder(Set<EKodkodModel> models,Collection<Relation>extraRels)
    {
        Set<Object> uni = numberCollection();
        for(EKodkodModel x2k : models){
            uni.addAll(x2k.getUniverse());
        }

        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        initNumbers();
        makeStringBounds(models);
        for(EKodkodModel x2k :models)
            makeBounds(x2k);
    }



    private void makeBounds (EKodkodModel x2k)
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
