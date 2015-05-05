package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4viz.VizState;
import pt.uminho.haslab.echo.EchoOptionsSetup.EchoOptions;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.CoreFactory;
import pt.uminho.haslab.echo.engine.ast.CoreMetamodel;
import pt.uminho.haslab.echo.engine.ast.CoreModel;
import pt.uminho.haslab.echo.engine.ast.CoreTransformation;
import pt.uminho.haslab.echo.painter.GraphPainter;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Echo's main class, launches tasks given already processed artifacts.
 * 
 * @author nmm
 * @version 0.4 18/03/2015
 */
public class EchoRunner {

	/** the current engine runner; should be reseted between runs. */
	private CoreRunner runner = null;
    /** the service managing the problem execution.
     * only needed for non-atomic problems(generate, repair, enforce). */
	private ExecutorService executor = Executors.newFixedThreadPool(5);
    /** the current problem being executed. */
	private Future<Boolean> currentProblem = null;
	/** the solutions of the current problem. */
	private List<EchoSolution> solutions = new ArrayList<EchoSolution>();
	/** the index of the current shown solution. */
	private int current_solution = 0;
	
    public EchoRunner() throws EErrorUnsupported {
    	EchoOptions options = EchoOptionsSetup.getInstance();
    	if (options.getCore().equals(CoreFactory.KODKOD) && options.isOperationBased())
    		throw new EErrorUnsupported(EErrorUnsupported.KODKOD, "Operation-based not supported under Kodkod.", Task.ECHO_RUN);
        CoreTranslator.init(options.getCore());
        EchoReporter.getInstance().debug("Echo initialized with "+options.getCore().toString()+" core.");
    }
    
	/**
	 * Translates a meta-model into its engine representation.
	 * @param metamodel the the meta-model to be translated.
	 * @return 
	 * @throws EError
	 */
	public CoreMetamodel addMetamodel(EMetamodel metamodel) throws EError {
		return CoreTranslator.getInstance().translateMetamodel(metamodel);
	}

	/**
	 * Removes a meta-model from the system.
	 * @param metamodelID the ID of the meta-model to remove.
	 */
	public void remMetaModel(String metamodelID) {
		CoreTranslator.getInstance().remMetamodel(metamodelID);
	}

	/**
	 * Checks whether a meta-model exists in the system.
	 * @param metamodelID the ID of the meta-model.
	 * @return whether the meta-model has been processed.
	 */
	public boolean hasMetaModel(String metamodelID) {
		return CoreTranslator.getInstance().hasMetamodel(metamodelID);
	}

	/**
	 * Translates a model into Alloy
	 * @param model the EObject representing the model to translate
	 * @return 
	 * @throws EError
	 */
	public CoreModel addModel(EModel model) throws EError {
		return CoreTranslator.getInstance().translateModel(model);
	}

	/**
	 * Removes a model from the system
	 * @param modelID the URI of the model to remove
	 * @throws EError 
	 */
	public void remModel(String modelID) throws EError {
		CoreTranslator.getInstance().remModel(modelID);
		for (String c : EConstraintManager.getInstance().getConstraintsModel(modelID))
			removeConstraint(c);
	}
	
	public void reloadModel(EModel model) throws EError {
		CoreTranslator.getInstance().remModel(model.ID);
		CoreTranslator.getInstance().translateModel(model);
	}

	public EConstraint addConstraint(String transformationID, List<String> models) throws EError {
		return EConstraintManager.getInstance().addConstraint(transformationID, models);
	}
	
	public List<String> getConstraints() {
		return EConstraintManager.getInstance().getAllConstraints();
	}
	
	public List<String> getConstraintsTransformation(String transformationID) {
		List<String> constraints = EConstraintManager.getInstance().getConstraintsTransformation(transformationID);
		if (constraints == null) constraints = new ArrayList<String>();
		return constraints;
	}
	
	public List<String> getConstraintsModel(String modelID) {
		List<String> constraints = EConstraintManager.getInstance().getConstraintsModel(modelID);
		if (constraints == null) constraints = new ArrayList<String>();
		return constraints;
	}

	public void removeAllConstraint(String transID) throws EError {
		for (String c :  EConstraintManager.getInstance().getConstraintsTransformation(transID))
			removeConstraint(c);
	}
	
	public void removeConstraint(String c) throws EError {
		EConstraint cons = EConstraintManager.getInstance().getConstraintID(c);
		EConstraintManager.getInstance().removeConstraint(c);
		if (EConstraintManager.getInstance().getConstraintsTransformation(cons.transformationID) == null ||
				EConstraintManager.getInstance().getConstraintsTransformation(cons.transformationID).size() == 0)
			remTransformation(cons.transformationID);
	}
	
	/**
	 * Tests if a model exists in the system
	 * @param modelID the URI of the model
	 */
	public boolean hasModel(String modelID) {
		return CoreTranslator.getInstance().hasModel(modelID);
	}

	/**
	 * Translates a QVT-R transformation into Alloy 
	 * @param transformation the RelationalTransformation representing the QVT-R transformation to translate
	 * @return 
	 * @throws EError
	 */
	public CoreTransformation addTransformation(ETransformation transformation) throws EError {
		return CoreTranslator.getInstance().translateTransformation(transformation);
	}

	public boolean hasTransformation(String transformationID) {
		return CoreTranslator.getInstance().hasTransformation(transformationID);
	}
	
	public void remTransformation(String transformationID) {
		CoreTranslator.getInstance().remTransformation(transformationID);
	}

	/**
	 * Tests if a list of models conform to their meta-models
	 * @param modelIDs the URIs of the models to test conformity
	 * @return true if all models conform to the meta-models
	 * @throws EErrorCore
	 */
	public boolean conforms(List<String> modelIDs) throws EError {
		CoreRunner runner  = EchoOptionsSetup.getInstance().getCore().createRunner();
		runner.conforms(modelIDs);
		return runner.getSolution().satisfiable();
	}
	
	public boolean show(List<String> modeluris) throws EError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;
		runner = EchoOptionsSetup.getInstance().getCore().createRunner();
		runner.show(modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Repairs a model not conforming to its meta-model
	 * @param targetID the URI of the model to repair
	 * @return true if the model was successfully repaired
	 * @throws EErrorCore
	 */
	public boolean repair(final String targetID) {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner = EchoOptionsSetup.getInstance().getCore().createRunner();
		
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EExceptMaxDelta, EErrorCore, EExceptConsistent, EErrorParser {
           		return runner.repair(targetID);
            }
        };
        
        currentProblem = executor.submit(x);
		
		try {
			return currentProblem.get().booleanValue();
		} catch (Exception e) {
			runner.cancel();
			e.printStackTrace();
			return false;
		} 

	}

	/**
	 * Generates a model conforming to the given meta-model
	 * @param metamodelID the ID of the meta-model
	 * @param scope the exact scopes of the model to generate
	 * @param targetURI the URI for model instance to be generated
	 * @throws EException 
	 */
	public boolean generate(final Map<String,Map<String,Integer>> scope, final String targetID) throws EException {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;
		runner =  EchoOptionsSetup.getInstance().getCore().createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EError, EExceptMaxDelta {
            	EchoReporter.getInstance().start(Task.GENERATE_TASK, "Generating new model.");
            	EchoReporter.getInstance().debug("Mode: "+(EchoOptionsSetup.getInstance().isOperationBased()?"OBD":"GED")+".");
				runner.generate(targetID,scope);
            	EchoReporter.getInstance().result(Task.GENERATE_TASK, "Model generated.",true);
                return true;
            }
        };
        currentProblem = executor.submit(x);
        
		try {
			return currentProblem.get().booleanValue();
		} catch (ExecutionException  e) {
			runner.cancel();
			if (e.getCause() instanceof EException)
				throw (EException) e.getCause();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			runner.cancel();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
	}

	/**
	 * Checks if models are consistent according to a QVT-R transformation
	 * @param qvtID the URI of the QVT-R transformation
	 * @param modelIDs the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @return true if consistent
	 * @throws EErrorCore
	 */
	public boolean check(String constraintID) throws EError {
		CoreRunner runner =  EchoOptionsSetup.getInstance().getCore().createRunner();
		runner.check(constraintID);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Starts enforcement run according to a QVT-R transformation
	 * @param transformationID the URI of the QVT-R transformation
	 * @param modelIDs the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targetIDs the URI of the target model
	 * @return 
	 * @throws EErrorCore
	 */
	public boolean enforce(final String constraintID, final List<String> targetIDs) throws EError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner = EchoOptionsSetup.getInstance().getCore().createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EError, EExceptConsistent, EExceptMaxDelta {
                return runner.enforce(constraintID, targetIDs);
            }
        };
        
        currentProblem = executor.submit(x);
        
		try {
			return currentProblem.get().booleanValue();
		} catch (Exception e) {
			runner.cancel();
			e.printStackTrace();
			return false;
		}
    }

	/**
	 * Generates a model conforming to the given meta-model and consistent with existing models through a QVT-R transformation
	 * @param constraintID the URI of the QVT-R transformation
	 * @param metamodelID the URI of the meta-model
	 * @param modelIDs the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targetURI the URI of the new model
	 * @throws EException 
	 * @throws EErrorCore
	 * @throws EErrorUnsupported 
	 */
	public boolean batch(final Map<String,Map<String,Integer>> scope, final List<String> targetIDs, final String constraintID) throws EException {

		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;
		runner =  EchoOptionsSetup.getInstance().getCore().createRunner();
		
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EError, EExceptMaxDelta {
            	EchoReporter.getInstance().start(Task.BATCH_TASK, "Generating new model.");
            	EchoReporter.getInstance().debug("Mode: "+(EchoOptionsSetup.getInstance().isOperationBased()?"OBD":"GED")+".");
//            	EchoReporter.getInstance().debug("Root: "+root+".");
                runner.batch(constraintID, targetIDs, scope);
            	EchoReporter.getInstance().result(Task.BATCH_TASK, "Model generated.",true);
                return true;
            }
        };
        
        currentProblem = executor.submit(x);
        
		try {
			return currentProblem.get().booleanValue();
		} catch (ExecutionException  e) {
			runner.cancel();
			if (e.getCause() instanceof EException)
				throw (EException) e.getCause();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			runner.cancel();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		
		
		


	}


	/**
	 * Shows the next Alloy instance, if any
	 * @return true if able to generate another instance
	 * @throws EExceptMaxDelta 
	 * @throws EErrorCore
	 */
	public void next() throws EExceptMaxDelta, EErrorCore {
		current_solution++;
		if (current_solution >= solutions.size())
			runner.nextSolution();
	}
	
	public void previous() throws EError {
		if (current_solution > 0)
			current_solution--;
	}

	/**
	 * Retrieves the current Alloy instance
	 * @return the Alloy instance, if satisfiable
	 */
	public EchoSolution getAInstance() {
		EchoSolution sol = null;

		if (solutions.size() > current_solution && solutions.get(current_solution) != null) {
			sol = solutions.get(current_solution);
		}
		else if (runner != null && runner.getSolution() != null
				&& runner.getSolution().satisfiable()) {
			solutions.add(current_solution,runner.getSolution());
			sol = runner.getSolution();
		} 
		return sol;
	}

	/**
	 * Applies a generated Alloy theme for a given instance
	 * @param vizstate the state of the visualizer
	 * @throws EError 
	 */
	public void generateTheme (VizState vizstate) throws EError {
		new GraphPainter(vizstate).generateTheme();
	}

	/**
	 * Writes an existing instance from the current Alloy solution into XMI
	 * @param modelID the URI of the existing model
	 * @throws EErrorTransform 
	 * @throws EErrorCore
	 */
	public void writeInstance (String modelID) throws EError {
		EModel model = MDEManager.getInstance().getModelID(modelID);
        //if(currentOperation!=null && !currentOperation.isAlive())
            CoreTranslator.getInstance().writeInstance(runner.getSolution(), model.ID);
	}

    public void cancel(){
        if(currentProblem!=null) {
        	EchoReporter.getInstance().debug("Cancelling");
        	currentProblem.cancel(true);
        	EchoReporter.getInstance().debug("Cancelled");        	 
        }
    }
	
	public enum Task {
		ECHO_RUN("echorun"),
		PROCESS_RESOURCES("processresources"),
		CONFORMS_TASK("conformstask"),
		REPAIR_TASK( "repairtask"),
		CHECK_TASK("checktask"),
		ENFORCE_TASK("enforcetask"),
		GENERATE_TASK("generatetask"),
		BATCH_TASK("batchtask"),
		TRANSLATE_METAMODEL("translatemetamodel"),
		TRANSLATE_MODEL("translatemodel"), 
		TRANSLATE_OCL("translateocl"), 
		TRANSLATE_TRANSFORMATION("translatetransformation"), 
		CORE_RUN("corerun"), DRAW("draw"), PLUGIN("plugin");

		private Task(String label) { this.label = label; }

		private final String label;
		
		public String toString() {
			return label;
		}
	}

	public void backUpInstance(String targetPath) throws EErrorParser {
		MDEManager.getInstance().backUpTarget(targetPath);
	}


}