package pt.uminho.haslab.echo;

import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.engine.alloy.EErrorAlloy;

/**
 * Interface for Echo's core engine MDE tasks.
 * 
 * @author nmm, tmg
 * @version 0.4 18/09/2014
 */
public interface CoreRunner {

	/**
	 * Creates a solution exactly representing a set of models.
	 * 
	 * @param modelIDs
	 *            the model IDs.
	 * @throws EErrorCore
	 *             if the core engine failed to execute.
	 */
	void show(List<String> modelIDs) throws EErrorCore;

	/**
	 * Tests if a set of models conform to respective meta-models.
	 * 
	 * @param modelIDs
	 *            the model IDs.
	 * @return whether the models conform to the meta-models.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 */
	boolean conforms(List<String> modelIDs) throws EErrorCore;

	/**
	 * Launches an intra-model consistency repair task for a given model.
	 * 
	 * @param modelID
	 *            the model ID.
	 * @return whether the repair was successful.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 * @throws EExceptMaxDelta
	 *             if the maximum delta was reached before consistency.
	 * @throws EExceptConsistent
	 *             if the model was already consistent.
	 */
	boolean repair(String modelID) throws EErrorCore, EExceptMaxDelta, EExceptConsistent;

	/**
	 * Launches a model generation task conforming to given meta-model. Assumes
	 * that the fresh (empty) model is already being managed.
	 * 
	 * @param modelID
	 *            ID of the fresh model.
	 * @param scope
	 *            additional scope for the generation (Package,(Class,Scope)).
	 * @return whether the generation was successful.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 * @throws EErrorUnsupported
	 *             if some unsupported feature in the meta-model (currently,
	 *             multiple root classes).
	 * @throws EExceptMaxDelta
	 *             if the maximum delta was reached before consistency.
	 * @throws EErrorParser
	 *             if the scope failed to be parsed.
	 */
	boolean generate(String modelID, Map<String, Map<String, Integer>> scope) throws EErrorCore, EErrorUnsupported,
			EExceptMaxDelta, EErrorParser;

	/**
	 * Tests whether a set of models are consistent according to a given
	 * inter-model constraint. The constraint contains information regarding the
	 * models and the transformation.
	 * 
	 * @param constraintID
	 *            the ID of the inter-model constraint.
	 * @return whether the constraint holds.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 */
	boolean check(String constraintID) throws EErrorCore;

	/**
	 * Launches an inter-model consistency repair tasks for a given constraint
	 * over selected targets.
	 * 
	 * @param constraintID
	 *            the ID of the inter-model constraint.
	 * @param targetIDs
	 *            the target model IDs (should be a subset of the constriant's
	 *            models).
	 * @return whether the repair was successful.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 * @throws EExceptMaxDelta
	 *             if the maximum delta was reached before consistency.
	 * @throws EExceptConsistent
	 *             if the model was already consistent.
	 */
	boolean enforce(String constraintID, List<String> targetIDs) throws EExceptConsistent, EExceptMaxDelta, EErrorAlloy;

	/**
	 * Launches a batch generation task for a given constraint over selected
	 * targets.
	 * 
	 * @param constraintID
	 *            the ID of the inter-model constraint.
	 * @param targetIDs
	 *            the target model IDs (should be a subset of the constriant's
	 *            models).
	 * @param scope
	 *            additional scope for the generation (Package,(Class,Scope)).
	 * @return true if successful.
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 * @throws EErrorUnsupported
	 *             if some unsupported feature in the meta-model (currently,
	 *             multiple root classes).
	 * @throws EExceptMaxDelta
	 *             if the maximum delta was reached before consistency.
	 * @throws EErrorParser
	 *             if the scope failed to be parsed.
	 */
	boolean batch(String constraintID, List<String> targetIDs, Map<String, Map<String, Integer>> scope)
			throws EErrorCore, EErrorUnsupported, EExceptMaxDelta, EErrorParser;

	/**
	 * Calculates the next solution to the current task.
	 * 
	 * @throws EErrorCore
	 *             if the engine failed to execute.
	 * @throws EExceptMaxDelta
	 *             if the maximum delta was reached before consistency.
	 */
	void nextSolution() throws EErrorCore, EExceptMaxDelta;

	/**
	 * Retrieves the current solution to the current task.
	 * 
	 * @return the current solution.
	 */
	EchoSolution getSolution();

	/**
	 * Cancels the execution of the current task.
	 */
	void cancel();
}
