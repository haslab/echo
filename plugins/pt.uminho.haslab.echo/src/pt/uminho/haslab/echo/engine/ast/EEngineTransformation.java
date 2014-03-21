package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.alloy.ErrorAlloy;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;

import java.util.List;
import java.util.Map;

/**
 * An embedding of a model transformation in an abstract Echo engine.
 * 
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class EEngineTransformation {

	/** the transformation being translated */
	public final ETransformation transformation;

	/**
	 * Embeds a model Transformation in an Echo engine. A
	 * {@code EEngineRelation} is created for every top relation and dependency.
	 * 
	 * @param transformation
	 *            the model transformation being translated
	 * @param dependencies
	 *            the dependencies set for each relation
	 * @throws EchoError
	 */
	protected EEngineTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_TRANSFORMATION,
				transformation.getName());
		this.transformation = transformation;
		boolean trace = false;
		if (transformation instanceof EATLTransformation)
			trace = true;

		initModelParams();

		// translates each top relation
		// in ATL all transformations (including lazy ones) must be translated at top-level due to implicit calls
		for (ERelation rel : transformation.getRelations()) {
			if (rel.isTop()||trace)
				for (EDependency dep : dependencies.get(rel.getName())) 
					createRelation(rel, dep, trace);
		}

		processConstraint();
		EchoReporter.getInstance().result(Task.TRANSLATE_TRANSFORMATION,
				transformation.getName(), true);
	}

	/**
	 * Embeds a transformation relation in an Echo engine.
	 * 
	 * @param rel
	 *            the relation to translate
	 * @param dep
	 *            the dependency (direction) being translated
	 * @param trace 
	 * @throws EchoError
	 */
	protected abstract void createRelation(ERelation rel, EDependency dep, boolean trace)
			throws EchoError;

	/**
	 * Initializes anything related with model parameters.
	 * 
	 * @throws ErrorInternalEngine
	 */
	protected abstract void initModelParams() throws ErrorInternalEngine;

	/**
	 * Processes the transformation constraint.
	 * 
	 * @throws ErrorInternalEngine
	 */
	protected abstract void processConstraint() throws ErrorInternalEngine;

	/**
	 * Calculates the transformation constraint for specific models. Function
	 * parameters are the model variables.
	 * 
	 * @param vars
	 *            the model variables
	 * @return the constraint over the variables
	 */
	protected abstract IFormula getConstraint(List<String> modelIDs);

	/**
	 * Defines a sub-relation call relation previously inserted by
	 * <code>addSubRelationCall</code>. Called by containing relations.
	 * 
	 * @param rel
	 *            the relation being defined
	 * @param def
	 *            defines the sub-relation field
	 * @throws ErrorInternalEngine
	 */
	protected abstract void defineSubRelationField(EEngineRelation rel,
			IFormula def) throws ErrorInternalEngine;

	/**
	 * Adds a new expression denoting a sub-relation call. Called by containing
	 * relations.
	 * 
	 * @param rel
	 *            the relation of which the predicate will be created
	 * @param field
	 *            the expression that denotes the call
	 * @throws ErrorInternalEngine
	 */
	protected abstract void addSubRelationField(EEngineRelation rel,
			IExpression field) throws ErrorInternalEngine;

	/**
	 * Adds a new top-relation constraint. Called by containing relations.
	 * 
	 * @param rel
	 *            the relation whose constraint is being created
	 * @throws ErrorInternalEngine
	 */
	protected abstract void addTopRelationConstraint(EEngineRelation rel)
			throws ErrorInternalEngine;

	/**
	 * Returns the expression resulting from calling a relation with specific
	 * parameters.
	 * 
	 * @param rel
	 *            the relation being called
	 * @param context
	 *            the context of the transformation
	 * @param params
	 *            the parameters of the relation
	 * @return the expression resulting from calling
	 *         <code>rel</rel> over <code>params</code>
	 */
	public abstract IFormula callRelation(ERelation rel, ITContext context,
			List<IExpression> params);

	public abstract IExpression callAllRelation(ITContext context,
			IExpression param) throws ErrorAlloy, ErrorUnsupported;

}
