package pt.uminho.haslab.echo;

import edu.mit.csail.sdg.alloy4viz.VizState;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.TransformFactory;
import pt.uminho.haslab.echo.engine.alloy.GraphPainter;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class EchoRunner {

	private EngineRunner runner = null;
    private TransformFactory transformFactory;
	private ExecutorService executor = Executors.newFixedThreadPool(5);
    private Future<Boolean> currentOperation = null;
	private List<EchoSolution> solutions = new ArrayList<EchoSolution>();
	private int current_solution = 0;
    public EchoRunner(TransformFactory factory) {
        transformFactory = factory;
        EchoTranslator.init(factory);
    }
    


	/**
	 * Translates a meta-model into Alloy
	 * @param metaModel the EPackage representing the meta-model to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addMetaModel(EMetamodel metaModel) throws EchoError {
		EchoTranslator.getInstance().translateMetaModel(metaModel);
	}

	/**
	 * Removes a meta-model from the system
	 * @param metamodelID the URI of the meta-model to remove
	 */
	public void remMetaModel(String metamodelID) {
		EchoTranslator.getInstance().remMetaModel(metamodelID);
	}

	/**
	 * Tests if a meta-model exists in the system
	 * @param metamodelID the URI of the meta-model
	 */
	public boolean hasMetaModel(String metamodelID) {
		return EchoTranslator.getInstance().hasMetaModel(metamodelID);
	}

	/**
	 * Translates a model into Alloy
	 * @param model the EObject representing the model to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addModel(EModel model) throws EchoError {
		EchoTranslator.getInstance().translateModel(model);
	}

	/**
	 * Removes a model from the system
	 * @param modelID the URI of the model to remove
	 * @throws EchoError 
	 */
	public void remModel(String modelID) throws EchoError {
		EchoTranslator.getInstance().remModel(modelID);
		for (EConstraint c : EConstraintManager.getInstance().getConstraintsModel(modelID))
			removeConstraint(c);
	}
	
	public void reloadModel(EModel model) throws EchoError {
		EchoTranslator.getInstance().remModel(model.ID);
		EchoTranslator.getInstance().translateModel(model);
	}

	public EConstraint addConstraint(ETransformation transformation, List<EModel> models) {
		return EConstraintManager.getInstance().addConstraint(transformation, models);
	}
	
	public List<EConstraint> getConstraints() {
		return EConstraintManager.getInstance().getAllConstraints();
	}
	
	public List<EConstraint> getConstraintsTransformation(String transformationID) {
		List<EConstraint> constraints = EConstraintManager.getInstance().getConstraintsTransformation(transformationID);
		if (constraints == null) constraints = new ArrayList<EConstraint>();
		return constraints;
	}
	
	public List<EConstraint> getConstraintsModel(String modelID) {
		List<EConstraint> constraints = EConstraintManager.getInstance().getConstraintsModel(modelID);
		if (constraints == null) constraints = new ArrayList<EConstraint>();
		return constraints;
	}

	public void removeAllConstraint(String qvtID) throws EchoError {
		for (EConstraint c :  EConstraintManager.getInstance().getConstraintsTransformation(qvtID))
			removeConstraint(c);
	}
	
	public void removeConstraint(EConstraint c) throws EchoError {
		EConstraintManager.getInstance().removeConstraint(c);
		if (EConstraintManager.getInstance().getConstraintsTransformation(c.transformation.ID) == null ||
				EConstraintManager.getInstance().getConstraintsTransformation(c.transformation.ID).size() == 0)
			remTransformation(c.transformation.ID);
	}
	
	/**
	 * Tests if a model exists in the system
	 * @param modelID the URI of the model
	 */
	public boolean hasModel(String modelID) {
		return EchoTranslator.getInstance().hasModel(modelID);
	}

	/**
	 * Translates a QVT-R transformation into Alloy 
	 * @param transformation the RelationalTransformation representing the QVT-R transformation to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addTransformation(ETransformation transformation) throws EchoError {
		EchoTranslator.getInstance().translateTransformation(transformation);
	}

	public boolean hasTransformation(String transformationID) {
		return EchoTranslator.getInstance().hasTransformation(transformationID);
	}
	
	public void remTransformation(String transformationID) {
		EchoTranslator.getInstance().remTransformation(transformationID);
	}

	/**
	 * Tests if a list of models conform to their meta-models
	 * @param modelIDs the URIs of the models to test conformity
	 * @return true if all models conform to the meta-models
	 * @throws ErrorInternalEngine
	 */
	public boolean conforms(List<String> modelIDs) throws EchoError {
		EngineRunner runner  = transformFactory.createRunner();
		runner.conforms(modelIDs);
		return runner.getSolution().satisfiable();
	}
	
	public boolean show(List<String> modeluris) throws EchoError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;
		runner = transformFactory.createRunner();
		runner.show(modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Repairs a model not conforming to its meta-model
	 * @param targetID the URI of the model to repair
	 * @return true if the model was successfully repaired
	 * @throws ErrorInternalEngine
	 */
	public void repair(String targetID) throws EchoError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner = transformFactory.createRunner();
		runner.repair(targetID);
	}

	/**
	 * Generates a model conforming to the given meta-model
	 * @param metamodelID the URI of the meta-model
	 * @param scope the exact scopes of the model to generate
	 * @param targetURI the URI for the generated model
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform 
	 * @throws ErrorUnsupported 
	 */
	public void generate(final String metamodelID, final Map<Entry<String,String>,Integer> scope, final String targetURI) throws EchoError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner =  transformFactory.createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EchoError {
                try {
					runner.generate(metamodelID,scope,targetURI);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
                return true;
            }
        };
        currentOperation = executor.submit(x);
        
		try {
			currentOperation.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			throw (EchoError) e.getCause();
		}
	}

	/**
	 * Checks if models are consistent according to a QVT-R transformation
	 * @param qvtID the URI of the QVT-R transformation
	 * @param modelIDs the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @return true if consistent
	 * @throws ErrorInternalEngine
	 */
	public boolean check(String qvtID, List<String> modelIDs) throws EchoError {
		EngineRunner runner =  transformFactory.createRunner();
		runner.check(qvtID, modelIDs);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Starts enforcement run according to a QVT-R transformation
	 * @param transformationID the URI of the QVT-R transformation
	 * @param modelIDs the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targetIDs the URI of the target model
	 * @return 
	 * @throws ErrorInternalEngine
	 */
	public boolean enforce(final String transformationID, final List<String> modelIDs, final List<String> targetIDs) throws EchoError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner = transformFactory.createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EchoError {
                try {
                    runner.enforce(transformationID, modelIDs, targetIDs);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
                return true;
            }
        };
        
        currentOperation = executor.submit(x);
        
		try {
			currentOperation.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw (EchoError) e.getCause();
		}
		return true;
    }

	/**
	 * Generates a model conforming to the given meta-model and consistent with existing models through a QVT-R transformation
	 * @param qvtUri the URI of the QVT-R transformation
	 * @param metaModelUri the URI of the meta-model
	 * @param modelUris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targetUri the URI of the new model
	 * @throws ErrorInternalEngine
	 * @throws ErrorUnsupported 
	 */
	public void generateQvt(final String qvtUri, final String metaModelUri, final List<String> modelUris, final String targetUri) throws EchoError {
		solutions = new ArrayList<EchoSolution>();
		current_solution = 0;

		runner =  transformFactory.createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EchoError {
                try {
                    runner.generateQvt(qvtUri, modelUris, targetUri, metaModelUri);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
                return true;
            }
        };
        
		try {
			currentOperation.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			throw (EchoError) e.getCause();
		}

	}


	/**
	 * Shows the next Alloy instance, if any
	 * @return true if able to generate another instance
	 * @throws ErrorInternalEngine
	 */
	public void next() throws EchoError {
		current_solution++;
		if (current_solution >= solutions.size())
			runner.nextInstance();
	}
	
	public void previous() throws EchoError {
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
	 * @throws EchoError 
	 */
	public void generateTheme (VizState vizstate) throws EchoError {
		new GraphPainter(vizstate).generateTheme();
	}

	/**
	 * Writes a new instance from the current Alloy solution into XMI
	 * @param metamodelID the URI of the meta-model of the new model
	 * @param modelURI the URI of the new model
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine
	 * @throws ErrorUnsupported 
	 */
	public void writeAllInstances (String metamodelID, String modelURI) throws EchoError {
		//if(currentOperation!=null && !currentOperation.isAlive())
            EchoTranslator.getInstance().writeAllInstances(runner.getSolution(), metamodelID, modelURI);
	}

	/**
	 * Writes an existing instance from the current Alloy solution into XMI
	 * @param modelID the URI of the existing model
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine
	 */
	public void writeInstance (String modelID) throws EchoError {
		EModel model = MDEManager.getInstance().getModel(modelID, false);
        //if(currentOperation!=null && !currentOperation.isAlive())
            EchoTranslator.getInstance().writeInstance(runner.getSolution(), model.ID);
	}

    public void cancel(){
        if(currentOperation!=null)
            currentOperation.cancel(true);
    }
	
	public enum Task {
		ECHO_RUN("echorun"),
		PROCESS_RESOURCES("processresources"),
		CONFORMS_TASK("conformstask"),
		REPAIR_TASK( "repairtask"),
		CHECK_TASK("checktask"),
		ENFORCE_TASK("enforcetask"),
		GENERATE_TASK("generatetask"),
		TRANSLATE_METAMODEL("translatemetamodel"),
		TRANSLATE_MODEL("translatemodel"), 
		TRANSLATE_OCL("translateocl"), 
		TRANSLATE_TRANSFORMATION("translatetransformation"), 
		ALLOY_RUN("alloyrun"), DRAW("draw");

		private Task(String label) { this.label = label; }

		private final String label;

		
		public String toString() {
			return label;
		}
	}

	public void backUpInstance(String targetPath) throws ErrorParser {
		MDEManager.getInstance().backUpTarget(targetPath);
	}


}