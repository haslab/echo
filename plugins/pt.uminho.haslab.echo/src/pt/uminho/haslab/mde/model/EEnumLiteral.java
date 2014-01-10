package pt.uminho.haslab.mde.model;


public class EEnumLiteral extends EValue {
	private org.eclipse.emf.ecore.EEnumLiteral value;
	public EEnumLiteral(org.eclipse.emf.ecore.EEnumLiteral value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

}
