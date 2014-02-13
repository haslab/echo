package pt.uminho.haslab.mde.transformation.atl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

public class ATLRelation implements ERelation {
	private EObject relation;
	private List<EModelDomain> domains = new ArrayList<EModelDomain>();


	public ATLRelation(EObject rule) throws ErrorParser {
		if (rule.eClass().getName().equals("MatchedRule") || rule.eClass().getName().equals("LazyMatchedRule") )
			this.relation = rule;
		else throw new ErrorParser("Bad atl");
		EStructuralFeature inmdls = relation.eClass().getEStructuralFeature("inPattern");
		EStructuralFeature outmdls = relation.eClass().getESuperTypes().get(0).getEStructuralFeature("outPattern");
		EObject obj = (EObject) relation.eGet(inmdls);
		domains.add(new ATLDomain(obj));
		obj = (EObject) relation.eGet(outmdls);
		domains.add(new ATLDomain(obj));
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
	public ATLCondition getPost() {
		return null;
	}

	@Override
	public ATLCondition getPre() {
		return null;
	}

}
