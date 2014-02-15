package pt.uminho.haslab.echo.engine.kodkod;

import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

public class EKodkodTransformation extends EEngineTransformation {

	protected EKodkodTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createRelation(ERelation rel, EDependency dep)
			throws EchoError {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createParams(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generateConstraints(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IFormula getTransformationConstraint(List<IExpression> vars) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addSubRelationDef(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IFormula e)
			throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSubRelationCall(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IExpression exp)
			throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addTopRelationCall(EEngineRelation arelation,
			List<IDecl> model_params_decls, IFormula fact)
			throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IFormula callRelation(ERelation n, EDependency dep,
			List<IExpression> aux) {
		// TODO Auto-generated method stub
		return null;
	}

}
