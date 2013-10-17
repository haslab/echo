package pt.uminho.haslab.echo.consistency;

import java.util.List;

public interface Transformation {

	public List<Model> getModels();
	public List<Relation> getRelations();
	
	public String getName();
	
}
