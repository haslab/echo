package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.mde.model.ECondition;
import pt.uminho.haslab.mde.model.EVariable;

/**
 * Represents a model domain of a relation
 * Consists of a root variable from a model restricted by a condition
 * Should be extended to match the implementation of the parent relation
 * @author nmm
 *
 */
public abstract class EModelDomain {
	
	/** the parent consistency relation */
	abstract public ERelation getRelation();
	/** the model parameter regarding this model domain */
	abstract public EModelParameter getModel();
	/** the root variable of the domain */
	abstract public EVariable getRootVariable();
	/** the condition restricting the root variable */
	abstract public ECondition getCondition();

	public String toString() {
		return getModel().getName() + "::" + getModel().getMetamodelURI();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof EModelDomain)) return false;
		EModelDomain in = (EModelDomain) obj;
		return getModel().equals(in.getModel());
	}
	
}
