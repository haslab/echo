package pt.uminho.haslab.echo.engine.kodkod;

import pt.uminho.haslab.echo.CoreRunner;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.CoreFactory;

/**
 * Launches Kodkod transformation and runner objects.
 * 
 * @author tmg
 * @version 0.4 20/02/2014
 */
public class KodkodFactory implements CoreFactory {
	
	/** {@inheritDoc} */
	@Override
    public CoreRunner createRunner() {
        return new KodkodRunner();
    }

	/** {@inheritDoc} */
    @Override
    public CoreTranslator createTranslator() {
        return new KodkodTranslator();
    }
    
	public String toString() {
		return "Kodkod";
	}


}
