package pt.uminho.haslab.echo.consistency;

public abstract class EModelParameter {
	
	abstract public String getMetamodelURI();
	abstract public String getName();
	
	public boolean equals(Object obj) {
		if (!(obj instanceof EModelParameter)) return false;
		EModelParameter in = (EModelParameter) obj;
		return getMetamodelURI().equals(in.getMetamodelURI()) && getName().equals(in.getName());
	}
	
}
