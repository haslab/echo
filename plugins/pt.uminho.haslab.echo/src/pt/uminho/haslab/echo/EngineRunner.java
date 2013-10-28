package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;

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

    boolean repair(String targetUri) throws ErrorInternalEngine;

    boolean generate(String metaModelUri, Map<Entry<String, String>,Integer> scope) throws ErrorInternalEngine, ErrorUnsupported;

    void check(String qvtUri, List<String> modelUris) throws ErrorInternalEngine;

    boolean enforce(String qvtUri, List<String> modelUris, String targetUri) throws ErrorInternalEngine;

    boolean generateQvt(String qvtUri, List<String> modelUris, String targetUri, String metaModelUri) throws ErrorInternalEngine, ErrorUnsupported;

    void nextInstance() throws ErrorInternalEngine;

    EchoSolution getSolution();
}
