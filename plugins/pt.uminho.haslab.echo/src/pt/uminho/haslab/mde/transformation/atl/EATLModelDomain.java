package pt.uminho.haslab.mde.transformation.atl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

/**
 * An embedding of an EMF ATL model domain in Echo.
 * 
 * TODO: Very incomplete
 * 
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EATLModelDomain extends EModelDomain {
	private EObject domain; // InPattern or OutPattern
	private EATLRelation relation;

	public EATLModelDomain(EATLRelation rule, EObject dom) throws ErrorParser {
		if (dom.eClass().getName().equals("InPattern") || dom.eClass().getName().equals("OutPattern") )
			this.domain = dom;
		else throw new ErrorParser("Bad atl");
		this.relation = rule;
		getModel();
	}

	@Override
	public EATLModelParameter getModel() {
		EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
		EList<EObject> objs = (EList<EObject>) domain.eGet(elems); 
		EObject var = objs.get(0); // InPatternElement or OutPatternElement
		for (EObject x : var.eContents())
			if (x.eClass().getName().equals("OclModelElement")) {
				EObject mdlref = x.eCrossReferences().get(0);
				EObject metamdlref = ((EList<EObject>) mdlref.eGet(mdlref.eClass().getEStructuralFeature("model"))).get(0);
				String name = (String) metamdlref.eGet(metamdlref.eClass().getEStructuralFeature("name"));
				return EATLModelParameter.get(name);
			}
		return null;
	}

	@Override
	public EVariable getRootVariable() {
		EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
		EList<EObject> objs = (EList<EObject>) domain.eGet(elems);
		EObject obj = objs.get(0); // InPatternElement or OutPatternElement
		return EVariable.getVariable(obj);
	}

	@Override
	public EATLPredicate getCondition() {
		EATLPredicate x = new EATLPredicate();
		if (domain.eClass().getName().equals("InPattern")) {
			EObject filter = (EObject) domain.eGet(domain.eClass().getEStructuralFeature("filter"));
			if (filter != null)
				x.addCondition(filter);
		} 
//		else if (domain.eClass().getName().equals("OutPattern")) {
//			EStructuralFeature elems = domain.eClass().getEStructuralFeature("elements");
//			EList<EObject> objs = (EList<EObject>) domain.eGet(elems);
//			EObject var = objs.get(0);
//			EStructuralFeature bindings = var.eClass().getEStructuralFeature("bindings");
//			for (EObject bd : (EList<EObject>) var.eGet(bindings))
//				x.addCondition(bd);
//		}
		return x;
	}

	@Override
	public EATLRelation getRelation() {
		return relation;
	}

}
