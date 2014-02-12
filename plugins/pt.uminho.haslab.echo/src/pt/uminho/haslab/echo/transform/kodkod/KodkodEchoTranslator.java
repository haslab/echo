package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.echo.transform.ast.IIntExpression;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:41 PM
 */
public class KodkodEchoTranslator extends EchoTranslator {
    public KodkodEchoTranslator(){}


    public static KodkodEchoTranslator getInstance() {
        return (KodkodEchoTranslator) EchoTranslator.getInstance();
    }

    /** maps meta-models Uris into translators*/
    private Map<String,EKodkodMetamodel> metaModels = new HashMap<>();
    /** maps models Uris into translators*/
    private Map<String,XMI2Kodkod> models = new HashMap<>();
    /** maps models Uris into meta-models Uris*/
    private Map<String,String> model2metaModel = new HashMap<>();

    @Override
    public void translateMetaModel(EMetamodel metaModel) throws EchoError {

        EKodkodMetamodel e2k = new EKodkodMetamodel(metaModel);
        metaModels.put(metaModel.ID, e2k);
        try {
            e2k.translate();
        } catch (EchoError e) {
            metaModels.remove(metaModel.ID);
            throw e;
        }
    }

    @Override
    public void remMetaModel(String metaModelUri) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    XMI2Kodkod getModel(String modelID){
        return models.get(modelID);
    }

    @Override
    public void translateModel(EModel model) throws EchoError {
    	String modelID = model.ID;
        String metaModelID = model.getMetamodel().ID;
        EKodkodMetamodel e2k = metaModels.get(metaModelID);
        XMI2Kodkod x2k = new XMI2Kodkod(model.getRootEElement().getEObject(),e2k);
        models.put(modelID,x2k);
        model2metaModel.put(modelID, metaModelID);
    }

	@Override
	public void remModel(String modelID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasModel(String modelID) {

		return models.containsKey(modelID);
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
		return new KodkodFormula(Formula.TRUE);
	}

    @Override
    public IFormula getFalseFormula() {
        return new KodkodFormula(Formula.FALSE);
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

    @Override
    public IIntExpression makeNumber(int n) {
        return new KodkodIntExpression(IntConstant.constant(n));
    }

    EKodkodMetamodel getMetaModel(String id){
        return metaModels.get(id);
    }


}
