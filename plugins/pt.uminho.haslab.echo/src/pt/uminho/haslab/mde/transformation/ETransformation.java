package pt.uminho.haslab.mde.transformation;

import java.util.List;

/**
 * A transformation between model parameters
 * Consists of a set of relations
 * @author nmm
 *
 */
public abstract class ETransformation {

	/** the model parameters (the transformation arguments) */
	public abstract List<EModelParameter> getModels();

	/** the comprising relations */
	public abstract List<ERelation> getRelations();
	
	/** the transformation name */
	public abstract String getName();
	
	public abstract String getURI();

	/** 
	 * the transformation unique identifier 
	 * not its URI, which may change if the resource is moved
	 * */	
	public final String ID;
	
	protected ETransformation(String ID) {
		this.ID = ID+this.hashCode();
	}
	
}
