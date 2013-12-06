package pt.uminho.haslab.echo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.alloy.GraphPainter;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class EchoRunner {

    //TODO: Finished Runner-> store the last finished runner, and use that one in write etc.
    private EngineRunner runner = null;
    private EngineFactory engineFactory;
	private ExecutorService executor = Executors.newFixedThreadPool(5);
    private Future<Boolean> currentOperation = null;
    public EchoRunner(EngineFactory factory) {
        engineFactory = factory;
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
	public void addMetaModel(EPackage metaModel) throws EchoError {
		EchoTranslator.getInstance().translateMetaModel(metaModel);
	}

	/**
	 * Removes a meta-model from the system
	 * @param metaModelUri the URI of the meta-model to remove
	 */
	public void remMetaModel(String metaModelUri) {
		EchoTranslator.getInstance().remMetaModel(metaModelUri);
	}

	/**
	 * Tests if a meta-model exists in the system
	 * @param metaModelUri the URI of the meta-model
	 */
	public boolean hasMetaModel(String metaModelUri) {
		return EchoTranslator.getInstance().hasMetaModel(metaModelUri);
	}

	/**
	 * Translates a model into Alloy
	 * @param model the EObject representing the model to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addModel(EObject model) throws EchoError {
		EchoTranslator.getInstance().translateModel(model);
	}

	/**
	 * Removes a model from the system
	 * @param modelUri the URI of the model to remove
	 */
	public void remModel(String modelUri) {
		EchoTranslator.getInstance().remModel(modelUri);
	}

	/**
	 * Tests if a model exists in the system
	 * @param modelUri the URI of the model
	 */
	public boolean hasModel(String modelUri) {
		return EchoTranslator.getInstance().hasModel(modelUri);
	}

	/**
	 * Translates a QVT-R transformation into Alloy 
	 * @param qvt the RelationalTransformation representing the QVT-R transformation to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addQVT(RelationalTransformation qvt) throws EchoError {
		EchoTranslator.getInstance().translateQVT(qvt);
	}

	public boolean hasQVT(String qvtUri) {
		return EchoTranslator.getInstance().getQVTFact(qvtUri) != null;
	}
	
	public boolean remQVT(String qvtUri) {
		return EchoTranslator.getInstance().remQVT(qvtUri);
	}
	
	
	public String getMetaModelFromModelPath(String path)
    {
        return EchoTranslator.getInstance().getMetaModelFromModelPath(path);
    }
	
	public void addATL(EObject atl, EObject mdl1, EObject mdl2) throws EchoError {
		EchoTranslator.getInstance().translateATL(atl,mdl1,mdl2);
	}


	
	/**
	 * Tests if a list of models conform to their meta-models
	 * @param modeluris the URIs of the models to test conformity
	 * @return true if all models conform to the meta-models
	 * @throws ErrorInternalEngine
	 */
	public boolean conforms(List<String> modeluris) throws EchoError {
		EngineRunner runner  = engineFactory.createRunner();
		runner.conforms(modeluris);
		return runner.getSolution().satisfiable();
	}
	
	public boolean show(List<String> modeluris) throws EchoError {
		runner = engineFactory.createRunner();
		runner.show(modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Repairs a model not conforming to its meta-model
	 * @param targeturi the URI of the model to repair
	 * @return true if the model was successfully repaired
	 * @throws ErrorInternalEngine
	 */
	public void repair(String targeturi) throws EchoError {
		runner = engineFactory.createRunner();
		runner.repair(targeturi);
	}

	/**
	 * Generates a model conforming to the given meta-model
	 * @param metamodeluri the URI of the meta-model
	 * @param scope the exact scopes of the model to generate
	 * @return true if able to generate conforming model
	 * @throws ErrorInternalEngine
	 * @throws ErrorTransform 
	 * @throws ErrorUnsupported 
	 */
	public void generate(final String metamodeluri, final Map<Entry<String,String>,Integer> scope) throws EchoError {
		runner =  engineFactory.createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EchoError {
                try {
					runner.generate(metamodeluri,scope);
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
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @return true if consistent
	 * @throws ErrorInternalEngine
	 */
	public boolean check(String qvturi, List<String> modeluris) throws EchoError {
		EngineRunner runner =  engineFactory.createRunner();
		runner.check(qvturi, modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Starts enforcement run according to a QVT-R transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targeturi the URI of the target model
	 * @return 
	 * @throws ErrorInternalEngine
	 */
	public boolean enforce(final String qvturi, final List<String> modeluris, final String targeturi) throws EchoError {
		runner = engineFactory.createRunner();
		Callable<Boolean> x = new Callable<Boolean>() {
            @Override
            public Boolean call() throws EchoError {
                try {
                    runner.enforce(qvturi, modeluris, targeturi);
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
		runner =  engineFactory.createRunner();
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
		runner.nextInstance();
	}

	/**
	 * Retrieves the current Alloy instance
	 * @return the Alloy instance, if satisfiable
	 */
	public EchoSolution getAInstance() {
		if (runner != null &&
                runner.getSolution()!= null &&
                runner.getSolution().satisfiable())
            return runner.getSolution();
		else
            return null;
	}

	/**
	 * Applies a generated Alloy theme for a given instance
	 * @param vizstate the state of the visualizer
	 */
	public void generateTheme (VizState vizstate) {
		new GraphPainter(vizstate).generateTheme();
	}

	/**
	 * Writes a new instance from the current Alloy solution into XMI
	 * @param metamodeluri the URI of the meta-model of the new model
	 * @param modeluri the URI of the new model
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine
	 * @throws ErrorUnsupported 
	 */
	public void writeAllInstances (String metamodeluri, String modeluri) throws EchoError {
		//if(currentOperation!=null && !currentOperation.isAlive())
            EchoTranslator.getInstance().writeAllInstances(runner.getSolution(), metamodeluri, modeluri);
	}

	/**
	 * Writes an existing instance from the current Alloy solution into XMI
	 * @param modeluri the URI of the existing model
	 * @throws ErrorTransform 
	 * @throws ErrorInternalEngine
	 */
	public void writeInstance (String modeluri) throws EchoError {
        //if(currentOperation!=null && !currentOperation.isAlive())
            EchoTranslator.getInstance().writeInstance(runner.getSolution(), modeluri);
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
		ITERATION("iteration"),
		TRANSLATE_METAMODEL("translatemetamodel");

		private Task(String label) { this.label = label; }

		private final String label;

		
		public String toString() {
			return label;
		}
	}

	public void backUpInstance(String targetPath) {
		EchoParser.getInstance().backUpTarget(targetPath);
	}


}