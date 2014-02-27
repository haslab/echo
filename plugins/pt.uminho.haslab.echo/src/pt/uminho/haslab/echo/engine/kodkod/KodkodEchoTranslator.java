package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ITContext;
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
 * Manages the translation of Echo artifacts into Kodkod.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
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
    /** maps QVT-R IDs to the respective Kodkod translator*/
	private Map<String,EKodkodTransformation> transKodkod = new HashMap<String,EKodkodTransformation>();


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
    public void translateTransformation(ETransformation constraint) throws EchoError {
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
        translateTransformation(constraint,deps);
    }
	
	public void translateTransformation(ETransformation constraint, Map<String,List<EDependency>> deps) throws EchoError {
		EKodkodTransformation qvtrans = new EKodkodTransformation(constraint,deps);	
		transKodkod.put(constraint.ID, qvtrans);
	}

	@Override
	public void remTransformation(String transformationID)  {
		transKodkod.remove(transformationID);
	}

	@Override
	public boolean hasTransformation(String transformationID) {
		return transKodkod.containsKey(transformationID);
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
	public void writeAllInstances(EchoSolution solution, String metaModelID,
			String modelUri) throws EchoError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeInstance(EchoSolution solution, String modelID)
			throws EchoError {



		
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


	@Override
	public EKodkodTransformation getQVTTransformation(String qvtID) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ITContext newContext() {
		return new KodkodContext();
	}



}
