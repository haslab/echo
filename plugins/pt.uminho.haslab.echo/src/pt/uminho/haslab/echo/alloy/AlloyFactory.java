package pt.uminho.haslab.echo.alloy;

import pt.uminho.haslab.echo.EngineFactory;
import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.transform.EchoTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 7:05 PM
 */
public class AlloyFactory extends EngineFactory {


    @Override
    public EngineRunner createRunner() {
        return  new AlloyRunner();
    }

    @Override
    public EchoTranslator createTranslator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
