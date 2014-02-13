package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.mde.model.EMetamodel;

/**
 * Represents a model paramenter of a transformation
 * Should be extended to match the implementation of the transformation
 * @author nmm
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

	abstract public EMetamodel getMetamodel();

}
