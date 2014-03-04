package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.engine.Solution;
import kodkod.instance.Instance;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
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
		return metaModels.containsKey(metamodelID);
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
	public void writeAllInstances(EchoSolution solution, String metaModelID, String modelUri) throws EchoError {
        EKodkodMetamodel e2k = metaModels.get(metaModelID);
        List<EClass> rootclasses = e2k.getRootClass();
        if (rootclasses.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootclasses);

        Instance instance = ((Solution) solution.getContents()).instance();

        EObject root =(EObject) instance.tuples(e2k.getRelation(rootclasses.get(0))).iterator().next().atom(0);

        writeXMIKodkod(instance,modelUri,e2k,root);
	}

	@Override
	public void writeInstance(EchoSolution solution, String modelID)
			throws EchoError {

        EKodkodModel x2k = models.get(modelID);
        EObject root = x2k.getModel().getRootEElement().getEObject();

        Instance instance = ((Solution) solution.getContents()).instance();

        EKodkodMetamodel e2k = x2k.getMetamodel();

        writeXMIKodkod(instance,x2k.getModel().getURI(),e2k,root);
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
		return  new KodkodExpression(
                Expression.NONE);
	}


	@Override
	public EKodkodTransformation getQVTTransformation(String qvtID) {
		return transKodkod.get(qvtID);
	}


	@Override
	public ITContext newContext() {
		return new KodkodContext();
	}

    private void writeXMIKodkod(Instance ins, String targetURI, EKodkodMetamodel e2k, EObject root) throws EchoError {
        Kodkod2XMI k2x = new Kodkod2XMI(ins,root,e2k);

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "*", new XMIResourceFactoryImpl());

        Resource resource = resourceSet.createResource(URI.createURI(targetURI));
        resource.getContents().add(k2x.getModel());

		/*
		* Save the resource using OPTION_SCHEMA_LOCATION save option toproduce
		* xsi:schemaLocation attribute in the document
		*/
        Map<Object,Object> options = new HashMap<Object,Object>();
        options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        try{
            resource.save(options);
        }catch (Exception e) {
            throw new ErrorTransform(e.getMessage());
        }

    }

}
