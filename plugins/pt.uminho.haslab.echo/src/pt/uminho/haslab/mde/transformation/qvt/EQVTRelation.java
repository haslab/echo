package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.qvtd.pivot.qvtbase.Pattern;
import org.eclipse.qvtd.pivot.qvtbase.Predicate;
import org.eclipse.qvtd.pivot.qvtbase.Rule;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;

import java.util.ArrayList;
import java.util.List;

/**
 * An embedding of an EMF QVT-R relation in Echo.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EQVTRelation implements ERelation {
	
	/** the processed EMF relation */
	private org.eclipse.qvtd.pivot.qvtrelation.Relation relation;
	/** the model domains of the relation */
	private List<EModelDomain> domains = new ArrayList<EModelDomain>();

	/**
	 * Processes an EMF QVT-R relation.
	 * @param rule the original EMF relation
	 * @throws ErrorUnsupported
	 */
	public EQVTRelation(Rule rule) throws ErrorUnsupported {
		if (rule instanceof org.eclipse.qvtd.pivot.qvtrelation.Relation)
			this.relation = (org.eclipse.qvtd.pivot.qvtrelation.Relation) rule;
		else throw new ErrorUnsupported("Rule not a relation");
		for (org.eclipse.qvtd.pivot.qvtbase.Domain dom : relation.getDomain())
			domains.add(new EQVTModelDomain(this,dom));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTop(){
		return relation.isIsTopLevel();
	}

	/** {@inheritDoc} 
	 * @throws EchoError */
	@Override
	public EQVTTransformation getTransformation() throws EchoError {
		String URI = EcoreUtil.getURI(relation.getTransformation()).path();
		return (EQVTTransformation) MDEManager.getInstance().getETransformation(URI, false);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return relation.getName();
	}

	/** {@inheritDoc} */
	@Override
	public List<EModelDomain> getDomains() {
		return domains;
	}

	/** {@inheritDoc} */
	@Override
	public EQVTPredicate getPost() {
		EQVTPredicate oclwhere = new EQVTPredicate();
		Pattern pattern = relation.getWhere();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclwhere.addCondition(predicate.getConditionExpression());
		return oclwhere;
	}

	/** {@inheritDoc} */
	@Override
	public EQVTPredicate getPre() {
		EQVTPredicate oclWhen = new EQVTPredicate();
		Pattern pattern = relation.getWhen();
		if(pattern == null) return null;
		for (Predicate predicate : pattern.getPredicate())
			oclWhen.addCondition(predicate.getConditionExpression());
		return oclWhen;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getName();
	}

}
