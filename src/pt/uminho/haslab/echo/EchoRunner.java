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
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class EchoRunner {
	
	private final EchoOptions options;
	private final EMFParser parser;
	private final EMF2Alloy translator;
	public final EchoTimer timer;
	private AlloyRunner runner;
	
	public EchoRunner (EchoOptions options) throws ErrorParser, ErrorAlloy, ErrorTransform {
		this.options = options;
		timer = new EchoTimer();
		parser = new EMFParser(options);
		translator = new EMF2Alloy(parser, options);
	}
	
	/**
	 * Parses and processes into Alloy a EPackage model from its URI
	 * @param uri the URI of the EPackage to load
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
	 * Parses and processes into Alloy a EObject instance from its URI
	 * @param uri the URI of the EObject to load
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
	
	public void addQVT(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser, Err{
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
	 * Generates instances for the given models
	 * If overall size greater than zero, tries to generate with exact scope
	 * Otherwise, tries to generate minimum (incremental)
	 * @param uris the URIs of the models
	 * @return
	 * @throws ErrorAlloy
	 * @throws ErrorParser 
	 * @throws ErrorSyntax 
	 */
	public boolean generate(List<String> uris) throws ErrorAlloy, ErrorParser, ErrorSyntax {
		if (options.getSize() != 0) {
			translator.createScopesFromSizes(options.getSize(), options.getScopes());
			runner = new AlloyRunner(translator);
			runner.generate(uris);
			return runner.getSolution().satisfiable();
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
			return runner.getSolution().satisfiable();				
		}
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
	 * @return true if success
	 * @throws ErrorAlloy
	 * @throws Err 
	 * @throws ErrorParser 
	 */
	public boolean enforce(String qvturi, List<String> insturis, String targetarg) throws ErrorAlloy, Err, ErrorParser {
		runner = new AlloyRunner(translator);
		runner.enforce(qvturi, insturis, targetarg);
		return runner.getSolution().satisfiable();
	}
	
	/**
	 * Searchs for an instance with larger bounds and delta (if defined)
	 * Can be used for instance generation (no delta) or QVT-R enforcement
	 * @return true if success
	 * @throws ErrorAlloy
	 * @throws Err 
	 * @throws ErrorParser 
	 */
	public boolean increment() throws ErrorAlloy, ErrorParser, ErrorSyntax {
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
	 * Retrieves the Alloy instance
	 * @return the Alloy instance, if satisfiable
	 * @throws ErrorAlloy 
	 */
	public A4Solution getAInstance() {
		if (runner.getSolution().satisfiable()) return runner.getSolution();
		else return null;
	}
	
	/**
	 * Generates the Alloy theme for a given instance
	 */
	public void generateTheme (VizState vizstate) {
		runner.generateTheme(vizstate);
	}
	
}

