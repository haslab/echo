package pt.uminho.haslab.echo.transform;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.echo.transform.ast.IIntExpression;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/23/13
 * Time: 6:26 PM
 */
public abstract class EchoTranslator {

    private static EchoTranslator instance;

    public static EchoTranslator getInstance() {
        return instance;
    }

    public static void init(TransformFactory factory){
        instance = factory.createTranslator();
    }
	
    public abstract void translateModel(EModel model) throws EchoError;

    public abstract void remModel(String modelID);

    public abstract boolean hasModel(String modelID);
    
    public abstract void translateMetaModel(EMetamodel metaModel) throws EchoError;

    public abstract boolean hasMetaModel(String metamodelID);

    public abstract void remMetaModel(String metamodelID);

    public abstract void translateTransformation(ETransformation constraint) throws EchoError;

    public abstract boolean hasTransformation(String qvtID);

    public abstract void remTransformation(String qvtID);
    
    public abstract IFormula getTrueFormula();

    public abstract IFormula getFalseFormula();

    public abstract void writeAllInstances(EchoSolution solution, String metaModelUri, String modelUri) throws EchoError;

    public abstract void writeInstance(EchoSolution solution, String modelUri) throws EchoError;

    public abstract IIntExpression makeNumber(int n);
}
