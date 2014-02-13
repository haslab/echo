package pt.uminho.haslab.mde.transformation;

import java.util.List;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.ECondition;

/**
 * A relation between a set of model domains, associated with pre- and post-conditions
 * @author nmm
 *
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
	public ECondition getPost();

	/** the pre-condition */
	public ECondition getPre();


}
