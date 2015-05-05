package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.ast.CoreRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KodkodRelation extends CoreRelation {

	public KodkodRelation(CoreRelation parentTranslator, ERelation relation)
			throws EError {
		super(relation, parentTranslator);
	}

	public KodkodRelation(KodkodTransformation eAlloyTransformation,
			EDependency dep, ERelation rel, boolean trace) throws EError {
		super(rel, dep, eAlloyTransformation, trace);
	}

	/** {@inheritDoc} */
	@Override
	protected KodkodFormula simplify(IFormula formula) throws EErrorUnsupported {
		return (KodkodFormula) formula;
	}
	

	/** {@inheritDoc} */
	@Override
	protected KodkodExpression addNonTopRel(List<? extends EModelDomain> eModelDomains) throws EError {
		Relation field = Relation.binary(EchoHelper.relationFieldName(relation,dependency.target));

        List<IDecl> rootVars = new ArrayList<>();
        for (EModelDomain d : eModelDomains)
            rootVars.add(rootVar2engineDecl.get(d.getRootVariable().getName()));

        //TODO this should not be univ x univ(bounds)
        Expression exp = ((KodkodDecl)  rootVars.get(0)).decl.expression();
        extraRelConstraint = extraRelConstraint.and(new KodkodFormula(field.in(exp.product(Expression.UNIV))));

        KodkodMetamodel leftMeta = ((KodkodMetamodel) CoreTranslator.getInstance().getMetamodel(
                eModelDomains.get(0).getModel().getMetamodel().ID));

        KodkodMetamodel rightMeta = ((KodkodMetamodel) CoreTranslator.getInstance().getMetamodel(
                eModelDomains.get(1).getModel().getMetamodel().ID));

        Pair<Set<Relation>,Set<Relation>> type = new Pair<>(
                leftMeta.getRelDomain(eModelDomains.get(0).getRootVariable().getType()),
                rightMeta.getRelDomain(eModelDomains.get(1).getRootVariable().getType()));

        ((KodkodTransformation) transformation).defineRelationType(field, type);

		return new KodkodExpression(field);
	}

	/** {@inheritDoc} */
	@Override
	public void newRelation(ERelation rel) throws EError {
		new KodkodRelation(this, rel);
	}

	/** {@inheritDoc} */
	@Override
	protected void manageModelParams() throws EErrorCore,
			EErrorUnsupported, EErrorParser {
		// TODO Is there need to do anything about this in Kodkod?
	}
	
}
