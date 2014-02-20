package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<String,EKodkodModel> models = new HashMap<>();
    /** maps models Uris into meta-models Uris*/
    private Map<String,String> model2metaModel = new HashMap<>();
    /**maps transformationsID into EKodkodTransformation*/
    private Map<String,EKodkodTransformation> transformations = new HashMap<>();

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

    public EKodkodModel getModel(String modelID){
        return models.get(modelID);
    }

    @Override
    public void translateModel(EModel model) throws EchoError {
    	String modelID = model.ID;
        String metaModelID = model.getMetamodel().ID;
        EKodkodMetamodel e2k = metaModels.get(metaModelID);
        EKodkodModel x2k = new EKodkodModel(model,e2k);
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
        Map<String,List<EDependency>> deps = new HashMap<String,List<EDependency>>();
        for (ERelation r : constraint.getRelations()) {
            List<EDependency> aux2 = new ArrayList<EDependency>();
            for (EModelDomain dom : r.getDomains()) {
                List<EModelDomain> aux = new ArrayList<EModelDomain>(r.getDomains());
                aux.remove(dom);
                aux2.add(new EDependency(dom,aux,null));
            }
            deps.put(r.getName(),aux2);
        }
		EKodkodTransformation t2k = new EKodkodTransformation(constraint,deps);
        transformations.put(constraint.ID,t2k);
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

    @Override
	public EKodkodMetamodel getMetamodel(String id){
        return metaModels.get(id);
    }


	@Override
	public IExpression getEmptyExpression() {
		// TODO Auto-generated method stub
		return null;
	}


    public EKodkodTransformation getTransformation(String transformationID) {
        return null;
    }
}
