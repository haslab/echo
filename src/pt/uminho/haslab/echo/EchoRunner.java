package pt.uminho.haslab.echo;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import pt.uminho.haslab.echo.alloy.AlloyRunner;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class EchoRunner {
	
	public final EchoOptions options;
	public final EMFParser parser;
	public final EMF2Alloy translator;
	public final EchoTimer timer;
	private AlloyRunner runner;
	
	/**
	 * Creates a new EchoRunner which runs commands in Alloy
	 * @param options the Echo run options
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 */
	public EchoRunner (EchoOptions options) throws ErrorAlloy, ErrorTransform {
		this.options = options;
		timer = new EchoTimer();
		parser = new EMFParser(options);
		translator = new EMF2Alloy(parser, options);
	}
	
	/**
	 * Parses and processes into Alloy a {@link EPackage} model from its URI
	 * @param uri the URI of the package to load
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addModel(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser{
		EPackage mdl = parser.loadModel(uri);
		translator.createModelStateSigs(mdl);
		translator.translateModel(mdl);
	}

	/**
	 * Parses and processes into Alloy a {@link EObject} instance from its URI
	 * @param uri the URI of the model instance to load
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addInstance(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser{
		EObject inst = parser.loadInstance(uri);
		translator.createInstanceStateSigs(inst);
		translator.translateInstance(inst);
	}
	
	/**
	 * Parses and processes into Alloy a QVT-R transformation from its URI
	 * @param uri the URI of the QVT-R transformation to load
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorParser
	 */
	public void addQVT(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		parser.loadQVT(uri);
		translator.translateQVT(uri);
	}
	
	/**
	 * Tests of a list of instances conform to their models
	 * @param uri the URIs of the EObjects to test conformity
	 * @return true if the instance all instances conform to the models
	 * @throws ErrorAlloy
	 */
	public boolean conforms(List<String> uri) throws ErrorAlloy {
		runner = new AlloyRunner(translator);
		runner.conforms(uri);
		return runner.getSolution().satisfiable();
	}
	
	/**
	 * Repairs a list of instances not conforming to their models
	 * @param uri the URIs of the EObjects to repair
	 * @return true if the instance all instances conform to the models
	 * @throws ErrorAlloy
	 */
	public boolean repair(List<String> uri, String dir) throws ErrorAlloy {
		runner = new AlloyRunner(translator);
		runner.repair(uri,dir);
		return runner.getSolution().satisfiable();
	}
	
	/**
	 * Generates instances for the given models
	 * If overall size greater than zero, tries to generate with exact scope
	 * Otherwise, tries to generate minimum (incremental)
	 * @param uris the URIs of the models
	 * @return true if able to generate instance
	 * @throws ErrorAlloy
	 * @throws ErrorTransform 
	 */
	public boolean generate(List<String> uris) throws ErrorAlloy, ErrorTransform {
		if (options.getOverallScope() != 0) {
			translator.createScopesFromSizes(options.getOverallScope(), options.getScopes());
			runner = new AlloyRunner(translator);
			runner.generate(uris);
		}
		else {
			Map<Entry<String,String>,Integer> scopes = new HashMap<Entry<String,String>,Integer>();
			for (String uri : uris){
				EClass cla = parser.getTopObject(uri).get(0);
				scopes.put(new SimpleEntry<String,String>(cla.getEPackage().getName(),cla.getName()),1);
			}
			translator.createScopesFromSizes(1, scopes);
			runner = new AlloyRunner(translator);
			runner.generate(uris);
			while (!runner.getSolution().satisfiable()) {
				runner.increment();
				runner.generate(uris);
			}
		}
		return runner.getSolution().satisfiable();				
	}
	
	/**
	 * Checks if instances are consistent according to a QVT transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param insturis the URIs of the instances (should be in the order of the QVT-R transformation arguments)
	 * @return true if consistent
	 * @throws ErrorAlloy
	 */
	public boolean check(String qvturi, List<String> insturis) throws ErrorAlloy {
		runner = new AlloyRunner(translator);
		runner.check(qvturi, insturis);
		return runner.getSolution().satisfiable();
	}
	
	/**
	 * Starts enforcement run according to a QVT transformation
	 * @param qvturi the URI of the QVT-R transformation
	 * @param insturis the URIs of the instances (should be in the order of the QVT-R transformation arguments)
	 * @param targeturi the uri of the target instance
	 * @return true if able to generate instance
	 * @throws ErrorAlloy
	 */
	public boolean enforce(String qvturi, List<String> insturis, String targeturi) throws ErrorAlloy {
		runner = new AlloyRunner(translator);
		runner.enforce(qvturi, insturis, targeturi);
		return runner.getSolution().satisfiable();
	}
	
	/**
	 * Searches for an instance with larger bounds and delta (if defined)
	 * Can be used for instance generation (no delta) or QVT-R enforcement
	 * @return true if able to generate instance
	 * @throws ErrorAlloy
	 */
	public boolean increment() throws ErrorAlloy {
		runner.increment();
		return runner.getSolution().satisfiable();
	}

	/**
	 * Shows the next instance, if any
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
		if (runner.getSolution().satisfiable()) return runner.getSolution();
		else return null;
	}
	
	/**
	 * Applies a generated Alloy theme for a given instance
	 * @param vizstate the state of the visualizer
	 */
	public void generateTheme (VizState vizstate) {
		runner.generateTheme(vizstate);
	}
	
	/**
	 * Writes all instances of a given meta-model from the current Alloy solution into XMI
	 * @param uri the uri of the meta-model
	 * @throws ErrorTransform 
	 * @throws ErrorAlloy 
	 */
	public void writeAllInstances (String mmuri) throws ErrorAlloy, ErrorTransform {
		translator.writeAllInstances(runner.getSolution(), mmuri);
	}
	
	/**
	 * Writes a particular instance from the current Alloy solution into XMI
	 * @param uri the uri of the instance
	 * @throws ErrorTransform 
	 * @throws ErrorAlloy 
	 */
	public void writeInstance (String insturi) throws ErrorAlloy, ErrorTransform {
		translator.writeInstance(runner.getSolution(), insturi,runner.getTargetStateSig());
	}
	
	
	public String backUpInstance (String insturi) {
		return parser.backUpTarget(insturi);
	}
	
	public int getCurrentDelta() {
		return runner.getDelta();
	}
}

