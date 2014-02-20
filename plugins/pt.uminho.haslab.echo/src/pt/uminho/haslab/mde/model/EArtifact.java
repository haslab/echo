package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;

/**
 * The Echo representation of an abstract artifact.
 * Should be extended to represent models, metamodels, transformations.
 * Has a immutable ID and a URI which may be updated.
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public abstract class EArtifact {

	/** the artifact unique identifier (may not change) */
	public final String ID;
	/** the artifact URI (may change) */
	private String URI;

	protected EArtifact(String iD, EObject artifact) throws ErrorUnsupported, ErrorParser {
		ID = iD+this.hashCode();
		URI = EcoreUtil.getURI(artifact).path();
		process(artifact);
	}

	/**
	 * Returns the artifact URI
	 * 
	 * @return the artifact URI
	 */
	public String getURI() {
		return URI;
	}

	/**
	 * Retrieves the original EObject
	 * @return the EObject being represented
	 */
	abstract public EObject getEObject();

	/**
	 * Updates the content of the artifact
	 * 
	 * @param artifact the new artifact
	 * @throws ErrorUnsupported
	 * @throws ErrorParser
	 */
	public void update(EObject artifact) throws ErrorUnsupported, ErrorParser {
		URI = EcoreUtil.getURI(artifact).path();
		process(artifact);
	}

	/**
	 * Effectively processes the artifact
	 * @param artifact the object to be processed
	 * @throws ErrorUnsupported
	 * @throws ErrorParser
	 */
	protected abstract void process(EObject artifact) throws ErrorUnsupported, ErrorParser;
}
