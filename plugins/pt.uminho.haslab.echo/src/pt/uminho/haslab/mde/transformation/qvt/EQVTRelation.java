package pt.uminho.haslab.mde.transformation.qvt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.qvtd.pivot.qvtbase.Pattern;
import org.eclipse.qvtd.pivot.qvtbase.Predicate;
import org.eclipse.qvtd.pivot.qvtbase.Rule;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;

/**
 * An implementation of a transformation relation in QVT-R 
 *
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EQVTRelation implements ERelation {
	private org.eclipse.qvtd.pivot.qvtrelation.Relation relation;
	private List<EModelDomain> domains = new ArrayList<EModelDomain>();


	public EQVTRelation(Rule rule) throws ErrorUnsupported {
		if (rule instanceof org.eclipse.qvtd.pivot.qvtrelation.Relation)
			this.relation = (org.eclipse.qvtd.pivot.qvtrelation.Relation) rule;
		else throw new ErrorUnsupported("Rule not a relation");
		for (org.eclipse.qvtd.pivot.qvtbase.Domain dom : relation.getDomain())
			domains.add(new EQVTDomain(this,dom));
	}

	@Override
	public boolean isTop(){
		return relation.isIsTopLevel();
	}

	@Override
	public EQVTTransformation getTransformation() throws ErrorUnsupported, ErrorParser {
		String URI = EcoreUtil.getURI(relation.getTransformation()).path();
		return MDEManager.getInstance().getQVTTransformation(URI, false);
	}

	@Override
	public String getName() {
		return relation.getName();
	}

	@Override
	public List<EModelDomain> getDomains() {
		return domains;
	}

	@Override
	public EQVTCondition getPost() {
		EQVTCondition oclwhere = new EQVTCondition();
		Pattern pattern = relation.getWhere();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclwhere.addCondition(predicate.getConditionExpression());
		return oclwhere;
	}

	@Override
	public EQVTCondition getPre() {
		EQVTCondition oclWhen = new EQVTCondition();
		Pattern pattern = relation.getWhen();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclWhen.addCondition(predicate.getConditionExpression());
		return oclWhen;
	}

	@Override
	public String toString() {
		return getName();
	}

}
