package pt.uminho.haslab.echo.transform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import pt.uminho.haslab.echo.*;

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

    public static void init(EngineFactory factory){
        instance = factory.createTranslator();
    }

    public abstract void writeAllInstances(EchoSolution solution, String metaModelUri, String modelUri) throws ErrorTransform, ErrorUnsupported, ErrorInternalEngine;

    public abstract void writeInstance(EchoSolution solution, String modelUri) throws ErrorInternalEngine, ErrorTransform;

    public abstract String getMetaModelFromModelPath(String path);

    public abstract void translateMetaModel(EPackage metaModel) throws ErrorUnsupported, ErrorInternalEngine, ErrorTransform, ErrorParser;

    public abstract void remMetaModel(String metaModelUri);

    public abstract void translateModel(EObject model) throws ErrorUnsupported, ErrorInternalEngine, ErrorTransform, ErrorParser;

    public abstract void remModel(String modelUri);

    public abstract void translateQVT(RelationalTransformation qvt) throws ErrorTransform, ErrorInternalEngine, ErrorUnsupported, ErrorParser;

    public abstract Object getQVTFact(String qvtUri);

    public abstract void translateATL(EObject atl, EObject mdl1, EObject mdl2) throws ErrorTransform, ErrorInternalEngine, ErrorUnsupported, ErrorParser;

    public abstract boolean remQVT(String qvtUri);

    public abstract boolean hasMetaModel(String metaModelUri);

    public abstract boolean hasModel(String modelUri);
}
