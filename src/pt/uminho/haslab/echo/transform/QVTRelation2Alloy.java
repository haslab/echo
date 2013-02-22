package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

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

import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTRelation2Alloy {

	// target variables are variables from target domain that do not occur in the source domains
	private List<Variable> sourcevariables = new ArrayList<Variable>();
	private List<Variable> targetvariables = new ArrayList<Variable>();
	// separated target and source domains
	private RelationDomain targetdomain;
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();
	// the alloy signatures of each metamodel
	private Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	// Declarations of quantified variables; needed for respective variable occurrences;
	private List<Decl> decls = new ArrayList<Decl>();
	// the Alloy expression rising from this relations
	final Expr fact;
	

	public QVTRelation2Alloy (TypedModel target, Relation rel, Transformation qvt, Map<String,List<Sig>> modelsigs) throws Exception {
		this.modelsigs = modelsigs;

		Expr fact;
		
		for (Object dom1 : rel.getDomain()) { // should be Domain
			Domain dom = (Domain) dom1;
			// "Domains" of QVT Relations must be "RelationDomains"
			if (!(dom instanceof RelationDomain)) 
				throw new ErrorTransform("Not a domain relation.","QVTRelation2Alloy",dom);
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
			sourceExpr = AlloyUtil.cleanAnd(sourceExpr,patternToExpr(dom));
		}
		
		aux = new ArrayList<Decl>(alloysourcevars);
		aux.remove(0);
		aux2 = new Decl[aux.size()];
		aux2 = aux.toArray(aux2);

		fact = (sourceExpr.implies(fact)).forAll(alloysourcevars.get(0),aux2);	
		System.out.println("Fact relation "+rel.getName()+":" +fact.toString());
		
		this.fact = fact;
	}
	
	// separates source from target variables (eventually also when and where variables)
	private void initVariableLists() throws ErrorUnsupported{
		TemplateExp temp;
		List<PropertyTemplateItem> temps;
		for (RelationDomain dom : sourcedomains) {
			temp = dom.getPattern().getTemplateExpression();
			// No support for CollectionTemplateExp
			if (!(temp instanceof ObjectTemplateExp)) 
				throw new ErrorUnsupported ("Template not an ObjectTemplate.","QVTRelation2Alloy",temp);
			temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
			for (PropertyTemplateItem item : temps)
				sourcevariables.addAll(AlloyUtil.variablesOCLExpression(item.getValue()));
			sourcevariables.add(dom.getRootVariable());
		}
		
		temp = targetdomain.getPattern().getTemplateExpression();
		// No support for CollectionTemplateExp
		if (!(temp instanceof ObjectTemplateExp)) 
			throw new ErrorUnsupported ("Template not an ObjectTemplate.","QVTRelation2Alloy",temp);
		temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
		for (PropertyTemplateItem item : temps)
			targetvariables.addAll(AlloyUtil.variablesOCLExpression(item.getValue()));
		targetvariables.add(targetdomain.getRootVariable());
		targetvariables.removeAll(sourcevariables);
	}

	// calls OCL2Alloy on the domain pattern
	private Expr patternToExpr (RelationDomain domain) throws Exception {
		OCL2Alloy ocltrans = new OCL2Alloy(domain,modelsigs,targetdomain.getTypedModel(),decls);

		DomainPattern pattern = domain.getPattern();
		TemplateExp temp = pattern.getTemplateExpression(); 
		
		return ocltrans.oclExprToAlloy(temp);
	}
	
	public Expr getFact() {
		return fact;
	}
}
