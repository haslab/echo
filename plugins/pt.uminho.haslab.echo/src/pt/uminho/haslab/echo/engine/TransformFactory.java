package pt.uminho.haslab.echo.engine;

import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.engine.alloy.AlloyFactory;
import pt.uminho.haslab.echo.engine.kodkod.KodkodFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:59 PM
 */
public interface TransformFactory {


    public static final TransformFactory ALLOY = new AlloyFactory();
    public static final TransformFactory KODKOD = new KodkodFactory();

    public EngineRunner createRunner();

    public EchoTranslator createTranslator();



    //public abstract OCLTranslator createOCLTranslator();


}
