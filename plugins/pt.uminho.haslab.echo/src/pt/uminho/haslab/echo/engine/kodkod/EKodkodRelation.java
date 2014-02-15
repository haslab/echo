package pt.uminho.haslab.echo.engine.kodkod;

import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;

public class EKodkodRelation extends EEngineRelation {

	public EKodkodRelation(ERelation relation,
			EDependency dependency, EEngineTransformation transformation) throws EchoError {
		super(relation, dependency, transformation);
	}

	/** {@inheritDoc} */
	@Override
	protected IFormula translateCondition(EPredicate targetCondition)
			throws EchoError {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected IDecl createDecl(String metamodelID) throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, IDecl> createVarDecls(
			Map<EVariable, String> sourcevar2model, boolean b) throws EchoError {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected IExpression createNonTopRel(IDecl fst) throws ErrorInternalEngine {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** {@inheritDoc} */
	@Override
	protected IFormula simplify(IFormula formula) throws ErrorUnsupported {
		return formula;
	}

}
