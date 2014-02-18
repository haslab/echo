package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;
import kodkod.util.nodes.PrettyPrinter;
import pt.uminho.haslab.echo.*;

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

            System.out.println(PrettyPrinter.print(e2k.getFacts(),2));

            sol = solver.solve(e2k.getFacts(), new SATBinder(x2k).getBounds());
        }

    }

    @Override
    public boolean repair(String modelID) throws ErrorInternalEngine {
        EKodkodModel x2k = KodkodEchoTranslator.getInstance().getModel(modelID);
        final Solver solver = new Solver();

        solver.options().setSolver(SATFactory.PMaxSAT4J);
        solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());

        EKodkodMetamodel e2k = x2k.getMetamodel();

        System.out.println(PrettyPrinter.print(e2k.getFacts(),2));

        sol = solver.solve(e2k.getFacts(), new TargetBinder(x2k).getBounds());
   
        return sol.instance() != null;
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
