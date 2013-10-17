package pt.uminho.haslab.echo.consistency;

import java.util.List;

import pt.uminho.haslab.echo.ErrorParser;

public interface Relation {
		
	public Transformation getTransformation();
	public boolean isTop();
	public String getName();
	public List<Domain> getDomains();
	public Condition getPost();
	public Condition getPre();
	
	
}
