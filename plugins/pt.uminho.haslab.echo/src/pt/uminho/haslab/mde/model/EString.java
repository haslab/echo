package pt.uminho.haslab.mde.model;

/**
 * Echo representation of a string value
 * @author nmm
 *
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
