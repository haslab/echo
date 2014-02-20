package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;

/**
 * An embedding of an EMF QVT-R model domain in Echo.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EQVTModelDomain extends EModelDomain {

	/** the processed EMF relation model domain */
	private RelationDomain domain;
	/** the parent QVT-R relation */
	private EQVTRelation relation;

	public EQVTModelDomain(ERelation relation, org.eclipse.qvtd.pivot.qvtbase.Domain domain) throws ErrorUnsupported {
		this.relation = (EQVTRelation) relation;
		if (domain instanceof RelationDomain)
			this.domain = (RelationDomain) domain;
		else throw new ErrorUnsupported("Domain not a relation domain");
	}

	@Override
	public EQVTModelParameter getModel() throws ErrorUnsupported, ErrorParser {
		return relation.getTransformation().getModelParameter(domain.getTypedModel().getName());
	}

	@Override
	public EVariable getRootVariable() {
		return EVariable.getVariable(domain.getRootVariable());

	}

	@Override
	public EQVTPredicate getCondition() {
		DomainPattern pattern = domain.getPattern();
		ObjectTemplateExp temp = (ObjectTemplateExp) pattern.getTemplateExpression();
		EQVTPredicate res = new EQVTPredicate();
		res.addCondition(temp);
		return res;
	}

	@Override
	public EQVTRelation getRelation() {
		return relation;
	}

}
