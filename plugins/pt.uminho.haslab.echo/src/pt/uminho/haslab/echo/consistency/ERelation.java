package pt.uminho.haslab.echo.consistency;

import java.util.List;

public interface ERelation {
		
	public ETransformation getTransformation();
	public boolean isTop();
	public String getName();
	public List<EModelDomain> getDomains();
	public ECondition getPost();
	public ECondition getPre();
	
	
}
