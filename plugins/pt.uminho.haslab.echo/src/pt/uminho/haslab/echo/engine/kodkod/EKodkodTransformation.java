package pt.uminho.haslab.echo.engine.kodkod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

public class EKodkodTransformation extends EEngineTransformation {

	/** the Kodkod expression rising from this QVT Transformation*/
	private Formula form;
	
	/** the Kodkod functions defining sub-relation consistency */
	private Map<String,Formula> subRelationCallDefs;

	/** the Kodkod functions defining sub-relation calls */
	private Map<String,Relation> subRelationCallFuncs;
	
	/** the Kodkod functions defining top-relation calls */
	private Map<String,Formula> topRelationCallFuncs;
	
	/** {@inheritDoc} */
	EKodkodTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
	}

	/** {@inheritDoc} */
	@Override
	protected void createRelation(ERelation rel, EDependency dep) throws EchoError {
		new EKodkodRelation(this,dep,rel);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void manageModelParams() throws ErrorKodkod {
		// Anything needed in Kodkod?
	}
	
	/** {@inheritDoc} */
	@Override
	protected void generateConstraints() throws ErrorKodkod {
		
		Formula fact = Formula.TRUE;
		for (Formula f : topRelationCallFuncs.values()) {
			fact = fact.and(f);
		}
		
		for (Formula f : subRelationCallDefs.values()) {
			fact = fact.and(f);
		}
		
		form = fact;

	}
	
	/** {@inheritDoc} */
	@Override
	protected KodkodFormula getTransformationConstraint(List<IExpression> vars) {
		return new KodkodFormula(form);
	}

	/** {@inheritDoc} */
	@Override
	protected void defineSubRelationCall(EEngineRelation relation, IFormula e) throws ErrorKodkod {
		if (subRelationCallDefs == null) subRelationCallDefs = new HashMap<String,Formula>();

		subRelationCallDefs.put(EchoHelper.relationFieldName(
					relation.relation, relation.dependency.target), ((KodkodFormula) e).formula);
	}

	/** {@inheritDoc} */
	@Override
	public void addSubRelationCall(EEngineRelation relation, IExpression exp) throws ErrorKodkod {
		
		if (subRelationCallFuncs == null) subRelationCallFuncs = new HashMap<String,Relation>();
		Relation field = (Relation) ((KodkodExpression) exp).EXPR;
		
		subRelationCallFuncs.put(EchoHelper.relationFieldName(
					relation.relation, relation.dependency.target), field);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void addTopRelationCall(EEngineRelation relation) {
		
		if (topRelationCallFuncs == null) topRelationCallFuncs = new HashMap<String,Formula>();
		
		topRelationCallFuncs.put(EchoHelper.relationPredName(
					relation.relation, relation.dependency.target), ((KodkodFormula) relation.constraint).formula);
	}

	/** {@inheritDoc} */
	@Override
	public KodkodFormula callRelation(ERelation n, ITContext context, List<IExpression> params) {
		if (subRelationCallFuncs == null) return null;
		Relation f = subRelationCallFuncs.get(EchoHelper.relationFieldName(n,context.getCallerRel().dependency.target));
		if (f == null) return null;
	
		IExpression expp = new KodkodExpression(f);
		// applies the parameters to the relation function
		IExpression insig = params.get(params.size() - 1);
		params.remove(insig);
		for (IExpression param : params)
			expp = param.join(expp);
		IFormula form = insig.in(expp);
		
		return (KodkodFormula) form;
	}
	
}
