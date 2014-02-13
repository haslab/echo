package pt.uminho.haslab.mde.transformation;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EArtifact;

/**
 * A transformation between model parameters.
 * Consists of a set of ERelations.
 * Should be extended by concrete transformations (QVT-R, ATL)
 *
 * @author nmm
 * @version 0.4 13/02/2014
 */
public abstract class ETransformation extends EArtifact {

	/** the model parameters (the transformation arguments) */
	public abstract List<EModelParameter> getModels();

	/** the comprising relations */
	public abstract List<ERelation> getRelations();

	/** the transformation name */
	public abstract String getName();

	protected ETransformation(String ID, EObject artifact) throws ErrorUnsupported, ErrorParser {
		super(ID, artifact);
	}

}
