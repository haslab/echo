package pt.uminho.haslab.echo.consistency.atl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.consistency.Domain;
import pt.uminho.haslab.echo.consistency.Relation;

public class ATLRelation implements Relation {
	private EObject relation;
	private List<Domain> domains = new ArrayList<Domain>();
	
	
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
	public ATLTransformation getTransformation() {
		EStructuralFeature module = relation.eClass().getEStructuralFeature("module");
		ATLTransformation x = ATLTransformation.get((EObject) relation.eGet(module));
		return x;
	}
	
	@Override
	public String getName() {
		EStructuralFeature name = relation.eClass().getEStructuralFeature("name");
		return (String) relation.eGet(name);
	}

	@Override
	public List<Domain> getDomains() {
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
