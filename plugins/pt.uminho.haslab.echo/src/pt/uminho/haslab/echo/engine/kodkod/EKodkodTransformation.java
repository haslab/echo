package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An embedding of a model transformation in Kodkod.
 * 
 * @author nmm
 * @version 0.4 17/02/2014
 */
public class EKodkodTransformation extends EEngineTransformation {

	/** the Kodkod constraint rising from this Transformation */
	private Formula form;

	/** the Kodkod formulas defining sub-relation constraints */
	private Map<String, Formula> subRelationDefs;

	/** the Kodkod relations denoting sub-relation calls */
	private Map<String, Relation> subRelationFields;

	/** the Kodkod formulas defining top-relation constraints */
	private Map<String, Formula> topRelationConstraints;

    /**the types of kodkod relations denoting sub-relation calls*/
    private Map<Relation,Pair<Set<Relation>,Set<Relation>>> relationTypes;

	/** {@inheritDoc} */
	EKodkodTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
	}

    void defineRelationType(Relation r,Pair<Set<Relation>,Set<Relation>> type)
    {
    	if(relationTypes ==null)
    		relationTypes = new HashMap<>();    		
        relationTypes.put(r,type);
    }

    Map<Relation, Pair<Set<Relation>,Set<Relation>>> getRelationTypes() {
        return relationTypes;
    }

	/** {@inheritDoc} */
	@Override
	protected void createRelation(ERelation rel, EDependency dep)
			throws EchoError {
		new EKodkodRelation(this, dep, rel);
	}

	/** {@inheritDoc} */
	@Override
	protected void initModelParams() throws ErrorKodkod {
		// Anything needed in Kodkod?
	}

	/** {@inheritDoc} */
	@Override
	protected void processConstraint() throws ErrorKodkod {
		Formula fact = Formula.TRUE;

		// add all top relation constraints
		if (topRelationConstraints != null)
		for (Formula f : topRelationConstraints.values())
			fact = fact.and(f);

		// add all sub-relation field definitions
		if(subRelationDefs != null)
			for (Formula f : subRelationDefs.values())
				fact = fact.and(f);

		form = fact;
	}

	/** {@inheritDoc} */
	@Override
	protected KodkodFormula getConstraint(List<String> modelIDs) {
		// TODO ignoring model IDs!
		return new KodkodFormula(form);
	}

	/** {@inheritDoc} */
	@Override
	protected void defineSubRelationField(EEngineRelation relation, IFormula e)
			throws ErrorKodkod {
		if (subRelationDefs == null)
			subRelationDefs = new HashMap<>();

		subRelationDefs.put(EchoHelper.relationFieldName(relation.relation,
				relation.dependency.target), ((KodkodFormula) e).formula);
	}

	/** {@inheritDoc} */
	@Override
	public void addSubRelationField(EEngineRelation relation, IExpression exp)
			throws ErrorKodkod {
		if (subRelationFields == null)
			subRelationFields = new HashMap<>();
		Relation field = (Relation) ((KodkodExpression) exp).EXPR;

		subRelationFields.put(EchoHelper.relationFieldName(relation.relation,
				relation.dependency.target), field);
	}

	/** {@inheritDoc} */
	@Override
	protected void addTopRelationConstraint(EEngineRelation relation) {
		if (topRelationConstraints == null)
			topRelationConstraints = new HashMap<>();

		EchoReporter.getInstance().debug("Rel fact: "+((KodkodFormula) relation.constraint).formula);

		topRelationConstraints.put(EchoHelper.relationPredName(
				relation.relation, relation.dependency.target),
				((KodkodFormula) relation.constraint).formula);
	}

	/** {@inheritDoc} */
	@Override
	public KodkodFormula callRelation(ERelation n, ITContext context,
			List<IExpression> params) {
		if (subRelationFields == null)
			return null;
		Relation f = subRelationFields.get(EchoHelper.relationFieldName(n,
				context.getCallerRel().dependency.target));
		if (f == null)
			return null;

		IExpression expp = new KodkodExpression(f);
		// applies the parameters to the relation function
		IExpression insig = params.get(params.size() - 1);
		params.remove(insig);
		for (IExpression param : params)
			expp = param.join(expp);
		IFormula form = insig.in(expp);

		return (KodkodFormula) form;
	}

    public Map<String, Relation> getSubRelationFields() {
        return subRelationFields;
    }
}
