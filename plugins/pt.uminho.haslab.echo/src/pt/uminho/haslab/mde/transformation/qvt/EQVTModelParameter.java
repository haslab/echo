package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelParameter;

/**
 * An embedding of an EMF QVT-R model parameter in Echo.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EQVTModelParameter extends EModelParameter {

	/** the processed EMF model parameter */
	private TypedModel modelParam;
	/** the type (metamodel) of the model parameter */
	private EMetamodel metamodel;

	/**
	 * Processes an EMF model parameter.
	 * @param modelParam the original EMF model parameter
	 */
	public EQVTModelParameter(TypedModel modelParam) {
		this.modelParam = modelParam;
		
		String metamodelURI = EcoreUtil.getURI(modelParam.getUsedPackage().get(0).getEPackage()).path().replace("/resource", "");
		try {
			metamodel = MDEManager.getInstance().getMetamodel(metamodelURI, false);
		} catch (EError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public EMetamodel getMetamodel() {
		return metamodel;
	}

	@Override
	public String getName() {
		return modelParam.getName();
	}

}
