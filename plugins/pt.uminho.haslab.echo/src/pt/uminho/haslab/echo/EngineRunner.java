package pt.uminho.haslab.echo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 7:00 PM
 */
public interface EngineRunner {
    void conforms(List<String> modelIDs) throws ErrorInternalEngine;

    void show(List<String> modelIDs) throws ErrorInternalEngine;

    boolean repair(String modelID) throws ErrorInternalEngine;

    boolean generate(String metamodelID, Map<Entry<String, String>, Integer> scope, String target) throws ErrorInternalEngine, ErrorUnsupported, InterruptedException;

    void check(String transformationID, List<String> modelIDs) throws ErrorInternalEngine;

    boolean enforce(String transformationID, List<String> modelIDs, List<String> targetIDs) throws ErrorInternalEngine,InterruptedException;

    boolean generateQvt(String transformationID, List<String> modelIDs, String diruri, String metamodelID) throws ErrorInternalEngine, ErrorUnsupported,InterruptedException;

    void nextInstance() throws ErrorInternalEngine;

    EchoSolution getSolution();

    void cancel();
}
