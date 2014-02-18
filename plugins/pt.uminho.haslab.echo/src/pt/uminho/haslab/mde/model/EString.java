package pt.uminho.haslab.mde.model;

/**
 * Echo representation of a string value
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EString extends EPrimitive {
	private String value;
	public EString(String value) {
		this.value = value;
	}
	@Override
	public String getValue() {
		return value;
	}
}
