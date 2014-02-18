package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.mde.model.EModel;

public interface EEngineModel {

	/** the constraint defining this model */
	public IFormula getModelConstraint();
	
	public EEngineMetamodel getMetamodel();
	
	public EModel getModel();
	
}
