package pt.uminho.haslab.echo.engine.alloy;

import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.TransformFactory;

/**
 * Launches Alloy transformation and runner objects.
 * 
 * @author tmg
 * @version 0.4 20/02/2014
 */
public class AlloyFactory implements TransformFactory {

	/** {@inheritDoc} */
	@Override
    public EngineRunner createRunner() {
        return  new AlloyRunner();
    }

	/** {@inheritDoc} */
	@Override
    public EchoTranslator createTranslator() {
        return new AlloyEchoTranslator();
    }

}
