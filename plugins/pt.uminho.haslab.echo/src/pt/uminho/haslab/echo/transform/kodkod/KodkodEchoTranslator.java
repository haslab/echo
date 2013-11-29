package pt.uminho.haslab.echo.transform.kodkod;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.EchoTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:41 PM
 */
public class KodkodEchoTranslator extends EchoTranslator {
    public KodkodEchoTranslator(){}

    //TODO

    @Override
    public void writeAllInstances(EchoSolution solution, String metaModelUri, String modelUri) throws ErrorTransform, ErrorUnsupported, ErrorInternalEngine {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeInstance(EchoSolution solution, String modelUri) throws ErrorInternalEngine, ErrorTransform {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getMetaModelFromModelPath(String path) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void translateMetaModel(EPackage metaModel) throws ErrorUnsupported, ErrorInternalEngine, ErrorTransform, ErrorParser {
        //TODO: Register meta-models already parsed.

        Ecore2Kodkod e2k = new Ecore2Kodkod(metaModel);
        e2k.translate();
    }

    @Override
    public void remMetaModel(String metaModelUri) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void translateModel(EObject model) throws ErrorUnsupported, ErrorInternalEngine, ErrorTransform, ErrorParser {
        /*TODO
        * Register models already parsed.
        * Get the appropriate meta-model
        */


        XMI2Kodkod x2k = new XMI2Kodkod(model,null);

    }

    @Override
    public void remModel(String modelUri) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void translateQVT(RelationalTransformation qvt) throws ErrorTransform, ErrorInternalEngine, ErrorUnsupported, ErrorParser {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void translateATL(EObject atl, EObject mdl1, EObject mdl2) throws ErrorTransform, ErrorInternalEngine, ErrorUnsupported, ErrorParser {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean remQVT(String qvtUri) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasMetaModel(String metaModelUri) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasModel(String modelUri) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public boolean hasQVT(String qvtUri) {
		// TODO Auto-generated method stub
		return false;
	}
}
