package pt.uminho.haslab.echo.model;

public class EInteger extends EValue {
	private Integer value;
	public EInteger(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}

}