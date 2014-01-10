package pt.uminho.haslab.mde.transformation;

import java.util.List;

/**
 * A transformation between model parameters
 * Consists of a set of relations
 * @author nmm
 *
 */
public interface ETransformation {

	/** the model parameters (the transformation arguments) */
	public List<EModelParameter> getModels();

	/** the comprising relations */
	public List<ERelation> getRelations();
	
	/** the transformation name */
	public String getName();

	/** 
	 * the transformation unique identifier 
	 * not its URI, which may change if the resource is moved
	 * */	
	public String getIdentifier();
	
}
