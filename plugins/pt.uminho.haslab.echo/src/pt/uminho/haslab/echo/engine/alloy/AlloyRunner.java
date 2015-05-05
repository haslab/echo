package pt.uminho.haslab.echo.engine.alloy;

import static com.google.common.primitives.Ints.max;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;

import pt.uminho.haslab.echo.CoreRunner;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EExceptConsistent;
import pt.uminho.haslab.echo.EExceptMaxDelta;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.SubsetSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

/**
 * Runs MDE tasks using Alloy as the underlying core engine.
 * Currently supports the following tasks:
 * 		show, conforms, repair, generate, check, enforce, batch
 * 
 * @author nmm, tmg
 * @version 0.4 01/04/2015
 */
class AlloyRunner implements CoreRunner {

	/** the current Alloy solution */
	private A4Solution sol;
	/** the Alloy command options */
	private A4Options aoptions;
	/** the Alloy reporter */
	private A4Reporter rep;

	/** the expression representing the delta (must be equaled to the desired delta). */
	private Expr edelta;
	/** the final command fact (without the delta expression). */
	private Expr finalfact = Sig.NONE.no();
	/** all the Alloy signatures of the model. */
	private Set<Sig> allsigs;

	/** the current delta value. */
	private int delta = 1;
	/** the current int bitwidth (will change as delta increases). */
	private int intscope;
	/** the current specific scopes. */
	private ConstList<CommandScope> current_scopes;
	/** the state signature of the target instance. */
	private Map<String, PrimSig> targetstates = new HashMap<String, PrimSig>();

	AlloyRunner() {
		// the Alloy reporter should communicate with the Echo reporter.
		rep = new A4Reporter() {
			@Override
			public void warning(ErrorWarning msg) {
				EchoReporter.getInstance().warning(msg.toString().trim(), Task.CORE_RUN);
			}

			@Override
			public void resultSAT(Object command, long solvingTime, Object solution) {
				EchoReporter.getInstance().result(Task.CORE_RUN, "SAT time: " + solvingTime + "ms", true);
			}

			@Override
			public void resultUNSAT(Object command, long solvingTime, Object solution) {
				EchoReporter.getInstance().result(Task.CORE_RUN, "UNSAT time: " + solvingTime + "ms", false);
			}

			@Override
			public void solve(int primaryVars, int totalVars, int clauses) {
				EchoReporter.getInstance().start(Task.CORE_RUN,
						"Primary vars: " + primaryVars + ", vars: " + totalVars + ", clauses: " + clauses);
			}
		};
		aoptions = new A4Options();
		aoptions.solver = A4Options.SatSolver.SAT4J;
		aoptions.noOverflow = true;
		intscope = EchoOptionsSetup.getInstance().getBitwidth();
	}

	/** {@inheritDoc} */
	@Override
	public void show(List<String> modelIDs) throws EErrorAlloy {
		finalfact = Sig.NONE.no();
		// add sigs and facts regarding the models
		for (String modelID : modelIDs) {
			addInstanceSigs(modelID);
			AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
			finalfact = finalfact.and(model.getModelConstraint().FORMULA);
		}
		// run the command
		try {
			Command cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);
		} catch (Err a) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_RUN, a.getMessage(), a, Task.DRAW);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean conforms(List<String> modelIDs) throws EErrorAlloy {
		finalfact = Sig.NONE.no();
		// add sigs and facts regarding the models and meta-models
		for (String modelID : modelIDs) {
			addInstanceSigs(modelID);
			AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
			finalfact = finalfact.and(model.getModelConstraint().FORMULA);
			finalfact = finalfact.and(model.metamodel.getConforms(modelID).FORMULA);
		}
		// run the command
		try {
			Command cmd = new Command(true, EchoOptionsSetup.getInstance().getOverallScope(), intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);
		} catch (Err a) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_RUN, a.getMessage(), a, Task.CONFORMS_TASK);
		}
		return sol.satisfiable();
	}

	/** {@inheritDoc} */
	@Override
	public boolean repair(String modelID) throws EErrorAlloy, EExceptConsistent, EExceptMaxDelta {
		List<String> modelIDs = new ArrayList<String>(Arrays.asList(modelID));
		// tests if the models are already consistent
		conforms(modelIDs);
		if (sol.satisfiable())
			throw new EExceptConsistent(EExceptConsistent.CONSISTENT, "Instances already consistent.", Task.REPAIR_TASK);
		else {
			// calculate the model and meta-model constraints
			AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
			PrimSig original = addInstanceSigs(modelID);
			finalfact = model.getModelConstraint().FORMULA;
			PrimSig target = model.setTarget();
			targetstates.put(model.emodel.ID, target);
			allsigs.add(target);
			finalfact = finalfact.and(model.metamodel.getConforms(model.emodel.ID).FORMULA);
			model.unsetTarget();

			// is graph-edit distance, calculate the delta function
			if (!EchoOptionsSetup.getInstance().isOperationBased()) {
				edelta = model.metamodel.getDeltaSetFunc().call(original, target);
				colorChangedAtoms(edelta);
				edelta = edelta.cardinality().iplus(model.metamodel.getDeltaRelFunc().call(original, target));
			}

			// calculate the scopes from the instance
			current_scopes = AlloyTranslator.getInstance().createScopesFromID(modelIDs,
					AlloyTranslator.getInstance().strings().size());

			// initializes the execution
			go();
			return true;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean generate(String modelID, Map<String, Map<String, Integer>> scope) throws EErrorAlloy,
			EErrorUnsupported, EExceptMaxDelta, EErrorParser {
		AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
		EClass root = model.emodel.getRootEElement().type;
		AlloyMetamodel metamodel = model.getMetamodel();

		Map<String, Integer> meta_scopes = scope.get(metamodel.metamodel.ID);
		if (meta_scopes == null)
			meta_scopes = new HashMap<String, Integer>();

		// scope of the root class must be 1
		if (meta_scopes.get(root.getName()) != null && meta_scopes.get(root.getName()) != 1)
			throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT, "Scope of root element must be 1.",
					Task.GENERATE_TASK);
		meta_scopes.put(root.getName(), 1);
		scope.put(metamodel.metamodel.ID, meta_scopes);

		// retrieve the model and meta-model constraint
		PrimSig original = addInstanceSigs(modelID);
		finalfact = model.getModelConstraint().FORMULA;
		PrimSig target = model.setTarget();
		targetstates.put(model.emodel.ID, target);
		allsigs.add(target);
		finalfact = finalfact.and(model.metamodel.getGenerate(model.emodel.ID).FORMULA);
		model.unsetTarget();

		// force root to exist
		finalfact = finalfact.and((model.metamodel.getStateFieldFromClass(root).join(target)).one());

		// if graph-edit distance, calculates the delta function
		if (!EchoOptionsSetup.getInstance().isOperationBased()) {
			edelta = model.metamodel.getDeltaSetFunc().call(original, target);
			edelta = edelta.cardinality().iplus(model.metamodel.getDeltaRelFunc().call(original, target));
		}

		// calculates the scopes given additional ones
		current_scopes = AlloyTranslator.getInstance().createScopesFromID(
				new ArrayList<String>(Arrays.asList(modelID)), scope, 1);

		// launches the command
		increment();
		go();
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean check(String constraintID) throws EErrorAlloy {
		finalfact = Sig.NONE.no();
		EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
		// retrieve the model constraints
		for (String model : constraint.getModels()) {
			addInstanceSigs(model);
			AlloyModel amodel = AlloyTranslator.getInstance().getModel(model);
			finalfact = finalfact.and(amodel.getModelConstraint().FORMULA);
			finalfact = finalfact.and(amodel.metamodel.getConforms(model).FORMULA);
		}
		// retrieve the transformation constraint
		finalfact = finalfact.and(((AlloyFormula) constraint.getConstraint()).FORMULA);

		// run the commnad
		try {
			Command cmd = new Command(true, EchoOptionsSetup.getInstance().getOverallScope(), intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);
		} catch (Err a) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_RUN, a.getMessage(), a, Task.CHECK_TASK);
		}
		return sol.satisfiable();
	}

	/** {@inheritDoc} */
	@Override
	public boolean enforce(String constraintID, List<String> targetIDs) throws EErrorAlloy, EExceptConsistent,
			EExceptMaxDelta {
		// tests whether models are already consistent
		check(constraintID);
		if (sol.satisfiable())
			throw new EExceptConsistent(EExceptConsistent.CONSISTENT, "Instances already consistent.", Task.REPAIR_TASK);
		else {
			EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
			finalfact = Sig.NONE.no();
			PrimSig original;
			// retrieve the model constraints
			for (String modelID : constraint.getModels()) {
				PrimSig state = addInstanceSigs(modelID);
				AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
				finalfact = finalfact.and(AlloyTranslator.getInstance().getModel(modelID).getModelConstraint().FORMULA);
				// for the targets, also retrieve the meta-model constraint
				if (targetIDs.contains(modelID)) {
					original = state;
					PrimSig target = model.setTarget();
					targetstates.put(model.emodel.ID, target);
					allsigs.add(target);
					finalfact = finalfact.and(model.metamodel.getConforms(model.emodel.ID).FORMULA);
					if (!EchoOptionsSetup.getInstance().isOperationBased()) {
						AlloyMetamodel metamodel = model.metamodel;
						edelta = metamodel.getDeltaSetFunc().call(original, target);
						colorChangedAtoms(edelta);
						edelta = edelta.cardinality().iplus(metamodel.getDeltaRelFunc().call(original, target));
					}
				}
			}
			// retrieve the transformation constraint
			finalfact = finalfact.and(((AlloyFormula) constraint.getConstraint()).FORMULA);
			for (String targetID : targetIDs)
				AlloyTranslator.getInstance().getModel(targetID).unsetTarget(); // can't be earlier

			// calculate the scopes for the targets
			current_scopes = AlloyTranslator.getInstance().createScopesFromID(constraint.getModels(), targetIDs,
					AlloyTranslator.getInstance().strings().size());

			// launch the command
			go();
			return true;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean batch(String constraintID, List<String> targetIDs, Map<String, Map<String, Integer>> scope)
			throws EErrorAlloy, EErrorUnsupported, EExceptMaxDelta, EErrorParser {
		// currently only supports batch generation of a single model
		if (targetIDs.size() != 1)
			throw new EErrorUnsupported(EErrorUnsupported.MULTIDIRECTIONAL, "Multidirectional not supproted.",
					Task.BATCH_TASK);
		String targetID = targetIDs.get(0);
		AlloyModel model = AlloyTranslator.getInstance().getModel(targetID);
		EClass root = model.emodel.getRootEElement().type;
		EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
		if(scope == null) scope = new HashMap<String,Map<String,Integer>>();
		
		Map<String, Integer> meta_scopes = scope.get(model.emodel.getMetamodel().ID);
		if (meta_scopes == null)
			meta_scopes = new HashMap<String, Integer>();

		// scope of the root class must be 1
		if (meta_scopes.get(root.getName()) != null && meta_scopes.get(root.getName()) != 1)
			throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT, "Scope of root element must be 1.",
					Task.GENERATE_TASK);

		meta_scopes.put(root.getName(), 1);
		scope.put(model.emodel.getMetamodel().ID, meta_scopes);

		PrimSig original = null, target = null;
		
		for (String modelID : constraint.getModels()) {
			// retrieve the meta-model constraint if target
			if (!modelID.equals(targetID)) {
				addInstanceSigs(modelID);
				finalfact = finalfact.and(AlloyTranslator.getInstance().getModel(modelID).getModelConstraint().FORMULA);
			} 
			// retrieve the model constraints otherwise
			else {
				original = addInstanceSigs(targetID);
				finalfact = model.getModelConstraint().FORMULA;
				target = model.setTarget();
				targetstates.put(model.emodel.ID, target);
				allsigs.add(target);
				finalfact = finalfact.and(model.metamodel.getGenerate(model.emodel.ID).FORMULA);
			}
		}
		// retrieve the transformation constraint
		finalfact = finalfact.and(((AlloyFormula) constraint.getConstraint()).FORMULA);
		model.unsetTarget();
		// force root to exist
		finalfact = finalfact.and((model.metamodel.getStateFieldFromClass(root).join(target)).one());

		// calculate the scope
		allsigs.addAll(model.metamodel.getAllSigs());
		current_scopes = AlloyTranslator.getInstance().createScopesFromID(constraint.getModels(), targetIDs, scope,
				EchoOptionsSetup.getInstance().getOverallScope());

		// if graph-edit distance, calculate the delta function
		if (!EchoOptionsSetup.getInstance().isOperationBased()) {
			edelta = model.metamodel.getDeltaSetFunc().call(original, target);
			edelta = edelta.cardinality().iplus(model.metamodel.getDeltaRelFunc().call(original, target));
		}
		
		// launche the command
		increment();
		go();
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public EchoSolution getSolution() {
		if (sol != null)
			return new EchoSolution() {
				AlloySolution tuple = new AlloySolution(sol, targetstates);

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

				@Override
				public void next() {
					try {
						sol.next();
					} catch (Err e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		else
			return null;
	}

	/** {@inheritDoc} */
	@Override
	public void nextSolution() throws EErrorAlloy, EExceptMaxDelta {
		try {
			sol = sol.next();
			go();
		} catch (Err a) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_RUN, a.getMessage(), a, Task.ECHO_RUN);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void cancel() {
		WorkerEngine.stop();
	}

	/**
	 * Iteratively calls Alloy using the current state of the runner.
	 * 
	 * @throws EExceptMaxDelta
	 * @throws EErrorAlloy
	 */
	private void go() throws EExceptMaxDelta, EErrorAlloy {
		while (!sol.satisfiable()) {
			if (delta >= EchoOptionsSetup.getInstance().getMaxDelta())
				throw new EExceptMaxDelta(EExceptMaxDelta.MAX, "Maximum delta reached ("
						+ EchoOptionsSetup.getInstance().getMaxDelta() + ").", Task.REPAIR_TASK);
			increment();
		}
	}

	/**
	 * Increments the scopes and tries to generate an instance using the current
	 * state of the runner. Should be run after a repair of generate methods.
	 * 
	 * @throws EErrorAlloy
	 */
	private void increment() throws EErrorAlloy {
		Expr runfact = finalfact;
		if (!EchoOptionsSetup.getInstance().isOperationBased()) {
			intscope = max((int) Math.ceil(1 + (Math.log(delta + 1) / Math.log(2))), intscope);
			runfact = finalfact.and(edelta.equal(ExprConstant.makeNUMBER(delta)));
		}
		current_scopes = AlloyTranslator.getInstance().incrementScopes(current_scopes);

		EchoReporter.getInstance().debug("Delta increased: " + delta);
		EchoReporter.getInstance().debug("Current scope: " + current_scopes);

		try {
			Command cmd = new Command(false, EchoOptionsSetup.getInstance().getOverallScope(), intscope, -1, runfact);
			cmd = cmd.change(current_scopes);

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);
			delta++;
		} catch (Err a) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_RUN, a.getMessage(), a, Task.ECHO_RUN);
		}

	}

	/**
	 * Adds all sigs relevant to a model to <code>this.allsigs</code>
	 * 
	 * @param modelID
	 *            the ID of the model
	 * @return the signature representing the model
	 * @throws EErrorAlloy
	 */
	private PrimSig addInstanceSigs(String modelID) throws EErrorAlloy {
		// state sig
		if (allsigs == null)
			allsigs = new HashSet<Sig>(Arrays.asList(AlloyTranslator.STATE));
		// model sigs
		allsigs.addAll(AlloyTranslator.getInstance().getModel(modelID).getAllSigs());
		// model state sigs
		PrimSig state = AlloyTranslator.getInstance().getModel(modelID).getModelSig();
		allsigs.add(state);
		allsigs.add(state.parent);
		// metamodel sigs
		AlloyModel model = AlloyTranslator.getInstance().getModel(modelID);
		allsigs.addAll(model.metamodel.getAllSigs());
		return state;
	}

	/**
	 * Creates the subset sig represented atoms changed between states.
	 * 
	 * @param dlt
	 *            expression denoting the set of changed atoms.
	 * @throws EErrorAlloy
	 *             if signature fails to be created.
	 */
	private void colorChangedAtoms(Expr dlt) throws EErrorAlloy {
		try {
			Collection<Sig> aux = new ArrayList<Sig>();
			aux.add(Sig.UNIV);
			SubsetSig news = new SubsetSig(EchoHelper.NEWSNAME, aux, new Attr[0]);
			allsigs.add(news);
			finalfact = finalfact.and(news.equal(dlt));
		} catch (Err e) {
			throw new EErrorAlloy(EErrorAlloy.FAIL_CREATE_SIG, e.getMessage(), e, Task.REPAIR_TASK);
		}
	}
}
