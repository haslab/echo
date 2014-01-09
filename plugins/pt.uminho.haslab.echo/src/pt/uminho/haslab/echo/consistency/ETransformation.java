package pt.uminho.haslab.echo.consistency;

import java.util.List;

public interface ETransformation {

	public List<EModelParameter> getModels();
	public List<ERelation> getRelations();
	
	public String getName();
	
}
