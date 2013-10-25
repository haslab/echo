package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.alloy.AlloyFactory;
import pt.uminho.haslab.echo.kodkod.KodkodFactory;
import pt.uminho.haslab.echo.transform.EchoTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:59 PM
 */
public abstract class EngineFactory {

    private static EngineFactory current;

    public static void init(EngineFactory factory)
    {
        current = factory;
    }

    public static EngineFactory instance(){
        return current;
    }

    public static final EngineFactory ALLOY = new AlloyFactory();
    public static final EngineFactory KODKOD = new KodkodFactory();


    public abstract EngineRunner createRunner();

    public abstract EchoTranslator createTranslator();



    //public abstract OCLTranslator createOCLTranslator();


}
