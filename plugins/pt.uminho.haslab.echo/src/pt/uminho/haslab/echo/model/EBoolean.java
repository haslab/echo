package pt.uminho.haslab.echo.model;

public class EBoolean extends EPrimitive {
	private Boolean value;
	public EBoolean(Boolean value) {
		this.value = value;
	}
	
	public Boolean getValue() {
		return value;
	}

}
