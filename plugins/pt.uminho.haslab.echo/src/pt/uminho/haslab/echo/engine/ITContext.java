package pt.uminho.haslab.echo.engine;

import java.util.List;

import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IExpression;

public interface ITContext extends IContext {
	
	public IExpression getModelParam(String name);

	public List<IExpression> getModelParams();

	public EEngineRelation getCurrentRel();
	
	public IExpression addModelParam(boolean pre, String name, IExpression var);

	public IExpression addModelParamX(boolean pre, String name, IExpression var);

}
