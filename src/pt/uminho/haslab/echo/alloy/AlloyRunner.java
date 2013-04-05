package pt.uminho.haslab.echo.alloy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class AlloyRunner {
	
	/** the final command fact (model + delta)*/
	private Expr finalfact;
	/** all the Alloy signatures of the model*/
	private List<Sig> allsigs = new ArrayList<Sig>(Arrays.asList(EMF2Alloy.STATE));
	/** the current delta value*/
	private int delta = -1;	
	/** the current int bitwidth*/
	private int intscope = 2;
	/** the echo options */
	private EchoOptions eoptions;
	private EMF2Alloy translator;
	/** the Alloy solution */
	private A4Solution sol;
	/** the Alloy command options*/
	private A4Options aoptions;
	/** the Alloy reporter*/
	private A4Reporter rep;
	/** the Alloy visualizer used to present instances */
	private VizGUI viz = null;

	private ConstList<CommandScope> targetscopes;

	/** Constructs a new Alloy Runner, that runs QVT transformations.
	 * 
	 * @param allsigs all the Alloy signatures of the model
	 * @param modelfact the fact representing the model (instances + QVT)
	 * @param deltaexpr the delta expression for the target model
	 * @param targetscopes the signature scopes for the target model
	 * @param path the path of the transformation
	 */
	public AlloyRunner (EMF2Alloy translator, EchoOptions eoptions) {
		
		this.eoptions = eoptions;
		this.translator = translator;
		this.targetscopes = translator.getTargetScopes();
		
		allsigs.addAll(translator.getModelSigs());
		for (Expr s : translator.getModelStateSigs()){
			//System.out.println(s);
			allsigs.add((Sig) s);
			}
		allsigs.addAll(translator.getInstanceSigs());
		allsigs.addAll(translator.getInstanceStateSigs());
		
		if (eoptions.isEnforce()) allsigs.add(translator.getTargetStateSig());
		
		rep = new A4Reporter() {
			@Override public void warning(ErrorWarning msg) {
				System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
				System.out.flush();
			}
		};
		
		aoptions = new A4Options();
		aoptions.solver = A4Options.SatSolver.SAT4J;
		aoptions.noOverflow = true;
	}

	/** Runs a QVT enforcement command for the current delta.
	 * @throws ErrorAlloy 
	 * @throws ErrorParser */
	public void enforce() throws ErrorAlloy, ErrorParser {
		if (delta < eoptions.getMaxDelta().intValue()) {
			finalfact = translator.getInstanceFact().and(translator.getQVTFact()).and(translator.getDeltaFact().equal(ExprConstant.makeNUMBER(++delta)));	
			if (delta == 0) intscope = 2;
			else intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
	
			try {
				Command cmd = new Command(false, 0, intscope, -1, finalfact);
				targetscopes = AlloyUtil.incrementScopes(targetscopes);
				cmd = cmd.change(targetscopes);
				sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
		} else throw new ErrorAlloy ("Maximum delta reached.","AlloyRunner");
	}
	
	/** Runs a QVT checking command.
	 * @throws ErrorAlloy */
	public void check() throws ErrorAlloy {
		finalfact = translator.getInstanceFact().and(translator.getQVTFact());	
		try {
			Command cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/** Tests conformity
	 * @throws ErrorAlloy */
	public void conforms() throws ErrorAlloy {
		finalfact = translator.getInstanceFact();
		//System.out.println(allsigs);
		try {
			Command cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	
	/** Opens the Alloy visualizer and presents the current solution.*/
	public void show() throws ErrorAlloy {
		if (!sol.satisfiable()) throw new ErrorAlloy ("Solution not satisfiable.","AlloyRunner");
		try{
			sol.writeXML("alloy_output.xml");
			if (viz == null) viz = new VizGUI(true, "alloy_output.xml", null);
			else viz.loadXML("alloy_output.xml", true);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
		if (eoptions.isQVT()) {
			String theme = (eoptions.getQVTPath()).replace(".qvtr", ".thm");
			if (new File(theme).isFile()) viz.loadThemeFile(theme);		
		}
	}
	
	/** Calculates the next Alloy solution.
	 * @throws ErrorAlloy */
	public void nextInstance() throws ErrorAlloy  {
		try {
			sol = sol.next();
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/** Closes the visualizer.*/
	public void closeViz() {
		SwingUtilities.getWindowAncestor(viz.getPanel()).dispose();
		new File("alloy_output.xml").delete();
	}
	
	/** Returns the Alloy solution.
	 * 
	 * @return this.sol
	 */
	public A4Solution getSolution() {
		return sol;
	}

	/** Returns the current delta value.
	 * 
	 * @return this.delta
	 */
	public int getDelta() {
		return delta;
	}
	
	/** Returns the current int bitwidth.
	 * 
	 * @return this.instscope
	 */
	public int getIntScope() {
		return intscope;
	}	
	
	/** Returns the final command fact.
	 * 
	 * @return this.fact
	 */
	public Expr getFact() {
		return finalfact;
	}	
	
	/** Returns the target scopes.
	 * 
	 * @return this.targetscopes
	 */
	public ConstList<CommandScope> getScopes() {
		return targetscopes;
	}	
}
