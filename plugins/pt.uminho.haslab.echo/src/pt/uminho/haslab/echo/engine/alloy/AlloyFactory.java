package pt.uminho.haslab.echo.engine.alloy;

import pt.uminho.haslab.echo.CoreRunner;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.CoreFactory;

/**
 * Launches Alloy transformation and runner objects.
 * 
 * @author tmg
 * @version 0.4 20/02/2014
 */
public class AlloyFactory implements CoreFactory {

	/** {@inheritDoc} */
	@Override
    public CoreRunner createRunner() {
        return  new AlloyRunner();
    }

	/** {@inheritDoc} */
	@Override
    public CoreTranslator createTranslator() {
        return new AlloyTranslator();
    }

	public String toString() {
		return "Alloy";
	}
}
