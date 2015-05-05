package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.Universe;
import pt.uminho.haslab.echo.EchoOptionsSetup;

import java.util.HashSet;
import java.util.Set;

/**
 * Generic Binder behavior.
 * 
 * @author tmg,nmm
 * @version 0.4 23/03/2015
 */

abstract class AbstractBinder implements Binder{

    protected TupleFactory factory;
    protected Universe universe;
    protected Bounds bounds;


    public Bounds getBounds(){
        return bounds;
    }

    /**
     * Creates the string bounds from a set of models.
     * @param model
     */
    protected void makeStringBounds(Set<KodkodModel> models) {
        Set<Tuple> tuples = new HashSet<>();
        for(String s: stringCollection())
            tuples.add(factory.tuple(s));
        for(KodkodModel x2k: models){
            Set<String> strings = x2k.getStrings();
            for(String s: strings)
                tuples.add(factory.tuple(s));
        }
        bounds.boundExactly(KodkodUtil.stringRel,factory.setOf(tuples));
    }

    /**
     * Creates the string bounds from a model.
     * @param model
     */
    protected void bindStrings(Set<KodkodModel> models) {
        Set<String> strings = new HashSet<String>();
        for (KodkodModel model : models)
        	strings.addAll(model.getStrings());
        strings.addAll(stringCollection());
        Set<Tuple> tuples = new HashSet<>();
        for(String s: strings)
            tuples.add(factory.tuple(s));
        bounds.boundExactly(KodkodUtil.stringRel,factory.setOf(tuples));
    }

    /**
     * Sets the bounds of integer atoms.
     */
    protected void bindInts() {
        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            bounds.boundExactly(i, factory.setOf(Integer.toString(i)));
    }

    /**
     * Creates the string universe from the default scope.
     * @return
     */
    protected Set<String> stringCollection() {
	    Set<String> defaultstrings = new HashSet<String>();
		int overallscope = EchoOptionsSetup.getInstance().getOverallScope();
	    for (int i = 0; i < overallscope; i++) {
	    	defaultstrings.add("str\"String$"+i+"\"");
	    }
	    return defaultstrings;
    }
    
    /**
     * Creates the integer universe from the bitwidth.
     * @return
     */
    protected Set<Object> numberCollection(){
        Set<Object> res =  new HashSet<>();

        Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
        Integer max = (int) (Math.pow(2, bitwidth) / 2);

        for(int i=-max; i< max; i++)
            res.add(Integer.toString(i));

        return res;
    }
}
