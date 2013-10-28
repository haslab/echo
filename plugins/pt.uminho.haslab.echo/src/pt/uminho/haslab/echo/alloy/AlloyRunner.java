package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.transform.alloy.AlloyEchoTranslator;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.SubsetSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

public class AlloyRunner implements EngineRunner{
	
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
	private Set<Sig> allsigs = new HashSet<Sig>(Arrays.asList(AlloyEchoTranslator.STATE));
	
	/** the current delta value*/
	private int delta = 1;	
	/** the current int bitwidth*/
	private int intscope;
	/** the current overall scope */
	private int overall;
	/** the current specific scopes */
	private ConstList<CommandScope> scopes;
	/** the state signature of the target instance */	
	private PrimSig targetstate;
	
	private Command cmd = null;
	private Monitor monitor;
	
	/** 
	 * Constructs a new Alloy Runner that performs tests and generates instances
	 */
	public AlloyRunner (Monitor monitor) {	
		this.monitor = monitor;
		rep = new A4Reporter() {
			@Override public void warning(ErrorWarning msg) {
				EchoReporter.getInstance().debug("[Alloy] Warning:"+(msg.toString().trim()));
			}

			@Override public void resultSAT (Object command, long solvingTime, Object solution) {
				EchoReporter.getInstance().debug("[Alloy] SAT time: "+ solvingTime+"ms");
			}

			@Override public void resultUNSAT (Object command, long solvingTime, Object solution) {
				EchoReporter.getInstance().debug("[Alloy] UNSAT time: "+ solvingTime+"ms");
			}

			@Override public void solve(int primaryVars, int totalVars, int clauses) {
				EchoReporter.getInstance().debug("[Alloy] Primary vars: "+ primaryVars+", vars: "+totalVars+", clauses: "+clauses);
			}
		};
		aoptions = new A4Options();
		aoptions.solver = A4Options.SatSolver.SAT4J;
		aoptions.noOverflow = true;
		intscope = EchoOptionsSetup.getInstance().getBitwidth();
		overall = EchoOptionsSetup.getInstance().getOverallScope();
        sol = null;
    }

	/**
	 * Tests the conformity of instances
	 * @param modeluris the URIs if the instances to be checked
	 * @throws ErrorAlloy
	 */
	public void conforms(List<String> modeluris) throws ErrorAlloy {
		for (String modeluri : modeluris) {
			addInstanceSigs(modeluri);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri));
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(modeluri));
		}
		
		/*for (Sig sig : allsigs) {
			EchoReporter.getInstance().debug(sig +" : "+((PrimSig)sig).parent + " : " + sig.attributes);
			for (Field f : sig.getFields())
				EchoReporter.getInstance().debug(f.label +" : "+f.type());
		}*/
		
		try {
			cmd = new Command(true, overall, intscope, -1, finalfact);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		try {
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}

	/**
	 * Initializes a repair command
	 * @param modeluri the URIs of the instances
	 * @throws ErrorAlloy
	 */
	public boolean repair(String modeluri) throws ErrorAlloy {
		if (EchoOptionsSetup.getInstance().isOperationBased())
			AlloyEchoTranslator.getInstance().createScopesFromOps(modeluri);
		else
			AlloyEchoTranslator.getInstance().createScopesFromURI(modeluri);
		conforms(new ArrayList<String>(Arrays.asList(modeluri)));	
		if (sol.satisfiable()) throw new ErrorAlloy ("Instances already consistent.");
		else {			
			try {
				scopes = AlloyEchoTranslator.getInstance().getScopes(cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			allsigs = new HashSet<Sig>(Arrays.asList(AlloyEchoTranslator.STATE));
			finalfact = Sig.NONE.no();
			PrimSig original;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			PrimSig state = addInstanceSigs(modeluri);
			original = state;
			try { 
				targetstate = new PrimSig("'"+original.label, original.parent, Attr.ONE); 
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
			allsigs.add(targetstate);
			sigs.add(targetstate);
			String metamodeluri = AlloyEchoTranslator.getInstance().getModelMetamodel(modeluri);
			edelta = AlloyEchoTranslator.getInstance().getMetamodelDeltaExpr(metamodeluri).call(original, targetstate).cardinality();
			edelta = edelta.iplus(AlloyEchoTranslator.getInstance().getMetamodelDeltaRelFunc(metamodeluri).call(original, targetstate));
			AlloyEchoTranslator.getInstance().createScopesFromURI(modeluri);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri, targetstate));
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(modeluri));
            while(!sol.satisfiable()) {
            	if (monitor.isCancelled()) return false;
            	if (delta >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
            	if (overall >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
            	increment();
            }
            return true;
		} 
	}
	
	/** 
	 * Generates instances conforming to given models
	 * @param metaModelUri the URIs of the models
	 * @throws ErrorAlloy 
	 * @throws ErrorUnsupported 
	 */
	public boolean generate(String metaModelUri, Map<Entry<String,String>,Integer> scope) throws ErrorAlloy, ErrorUnsupported {
		List<EClass> rootobjects = AlloyEchoTranslator.getInstance().getRootClass(metaModelUri);
		if (rootobjects.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootobjects);

		if (scope.get(rootobjects.get(0).getName()) == null)
			scope.put(new SimpleEntry<String,String>(URIUtil.resolveURI(rootobjects.get(0).getEPackage().eResource()),rootobjects.get(0).getName()),1);
		AlloyEchoTranslator.getInstance().createScopesFromSizes(EchoOptionsSetup.getInstance().getOverallScope(), scope, metaModelUri);
		
		allsigs.addAll(AlloyEchoTranslator.getInstance().getMetamodelSigs(metaModelUri));
		scopes = AlloyEchoTranslator.getInstance().getScopes();
		
		PrimSig state = (PrimSig) AlloyEchoTranslator.getInstance().getMetaModelStateSig(metaModelUri);
		try { 
			targetstate = new PrimSig("'"+state, state, Attr.ONE); 
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }

		finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getGenerateInstance(metaModelUri,targetstate));
		allsigs.add(targetstate);

		try {
			//System.out.println(finalfact);
			//System.out.println(allsigs);
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			//System.out.println("DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
        while(!sol.satisfiable()) {
        	if (monitor.isCancelled()) return false;
        	if (delta >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
        	if (overall >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
        	increment();
        }
        return true;
	}
		
	/** 
	 * Runs a QVT-R checking command
	 * @param qvturi the URI of the QVT-R transformation to be applied
	 * @param insturis the URIs of the instances to be checked
	 * @throws ErrorAlloy 
	 */
	public void check(String qvturi, List<String> insturis) throws ErrorAlloy {
		Func func = AlloyEchoTranslator.getInstance().getQVTFact(qvturi);
		List<PrimSig> sigs = new ArrayList<PrimSig>();
		for (String uri : insturis) {
			PrimSig state = addInstanceSigs(uri);
			sigs.add(state);
			//System.out.println("Model fact: "+ uri);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(uri));
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(uri));
		}
		//EchoReporter.getInstance().debug("Check: FACT "+func.getBody());
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		
		//EchoReporter.getInstance().debug("Check: DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);
		//EchoReporter.getInstance().debug("Check: SIGS "+allsigs);
		//EchoReporter.getInstance().debug("Check: FACT "+finalfact);
		try {
			cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Initializes q QVT-R enforcement command
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the instances
	 * @param diruri the URI of the target model
	 * @return 
	 * @throws ErrorAlloy
	 */
	public boolean enforce(String qvturi, List<String> modeluris, String diruri) throws ErrorAlloy {
		if (EchoOptionsSetup.getInstance().isOperationBased())
			AlloyEchoTranslator.getInstance().createScopesFromOps(diruri);
		else
			AlloyEchoTranslator.getInstance().createScopesFromURI(diruri);
		check(qvturi,modeluris);
		if (sol.satisfiable()) throw new ErrorAlloy ("Instances already consistent.");
		else {			
			try {
				scopes = AlloyEchoTranslator.getInstance().getScopes(cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			finalfact = Sig.NONE.no();
			Func func = AlloyEchoTranslator.getInstance().getQVTFact(qvturi);
			PrimSig original;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			for (String modeluri : modeluris) {
				PrimSig state = addInstanceSigs(modeluri);
				if (modeluri.equals(diruri)) {
					original = state;
					try { 
						targetstate = new PrimSig("'"+original.label, original.parent, Attr.ONE);
					}
					catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
					allsigs.add(targetstate);
					sigs.add(targetstate);
					String metamodeluri = AlloyEchoTranslator.getInstance().getModelMetamodel(modeluri);
					edelta = AlloyEchoTranslator.getInstance().getMetamodelDeltaExpr(metamodeluri).call(original, targetstate);
					try {
						Collection<Sig> aux = new ArrayList<Sig>();
						aux.add(PrimSig.UNIV);
						SubsetSig news = new SubsetSig(AlloyUtil.NEWSNAME, aux, new Attr[0]);
						allsigs.add(news);
						finalfact = finalfact.and(news.equal(edelta));
					} catch (Err e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					edelta = edelta.cardinality().iplus(AlloyEchoTranslator.getInstance().getMetamodelDeltaRelFunc(metamodeluri).call(original, targetstate));
					
					finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri, targetstate));
				} else {
					sigs.add(state);			
				}
				//System.out.println("INST "+EMF2Alloy.getInstance().getModelFact(modeluri));
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(modeluri));
			}
			//System.out.println(sigs + " for "+ func.decls.get(0).expr + " , "+func.getBody());
			finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
            while(!sol.satisfiable()) {
            	if (monitor.isCancelled()) return false;
            	if (delta >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
            	if (overall >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
            	increment();
            }
            return true;
		} 
	}
	
	
	public boolean generateQvt(String qvturi, List<String> insturis, String diruri, String metamodeluri) throws ErrorAlloy, ErrorUnsupported {
		Map<Entry<String,String>,Integer> scope = new HashMap<Entry<String,String>,Integer>();
		
		List<EClass> rootobjects = AlloyEchoTranslator.getInstance().getRootClass(metamodeluri);
		if (rootobjects.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootobjects);
		scope.put(new SimpleEntry<String,String>(URIUtil.resolveURI(rootobjects.get(0).getEPackage().eResource()),rootobjects.get(0).getName()),1);
		AlloyEchoTranslator.getInstance().createScopesFromSizes(EchoOptionsSetup.getInstance().getOverallScope(), scope, metamodeluri);
	
		ArrayList<String> insts = new ArrayList<String>(insturis);
		insts.remove(diruri);
		allsigs.addAll(AlloyEchoTranslator.getInstance().getMetamodelSigs(metamodeluri));
		scopes = AlloyEchoTranslator.getInstance().getScopes();
		
		List<PrimSig> sigs = new ArrayList<PrimSig>();

		for (String uri : insturis) {
			if (!uri.equals(diruri)) {
				PrimSig state = addInstanceSigs(uri);
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(uri));
				sigs.add(state);			
			} else {
				PrimSig state = (PrimSig) AlloyEchoTranslator.getInstance().getMetaModelStateSig(metamodeluri);
				try { 
					targetstate = new PrimSig("'"+state, state, Attr.ONE); 
				} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
				sigs.add(targetstate);
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getGenerateInstance(metamodeluri,targetstate));
				allsigs.add(targetstate);
			}
		}
		Func func = AlloyEchoTranslator.getInstance().getQVTFact(qvturi);
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));

		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
        while(!sol.satisfiable()) {
        	if (monitor.isCancelled()) return false;
        	if (delta >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
        	if (overall >= EchoOptionsSetup.getInstance().getMaxDelta()) return false;
        	increment();
        }
        return true;
	}
	
	/**
	 * Increments the scopes and tries to generate an instance
	 * Increments the overall scope if different than zero and the concrete scopes if any
	 * @throws ErrorAlloy
	 */
	private void increment() throws ErrorAlloy {
		//System.out.println(edelta);
		Expr runfact = finalfact;
		if (edelta.isSame(Sig.NONE.no())) {
			scopes = AlloyUtil.incrementStringScopes(scopes);
			overall++;
		}
		else {
			try {
				intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
				if(!EchoOptionsSetup.getInstance().isOperationBased())
					runfact = finalfact.and(edelta.equal(ExprConstant.makeNUMBER(delta)));
				scopes = AlloyEchoTranslator.getInstance().incrementScopes(scopes);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		}
		try {
		
			Command cmd = new Command(false, overall, intscope, -1, runfact);
			cmd = cmd.change(scopes);
	
			//EchoReporter.getInstance().debug("DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);
			//for (Sig s : allsigs)
			//	System.out.println("sig "+s + " : " +((PrimSig) s).children());

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
			//if (sol.satisfiable()) System.out.println(sol.eval(edelta));
			delta++;
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}

	}
	
	/** 
	 * Calculates the next Alloy solution.
	 * @throws ErrorAlloy 
	 */
	public void nextInstance() throws ErrorAlloy  {
		try {
            sol = sol.next();
            while(!sol.satisfiable()){
                EchoReporter.getInstance().debug("No more instances: delta increased.");
                increment();
            }
        }
		catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Adds the signatures of an instance to this.allsigs
	 * @param modeluri the URI of the instance
	 * @return the signature representing the instance
	 * @throws ErrorAlloy 
	 */
	private PrimSig addInstanceSigs (String modeluri) throws ErrorAlloy {
		for (List<PrimSig> x : AlloyEchoTranslator.getInstance().getInstanceSigs(modeluri).values())
			allsigs.addAll(x);
		PrimSig state = AlloyEchoTranslator.getInstance().getModelStateSig(modeluri);
		allsigs.add(state);
		allsigs.add(state.parent);
		String metamodeluri = AlloyEchoTranslator.getInstance().getModelMetamodel(modeluri);
		allsigs.addAll(AlloyEchoTranslator.getInstance().getMetamodelSigs(metamodeluri));
		//System.out.println("All sigs: "+allsigs);
		return state;
	}
	
	
	
	/** 
	 * Returns the Alloy solution.
	 * @return this.sol
	 */
	public EchoSolution getSolution() {
		if(sol!=null)
        return new EchoSolution(){

            @Override
            public Object getContents() {
                return new AlloyTuple(sol,targetstate);
            }

            @Override
            public boolean satisfiable() {
                return sol.satisfiable();
            }
        };
        else return null;
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
	
	/** 
	 * Returns the state signature of the target instance.
	 * @return this.targetstate
	 */
	public PrimSig getTargetStateSig(){
		return targetstate;
	}
}

