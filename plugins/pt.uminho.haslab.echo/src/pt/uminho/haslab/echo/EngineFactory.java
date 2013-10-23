package pt.uminho.haslab.echo;

import pt.uminho.haslab.echo.transform.OCLTranslator;
import pt.uminho.haslab.echo.transform.TranslatorEngine;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:59 PM
 */
public abstract class EngineFactory {


    public abstract EngineRunner createRunner();

    public abstract TranslatorEngine createTranslator();

    //public abstract OCLTranslator createOCLTraslator();


}
