package pt.uminho.haslab.echo.consistency;

import java.util.List;

public interface Relation {
		
	public Transformation getTransformation();
	public boolean isTop();
	public String getName();
	public List<Domain> getDomains();
	public Condition getPost();
	public Condition getPre();
	
	
}
