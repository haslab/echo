package pt.uminho.haslab.mde.model;

/**
 * Echo representation of an integer value
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EInteger extends EValue {
	private Integer value;
	public EInteger(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

}