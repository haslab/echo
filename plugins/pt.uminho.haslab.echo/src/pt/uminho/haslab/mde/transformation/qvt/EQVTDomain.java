package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;

import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

/**
 * An implementation of a domain pattern in QVT-R
 *
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EQVTDomain extends EModelDomain {
	private RelationDomain domain;
	private ERelation relation;

	public EQVTDomain(ERelation rel, org.eclipse.qvtd.pivot.qvtbase.Domain dom) throws ErrorUnsupported {
		this.relation = rel;
		if (dom instanceof RelationDomain)
			this.domain = (RelationDomain) dom;
		else throw new ErrorUnsupported("Domain not a relation");
	}

	@Override
	public EModelParameter getModel() {
		return EQVTModel.get(domain.getTypedModel());
	}

	@Override
	public EVariable getRootVariable() {
		return EVariable.getVariable(domain.getRootVariable());

	}

	@Override
	public EQVTCondition getCondition() {
		DomainPattern pattern = domain.getPattern();
		ObjectTemplateExp temp = (ObjectTemplateExp) pattern.getTemplateExpression();
		EQVTCondition res = new EQVTCondition();
		res.addCondition(temp);
		return res;
	}

	@Override
	public ERelation getRelation() {
		return relation;
	}

}
