package pt.uminho.haslab.echo.transform.kodkod;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import pt.uminho.haslab.echo.transform.IFormula;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:41 PM
 */
public class KodkodEchoTranslator extends EchoTranslator {
    public KodkodEchoTranslator(){}

    /** maps meta-models Uris into translators*/
    private Map<String,Ecore2Kodkod> metaModels = new HashMap<>();
    /** maps models Uris into translators*/
    private Map<String,XMI2Kodkod> models = new HashMap<>();
    /** maps models Uris into meta-models Uris*/
    private Map<String,String> model2metaModel = new HashMap<>();

    @Override
    public void translateMetaModel(EMetamodel metaModel) throws ErrorUnsupported, ErrorInternalEngine, ErrorTransform, ErrorParser {
        //TODO: Register meta-models already parsed.

        Ecore2Kodkod e2k = new Ecore2Kodkod(metaModel.getEPackage());
        metaModels.put(metaModel.getURI(), e2k);
        try {
            e2k.translate();
        } catch (EchoError e) {
            metaModels.remove(metaModel.getURI());
            throw e;
        }
    }

    @Override
    public void remMetaModel(String metaModelUri) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void translateModel(EModel model) throws EchoError {
    	String modelUri = model.getURI();
        String metaModelURI = model.getMetamodel().getURI();
        Ecore2Kodkod e2k = metaModels.get(metaModelURI);
        XMI2Kodkod x2k = new XMI2Kodkod(model.getRootEElement().getEObject(),e2k);
        models.put(modelUri,x2k);
        model2metaModel.put(modelUri, metaModelURI);
    }

	@Override
	public void remModel(String modelID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasModel(String modelID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMetaModel(String metamodelID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void translateTransformation(ETransformation constraint)
			throws EchoError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasTransformation(String qvtID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remTransformation(String qvtID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IFormula getTrueFormula() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeAllInstances(EchoSolution solution, String metaModelUri,
			String modelUri) throws EchoError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeInstance(EchoSolution solution, String modelUri)
			throws EchoError {
		// TODO Auto-generated method stub
		
	}



}
