package pt.uminho.haslab.echo.consistency;

public abstract class EModelDomain {
	abstract public EModelParameter getModel();
	abstract public EVariable getRootVariable();
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
