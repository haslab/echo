package pt.uminho.haslab.mde.transformation;

import org.eclipse.emf.ecore.EObject;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EArtifact;

import java.util.List;

/**
 * An embedding of an EMF transformation between model parameters in Echo.
 * Consists of a set of ERelations.
 * Should be extended by concrete EMF transformations (QVT-R, ATL)
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class ETransformation extends EArtifact {

	/** returns the transformation model parameters (the transformation arguments) */
	public abstract List<? extends EModelParameter> getModelParams();

	/** returns comprising relations */
	public abstract List<? extends ERelation> getRelations();

	/** returns transformation name */
	public abstract String getName();
	
	/** 
	 * Returns a concrete model parameter.
	 * @param paramName the naming of the model parameter to return
	 * @return the matching model parameter
	 */
	public abstract EModelParameter getModelParameter(String paramName);
  
	/**
	 * Processes an EMF transformation itno Echo.
	 * @param ID the ID of the new transformation
	 * @param artifact the EMF artifact containing the transformation
	 * @throws ErrorUnsupported
	 * @throws ErrorParser
	 */
	protected ETransformation(String ID, EObject artifact) throws ErrorUnsupported, ErrorParser {
		super(ID, artifact);
	}

}
