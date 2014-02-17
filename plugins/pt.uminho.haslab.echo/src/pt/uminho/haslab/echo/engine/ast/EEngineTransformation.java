package pt.uminho.haslab.echo.engine.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

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
	 * Embeds a model Transformation in an Echo engine.
	 * A {@code EEngineRelation} is created for every top relation and dependency.
	 * @param transformation the model transformation being translated
	 * @param dependencies the dependencies set for each relation
	 * @throws EchoError
	 */
	protected EEngineTransformation (ETransformation transformation, Map<String,List<EDependency>> dependencies) throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_TRANSFORMATION, transformation.getName());
		this.transformation = transformation;

		manageModelParams();	

		for (ERelation rel : transformation.getRelations())
			if (rel.isTop()) {
				for (EDependency dep : dependencies.get(rel.getName()))
					createRelation(rel,dep);
			}
		
		generateConstraints();
		EchoReporter.getInstance().result(Task.TRANSLATE_TRANSFORMATION, transformation.getName(), true);
	}
	
	/**
	 * Embeds a transformation relation in an Echo engine.
	 * @param rel the relation to translate
	 * @param dep the dependency (direction) being translated
	 * @throws EchoError
	 */
	protected abstract void createRelation(ERelation rel, EDependency dep) throws EchoError;

	/**
	 * Creates the transoformation model parameters.
	 * @param model_params_decls
	 * @param model_params_vars
	 * @throws ErrorInternalEngine
	 */
	protected abstract void manageModelParams() throws ErrorInternalEngine;

	protected abstract void generateConstraints() throws ErrorInternalEngine;
	
	/**
	 * Returns the Alloy function corresponding to this QVT Transformation
	 * Function parameters are the model variables
	 * @return this.fact
	 */	
	protected abstract IFormula getTransformationConstraint(List<IExpression> vars);

	/**
 	 * Defines a sub-relation call relation previously inserted by <code>addSubRelationCall</code>.
	 * Called by containing relations. 
	 * @param rel the relation of which the predicate will be created
	 * @param modelParams the parameters of the predicate
	 * @param def the definition of the relation
	 * @throws ErrorInternalEngine
	 */
	protected abstract void defineSubRelationCall(EEngineRelation rel, IFormula def) throws ErrorInternalEngine;

	/**
 	 * Adds a new an expression denoting a sub-relation call.
	 * Called by containing relations. 
	 * @param rel the relation of which the predicate will be created
	 * @param modelParams the parameters of the predicate
	 * @param relation the expression (relation or field) that will denote the call
	 * @throws ErrorInternalEngine
	 */
	public abstract void addSubRelationCall(EEngineRelation rel, IExpression relation) throws ErrorInternalEngine;
	
	/**
 	 * Adds a new top-relation predicate.
	 * Called by containing relations. 
	 * @param rel the relation of which the predicate will be created
	 * @param modelParams the parameters of the predicate
	 * @throws ErrorInternalEngine
	 */
	protected abstract void addTopRelationCall(EEngineRelation rel) throws ErrorInternalEngine;

	/**
	 * Returns the expression resulting from calling a relation with specific parameters.
	 * @param rel the relation being called
	 * @param context the context of the transformation
	 * @param params the parameters of the relation
	 * @return the expression resulting from calling <code>rel</rel> over <code>params</code>
	 */
	public abstract IFormula callRelation(ERelation rel, ITContext context, List<IExpression> params);

}
