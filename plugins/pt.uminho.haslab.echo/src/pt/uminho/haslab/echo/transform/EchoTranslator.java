package pt.uminho.haslab.echo.transform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoSolution;
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

    public abstract IFormula getTrueFormula();

    public abstract void writeAllInstances(EchoSolution solution, String metaModelUri, String modelUri) throws EchoError;

    public abstract void writeInstance(EchoSolution solution, String modelUri) throws EchoError;

    public abstract String getMetaModelFromModelPath(String path);

    public abstract void translateMetaModel(EPackage metaModel) throws EchoError;

    public abstract void remMetaModel(String metaModelUri);

    public abstract void translateModel(EObject model) throws EchoError;

    public abstract void remModel(String modelUri);

    public abstract void translateConstraint(ETransformation constraint) throws EchoError;

    public abstract boolean hasQVT(String qvtUri);

    public abstract boolean remQVT(String qvtUri);

    public abstract ETransformation getQVT(String qvtUri);

    public abstract boolean hasMetaModel(String metaModelUri);

    public abstract boolean hasModel(String modelUri);

	public abstract EModel getModel(String modelUri);
}
