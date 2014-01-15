package pt.uminho.haslab.mde.model;

/**
 * Echo representation of an integer value
 * @author nmm
 *
 */
public class EInteger extends EValue {
	private Integer value;
	public EInteger(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}

}