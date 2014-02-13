package pt.uminho.haslab.mde.model;

/**
 * Echo representation of a boolean value
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EBoolean extends EPrimitive {
	private Boolean value;

	public EBoolean(Boolean value) {
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

}
