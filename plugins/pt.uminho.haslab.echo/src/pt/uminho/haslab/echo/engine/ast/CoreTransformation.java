package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.alloy.EErrorAlloy;
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
public abstract class CoreTransformation {

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
	 * @throws EError
	 */
	protected CoreTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EError {
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
	}

	/**
	 * Embeds a transformation relation in an Echo engine.
	 * 
	 * @param rel
	 *            the relation to translate
	 * @param dep
	 *            the dependency (direction) being translated
	 * @param trace 
	 * @throws EError
	 */
	protected abstract void createRelation(ERelation rel, EDependency dep, boolean trace)
			throws EError;

	/**
	 * Initializes anything related with model parameters.
	 * 
	 * @throws EErrorCore
	 */
	protected abstract void initModelParams() throws EErrorCore;

	/**
	 * Processes the transformation constraint.
	 * 
	 * @throws EErrorCore
	 */
	protected abstract void processConstraint() throws EErrorCore;

	/**
	 * Calculates the transformation constraint for specific models. Function
	 * parameters are the model variables.
	 * 
	 * @param vars
	 *            the model variables
	 * @return the constraint over the variables
	 */
	public abstract IFormula getConstraint(List<String> modelIDs);

	/**
	 * Defines a sub-relation call relation previously inserted by
	 * <code>addSubRelationCall</code>. Called by containing relations.
	 * 
	 * @param rel
	 *            the relation being defined
	 * @param def
	 *            defines the sub-relation field
	 * @throws EErrorCore
	 */
	protected abstract void defineSubRelationField(CoreRelation rel,
			IFormula def) throws EErrorCore;

	/**
	 * Adds a new expression denoting a sub-relation call. Called by containing
	 * relations.
	 * 
	 * @param rel
	 *            the relation of which the predicate will be created
	 * @param field
	 *            the expression that denotes the call
	 * @throws EErrorCore
	 */
	protected abstract void addSubRelationField(CoreRelation rel,
			IExpression field) throws EErrorCore;

	/**
	 * Adds a new top-relation constraint. Called by containing relations.
	 * 
	 * @param rel
	 *            the relation whose constraint is being created
	 * @throws EErrorCore
	 */
	protected abstract void addTopRelationConstraint(CoreRelation rel)
			throws EErrorCore;

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
	public abstract INode callRelation(ERelation rel, ITContext context,
			List<IExpression> params);

	public abstract IExpression callAllRelation(ITContext context,
			IExpression param) throws EErrorAlloy, EErrorUnsupported;

}
