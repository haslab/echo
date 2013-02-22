package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.qvtparser.model.emof.impl.PackageImpl;
import net.sourceforge.qvtparser.model.essentialocl.Variable;
import net.sourceforge.qvtparser.model.qvtbase.Domain;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;
import net.sourceforge.qvtparser.model.qvtrelation.DomainPattern;
import net.sourceforge.qvtparser.model.qvtrelation.Relation;
import net.sourceforge.qvtparser.model.qvtrelation.RelationDomain;
import net.sourceforge.qvtparser.model.qvttemplate.ObjectTemplateExp;
import net.sourceforge.qvtparser.model.qvttemplate.PropertyTemplateItem;
import net.sourceforge.qvtparser.model.qvttemplate.TemplateExp;


import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTRelation2Alloy {

	private TypedModel target;
	// targetvariables are variables from target domain that do not occur in the source domains
	private List<Variable> sourcevariables = new ArrayList<Variable>();
	private List<Variable> targetvariables = new ArrayList<Variable>();
	private RelationDomain targetdomain;
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();
	private Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	private List<Decl> decls = new ArrayList<Decl>();
	// the Alloy expression rising from this relations
	final Expr fact;
	

	public QVTRelation2Alloy (TypedModel target, Relation rel, Transformation qvt, Map<String,List<Sig>> modelsigs) throws Err {
		this.target = target;
		this.modelsigs = modelsigs;

		Expr fact;
		
		for (Object dom1 : rel.getDomain()) { // should be Domain
			Domain dom = (Domain) dom1;
			if (!(dom instanceof RelationDomain)) throw new Error("QVT2Alloy: Not a domain relation: "+dom.toString());
			else if (dom.getTypedModel().equals(target)) targetdomain = (RelationDomain) dom;
			else sourcedomains.add((RelationDomain) dom);
		}
		
		initVariableLists();
		
		String mdl;
		mdl = ((PackageImpl) target.getUsedPackage().get(0)).getName();
		
		List<Decl> alloytargetvars = AlloyUtil.variableListToExpr(targetvariables,modelsigs.get(mdl));
		decls.addAll(alloytargetvars);
		
		List<Sig> sigs = new ArrayList<Sig>();
		for (RelationDomain dom : sourcedomains) {
			mdl = ((PackageImpl) dom.getTypedModel().getUsedPackage().get(0)).getName();
			sigs.addAll(modelsigs.get(mdl));
		}
		List<Decl> alloysourcevars = AlloyUtil.variableListToExpr(sourcevariables,sigs);
		decls.addAll(alloysourcevars);

		List<Decl> aux = new ArrayList<Decl>(alloytargetvars);
		aux.remove(0);
		Decl[] aux2 = new Decl[aux.size()];
		aux2 = aux.toArray(aux2);
		fact = patternToExpr(targetdomain).forSome(alloytargetvars.get(0),aux2);

		Expr sourceExpr = Sig.NONE.no();

		for (RelationDomain dom : sourcedomains) {
			sourceExpr = sourceExpr.and(patternToExpr(dom));
		}
		
		aux = new ArrayList<Decl>(alloysourcevars);
		aux.remove(0);
		aux2 = new Decl[aux.size()];
		aux2 = aux.toArray(aux2);

		fact = (sourceExpr.implies(fact)).forAll(alloysourcevars.get(0),aux2);	
		System.out.println("Fact relation "+rel.getName()+":" +fact.toString());
		
		this.fact = fact;
	}
	
	public Expr getFact() {
		return fact;
	}
	
	// separates source from target variables (eventually also when and where variables)
	private void initVariableLists(){
		TemplateExp temp;
		List<PropertyTemplateItem> temps;
		for (RelationDomain dom : sourcedomains) {
			temp = dom.getPattern().getTemplateExpression();
			if (!(temp instanceof ObjectTemplateExp)) throw new Error ("Template not an ObjectTemplate.");
			temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
			for (PropertyTemplateItem item : temps)
				sourcevariables.addAll(AlloyUtil.variablesOCLExpression(item.getValue()));
			sourcevariables.add(dom.getRootVariable());
		}
		
		temp = targetdomain.getPattern().getTemplateExpression();
		if (!(temp instanceof ObjectTemplateExp)) throw new Error ("QVT2Alloy: Template not an ObjectTemplate.");
		temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
		for (PropertyTemplateItem item : temps)
			targetvariables.addAll(AlloyUtil.variablesOCLExpression(item.getValue()));
		targetvariables.add(targetdomain.getRootVariable());
		targetvariables.removeAll(sourcevariables);
	}

	
	private Expr patternToExpr (RelationDomain domain) throws Err {
		OCL2Alloy ocltrans = new OCL2Alloy(domain,modelsigs,target,decls);
		Expr result = Sig.NONE.no();
		DomainPattern pattern = domain.getPattern();
		TemplateExp temp = pattern.getTemplateExpression(); 
		
		result = ocltrans.oclExprToAlloy(temp);

		return result;
	}
	
	
}
