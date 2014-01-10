package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

public class QVTDomain extends EModelDomain {
	private final RelationDomain domain;
	private final ERelation relation;

	public QVTDomain(ERelation rel, org.eclipse.qvtd.pivot.qvtbase.Domain dom) throws ErrorParser {
		this.relation = rel;
		if (dom instanceof RelationDomain)
			this.domain = (RelationDomain) dom;
		else throw new ErrorParser("Domain not a relation");
	}

	@Override
	public EModelParameter getModel() {
		return QVTModel.get(domain.getTypedModel());
	}

	@Override
	public EVariable getRootVariable() {
		return EVariable.getVariable(domain.getRootVariable());

	}

	@Override
	public QVTCondition getCondition() {
		DomainPattern pattern = domain.getPattern();
		ObjectTemplateExp temp = (ObjectTemplateExp) pattern.getTemplateExpression(); 		
		QVTCondition res = new QVTCondition();
		res.addCondition(temp);
		return res;
	}

	@Override
	public ERelation getRelation() {
		return relation;
	}

}
