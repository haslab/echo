package pt.uminho.haslab.echo.engine;

import pt.uminho.haslab.echo.CoreRunner;
import pt.uminho.haslab.echo.engine.alloy.AlloyFactory;
import pt.uminho.haslab.echo.engine.kodkod.KodkodFactory;

/**
 * Defines Echo's core engines, either Alloy or Kodkod.
 *
 * @author tmg,nmm
 * @version 0.4 18/03/2015
 */
public interface CoreFactory {

    public static final CoreFactory ALLOY = new AlloyFactory();
    public static final CoreFactory KODKOD = new KodkodFactory();

    /** creates a new core runner for the selected engine */
    public CoreRunner createRunner();

    /** creates a new translator for the selected engine */
    public CoreTranslator createTranslator();

}
