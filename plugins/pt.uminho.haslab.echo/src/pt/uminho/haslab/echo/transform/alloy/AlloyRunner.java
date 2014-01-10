package pt.uminho.haslab.echo.transform.alloy;

<<<<<<< HEAD
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.EngineRunner;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.emf.URIUtil;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4.WorkerEngine;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
=======
import edu.mit.csail.sdg.alloy4.*;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.SubsetSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
<<<<<<< HEAD
=======
import org.eclipse.emf.ecore.EClass;
import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.emf.URIUtil;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe
import static com.google.common.primitives.Ints.max;

public class AlloyRunner implements EngineRunner{
	
/** the Alloy solution */
	private A4Solution sol;
	/** the Alloy command options*/
	private A4Options aoptions;
	/** the Alloy reporter*/
	private A4Reporter rep;
	
	/** the expression representing the delta (must be equaled to the desired delta)*/
	private Expr edelta = ExprConstant.makeNUMBER(0);
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
	private Map<String,PrimSig> targetstates = new HashMap<String,PrimSig>();
	
	private Command cmd = null;

	/** 
	 * Constructs a new Alloy Runner that performs tests and generates instances
	 */
	public AlloyRunner () {	

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

	public void show(List<String> modeluris) throws ErrorAlloy {
		for (String modeluri : modeluris) {
			addInstanceSigs(modeluri);
			//finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri));
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
		List<String> modeluris = new ArrayList<String>();
		modeluris.add(modeluri);
		if (EchoOptionsSetup.getInstance().isOperationBased())
			AlloyEchoTranslator.getInstance().createScopesFromOps(modeluris);
		else
			AlloyEchoTranslator.getInstance().createScopesFromURI(modeluris);
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
				PrimSig target = new PrimSig(AlloyUtil.targetName(original), original.parent, Attr.ONE);
				targetstates.put(modeluri,target); 
				allsigs.add(target);
				sigs.add(target);
				String metamodeluri = AlloyEchoTranslator.getInstance().getModelMetamodel(modeluri);
				edelta = AlloyEchoTranslator.getInstance().getMetamodelDeltaExpr(metamodeluri).call(original, target).cardinality();
				edelta = edelta.iplus(AlloyEchoTranslator.getInstance().getMetamodelDeltaRelFunc(metamodeluri).call(original, target));
				AlloyEchoTranslator.getInstance().createScopesFromURI(modeluris);
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri, target));
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(modeluri));
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
            while(!sol.satisfiable()) {
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
	public boolean generate(String metaModelUri, Map<Entry<String, String>, Integer> scope, String targeturi) throws ErrorAlloy, ErrorUnsupported {
		List<EClass> rootobjects = AlloyEchoTranslator.getInstance().getRootClass(metaModelUri);
		if (rootobjects.size() != 1) throw new ErrorUnsupported(ErrorUnsupported.MULTIPLE_ROOT,"Could not resolve root class: "+rootobjects,"Check the meta-model containment tree.",Task.GENERATE_TASK);

		if (scope.get(rootobjects.get(0).getName()) == null)
			scope.put(new SimpleEntry<String,String>(URIUtil.resolveURI(rootobjects.get(0).getEPackage().eResource()),rootobjects.get(0).getName()),1);
		AlloyEchoTranslator.getInstance().createScopesFromSizes(EchoOptionsSetup.getInstance().getOverallScope(), scope, metaModelUri);
		
		allsigs.addAll(AlloyEchoTranslator.getInstance().getMetamodelSigs(metaModelUri));
		scopes = AlloyEchoTranslator.getInstance().getScopes();
		
		PrimSig state = (PrimSig) AlloyEchoTranslator.getInstance().getMetaModelStateSig(metaModelUri);
		try { 
			PrimSig target = new PrimSig(AlloyUtil.targetName(state), state, Attr.ONE);
			targetstates.put(targeturi,target); 
			allsigs.add(target);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getGenerateInstance(metaModelUri,target));
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }


		try {
			//System.out.println(finalfact);
			//System.out.println(allsigs);
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			//System.out.println("DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
        while(!sol.satisfiable()) {
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
			System.out.println(uri);
			PrimSig state = addInstanceSigs(uri);
			sigs.add(state);
			//System.out.println("Model fact: "+ uri);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(uri));
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(uri));
		}
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		
		try {
			cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Initializes q QVT-R enforcement command
	 *
     * @param qvturi the URI of the QVT-R transformation
     * @param modeluris the URIs of the instances
     * @param diruri the URI of the target model
     * @return
	 * @throws ErrorAlloy
	 */
	public boolean enforce(String qvturi, List<String> modeluris, List<String> diruri) throws ErrorAlloy {
		EchoReporter.getInstance().debug("Enforce params: "+modeluris);

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
				if (diruri.contains(modeluri)) {
					original = state;
					PrimSig target = null;
					try { 
						target = new PrimSig(AlloyUtil.targetName(original), original.parent, Attr.ONE);
						targetstates.put(modeluri,target);
						allsigs.add(target);
						sigs.add(target);
						finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getConformsInstance(modeluri,target));
					}
					catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
					if (!EchoOptionsSetup.getInstance().isOperationBased()) {
						String metamodeluri = AlloyEchoTranslator.getInstance().getModelMetamodel(modeluri);
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
						Expr temp = AlloyEchoTranslator.getInstance().getMetamodelDeltaExpr(metamodeluri).call(original, target);
						edelta = edelta.iplus(temp.cardinality().iplus(AlloyEchoTranslator.getInstance().getMetamodelDeltaRelFunc(metamodeluri).call(original, target)));
					} else {
						edelta = Sig.NONE.no();
					}
					//EchoReporter.getInstance().debug("Conforms: "+AlloyEchoTranslator.getInstance().getConformsInstance(modeluri, targetstate));
				} else {
					sigs.add(state);			
				}
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getModelFact(modeluri));				
			}
			EchoReporter.getInstance().debug("Created params: "+sigs);
			finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
			while(!sol.satisfiable()) {
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
					PrimSig target = new PrimSig(AlloyUtil.targetName(state), state, Attr.ONE);
					targetstates.put(uri,target); 
					sigs.add(target);
					allsigs.add(target);
					finalfact = finalfact.and(AlloyEchoTranslator.getInstance().getGenerateInstance(metamodeluri,target));
				} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
			}
		}
		Func func = AlloyEchoTranslator.getInstance().getQVTFact(qvturi);
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));

		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()+"\n" + a.dump() + a.pos + "\n CAUSE ->" + a.getCause());}
        while(!sol.satisfiable()) {
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
		if (edelta == null) {
			scopes = AlloyUtil.incrementStringScopes(scopes);
			overall++;
		}
		else {
			try {
				intscope = max((int) Math.ceil(1+(Math.log(delta+1) / Math.log(2))),intscope);
				if(!EchoOptionsSetup.getInstance().isOperationBased())
					runfact = finalfact.and(edelta.equal(ExprConstant.makeNUMBER(delta)));
				scopes = AlloyEchoTranslator.getInstance().incrementScopes(scopes);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		}
		try {
		
			Command cmd = new Command(false, overall, intscope, -1, runfact);
			cmd = cmd.change(scopes);

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
		allsigs.addAll(AlloyEchoTranslator.getInstance().getInstanceSigs(modeluri));
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

			AlloyTuple tuple = new AlloyTuple(sol,targetstates);			
			
            @Override
            public Object getContents() {
                return tuple;
            }

            @Override
            public boolean satisfiable() {
                return sol.satisfiable();
            }
            


			@Override
			public void writeXML(String filename) {
				try {
					sol.writeXML(filename);
				} catch (Err e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
        };
        else return null;
	}

    @Override
    public void cancel() {
        WorkerEngine.stop();
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

}

