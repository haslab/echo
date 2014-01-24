package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.Universe;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 11/29/13
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
class Binder {

    private TupleFactory factory;
    private Universe universe;
    private Bounds bounds;

    Binder(XMI2Kodkod x2k)
    {
        Set<Object> uni = numberCollection();
        uni.addAll(x2k.getUniverse());
        universe = new Universe(uni);
        bounds = new Bounds(universe);
        factory = universe.factory();
        initNumbers();
        makeStringBounds(x2k);

        Map<Relation,Set<Object>> map = x2k.getBounds();

        for(Relation rel : map.keySet())
        {
            Set<Tuple> tuples = new HashSet<>();
            for(Object obj: map.get(rel))
            {
                System.out.println("in cycle "+rel);
                if( obj instanceof Pair){
                    Pair<?, ?> p = (Pair<?, ?>) obj;
                    tuples.add(factory.tuple(p.left,p.right));
                }else
                    tuples.add(factory.tuple(obj));
            }
            if(!tuples.isEmpty())
            	bounds.boundExactly(rel,factory.setOf(tuples));
            else
            	System.out.println(rel);
        }
    }

    private void makeStringBounds(XMI2Kodkod x2k) {
        Set<String> strings = x2k.getStrings();
        Set<Tuple> tuples = new HashSet<>();
        for(String s: strings)
             tuples.add(factory.tuple(s));
        bounds.boundExactly(KodkodUtil.stringRel,factory.setOf(tuples));
    }

    private void initNumbers() {

        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            bounds.boundExactly(i, factory.setOf(Integer.toString(i)));

    }

    Set<Object> numberCollection(){
        Set<Object> res =  new HashSet<>();


        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            res.add(Integer.toString(i));

        return res;
    }


    public Bounds getBounds()
   {
       System.out.println(bounds);
        return bounds;
   }

}
