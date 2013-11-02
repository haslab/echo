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
    void conforms(List<String> modelUris) throws ErrorInternalEngine;

    void show(List<String> modelUris) throws ErrorInternalEngine;

    boolean repair(String modeluri) throws ErrorInternalEngine;

    boolean generate(String metaModelUri, Map<Entry<String, String>, Integer> scope) throws ErrorInternalEngine, ErrorUnsupported, InterruptedException;

    void check(String qvtUri, List<String> modelUris) throws ErrorInternalEngine;

    boolean enforce(String qvtUri, List<String> modelUris, String targetUri) throws ErrorInternalEngine,InterruptedException;

    boolean generateQvt(String qvturi, List<String> insturis, String diruri, String metamodeluri) throws ErrorInternalEngine, ErrorUnsupported,InterruptedException;

    void nextInstance() throws ErrorInternalEngine;

    EchoSolution getSolution();

    void cancel();
}
