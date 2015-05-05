package pt.uminho.haslab.echo.engine.kodkod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import pt.uminho.haslab.echo.CoreRunner;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EExceptConsistent;
import pt.uminho.haslab.echo.EExceptMaxDelta;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

/**
 * Runs MDE tasks using Kodkod as the underlying core engine. Currently supports
 * the following tasks: conforms, repair, generate, check, enforce, batch
 * 
 * @author tmg, nmm
 * @version 0.4 01/04/2015
 */
public class KodkodRunner implements CoreRunner {

	/** the current Kodkod solution */
	private KodkodSolution sol = null;

	public KodkodRunner() { }

	/** {@inheritDoc} */
	@Override
	public void show(List<String> modelUris) throws EErrorCore {
		throw new UnsupportedOperationException("Presentation of instances not implemented in Kodkod.");
	}

	/** {@inheritDoc} */
	@Override
	public boolean conforms(List<String> modelIDs) throws EErrorCore {
		// currently checks each model individually
		for (String modelID : modelIDs) {
			// retrieve the meta-model constraint
			KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
			KodkodMetamodel metamodel = model.getMetamodel();
			Set<KodkodMetamodel> meta = new HashSet<>();
			meta.add(model.getMetamodel());
			Formula fact = metamodel.getConforms(modelID).formula;
			// calculates the (regular) bounds
			SATBinder sb = new SATBinder(model);
			
			// run the command
			final Solver solver = new Solver();
			solver.options().setSolver(SATFactory.DefaultSAT4J);
			solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
			sol = new KodkodSolution(solver.solveAll(fact, sb.getBounds()),
					meta);
		}
		return sol.satisfiable();
	}

	/** {@inheritDoc} */
	@Override
	public boolean repair(String modelID) throws EErrorCore, EExceptConsistent {
		List<String> modelIDs = new ArrayList<String>(Arrays.asList(modelID));
		// tests if the models are already consistent
		conforms(modelIDs);
		if (sol.satisfiable())
			throw new EExceptConsistent(EExceptConsistent.CONSISTENT, "Instances already consistent.", Task.REPAIR_TASK);
		else {
			// retrieve the meta-model constraint
			KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
			KodkodMetamodel metamodel = model.getMetamodel();
			Set<KodkodMetamodel> meta = new HashSet<>();
			meta.add(model.getMetamodel());
			Formula fact = metamodel.getConforms(modelID).formula;
			// calculates the bounds of the model with targets
			TargetBinder tb = new TargetBinder(model,new HashMap<String, Integer>());
			
			// runs the command
			final Solver solver = new Solver();
			solver.options().setSolver(SATFactory.DefaultSAT4J);
			solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
			sol = new KodkodSolution(solver.solveAll(fact, tb.getBounds()), meta);

			return sol.satisfiable();
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean generate(String modelID, Map<String, Map<String, Integer>> scope) throws EErrorCore,
			EErrorUnsupported, EExceptMaxDelta, EErrorParser {
		KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
		KodkodMetamodel metamodel = model.getMetamodel();
		EClass root = model.getModel().getRootEElement().type;

		Map<String, Integer> meta_scopes = scope.get(metamodel.metamodel.ID);
		if (meta_scopes == null)
			meta_scopes = new HashMap<String, Integer>();

		// scope of the root class must be 1
		if (meta_scopes.get(root.getName()) != null && meta_scopes.get(root.getName()) != 1)
			throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT, "Scope of root element must be 1.",
					Task.GENERATE_TASK);
		meta_scopes.put(root.getName(), 1);
		scope.put(metamodel.metamodel.ID, meta_scopes);

		Set<KodkodMetamodel> meta = new HashSet<>();
		meta.add(model.getMetamodel());

		// calculates the bounds assuming additional scopes
		TargetBinder tb = new TargetBinder(model, meta_scopes);

		EchoReporter.getInstance().debug("Formula: " + metamodel.getConforms(modelID).formula);
		EchoReporter.getInstance().debug("Bounds: " + tb.getBounds());
		EchoReporter.getInstance().debug("Targets: " + tb.getBounds().targets());

		// executes the command
		final Solver solver = new Solver();
		solver.options().setSolver(SATFactory.PMaxSAT4J);
		solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
		solver.options().setSymmetryBreaking(0);
		sol = new KodkodSolution(solver.solveAll(metamodel.getConforms(modelID).formula, tb.getBounds()), meta);

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean check(String constraintID) throws EErrorCore {
		EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
		KodkodTransformation transformation = KodkodTranslator.getInstance().getTransformation(constraintID);
		Map<Relation, Pair<Set<Relation>, Set<Relation>>> relationTypes = transformation.getRelationTypes();
		Formula facts = Formula.TRUE;
		Set<KodkodModel> models = new HashSet<>();
		Set<KodkodMetamodel> metas = new HashSet<>();
		// retrieve the meta-model constraints
		for (String modelID : constraint.getModels()) {
			KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
			facts = facts.and(model.getMetamodel().getConforms(modelID).formula);
			models.add(model);
			metas.add(model.getMetamodel());
		}
		// retrieve the transformation constraints
		facts = facts.and(transformation.getConstraint(constraint.getModels()).formula);

		// calculates the (regular) bounds
		SATBinder sb = new SATBinder(models, relationTypes);
		
		// runs the command
		final Solver solver = new Solver();
		solver.options().setSolver(SATFactory.DefaultSAT4J);
		solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
		sol = new KodkodSolution(solver.solveAll(facts, sb.getBounds()), metas);

		return sol.satisfiable();
	}

	/** {@inheritDoc} */
	@Override
	public boolean enforce(String constraintID, List<String> targetIDs) {
		KodkodTransformation transformation = KodkodTranslator.getInstance().getTransformation(constraintID);
		Map<Relation, Pair<Set<Relation>, Set<Relation>>> relationTypes = transformation.getRelationTypes();
		EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
		Formula facts = Formula.TRUE;
		Set<KodkodModel> models = new HashSet<>();
		Set<KodkodMetamodel> metaModels = new HashSet<>();
		for (String modelID : constraint.getModels()) {
			KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
			models.add(model);
			metaModels.add(model.getMetamodel());
		}

		// retrieve the meta-model constraint for the targets
		Set<KodkodModel> targets = new HashSet<>();
		for (String targetID : targetIDs) {
			KodkodModel model = KodkodTranslator.getInstance().getModel(targetID);
			facts = facts.and(model.getMetamodel().getConforms(targetID).formula);
			targets.add(model);
		}
		// retrieve the transformation constraint
		facts = facts.and(transformation.getConstraint(constraint.getModels()).formula);

		// calculate the bounds with targets
		TargetBinder tb = new TargetBinder(models, targets, relationTypes, new HashMap<String, Map<String, Integer>>());

		// run the command
		final Solver solver = new Solver();
		solver.options().setSolver(SATFactory.DefaultSAT4J);
		solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
		sol = new KodkodSolution(solver.solveAll(facts, tb.getBounds()), metaModels);

		return sol.satisfiable();
	}

	/** {@inheritDoc} */
	@Override
	public boolean batch(String constraintID, List<String> targetIDs, Map<String, Map<String, Integer>> scope)
			throws EErrorCore, EErrorUnsupported, EExceptMaxDelta, EErrorParser {
		EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
		KodkodTransformation transformation = KodkodTranslator.getInstance().getTransformation(
				constraint.transformationID);
		Map<Relation, Pair<Set<Relation>, Set<Relation>>> relationTypes = transformation.getRelationTypes();
		Formula facts = Formula.TRUE;
		Set<KodkodModel> models = new HashSet<>();
		Set<KodkodMetamodel> metaModels = new HashSet<>();
		for (String modelID : constraint.getModels()) {
			KodkodModel model = KodkodTranslator.getInstance().getModel(modelID);
			models.add(model);
			metaModels.add(model.getMetamodel());
		}
		
		if (scope == null) scope = new HashMap<String,Map<String,Integer>>();
		
		Set<KodkodModel> targets = new HashSet<>();
		// retrieve the meta-model constraints for the targets
		for (String targetID : targetIDs) {
			KodkodModel model = KodkodTranslator.getInstance().getModel(targetID);
			facts = facts.and(model.getMetamodel().getConforms(targetID).formula);
			targets.add(model);

			EClass root = model.getModel().getRootEElement().type;

			Map<String, Integer> meta_scopes = scope.get(model.getMetamodel().metamodel.ID);
			if (meta_scopes == null)
				meta_scopes = new HashMap<String, Integer>();

			// scope of the root class must be 1
			if (meta_scopes.get(root.getName()) != null && meta_scopes.get(root.getName()) != 1)
				throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT, "Scope of root element must be 1.",
						Task.GENERATE_TASK);

			meta_scopes.put(root.getName(), 1);
			scope.put(model.getMetamodel().metamodel.ID, meta_scopes);
		}
		// retrieve the transformation constraint
		facts = facts.and(transformation.getConstraint(constraint.getModels()).formula);

		// calculate the bounds with targets with additional scopes
		TargetBinder tb = new TargetBinder(models, targets, relationTypes, scope);

		EchoReporter.getInstance().debug("Formula: " + facts);
		EchoReporter.getInstance().debug("Bounds: " + tb.getBounds());
		EchoReporter.getInstance().debug("Targets: " + tb.getBounds().targets());

		// run the command
		final Solver solver = new Solver();
		solver.options().setSolver(SATFactory.PMaxSAT4J);
		solver.options().setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
		solver.options().setSymmetryBreaking(0);
		sol = new KodkodSolution(solver.solveAll(facts, tb.getBounds()), metaModels);

		return true;

	}

	/** {@inheritDoc} */
	@Override
	public void nextSolution() throws EErrorCore {
		throw new UnsupportedOperationException("Solution iteration not implemented in Kodkod.");
	}

	/** {@inheritDoc} */
	@Override
	public EchoSolution getSolution() {
		return sol;
	}

	/** {@inheritDoc} */
	@Override
	public void cancel() {
		return;
	}

	private class KodkodSolution implements EchoSolution {
		Iterator<Solution> sol;
		Solution current;
		Set<KodkodMetamodel> metas;

		KodkodSolution(Iterator<Solution> s, Set<KodkodMetamodel> metas) {
			sol = s;
			current = sol.next();
			this.metas = metas;
		}

		@Override
		public boolean satisfiable() {
			return sol.hasNext();
		}

		@Override
		public void writeXML(String filename) {
			InstanceViewer iv = new InstanceViewer(current.instance().relationTuples(), metas);

			XtextResourceSet resourceSet = new XtextResourceSet();

			XMLResource resource = (XMLResource) resourceSet.createResource(URI.createURI(filename));
			resource.getContents().add(iv.getAlloyInstance());

			Map<Object, Object> options = new HashMap<>();
			// options.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
			try {
				resource.save(options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object getContents() {
			return current;
		}

		@Override
		public void next() {
			current = sol.next();
		}
	}

}
