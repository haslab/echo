package pt.uminho.haslab.mde.transformation;

/**
 * Represents a model paramenter of a transformation
 * Should be extended to match the implementation of the transformation
 * @author nmm
 */
public abstract class EModelParameter {
	
	/** the metamodel URI (i.e., the model parameter type) */
	//TODO: Better URI management
	abstract public String getMetamodelURI();

	/** the parameter name */
	abstract public String getName();
	
	public boolean equals(Object obj) {
		if (!(obj instanceof EModelParameter)) return false;
		EModelParameter in = (EModelParameter) obj;
		return getMetamodelURI().equals(in.getMetamodelURI()) && getName().equals(in.getName());
	}
	
}
