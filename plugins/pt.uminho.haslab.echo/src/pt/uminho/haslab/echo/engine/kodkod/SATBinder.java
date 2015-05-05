package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import pt.uminho.haslab.echo.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Binder to use on Conforms and Checks.
 * Might be updated in the future to use Kodkod ?? l?? Echo
 */
class SATBinder extends AbstractBinder implements Binder {

	SATBinder(KodkodModel model) {
        Set<Object> uni = numberCollection();
        uni.addAll(model.getUniverse());
        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        bindInts();
        Set<KodkodModel> models = new HashSet<KodkodModel>();
        models.add(model);
        bindStrings(models);
        makeBounds(model);
    }

    SATBinder(Set<KodkodModel> models, Map<Relation, Pair<Set<Relation>, Set<Relation>>> extraRels) {
        Set<Object> uni = numberCollection();
        for(KodkodModel x2k : models){
            uni.addAll(x2k.getUniverse());
        }

        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        bindInts();
        bindStrings(models);
        for(KodkodModel x2k :models)
            makeBounds(x2k);

        for(Relation r: extraRels.keySet()){

            TupleSet leftTuples = factory.noneOf(1);
            for (Relation relation : extraRels.get(r).left)
                leftTuples.addAll(bounds.upperBound(relation));

            TupleSet rightTuples = factory.noneOf(1);
            for(Relation relation :extraRels.get(r).right)
                rightTuples.addAll(bounds.upperBound(relation));

            bounds.bound(r,leftTuples.product(rightTuples));

        }

    }



    private void makeBounds (KodkodModel x2k)
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
