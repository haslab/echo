package pt.uminho.haslab.echo;

import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;

import pt.uminho.haslab.echo.transform.AlloyUtil;
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
	
	/** the fact representing the model (instances + QVT)*/
	private Expr modelfact;
	/** the delta expression for the target model*/
	private Expr deltaexpr;
	/** the final command fact (model + delta)*/
	private Expr finalfact;
	/** all the Alloy signatures of the model*/
	private List<Sig> allsigs;
	/** the current delta value*/
	private int delta = -1;	
	/** the current int bitwidth*/
	private int intscope = 2;
	/** the current signature scopes for the target model */
	private ConstList<CommandScope> targetscopes;
	/** the path of the transformation */
	private String path;
	/** the Alloy solution */
	private A4Solution sol;
	/** the Alloy command options*/
	private A4Options options;
	/** the Alloy reporter*/
	private A4Reporter rep;
	/** the Alloy visualizer used to present instances */
	private VizGUI viz;

	/** Constructs a new Alloy Runner, that runs QVT transformations.
	 * 
	 * @param allsigs all the Alloy signatures of the model
	 * @param modelfact the fact representing the model (instances + QVT)
	 * @param deltaexpr the delta expression for the target model
	 * @param targetscopes the signature scopes for the target model
	 * @param path the path of the transformation
	 */
	public AlloyRunner (List<Sig> allsigs, Expr modelfact, Expr deltaexpr, ConstList<CommandScope> targetscopes, String path) {
		this.allsigs = allsigs;
		this.modelfact = modelfact;
		this.deltaexpr = deltaexpr;
		this.targetscopes = targetscopes;
		this.path = path;
		
		rep = new A4Reporter() {
			@Override public void warning(ErrorWarning msg) {
			System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
			System.out.flush();
			}
		};
		
		options = new A4Options();
		options.solver = A4Options.SatSolver.SAT4J;
		options.noOverflow = true;
		viz = new VizGUI(true, "", null);
	}

	/** Runs a QVT enforcement command for the current delta.
	 * @throws ErrorAlloy */
	public void enforce() throws ErrorAlloy {
		finalfact = modelfact.and(deltaexpr.equal(ExprConstant.makeNUMBER(++delta)));	
		if (delta == 0) intscope = 2;
		else intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));

		try {
			Command cmd = new Command(false, 0, intscope, -1, finalfact);
			targetscopes = AlloyUtil.incrementScopes(targetscopes);
			cmd = cmd.change(targetscopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/** Runs a QVT checking command.
	 * @throws ErrorAlloy */
	public void check() throws ErrorAlloy {
		finalfact = modelfact;	
		try {
			Command cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, options);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/** Opens the Alloy visualizer and presents the current solution.*/
	public void show() throws ErrorAlloy {
		if (!sol.satisfiable()) throw new ErrorAlloy ("Solution not satisfiable.","AlloyRunner");
		try{
			sol.writeXML("alloy_output.xml");
			viz.loadXML("alloy_output.xml", true);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
		String theme = (path).replace(".qvtr", ".thm");
		if (new File(theme).isFile()) viz.loadThemeFile(theme);		
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
}
