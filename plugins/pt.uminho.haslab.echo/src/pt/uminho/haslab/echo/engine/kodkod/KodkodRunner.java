package pt.uminho.haslab.echo.engine.kodkod;


import kodkod.ast.Formula;
import kodkod.ast.Formula;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;
import kodkod.util.nodes.PrettyPrinter;
import pt.uminho.haslab.echo.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 12:39 PM
 */
public class KodkodRunner implements EngineRunner{

    public KodkodRunner(){}   //TODO


    private Solution sol;

    @Override
    public void show(List<String> modelUris) throws ErrorInternalEngine {
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

            sol = solver.solve(e2k.getConforms(modelID).formula, new SATBinder(x2k).getBounds());
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

        sol = solver.solve(e2k.getConforms(modelID).formula, new TargetBinder(x2k).getBounds());
   
        return sol.instance() != null;
    }

    @Override
    public boolean generate(String metaModelUri, Map<Map.Entry<String, String>, Integer> scope, String targeturi) throws ErrorInternalEngine, ErrorUnsupported {
		return false;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void check(String transformationID, List<String> modelIDs) throws ErrorInternalEngine {
        EKodkodTransformation t2k = KodkodEchoTranslator.getInstance().getQVTTransformation(transformationID);

        Formula facts = Formula.TRUE;
        Set<EKodkodModel> models = new HashSet<>();
        for(String modelID : modelIDs){
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
            facts = facts.and(x2k.getMetamodel().getConforms(modelID).formula);
            models.add(x2k);
        }

        facts = facts.and(t2k.getConstraint(modelIDs).formula);

        final Solver solver = new Solver();

        solver.options().setSolver(SATFactory.DefaultSAT4J);
        solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());

        sol = solver.solve(facts,new SATBinder(models).getBounds());
    }

    @Override
    public boolean enforce(String transformationID, List<String> modelIDs, List<String> targetIDs) throws ErrorInternalEngine {

        EKodkodTransformation t2k = KodkodEchoTranslator.getInstance().getQVTTransformation(transformationID);

        Formula facts = Formula.TRUE;
        Set<EKodkodModel> models = new HashSet<>();
        for(String modelID : modelIDs){
            EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
            models.add(x2k);
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

        sol = solver.solve(facts,new TargetBinder(models,targets).getBounds());

        return sol.instance() != null;
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
        if(sol!=null)
            return new EchoSolution() {
                @Override
                public boolean satisfiable() {
                    return sol.instance() != null;
                }

                @Override
                public void writeXML(String filename) {

                }

                @Override
                public Object getContents() {
                    return sol;
                }
            };
        else
            return null;
    }

    @Override
    public void cancel() {

    }
}
