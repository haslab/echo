package pt.uminho.haslab.echo.transform;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:26 PM
 */
public abstract class EchoTranslator {

    private static EchoTranslator instance;

    public static EchoTranslator getInstance() {
        return instance;
    }

    public static void init(EngineFactory factory){
        instance = factory.createTranslator();
    }

    public abstract void writeAllInstances(EchoSolution solution, String metaModelUri, String modelUri) throws ErrorTransform, ErrorUnsupported, ErrorInternalEngine;

    public abstract void writeInstance(EchoSolution solution, String modelUri) throws ErrorInternalEngine, ErrorTransform;
}
