package pt.uminho.haslab.echo.engine;

import java.util.List;

import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IExpression;

/**
 * Auxiliary context for the translation of transformations to engine.
 * Mainly used for model parameter management.
 * If in the context of a transformation, should use model parameter names as keys.
 * Otherwise, should use the meta-model IDs.
 *
 * @author nmm
 * @version 0.4 17/02/2014
 */

public interface ITContext extends IContext {
	
	/**
	 * Retrieves the expression representing a given model.
	 * If meta-model ID, will return the meta-model set.
	 * If model parameter name (in the context of transformations) should return specific variable.
	 * @param modelName the name of model (may be ID or parameter name).
	 * @return the expression representing it
	 */
	public IExpression getModelExpression(String modelName);

	/**
	 * Return all model expressions involved in the context.
	 * If in the context of a transformation, will return the specific model parameters.
	 * Otherwise, will return the meta-model sets.
	 * @return the model expressions involved in the context
	 */
	public List<IExpression> getModelExpressions();

	/**
	 * The relation that called the current transformation.
	 * Should be self it top or parent if non-top.
	 * @return the caller relation
	 */
	public EEngineRelation getCallerRel();
	
	/**
	 * Sets the relation called the current transformation.
	 * Should be self it top or parent if non-top.
	 * @param parentRelation the caller relation
	 */
	public void setCurrentRel(EEngineRelation parentRelation);

	/**
	 * Adds a new expression representing a meta-model.
	 * @param pre if it represents the pre or post-sate
	 * @param metamodelID the ID of the meta-model
	 * @param var the expression representing the meta-model
	 * @return
	 */
	public IExpression addMetamodelExpression(boolean pre, String metamodelID, IExpression var);

	/**
	 * Adds a new expression representing a model parameter.
	 * @param pre if it represents the pre or post-sate
	 * @param metamodelID the ID of the meta-model
	 * @param var the expression representing the meta-model
	 * @return
	 */
	public IExpression addParamExpression(boolean pre, String paramName, IExpression var);


}
