package pt.uminho.haslab.echo.kodkod;

import pt.uminho.haslab.echo.EngineFactory;
import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.Monitor;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import pt.uminho.haslab.echo.transform.kodkod.KodkodEchoTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:31 PM
 */
public class KodkodFactory extends EngineFactory{
    @Override
    public EngineRunner createRunner() {
        return new KodkodRunner();
    }

    @Override
    public EchoTranslator createTranslator() {
        return new KodkodEchoTranslator();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
