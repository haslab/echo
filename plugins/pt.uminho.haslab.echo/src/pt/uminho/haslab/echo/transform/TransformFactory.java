package pt.uminho.haslab.echo.transform;

import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.transform.alloy.AlloyFactory;
import pt.uminho.haslab.echo.transform.kodkod.KodkodFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:59 PM
 */
public interface TransformFactory {


    public static final TransformFactory ALLOY = new AlloyFactory();
    public static final TransformFactory KODKOD = new KodkodFactory();


    public abstract EngineRunner createRunner();

    public abstract EchoTranslator createTranslator();





    //public abstract OCLTranslator createOCLTranslator();


}
