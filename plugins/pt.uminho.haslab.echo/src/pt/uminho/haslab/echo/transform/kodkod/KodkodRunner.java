package pt.uminho.haslab.echo.transform.kodkod;

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

    public KodkodRunner(){}   //TODO

    @Override
    public void show(List<String> modelUris) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public void conforms(List<String> modelUris) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean repair(String targetUri) throws ErrorInternalEngine {
		return false;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean generate(String metaModelUri, Map<Map.Entry<String, String>, Integer> scope, String targeturi) throws ErrorInternalEngine, ErrorUnsupported {
		return false;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void check(String qvtUri, List<String> modelUris) throws ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean enforce(String qvtUri, List<String> modelUris, List<String> targetUri) throws ErrorInternalEngine {
		return false;
		//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean generateQvt(String qvtUri, List<String> modelUris, String targetUri, String metaModelUri) throws ErrorInternalEngine, ErrorUnsupported {
		return false;
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

    @Override
    public void cancel() {

    }
}
