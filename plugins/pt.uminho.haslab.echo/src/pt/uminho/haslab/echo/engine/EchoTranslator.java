package pt.uminho.haslab.echo.engine;

import pt.uminho.haslab.echo.EchoError;
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
 * @version 0.4 21/03/2014
 */
public abstract class EchoTranslator {

    private static EchoTranslator instance;

    public static EchoTranslator getInstance() {
        return instance;
    }

    public static void init(TransformFactory factory){
        instance = factory.createTranslator();
    }
	
    /**
     * Processes a model.
     * @param model the model to be processed
     * @throws EchoError
     */
    public abstract void translateModel(EModel model) throws EchoError;

	/**
	 * Retrieves the processed model.
	 * @param modelID the model ID
	 * @return the processed model
	 */
	public abstract EEngineModel getModel(String modelID);

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
     * Processes a meta-model.
     * @param metaModel the meta-model to be processed
     * @throws EchoError
     */
    public abstract void translateMetaModel(EMetamodel metaModel) throws EchoError;

	/**
	 * Retrieves the processed meta-model.
	 * @param metamodelID the meta-model ID
	 * @return the processed meta-model
	 */
	public abstract EEngineMetamodel getMetamodel(String metamodelID);

    /**
     * Removes a meta-model.
     * @param metamodelID the meta-model ID
     */
    public abstract void remMetaModel(String metamodelID);

    /**
     * Tests if a meta-model has been processed.
     * @param metamodelID the meta-model ID
     * @return
     */
    public abstract boolean hasMetaModel(String metamodelID);

    /**
     * Processes a transformation.
     * @param transformation the transformation to be processed
     * @throws EchoError
     */
    public abstract void translateTransformation(ETransformation transformation) throws EchoError;

	/**
	 * Retrieves the processed transformation.
	 * @param transformationID the transformation ID
	 * @return the processed transformation
	 */
	public abstract EEngineTransformation getQVTTransformation(String transformationID);

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
	 * Persists all models in a solution belonging to a particular meta-model.
	 * @param solution the solution
	 * @param metamodelID the meta-model ID
	 * @param modelURI the URI of the new model
	 * @throws EchoError
	 */
	public abstract void writeAllInstances(EchoSolution solution, String metamodelID, String modelURI) throws EchoError;

	/**
	 * Persists a particular model from a solution.
	 * @param solution the solution
	 * @param modelID the model ID
	 * @throws EchoError
	 */
    public abstract void writeInstance(EchoSolution solution, String modelID) throws EchoError;
}
