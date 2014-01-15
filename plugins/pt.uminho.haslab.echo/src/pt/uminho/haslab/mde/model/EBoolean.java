package pt.uminho.haslab.mde.model;

/**
 * Echo representation of a boolean value
 * @author nmm
 *
 */
public class EBoolean extends EPrimitive {
	private Boolean value;
	
	public EBoolean(Boolean value) {
		this.value = value;
	}
	
	public Boolean getValue() {
		return value;
	}

}
