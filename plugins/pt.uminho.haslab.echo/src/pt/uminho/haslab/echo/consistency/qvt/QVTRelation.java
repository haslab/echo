package pt.uminho.haslab.echo.consistency.qvt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.qvtd.pivot.qvtbase.Pattern;
import org.eclipse.qvtd.pivot.qvtbase.Predicate;
import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.Domain;
import pt.uminho.haslab.echo.consistency.Relation;

public class QVTRelation implements Relation {
	private org.eclipse.qvtd.pivot.qvtrelation.Relation relation;
	private List<Domain> domains = new ArrayList<Domain>();
	
	
	public QVTRelation(Rule rule) throws ErrorParser {
		if (rule instanceof org.eclipse.qvtd.pivot.qvtrelation.Relation)
			this.relation = (org.eclipse.qvtd.pivot.qvtrelation.Relation) rule;
		else throw new ErrorParser("Rule not a relation");
		for (org.eclipse.qvtd.pivot.qvtbase.Domain dom : relation.getDomain())
			domains.add(new QVTDomain(dom));
	}

	@Override
	public boolean isTop(){
		return relation.isIsTopLevel();
	}
	
	@Override
	public QVTTransformation getTransformation()  {
		return QVTTransformation.get((RelationalTransformation) relation.getTransformation());
	}
	
	@Override
	public String getName() {
		return relation.getName();
	}

	@Override
	public List<Domain> getDomains() {
		return domains;

	}

	@Override
	public QVTCondition getPost() {
		QVTCondition oclwhere = new QVTCondition();
		Pattern pattern = relation.getWhere();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclwhere.addCondition(predicate.getConditionExpression());
		return oclwhere;
	}

	@Override
	public QVTCondition getPre() {
		QVTCondition oclWhen = new QVTCondition();
		Pattern pattern = relation.getWhen();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclWhen.addCondition(predicate.getConditionExpression());
		return oclWhen;
	}
	
}
