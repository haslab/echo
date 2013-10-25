package pt.uminho.haslab.echo.kodkod;

import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:39 PM
 */
public class KodkodRunner implements EngineRunner{

    public KodkodRunner(){}

    @Override
    public void conforms(List<String> modelUris) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void repair(String targetUri) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void generate(String metaModelUri, Map<Map.Entry<String, String>, Integer> scope) throws ErrorInternalEngine, ErrorUnsupported {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void check(String qvtUri, List<String> modelUris) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enforce(String qvtUri, List<String> modelUris, String targetUri) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void generateQvt(String qvtUri, List<String> modelUris, String targetUri, String metaModelUri) throws ErrorInternalEngine, ErrorUnsupported {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nextInstance() throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EchoSolution getSolution() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
