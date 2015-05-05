package pt.uminho.haslab.echo.engine.kodkod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.engine.Solution;
import kodkod.instance.Instance;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorTransform;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.engine.CoreTranslator;
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

/**
 * Manages the translation of Echo artifacts into Kodkod.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
public class KodkodTranslator extends CoreTranslator {
    public KodkodTranslator(){}


    public static KodkodTranslator getInstance() {
        return (KodkodTranslator) CoreTranslator.getInstance();
    }

    /** maps meta-models Uris into translators*/
    private Map<String,KodkodMetamodel> metamodelkodkods = new HashMap<>();
    /** maps models Uris into translators*/
    private Map<String,KodkodModel> models = new HashMap<>();
    /** maps models Uris into meta-models Uris*/
    private Map<String,String> model2metamodel = new HashMap<>();
    /** maps transformation IDs to the respective Kodkod translator*/
	private Map<String,KodkodTransformation> transKodkod = new HashMap<String,KodkodTransformation>();

	/** Translates meta-models to the respective Kodkod representation.
	 * @param metamodel the meta-model to be translated.
	 * @return the Kodkod representation.
	 */
    @Override
    public KodkodMetamodel translateMetamodel(EMetamodel metamodel) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_METAMODEL,
				metamodel.ID);

        KodkodMetamodel kodkodmm = new KodkodMetamodel(metamodel);
		// must be registred prior to translation
		metamodelkodkods.put(metamodel.ID,kodkodmm);
		try {
			kodkodmm.translate();
		} catch (Exception e) {
			metamodelkodkods.remove(metamodel.ID);
			throw e;
		}    
		
		EchoReporter.getInstance().result(Task.TRANSLATE_METAMODEL, metamodel.getEObject().getName(), true);

		return kodkodmm;
	}

	/** 
	 * Checks whether a meta-model has already been processed into Kodkod.
	 * @param metamodelID the ID of the meta-model.
	 * @return whether the meta-model has been processed.
	 */
    @Override
    public boolean hasMetamodel(String metamodelID) {
        return metamodelkodkods.containsKey(metamodelID);
    }
    
    /**
	 * Retrieves a meta-model from its ID.
	 * Should have been already processed.
	 * @param metamodelID the ID of the meta-model.
	 * @return the processed meta-model.
     */
	@Override
	public KodkodMetamodel getMetamodel(String metamodelID) {
		if (metamodelID == null)
			EchoReporter.getInstance().warning("Looking for null metamodel ID.", Task.TRANSLATE_METAMODEL);
		KodkodMetamodel metamodel = metamodelkodkods.get(metamodelID);
		if (metamodel == null)
			EchoReporter.getInstance().warning("Looking for non-existent metamodel: "+metamodelID, Task.TRANSLATE_METAMODEL);
		return metamodel;
	}
	
    /**
	 * Removes a processed meta-model from the manager.
	 * @param metamodelID the ID of the meta-model.
	 * @return whether the meta-model was removed.
     */
	@Override
	public boolean remMetamodel(String metamodelID) {
		return metamodelkodkods.remove(metamodelID) != null;
	}
	
    public KodkodModel getModel(String modelID){
        return models.get(modelID);
    }

    @Override
    public KodkodModel translateModel(EModel model) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_MODEL, model.ID);

        String metamodelID = model.getMetamodel().ID;
        KodkodMetamodel e2k = metamodelkodkods.get(metamodelID);
        KodkodModel x2k = new KodkodModel(model,e2k);
        models.put(model.ID,x2k);
        model2metamodel.put(model.ID, metamodelID);

		EchoReporter.getInstance().result(Task.TRANSLATE_MODEL, model.ID, true);

		return x2k;
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
    public KodkodTransformation translateTransformation(ETransformation transformation) throws EError {
        Map<String,List<EDependency>> deps = new HashMap<String,List<EDependency>>();
        for (ERelation r : transformation.getRelations()) {
            List<EDependency> aux2 = new ArrayList<EDependency>();
            for (EModelDomain dom : r.getDomains()) {
                List<EModelDomain> aux = new ArrayList<EModelDomain>(r.getDomains());
                aux.remove(dom);
                aux2.add(new EDependency(dom,aux,null));
            }
            deps.put(r.getName(),aux2);
        }
        return translateTransformation(transformation,deps);
    }
	
	public KodkodTransformation translateTransformation(ETransformation transformation, Map<String,List<EDependency>> deps) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_TRANSFORMATION,
				transformation.ID);

		KodkodTransformation kodkodtrans = new KodkodTransformation(transformation,deps);	
		transKodkod.put(transformation.ID, kodkodtrans);

		EchoReporter.getInstance().result(Task.TRANSLATE_TRANSFORMATION,
				transformation.ID, true);
		
		return kodkodtrans;
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
	public void writeInstance(EchoSolution solution, String modelID)
			throws EError {

        KodkodModel x2k = models.get(modelID);
        EObject root = x2k.getModel().getRootEElement().getEObject();

        Instance instance = ((Solution) solution.getContents()).instance();

        KodkodMetamodel e2k = x2k.getMetamodel();

        writeXMIKodkod(instance,x2k.getModel().getURI(),e2k,root);
	}

    @Override
    public IIntExpression makeNumber(int n) {
        return new KodkodIntExpression(IntConstant.constant(n));
    }

	@Override
	public IExpression getEmptyExpression() {
		return  new KodkodExpression(
                Expression.NONE);
	}


	@Override
	public KodkodTransformation getTransformation(String transformationID) {
		return transKodkod.get(transformationID);
	}


	@Override
	public ITContext newContext() {
		return new KodkodContext();
	}

    private void writeXMIKodkod(Instance ins, String targetURI, KodkodMetamodel e2k, EObject root) throws EError {
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
	    	throw new EErrorTransform(EErrorParser.MODEL,e.getMessage(),Task.TRANSLATE_MODEL);
        }

    }


	@Override
	public Set<String> strings() {
		// TODO Auto-generated method stub
		return null;
	}

}
