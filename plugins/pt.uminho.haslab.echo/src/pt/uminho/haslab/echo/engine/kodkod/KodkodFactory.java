package pt.uminho.haslab.echo.engine.kodkod;

import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.TransformFactory;

/**
 * Launches Kodkod transformation and runner objects.
 * 
 * @author tmg
 * @version 0.4 20/02/2014
 */
public class KodkodFactory implements TransformFactory {
	
	/** {@inheritDoc} */
	@Override
    public EngineRunner createRunner() {
        return new KodkodRunner();
    }

	/** {@inheritDoc} */
    @Override
    public EchoTranslator createTranslator() {
        return new KodkodEchoTranslator();
    }

}
