package pt.uminho.haslab.mde.model;

/**
 * Echo representation of an enumeration literal.
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EEnumLiteral extends EValue {
	private org.eclipse.emf.ecore.EEnumLiteral value;
	public EEnumLiteral(org.eclipse.emf.ecore.EEnumLiteral value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
