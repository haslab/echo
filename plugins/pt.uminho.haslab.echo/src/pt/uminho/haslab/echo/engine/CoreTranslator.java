package pt.uminho.haslab.echo.engine;

import java.util.Set;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.engine.ast.*;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * Abstract Echo to engine translation class.
 * Should be extended for concrete engines.
 *
 * @author nmm,tmg
 * @version 0.4 18/03/2015
 */
public abstract class CoreTranslator {

    private static CoreTranslator instance;

    public static CoreTranslator getInstance() {
        return instance;
    }

    public static void init(CoreFactory factory){
        instance = factory.createTranslator();
    }
    
    /**
     * Processes a meta-model into its engine representation.
     * @param metaModel the meta-model to be processed
     * @return 
     * @throws EError
     */
    public abstract CoreMetamodel translateMetamodel(EMetamodel metaModel) throws EError;

	/**
	 * Retrieves the processed meta-model.
	 * @param metamodelID the meta-model ID
	 * @return the processed meta-model
	 */
	public abstract CoreMetamodel getMetamodel(String metamodelID);

    /**
     * Removes a meta-model.
     * @param metamodelID the meta-model ID.
     * @return whether the meta-model was removed.
     */
    public abstract boolean remMetamodel(String metamodelID);

    /**
     * Tests if a meta-model has been processed.
     * @param metamodelID the meta-model ID
     * @return
     */
    public abstract boolean hasMetamodel(String metamodelID);
	
    /**
     * Processes a model into its engine representation.
     * @param model the model to be processed
     * @return 
     * @throws EError
     */
    public abstract CoreModel translateModel(EModel model) throws EError;

	/**
	 * Retrieves the processed model.
	 * @param modelID the model ID
	 * @return the processed model
	 */
	public abstract CoreModel getModel(String modelID);

    /**
     * Removes a model.
     * @param modelID the model ID
     */
    public abstract void remModel(String modelID);

    /**
     * Tests if a model has been processed.
     * @param modelID the model ID
     * @return
     */
    public abstract boolean hasModel(String modelID);
    
    /**
     * Processes a transformation.
     * @param transformation the transformation to be processed
     * @throws EError
     */
    public abstract CoreTransformation translateTransformation(ETransformation transformation) throws EError;

	/**
	 * Retrieves the processed transformation.
	 * @param transformationID the transformation ID
	 * @return the processed transformation
	 */
	public abstract CoreTransformation getTransformation(String transformationID);

    /**
     * Removes a transformation.
     * @param transformationID the transformation ID
     */
    public abstract void remTransformation(String transformationID);

    /**
     * Tests if a transformation has been processed.
     * @param transformationID the transformation ID
     */
    public abstract boolean hasTransformation(String transformationID);

    /**
     * Returns the constant true formula for the particular engine.
     * @return the true formula
     */
    public abstract IFormula getTrueFormula();

    /**
     * Returns the constant false formula for the particular engine.
     * @return the false formula
     */
    public abstract IFormula getFalseFormula();

    /**
     * Returns the constant empty expression for the particular engine.
     * @return the empty expression
     */
	public abstract IExpression getEmptyExpression();
	
	/**
	 * Creates an engine representation of an integer
	 * @param i the integer
	 * @return the integer expression
	 */
    public abstract IIntExpression makeNumber(int i);

    /**
     * Creates a new transformation context for the engine.
     * @return the transformation context
     */
	public abstract ITContext newContext();

	/**
	 * Persists a particular model from a solution.
	 * @param solution the solution
	 * @param modelID the model ID
	 * @throws EError
	 */
    public abstract void writeInstance(EchoSolution solution, String modelID) throws EError;

	public abstract Set<String> strings();

}
