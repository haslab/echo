package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * An embedding of an EMF ATL relation in Echo.
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLRelation implements ERelation {
	private EObject relation;
	private List<EModelDomain> domains = new ArrayList<EModelDomain>();


	public EATLRelation(EObject rule) throws ErrorParser {
		if (rule.eClass().getName().equals("MatchedRule") || rule.eClass().getName().equals("LazyMatchedRule") )
			this.relation = rule;
		else throw new ErrorParser("Bad atl");
		EStructuralFeature inmdls = relation.eClass().getEStructuralFeature("inPattern");
		EStructuralFeature outmdls = relation.eClass().getESuperTypes().get(0).getEStructuralFeature("outPattern");
		EObject obj = (EObject) relation.eGet(inmdls);
		domains.add(new EATLModelDomain(obj));
		obj = (EObject) relation.eGet(outmdls);
		domains.add(new EATLModelDomain(obj));
	}

	@Override
	public boolean isTop(){
		return !relation.eClass().getName().equals("LazyMatchedRule");
	}

	@Override
	public ETransformation getTransformation() {
		EStructuralFeature module = relation.eClass().getEStructuralFeature("module");
		//EATLTransformation x = EATLTransformation.get((EObject) relation.eGet(module));
		return null;
	}

	@Override
	public String getName() {
		EStructuralFeature name = relation.eClass().getEStructuralFeature("name");
		return (String) relation.eGet(name);
	}

	@Override
	public List<EModelDomain> getDomains() {
		return domains;

	}

	@Override
	public EATLPredicate getPost() {
		return null;
	}

	@Override
	public EATLPredicate getPre() {
		return null;
	}

}
