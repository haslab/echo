package pt.uminho.haslab.echo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.alloy.GraphPainter;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class EchoRunner {

	private AlloyRunner runner;
	private static EchoRunner instance = new EchoRunner();
	
	private EchoRunner() {}
 	
	public static EchoRunner getInstance() {
		return instance;
	}

	/**
	 * Translates a meta-model into Alloy
	 * @param metamodel the EPackage representing the meta-model to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addMetamodel(EPackage metamodel) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		EchoTranslator.getInstance().translateMetaModel(metamodel);
	}

	/**
	 * Removes a meta-model from the system
	 * @param metamodeluri the URI of the meta-model to remove
	 */
	public void remMetamodel(String metamodeluri) {
		EchoTranslator.getInstance().remMetamodel(metamodeluri);
	}

	/**
	 * Tests if a meta-model exists in the system
	 * @param metamodeluri the URI of the meta-model
	 */
	public boolean hasMetamodel(String metamodeluri) {
		return EchoTranslator.getInstance().getMetamodelStateSig(metamodeluri) != null;
	}

	/**
	 * Translates a model into Alloy
	 * @param model the EObject representing the model to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addModel(EObject model) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		EchoTranslator.getInstance().translateModel(model);
	}

	/**
	 * Removes a model from the system
	 * @param modeluri the URI of the model to remove
	 */
	public void remModel(String modeluri) {
		EchoTranslator.getInstance().remModel(modeluri);
	}

	/**
	 * Tests if a model exists in the system
	 * @param modeluri the URI of the model
	 */
	public boolean hasModel(String modeluri) {
		return EchoTranslator.getInstance().getModelStateSig(modeluri) != null;
	}

	/**
	 * Translates a QVT-R transformation into Alloy 
	 * @param qvt the RelationalTransformation representing the QVT-R transformation to translate
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addQVT(RelationalTransformation qvt) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		EchoTranslator.getInstance().translateQVT(qvt);
	}

	public boolean hasQVT(String qvturi) {
		return EchoTranslator.getInstance().getQVTFact(qvturi) != null;
	}
	
	public boolean remQVT(String qvturi) {
		return EchoTranslator.getInstance().remQVT(qvturi);
	}
	
	
		
	
	public void addATL(EObject atl, EObject mdl1, EObject mdl2) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		EchoTranslator.getInstance().translateATL(atl,mdl1,mdl2);
	}


	
	/**
	 * Tests if a list of models conform to their meta-models
	 * @param modeluris the URIs of the models to test conformity
	 * @return true if all models conform to the meta-models
	 * @throws ErrorAlloy
	 */
	public boolean conforms(List<String> modeluris) throws ErrorAlloy {
		runner = new AlloyRunner();
		runner.conforms(modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Repairs a model not conforming to its meta-model
	 * @param targeturi the URI of the model to repair
	 * @return true if the model was successfully repaired
	 * @throws ErrorAlloy
	 */
	public boolean repair(String targeturi) throws ErrorAlloy {
		runner = new AlloyRunner();
		runner.repair(targeturi);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Generates a model conforming to the given meta-model
	 * @param metamodeluri the URI of the meta-model
	 * @param scope the exact scopes of the model to generate
	 * @return true if able to generate conforming model
	 * @throws ErrorAlloy
	 * @throws ErrorTransform 
	 * @throws ErrorUnsupported 
	 */
	public boolean generate(String metamodeluri, Map<Entry<String,String>,Integer> scope) throws ErrorAlloy, ErrorUnsupported {
		runner = new AlloyRunner();	
		runner.generate(metamodeluri,scope);
		while (!runner.getSolution().satisfiable())
			runner.increment();
		return runner.getSolution().satisfiable();				
	}

	/**
	 * Checks if models are consistent according to a QVT-R transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @return true if consistent
	 * @throws ErrorAlloy
	 */
	public boolean check(String qvturi, List<String> modeluris) throws ErrorAlloy {
		runner = new AlloyRunner();
		runner.check(qvturi, modeluris);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Starts enforcement run according to a QVT-R transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targeturi the URI of the target model
	 * @return true if able to generate model
	 * @throws ErrorAlloy
	 */
	public boolean enforce(String qvturi, List<String> modeluris, String targeturi) throws ErrorAlloy {
		runner = new AlloyRunner();
		runner.enforce(qvturi, modeluris, targeturi);
		return runner.getSolution().satisfiable();
	}

	/**
	 * Generates a model conforming to the given meta-model and consistent with existing models through a QVT-R transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param metamodeluri the URI of the meta-model
	 * @param modeluris the URIs of the models (should be in the order of the QVT-R transformation arguments)
	 * @param targeturi the URI of the new model
	 * @return true if able to generate conforming model
	 * @throws ErrorAlloy
	 * @throws ErrorTransform 
	 * @throws ErrorUnsupported 
	 */
	public boolean generateqvt(String qvturi, String metamodeluri, List<String> modeluris, String targeturi) throws ErrorAlloy, ErrorUnsupported {
		runner = new AlloyRunner();
		runner.generateqvt(qvturi, modeluris, targeturi, metamodeluri);
		while (!runner.getSolution().satisfiable())
			runner.increment();
		return runner.getSolution().satisfiable();
	}

	/**
	 * Searches for a model with larger bounds and delta (if defined)
	 * Can be used for model generation (no delta) or QVT-R enforcement
	 * @return true if able to generate model
	 * @throws ErrorAlloy
	 */
	public boolean increment() throws ErrorAlloy {
		runner.increment();
		return runner.getSolution().satisfiable();
	}

	/**
	 * Shows the next Alloy instance, if any
	 * @return true if able to generate another instance
	 * @throws ErrorAlloy 
	 */
	public boolean next() throws ErrorAlloy {
		runner.nextInstance();
		return runner.getSolution().satisfiable();
	}

	/**
	 * Retrieves the current Alloy instance
	 * @return the Alloy instance, if satisfiable
	 */
	public A4Solution getAInstance() {
		if (runner != null && runner.getSolution()!= null && runner.getSolution().satisfiable()) return runner.getSolution();
		else return null;
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
	 * @throws ErrorAlloy 
	 * @throws ErrorUnsupported 
	 */
	public void writeAllInstances (String metamodeluri, String modeluri) throws ErrorAlloy, ErrorTransform, ErrorUnsupported {
		EchoTranslator.getInstance().writeAllInstances(runner.getSolution(), metamodeluri, modeluri,runner.getTargetStateSig());
	}

	/**
	 * Writes an existing instance from the current Alloy solution into XMI
	 * @param modeluri the URI of the existing model
	 * @throws ErrorTransform 
	 * @throws ErrorAlloy 
	 */
	public void writeInstance (String modeluri) throws ErrorAlloy, ErrorTransform {
		EchoTranslator.getInstance().writeInstance(runner.getSolution(), modeluri,runner.getTargetStateSig());
	}
	
	public enum Task {
		ECHO_RUN("echorun"),
		PROCESS_RESOURCES("processresources"),
		CONFORMS_TASK("conformstask"),
		REPAIR_TASK( "repairtask"),
		CHECK_TASK("checktask"),
		ENFORCE_TASK("enforcetask"),
		GENERATE_TASK("generatetask"),
		ITERATION("iteration");

		private Task(String label) { this.label = label; }

		private final String label;
		
		public String toString() {
			return label;
		}
	}


}