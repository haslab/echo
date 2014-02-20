package pt.uminho.haslab.echo.engine.alloy;

import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.TransformFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 7:05 PM
 */
public class AlloyFactory implements TransformFactory {


    @Override
    public EngineRunner createRunner() {
        return  new AlloyRunner();
    }

    @Override
    public EchoTranslator createTranslator() {
        return new AlloyEchoTranslator();
    }

    @Override
    public ITContext createContext() {
        return new AlloyContext();
    }
}
