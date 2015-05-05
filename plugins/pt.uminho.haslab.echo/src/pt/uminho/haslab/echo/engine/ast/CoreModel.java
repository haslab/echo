package pt.uminho.haslab.echo.engine.ast;

import java.util.Set;

import pt.uminho.haslab.mde.model.EModel;

public interface CoreModel {

	/** the constraint defining this model */
	public IFormula getModelConstraint();
	
	public CoreMetamodel getMetamodel();
	
	public EModel getModel();

	Set<String> strings();
	
}
