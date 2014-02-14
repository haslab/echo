package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.mde.model.EMetamodel;

/**
 * An embedding of a model parameter of an EMF transformation in Echo.
 * Should be extended by concrete EMF transformations (QVT-R, ATL).
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class EModelParameter {

	/** the parameter name */
	abstract public String getName();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EModelParameter)) return false;
		EModelParameter in = (EModelParameter) obj;
		return getMetamodel().ID.equals(in.getMetamodel().ID);
	}

	/** returns the type (metamodel) of the model parameter */
	abstract public EMetamodel getMetamodel();

}
