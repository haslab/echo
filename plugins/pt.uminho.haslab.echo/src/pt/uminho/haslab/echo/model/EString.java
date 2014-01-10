package pt.uminho.haslab.echo.model;


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
