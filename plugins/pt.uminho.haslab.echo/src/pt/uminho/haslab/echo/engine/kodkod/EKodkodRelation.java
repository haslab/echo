package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EKodkodRelation extends EEngineRelation {

	public EKodkodRelation(EEngineRelation parentTranslator, ERelation relation)
			throws EchoError {
		super(relation, parentTranslator);
	}

	public EKodkodRelation(EKodkodTransformation eAlloyTransformation,
			EDependency dep, ERelation rel) throws EchoError {
		super(rel, dep, eAlloyTransformation);
	}

	/** {@inheritDoc} */
	@Override
	protected KodkodFormula simplify(IFormula formula) throws ErrorUnsupported {
		return (KodkodFormula) formula;
	}
	

	/** {@inheritDoc} */
	@Override
	protected KodkodExpression addNonTopRel(List<? extends EModelDomain> eModelDomains) throws EchoError {
		Relation field = Relation.binary(EchoHelper.relationFieldName(relation,dependency.target));

        List<IDecl> rootVars = new ArrayList<>();
        for (EModelDomain d : eModelDomains)
            rootVars.add(rootVar2engineDecl.get(d.getRootVariable().getName()));

        //TODO this should not be univ x univ(bounds)
        Expression exp = ((KodkodDecl)  rootVars.get(0)).decl.expression();
        extraRelConstraint = extraRelConstraint.and(new KodkodFormula(field.in(exp.product(Expression.UNIV))));

        EKodkodMetamodel leftMeta = ((EKodkodMetamodel) EchoTranslator.getInstance().getMetamodel(
                eModelDomains.get(0).getModel().getMetamodel().ID));

        EKodkodMetamodel rightMeta = ((EKodkodMetamodel) EchoTranslator.getInstance().getMetamodel(
                eModelDomains.get(1).getModel().getMetamodel().ID));

        Pair<Set<Relation>,Set<Relation>> type = new Pair<>(
                leftMeta.getRelDomain(eModelDomains.get(0).getRootVariable().getType()),
                rightMeta.getRelDomain(eModelDomains.get(1).getRootVariable().getType()));

        ((EKodkodTransformation) transformation).defineRelationType(field, type);

		return new KodkodExpression(field);
	}

	/** {@inheritDoc} */
	@Override
	public void newRelation(EQVTRelation rel) throws EchoError {
		new EKodkodRelation(this, rel);
	}

	/** {@inheritDoc} */
	@Override
	protected void manageModelParams() throws ErrorInternalEngine,
			ErrorUnsupported, ErrorParser {
		// TODO Is there need to do anything about this in Kodkod?
	}
	
}
