package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.Universe;
import pt.uminho.haslab.echo.EchoOptionsSetup;

import java.util.HashSet;
import java.util.Set;

/**
 * Generic Binder behaviour.
 *
 */

abstract class AbstractBinder implements Binder{

    protected TupleFactory factory;
    protected Universe universe;
    protected Bounds bounds;


    protected void makeStringBounds(EKodkodModel x2k) {
        Set<String> strings = x2k.getStrings();
        Set<Tuple> tuples = new HashSet<>();
        for(String s: strings)
            tuples.add(factory.tuple(s));
        bounds.boundExactly(KodkodUtil.stringRel,factory.setOf(tuples));
    }

    protected void initNumbers() {

        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            bounds.boundExactly(i, factory.setOf(Integer.toString(i)));

    }

    protected Set<Object> numberCollection(){
        Set<Object> res =  new HashSet<>();


        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            res.add(Integer.toString(i));

        return res;
    }


}
