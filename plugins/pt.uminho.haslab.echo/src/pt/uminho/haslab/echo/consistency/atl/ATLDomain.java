package pt.uminho.haslab.echo.consistency.atl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.Domain;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Variable;

public class ATLDomain implements Domain {
	private EObject domain;

	public ATLDomain(EObject dom) throws ErrorParser {
		if (dom.eClass().getName().equals("InPattern") || dom.eClass().getName().equals("OutPattern") )
			this.domain = dom;
		else throw new ErrorParser("Bad atl");
		getModel();
	}

	@Override
	public Model getModel() {
		EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
		EList<EObject> objs = (EList<EObject>) domain.eGet(elems);
		EObject var = objs.get(0);
		for (EObject x : var.eContents())
			if (x.eClass().getName().equals("OclModelElement")) {
				return ATLModel.get(x.eCrossReferences().get(0));
			}
		return null;
	}

	@Override
	public Variable getVariable() {
		EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
		EList<EObject> objs = (EList<EObject>) domain.eGet(elems);
		EObject obj = objs.get(0);
		return Variable.getVariable(obj);
	}

	@Override
	public ATLCondition getCondition() {
		ATLCondition x = new ATLCondition();
		if (domain.eClass().getName().equals("InPattern")) {
			EObject filter = (EObject) domain.eGet(domain.eClass().getEStructuralFeature("filter"));
			x.addCondition(filter);
		} else if (domain.eClass().getName().equals("OutPattern")) {
			EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
			EList<EObject> objs = (EList<EObject>) domain.eGet(elems);
			EObject var = objs.get(0);
			EStructuralFeature bindings = var.eClass().getEStructuralFeature("bindings");
			for (EObject bd : (EList<EObject>) var.eGet(bindings))
				x.addCondition(bd);
		}

		return x;		
	}

}
