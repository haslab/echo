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

    void repair(String targetUri) throws ErrorInternalEngine;

    void generate(String metaModelUri, Map<Entry<String, String>,Integer> scope) throws ErrorInternalEngine, ErrorUnsupported;

    void check(String qvtUri, List<String> modelUris) throws ErrorInternalEngine;

    void enforce(String qvtUri, List<String> modelUris, String targetUri) throws ErrorInternalEngine;

    void generateQvt(String qvtUri, List<String> modelUris, String targetUri, String metaModelUri) throws ErrorInternalEngine, ErrorUnsupported;

    void nextInstance() throws ErrorInternalEngine;

    EchoSolution getSolution();
}
