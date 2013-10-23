package pt.uminho.haslab.echo.consistency.qvt;

import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.Domain;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Variable;

public class QVTDomain implements Domain {
	private RelationDomain domain;

	public QVTDomain(org.eclipse.qvtd.pivot.qvtbase.Domain dom) throws ErrorParser {
		if (dom instanceof RelationDomain)
			this.domain = (RelationDomain) dom;
		else throw new ErrorParser("Domain not a relation");
	}

	@Override
	public Model getModel() {
		return QVTModel.get(domain.getTypedModel());
	}

	@Override
	public Variable getVariable() {
		return Variable.getVariable(domain.getRootVariable());

	}

	@Override
	public QVTCondition getCondition() {
		DomainPattern pattern = domain.getPattern();
		ObjectTemplateExp temp = (ObjectTemplateExp) pattern.getTemplateExpression(); 		
		QVTCondition res = new QVTCondition();
		res.addCondition(temp);
		return res;
	}

}
