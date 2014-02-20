package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EPredicate;

import java.util.List;

/**
 * An embedding of a relation of an EMF transformation in Echo.
 * Consists of a relation between a set of model domains, associated with pre- and post-conditions
 * Should be extended by concrete EMF transformations (QVT-R, ATL)
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public interface ERelation {

	/** the parent transformation 
	 * @throws ErrorParser 
	 * @throws ErrorUnsupported */
	public ETransformation getTransformation() throws ErrorUnsupported, ErrorParser;

	/** if the relation is top level */
	public boolean isTop();

	/** the relation name  */
	public String getName();

	/** the model domains */
	public List<EModelDomain> getDomains();

	/** the post-condition */
	public EPredicate getPost();

	/** the pre-condition */
	public EPredicate getPre();


}
