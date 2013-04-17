package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.transform.ECore2Alloy;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import pt.uminho.haslab.echo.transform.XMI2Alloy;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4graph.DotColor;
import edu.mit.csail.sdg.alloy4viz.AlloyRelation;
import edu.mit.csail.sdg.alloy4viz.AlloySet;
import edu.mit.csail.sdg.alloy4viz.AlloyType;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class AlloyRunner {
	
	/** the EMF translator, containing information about the EMF artifacts */
	private EMF2Alloy translator;
	/** the Alloy solution */
	private A4Solution sol;
	/** the Alloy command options*/
	private A4Options aoptions;
	/** the Alloy reporter*/
	private A4Reporter rep;
	
	/** the expression representing the delta (must be equaled to the desired delta)*/
	private Expr edelta = Sig.NONE.no();
	/** the final command fact (without the delta expression)*/
	private Expr finalfact = Sig.NONE.no();
	/** all the Alloy signatures of the model*/
	private Set<Sig> allsigs = new HashSet<Sig>(Arrays.asList(EMF2Alloy.STATE));
	
	/** the current delta value*/
	private int delta = 1;	
	/** the current int bitwidth*/
	private int intscope = 2;
	/** the current overall scope */
	private int overall = 0;
	/** the current specific scopes */
	private ConstList<CommandScope> scopes;

	/** 
	 * Constructs a new Alloy Runner that performs tests and generates instances
	 * @param translator the translator containing information about the EMF artifacts
	 */
	public AlloyRunner (EMF2Alloy translator) {	
		this.translator = translator;
		this.overall = translator.options.getSize();
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

	/**
	 * Tests the conformity of instances
	 * @param uris the URIs if the instances to be checked
	 * @throws ErrorAlloy
	 */
	public void conforms(List<String> uris) throws ErrorAlloy {
		for (String uri : uris) {
			addInstanceSigs(uri);
			finalfact = finalfact.and(translator.getInstanceFact(uri));
		}
		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}

	/** 
	 * Generates instances conforming to given models
	 * @param uris the URIs of the models
	 * @throws ErrorAlloy 
	 */
	public void generate(List<String> uris) throws ErrorAlloy {
		for (String uri : uris) {
			allsigs.addAll(translator.getModelSigsFromURI(uri));
		}
		scopes = translator.getScopes();
		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
		
	/** 
	 * Runs a QVT-R checking command
	 * @param qvturi the URI of the QVT-R transformation to be applied
	 * @param insturis the URIs of the instances to be checked
	 * @throws ErrorAlloy 
	 */
	public void check(String qvturi, List<String> insturis) throws ErrorAlloy {
		Func func = translator.getQVTFact(qvturi);
		List<PrimSig> sigs = new ArrayList<PrimSig>();
		for (String uri : insturis) {
			PrimSig state = addInstanceSigs(uri);
			sigs.add(state);
			finalfact = finalfact.and(translator.getInstanceFact(uri));
		}
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		try {
			Command cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/**
	 * Initializes q QVT-R enforcement command
	 * @param qvturi the URI of the QVT-R transformation
	 * @param insturis the URIs of the instances
	 * @param dirarg the direction of the transformation (one of the QVT-R arguments)
	 * @throws ErrorAlloy
	 */
	public void enforce(String qvturi, List<String> insturis, String dirarg) throws ErrorAlloy {
		check(qvturi,insturis);
		if (sol.satisfiable()) throw new ErrorAlloy ("Instances already consistent.");
		else {			
			allsigs = new HashSet<Sig>(Arrays.asList(EMF2Alloy.STATE));
			finalfact = Sig.NONE.no();
			Func func = translator.getQVTFact(qvturi);
			RelationalTransformation qvt = translator.getQVTTransformation(qvturi);
			PrimSig original, target;
			ECore2Alloy e2a = null;
			XMI2Alloy x2a;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			for (int i = 0 ; i < insturis.size(); i++) {
				String uri = insturis.get(i);
				PrimSig state = addInstanceSigs(uri);
				if (qvt.getModelParameter().get(i).getName().equals(dirarg)) {
					original = state;
					try { target = new PrimSig(original.label+"_new_", original.parent, Attr.ONE); }
					catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
					e2a = translator.getModelTranslator(original.parent.label);
					x2a = translator.getInstanceTranslator(uri);
					allsigs.add(target);
					sigs.add(target);
					edelta = e2a.getDeltaExpr(original, target);
					scopes = AlloyUtil.createScopeFromSigs(e2a.getSigList(), x2a.getSigMap());
				} else {
					sigs.add(state);			
				}
				finalfact = finalfact.and(translator.getInstanceFact(uri));
			}
			finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		} 
	}
	
	/**
	 * Increments the scopes and tries to generate an instance
	 * Increments the overall scope if different than zero and the concrete scopes if any
	 * @throws ErrorAlloy
	 */
	public void increment() throws ErrorAlloy {
		if (overall != 0) {
			scopes = AlloyUtil.incrementStringScopes(scopes);
			overall++;
			if (overall >= translator.options.getMaxDelta()) throw new ErrorAlloy ("Maximum delta reached.","AlloyRunner");
		}
		try {
			intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
			Command cmd = new Command(false, overall, intscope, -1, finalfact.and(edelta.equal(ExprConstant.makeNUMBER(++delta))));
			scopes = AlloyUtil.incrementScopes(scopes);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyRunner");}
	}
	
	/** 
	 * Calculates the next Alloy solution.
	 * @throws ErrorAlloy 
	 */
	public void nextInstance() throws ErrorAlloy  {
		try { sol = sol.next(); }
		catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Adds the signatures of an instance to this.allsigs
	 * @param uri the URI of the intance
	 * @return the signature representing the instance
	 */
	private PrimSig addInstanceSigs (String uri) {
		allsigs.addAll(translator.getInstanceSigs(uri));
		PrimSig state = translator.getInstanceStateSigFromURI(uri);		
		allsigs.add(state);
		allsigs.add(state.parent);
		allsigs.addAll(translator.getModelSigsFromName(state.parent.label));
		return state;
	}
	
	/**
	 * Generates the Alloy theme for a Viz instance
	 * @param vizstate the state where the instance is stored
	 */
	public void generateTheme(VizState vizstate) {
		List<DotColor> availablecolors = new ArrayList<DotColor>(Arrays.asList(DotColor.values()));
		for (AlloyType t : vizstate.getCurrentModel().getTypes()){
			String label = vizstate.label.get(t);
			if (label.split("_").length == 2) {
				vizstate.label.put(t, label.split("_")[1]);
				vizstate.hideUnconnected.put(t, true);
				vizstate.nodeColor.put(t, availablecolors.get(0));
				availablecolors.remove(0);
			} else if (label.split("_").length == 1) {
				if (t.getName().equals("State_")) {
					vizstate.project(t);
				}
				if (t.getName().equals("String") || t.getName().equals("Int"))
					vizstate.nodeVisible.put(t, false);
			} else if (label.split("_").length == 3) {
				vizstate.label.put(t, label.split("_")[1] + label.split("_")[2]);
			}		
		}
		for (AlloySet t : vizstate.getCurrentModel().getSets()){
			String label = vizstate.label.get(t);
			if (label.split("_").length == 2) {
				vizstate.label.put(t, label.split("_")[1]);
				if (label.endsWith("_")) vizstate.showAsLabel.put(t, false);
			}
		}
		for (AlloyRelation t : vizstate.getCurrentModel().getRelations()){
			String label = vizstate.label.get(t);
			if (label.split("_").length == 2) {
				String pck = label.split("_")[0];
				String ref = label.split("_")[1];
				AlloyType sig = t.getTypes().get(0);
				String cla = sig.getName().split("_")[1];
				ECore2Alloy e2a = translator.getModelTranslator(pck);
				if (e2a != null) {
					EStructuralFeature sf = e2a.getSFeatureFromName(ref, cla);
					if (sf instanceof EAttribute) {
						vizstate.edgeVisible.put(t, false);
						vizstate.attribute.put(t, true);
					}
					vizstate.label.put(t, ref);
				} else {
					vizstate.edgeVisible.put(t, false);
					vizstate.attribute.put(t, false);
				}
			}
			
		}
	}
	
	/** 
	 * Returns the Alloy solution.
	 * @return this.sol
	 */
	public A4Solution getSolution() {
		return sol;
	}

	/** 
	 * Returns the current delta value.
	 * @return this.delta
	 */
	public int getDelta() {
		return delta;
	}
	
	/** 
	 * Returns the current int bitwidth.
	 * @return this.instscope
	 */
	public int getIntScope() {
		return intscope;
	}	
	
	/** 
	 * Returns the final command fact (instance + qvt).
	 * @return this.fact
	 */
	public Expr getFact() {
		return finalfact;
	}	
	
	/** 
	 * Returns the current target scopes.
	 * @return this.targetscopes
	 */
	public ConstList<CommandScope> getScopes() {
		return scopes;
	}	
}
