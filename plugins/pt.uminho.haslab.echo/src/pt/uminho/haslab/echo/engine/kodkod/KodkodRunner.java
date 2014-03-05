package pt.uminho.haslab.echo.engine.kodkod;


import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;
import kodkod.util.nodes.PrettyPrinter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:39 PM
 */
public class KodkodRunner implements EngineRunner{

    public KodkodRunner(){}   //TODO


    private KodkodSolution sol = null;

    @Override
    public void show(List<String> modelUris) throws ErrorInternalEngine {  //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public void conforms(List<String> modelIDs) throws ErrorInternalEngine {
        for (String modelID: modelIDs)
        {
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
            final Solver solver = new Solver();

            solver.options().setSolver(SATFactory.DefaultSAT4J);
            solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());

            EKodkodMetamodel e2k = x2k.getMetamodel();

            System.out.println(PrettyPrinter.print(e2k.getConforms(modelID).formula,2));

            Set<EKodkodMetamodel> meta = new HashSet<>();
            meta.add(x2k.getMetamodel());

            sol = new KodkodSolution(
                    solver.solve(e2k.getConforms(modelID).formula, new SATBinder(x2k).getBounds()),
                    meta);
        }

    }

    @Override
    public boolean repair(String modelID) throws ErrorInternalEngine {
        EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
        final Solver solver = new Solver();

        solver.options().setSolver(SATFactory.PMaxSAT4J);
        solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());

        EKodkodMetamodel e2k = x2k.getMetamodel();

        System.out.println(PrettyPrinter.print(e2k.getConforms(modelID).formula,2));

        Set<EKodkodMetamodel> meta = new HashSet<>();
        meta.add(x2k.getMetamodel());


        sol = new KodkodSolution(
                solver.solve(e2k.getConforms(modelID).formula, new TargetBinder(x2k).getBounds()),
                meta);
   
        return sol.satisfiable();
    }

    @Override
    public boolean generate(String metaModelUri, Map<Map.Entry<String, String>, Integer> scope, String targeturi) throws ErrorInternalEngine, ErrorUnsupported {
		return false;  //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void check(String transformationID, List<String> modelIDs) throws ErrorInternalEngine {
        EKodkodTransformation t2k = KodkodEchoTranslator.getInstance().getQVTTransformation(transformationID);

        Map<Relation,Pair<Set<Relation>,Set<Relation>>> relationTypes =  t2k.getRelationTypes();
        Formula facts = Formula.TRUE;
        Set<EKodkodModel> models = new HashSet<>();
        Set<EKodkodMetamodel> metas = new HashSet<>();
        for(String modelID : modelIDs){
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
            facts = facts.and(x2k.getMetamodel().getConforms(modelID).formula);
            models.add(x2k);
            metas.add(x2k.getMetamodel());
        }

        facts = facts.and(t2k.getConstraint(modelIDs).formula);

        final Solver solver = new Solver();

        solver.options().setSolver(SATFactory.DefaultSAT4J);
        solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());




        sol = new KodkodSolution(
                    solver.solve(facts,new SATBinder(models,relationTypes).getBounds()),
                    metas);
    }

    @Override
    public boolean enforce(String transformationID, List<String> modelIDs, List<String> targetIDs) throws ErrorInternalEngine {

        EKodkodTransformation t2k = KodkodEchoTranslator.getInstance().getQVTTransformation(transformationID);

        Map<Relation,Pair<Set<Relation>,Set<Relation>>> relationTypes =  t2k.getRelationTypes();


        Formula facts = Formula.TRUE;
        Set<EKodkodModel> models = new HashSet<>();
        Set<EKodkodMetamodel> metaModels = new HashSet<>();
        for(String modelID : modelIDs){
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
            models.add(x2k);
            metaModels.add(x2k.getMetamodel());
        }

        Set<EKodkodModel> targets = new HashSet<>();
        for(String targetID : targetIDs){
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(targetID);
            facts = facts.and(x2k.getMetamodel().getConforms(targetID).formula); 
            targets.add(x2k);
        }

        facts = facts.and(t2k.getConstraint(modelIDs).formula);

        final Solver solver = new Solver();

        solver.options().setSolver(SATFactory.PMaxSAT4J);
        solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());

        sol = new KodkodSolution(
                solver.solve(facts,new TargetBinder(models,targets,relationTypes).getBounds()),
                metaModels);

        return sol.satisfiable();
    }

    @Override
    public boolean generateQvt(String qvtUri, List<String> modelUris, String targetUri, String metaModelUri) throws ErrorInternalEngine, ErrorUnsupported {
		return false;      //TODO
		//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nextInstance() throws ErrorInternalEngine {  //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EchoSolution getSolution() {
       return sol;
    }

    @Override
    public void cancel() {   //TODO

    }

    private class KodkodSolution implements EchoSolution{
        Solution sol;
        Set<EKodkodMetamodel> metas;

        KodkodSolution(Solution s, Set<EKodkodMetamodel> metas)
        {
            sol = s;
            this.metas = metas;
        }

        @Override
        public boolean satisfiable() {
            return sol.instance() != null;
        }

        @Override
        public void writeXML(String filename) {
            InstanceViewer iv = new InstanceViewer(sol.instance().relationTuples(),metas);


            XtextResourceSet resourceSet = new XtextResourceSet();

            XMLResource resource = (XMLResource) resourceSet.createResource(URI.createURI(filename));
            resource.getContents().add(iv.getAlloyInstance());

            Map<Object,Object> options = new HashMap<>();
            //options.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
            try{
                resource.save(options);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object getContents() {
            return sol;
        }
    }
}
