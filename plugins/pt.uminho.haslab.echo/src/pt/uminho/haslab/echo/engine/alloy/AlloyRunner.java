package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.*;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.SubsetSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import org.eclipse.emf.ecore.EClass;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ast.IExpression;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import static com.google.common.primitives.Ints.max;

/**
 * @author nmm
 *
 */
public class AlloyRunner implements EngineRunner {
	
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
	public AlloyRunner() {

		rep = new A4Reporter() {
			@Override
			public void warning(ErrorWarning msg) {
				EchoReporter.getInstance().warning(msg.toString().trim(),
						Task.ALLOY_RUN);
			}

			@Override
			public void resultSAT(Object command, long solvingTime,
					Object solution) {
				EchoReporter.getInstance().result(Task.ALLOY_RUN,
						"SAT time: " + solvingTime + "ms", true);
			}

			@Override
			public void resultUNSAT(Object command, long solvingTime,
					Object solution) {
				EchoReporter.getInstance().result(Task.ALLOY_RUN,
						"UNSAT time: " + solvingTime + "ms", false);
			}

			@Override
			public void solve(int primaryVars, int totalVars, int clauses) {
				EchoReporter.getInstance().start(
						Task.ALLOY_RUN,
						"Primary vars: " + primaryVars + ", vars: " + totalVars
								+ ", clauses: " + clauses);
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
	 * Tests the conformity of a set models
	 * @param modelIDs the IDs of the models to be checked
	 * @throws ErrorAlloy
	 */
	@Override
	public void conforms(List<String> modelIDs) throws ErrorAlloy {
		for (String modelID : modelIDs) {
			addInstanceSigs(modelID);
			EAlloyModel model = AlloyEchoTranslator.getInstance().getModel(
					modelID);
			finalfact = finalfact.and(model.metamodel.getConforms().call(
					model.model_sig));
			finalfact = finalfact.and(model.getModelConstraint());
		}

		try {
			cmd = new Command(true, overall, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/**
	 * Creates a solution from a set of models
	 * @param modelIDs the IDs of the models to be depicted
	 */
	@Override
	public void show(List<String> modelIDs) throws ErrorAlloy {
		for (String modelID : modelIDs) {
			addInstanceSigs(modelID);
			finalfact = finalfact.and(AlloyEchoTranslator.getInstance()
					.getModel(modelID).getModelConstraint());
		}
		try {
			cmd = new Command(true, overall, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/**
	 * Initializes a repair command
	 * @param modelID the ID of the model to be repaired
	 * @return if the model was successfully repaired
	 * @throws ErrorAlloy
	 */
	@Override
	public boolean repair(String modelID) throws ErrorAlloy {
		List<String> modelIDs = new ArrayList<String>();
		modelIDs.add(modelID);
		if (EchoOptionsSetup.getInstance().isOperationBased())
			AlloyEchoTranslator.getInstance().createScopesFromOps(modelIDs);
		else
			AlloyEchoTranslator.getInstance().createScopesFromID(modelIDs);
		conforms(new ArrayList<String>(Arrays.asList(modelID)));
		if (sol.satisfiable())
			throw new ErrorAlloy("Instances already consistent.");
		else {
			try {
				scopes = AlloyEchoTranslator.getInstance().getScopes(
						cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			allsigs = new HashSet<Sig>(Arrays.asList(AlloyEchoTranslator.STATE));
			finalfact = Sig.NONE.no();
			PrimSig original;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			PrimSig state = addInstanceSigs(modelID);
			original = state;
			try {
				PrimSig target = new PrimSig(AlloyUtil.targetName(original),
						original.parent, Attr.ONE);
				targetstates.put(modelID, target);
				allsigs.add(target);
				sigs.add(target);
				EAlloyModel model = AlloyEchoTranslator.getInstance().getModel(
						modelID);
				EAlloyMetamodel metamodel = model.metamodel;
				edelta = metamodel.getDeltaSetFunc().call(original, target)
						.cardinality();
				edelta = metamodel.getDeltaRelFunc().call(original, target);
				AlloyEchoTranslator.getInstance().createScopesFromID(modelIDs);
				finalfact = finalfact.and(model.metamodel.getConforms().call(
						target));
				finalfact = finalfact.and(model.getModelConstraint());
			} catch (Err e) {
				throw new ErrorAlloy(e.getMessage());
			}
			while (!sol.satisfiable()) {
				if (delta >= EchoOptionsSetup.getInstance().getMaxDelta())
					return false;
				if (overall >= EchoOptionsSetup.getInstance().getMaxDelta())
					return false;
				increment();
			}
			return true;
		}
	}
	
	/**
	 * Generates a model conforming to a give metamodel
	 * @param metamodelID the ID of the new model's metamodel
	 * @param scope additional scopes for the new model
	 * @return if the model was successfully repaired
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 */
	@Override
	public boolean generate(String metamodelID,
			Map<Entry<String, String>, Integer> scope, String targetURI)
			throws ErrorAlloy, ErrorUnsupported {
		EAlloyMetamodel metamodel = AlloyEchoTranslator.getInstance()
				.getMetamodel(metamodelID);
		List<EClass> rootobjects = metamodel.getRootClass();
		if (rootobjects.size() != 1)
			throw new ErrorUnsupported(ErrorUnsupported.MULTIPLE_ROOT,
					"Could not resolve root class: " + rootobjects,
					"Check the meta-model containment tree.",
					Task.GENERATE_TASK);
		if (scope.get(rootobjects.get(0).getName()) == null)
			scope.put(new SimpleEntry<String, String>(metamodel.metamodel.ID,
					rootobjects.get(0).getName()), 1);
		AlloyEchoTranslator.getInstance().createScopesFromSizes(
				EchoOptionsSetup.getInstance().getOverallScope(), scope);

		allsigs.addAll(metamodel.getAllSigs());
		scopes = AlloyEchoTranslator.getInstance().getScopes();
		PrimSig state = metamodel.sig_metamodel;
		try {
			PrimSig target = new PrimSig(AlloyUtil.targetName(state), state,
					Attr.ONE);
			targetstates.put(targetURI, target);
			allsigs.add(target);
			finalfact = finalfact.and(metamodel.getGenerate().call(target));
		} catch (Err e) {
			throw new ErrorAlloy(e.getMessage());
		}

		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
		while (!sol.satisfiable()) {
			if (delta >= EchoOptionsSetup.getInstance().getMaxDelta())
				return false;
			if (overall >= EchoOptionsSetup.getInstance().getMaxDelta())
				return false;
			increment();
		}
		return true;
	}
		
	/** 
	 * Runs a inter-model consistency checking command
	 * @param transformationID the ID of the transformation to be applied
	 * @param modelIDs the IDs of the instances to be checked
	 * TODO: Should receive an EContraint rather than a ETransformation + EModels
	 * @throws ErrorAlloy 
	 */
	@Override
	public void check(String transformationID, List<String> modelIDs)
			throws ErrorAlloy {
		EAlloyTransformation trans = AlloyEchoTranslator.getInstance()
				.getQVTTransformation(transformationID);
		List<IExpression> sigs = new ArrayList<IExpression>();
		for (String modelID : modelIDs) {
			PrimSig state = addInstanceSigs(modelID);
			sigs.add(new AlloyExpression(state));
			EAlloyModel model = AlloyEchoTranslator.getInstance().getModel(
					modelID);
			EAlloyMetamodel metamodel = model.metamodel;
			finalfact = finalfact.and(model.getModelConstraint());
			finalfact = finalfact.and(metamodel.getConforms().call(
					model.model_sig));
		}
		finalfact = finalfact.and(trans.getTransformationConstraint(sigs).formula);
		EchoReporter.getInstance().debug("Final fact: "+finalfact);

		try {
			cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/**
	 * Initializes a inter-model consistency repair command
     * @param transformationID the ID of the transformation
     * @param modelIDs the IDs of the models
     * @param targetIDs the IDs of the target models
	 * @return if the model was successfully repaired
	 * TODO: Should receive a EContraint rather than a ETransformation + EModels
	 * @throws ErrorAlloy
	 */
	@Override
	public boolean enforce(String transformationID, List<String> modelIDs,
			List<String> targetIDs) throws ErrorAlloy {
		if (EchoOptionsSetup.getInstance().isOperationBased())
			AlloyEchoTranslator.getInstance().createScopesFromOps(targetIDs);
		else
			AlloyEchoTranslator.getInstance().createScopesFromID(targetIDs);
		check(transformationID, modelIDs);
		if (sol.satisfiable())
			throw new ErrorAlloy("Instances already consistent.");
		else {
			try {
				scopes = AlloyEchoTranslator.getInstance().getScopes(
						cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			finalfact = Sig.NONE.no();
			PrimSig original;
			List<IExpression> sigs = new ArrayList<IExpression>();
			for (String modeluri : modelIDs) {
				PrimSig state = addInstanceSigs(modeluri);
				EAlloyModel model = AlloyEchoTranslator.getInstance().getModel(
						modeluri);
				if (targetIDs.contains(modeluri)) {
					original = state;
					PrimSig target = null;
					try {
						target = new PrimSig(AlloyUtil.targetName(original),
								original.parent, Attr.ONE);
						targetstates.put(modeluri, target);
						allsigs.add(target);
						sigs.add(new AlloyExpression(target));
						finalfact = finalfact.and(model.metamodel.getConforms()
								.call(target));
					} catch (Err e) {
						throw new ErrorAlloy(e.getMessage());
					}
					if (!EchoOptionsSetup.getInstance().isOperationBased()) {
						EAlloyMetamodel metamodel = model.metamodel;
						try {
							Collection<Sig> aux = new ArrayList<Sig>();
							aux.add(Sig.UNIV);
							SubsetSig news = new SubsetSig(EchoHelper.NEWSNAME,
									aux, new Attr[0]);
							allsigs.add(news);
							finalfact = finalfact.and(news.equal(edelta));
						} catch (Err e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Expr temp = metamodel.getDeltaSetFunc().call(original,
								target);
						edelta = edelta.iplus(temp.cardinality().iplus(
								metamodel.getDeltaRelFunc().call(original,
										target)));
					} else {
						edelta = Sig.NONE.no();
					}
				} else {
					sigs.add(new AlloyExpression(state));
				}
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance()
						.getModel(modeluri).getModelConstraint());
			}
			AlloyFormula expr = AlloyEchoTranslator.getInstance()
					.getQVTTransformation(transformationID)
					.getTransformationConstraint(sigs);
			finalfact = finalfact.and(expr.formula);
			while (!sol.satisfiable()) {
				if (delta >= EchoOptionsSetup.getInstance().getMaxDelta())
					return false;
				if (overall >= EchoOptionsSetup.getInstance().getMaxDelta())
					return false;
				increment();
			}
			return true;

		}
	}

	/**
	 * Generates a new model from a transformation and existing models
     * @param transformationID the ID of the transformation
     * @param modelIDs the IDs of the models
     * @param targetURI the URI of model to be generated
     * @param metamodelID the metamodel ID of the model to be generated
	 * @return if the model was successfully generated
	 * TODO: Refine
	 * @throws ErrorAlloy
	 */
	@Override
	public boolean generateQvt(String transformationID, List<String> modelIDs,
			String targetURI, String metamodelID) throws ErrorAlloy,
			ErrorUnsupported {
		Map<Entry<String, String>, Integer> scope = new HashMap<Entry<String, String>, Integer>();
		EAlloyMetamodel metamodel = AlloyEchoTranslator.getInstance()
				.getMetamodel(metamodelID);
		List<EClass> rootobjects = metamodel.getRootClass();
		if (rootobjects.size() != 1)
			throw new ErrorUnsupported("Could not resolve root class: "
					+ rootobjects);
		scope.put(new SimpleEntry<String, String>(metamodel.metamodel.ID,
				rootobjects.get(0).getName()), 1);
		AlloyEchoTranslator.getInstance().createScopesFromSizes(
				EchoOptionsSetup.getInstance().getOverallScope(), scope);
		ArrayList<String> insts = new ArrayList<String>(modelIDs);
		insts.remove(targetURI);
		allsigs.addAll(metamodel.getAllSigs());
		scopes = AlloyEchoTranslator.getInstance().getScopes();

		List<IExpression> sigs = new ArrayList<IExpression>();

		for (String uri : modelIDs) {
			if (!uri.equals(targetURI)) {
				PrimSig state = addInstanceSigs(uri);
				finalfact = finalfact.and(AlloyEchoTranslator.getInstance()
						.getModel(uri).getModelConstraint());
				sigs.add(new AlloyExpression(state));
			} else {
				PrimSig state = metamodel.sig_metamodel;
				try {
					PrimSig target = new PrimSig(AlloyUtil.targetName(state),
							state, Attr.ONE);
					targetstates.put(uri, target);
					sigs.add(new AlloyExpression(target));
					allsigs.add(target);
					finalfact = finalfact.and(metamodel.getGenerate().call(
							target));
				} catch (Err e) {
					throw new ErrorAlloy(e.getMessage());
				}
			}
		}
		AlloyFormula expr = AlloyEchoTranslator.getInstance()
				.getQVTTransformation(transformationID).getTransformationConstraint(sigs);
		finalfact = finalfact.and(expr.formula);

		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage() + "\n" + a.dump() + a.pos
					+ "\n CAUSE ->" + a.getCause());
		}
		while (!sol.satisfiable()) {
			if (delta >= EchoOptionsSetup.getInstance().getMaxDelta())
				return false;
			if (overall >= EchoOptionsSetup.getInstance().getMaxDelta())
				return false;
			increment();
		}
		return true;
	}
	
	/**
	 * Increments the scopes and tries to generate an instance
	 * Increments the overall scope if different than zero and the concrete scopes if any
	 * Should be run after a repair of generate command
	 * @throws ErrorAlloy
	 */
	private void increment() throws ErrorAlloy {
		Expr runfact = finalfact;
		if (edelta.isSame(ExprConstant.makeNUMBER(0))) {
			scopes = AlloyUtil.incrementStringScopes(scopes);
			overall++;
		} else {
			try {
				intscope = max((int) Math.ceil(1 + (Math.log(delta + 1) / Math
						.log(2))), intscope);
				if (!EchoOptionsSetup.getInstance().isOperationBased())
					runfact = finalfact.and(edelta.equal(ExprConstant
							.makeNUMBER(delta)));
				scopes = AlloyEchoTranslator.getInstance().incrementScopes(
						scopes);
			} catch (Err a) {throw new ErrorAlloy(a.getMessage());}
		}
		try {
			Command cmd = new Command(false, overall, intscope, -1, runfact);
			cmd = cmd.change(scopes);

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd,
					aoptions);
			delta++;
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}

	}
	
	/**
	 * Calculates the next Alloy solution.
	 * @throws ErrorAlloy
	 */
	@Override
	public void nextInstance() throws ErrorAlloy {
		try {
			sol = sol.next();
			while (!sol.satisfiable()) {
				EchoReporter.getInstance().warning(
						"No more instances: delta increased.", Task.ALLOY_RUN);
				increment();
			}
		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/**
	 * Adds all sigs relevant to a model to <code>this.allsigs</code>
	 * 
	 * @param modelID
	 *            the ID of the model
	 * @return the signature representing the model
	 * @throws ErrorAlloy
	 */
	private PrimSig addInstanceSigs(String modelID) throws ErrorAlloy {
		allsigs.addAll(AlloyEchoTranslator.getInstance().getModel(modelID)
				.getAllSigs());
		PrimSig state = AlloyEchoTranslator.getInstance().getModel(modelID).model_sig;
		allsigs.add(state);
		allsigs.add(state.parent);
		EAlloyModel model = AlloyEchoTranslator.getInstance().getModel(modelID);
		allsigs.addAll(model.metamodel.getAllSigs());
		return state;
	}

	/**
	 * Returns the Alloy solution.
	 * 
	 * @return this.sol
	 */
	@Override
	public EchoSolution getSolution() {
		if (sol != null)
			return new EchoSolution() {

				AlloyTuple tuple = new AlloyTuple(sol, targetstates);

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
		else
			return null;
	}

	/**
	 * Cancels a running Alloy command
	 */
	@Override
	public void cancel() {
		WorkerEngine.stop();
	}

}

