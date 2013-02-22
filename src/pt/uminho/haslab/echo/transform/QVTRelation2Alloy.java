package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import net.sourceforge.qvtparser.model.essentialocl.OclExpression;
import net.sourceforge.qvtparser.model.essentialocl.Variable;
import net.sourceforge.qvtparser.model.qvtbase.Domain;
import net.sourceforge.qvtparser.model.qvtbase.Predicate;
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

	// variables occuring in the when constraint
	private List<Variable> whenvariables = new ArrayList<Variable>();
	// variables occuring in source domains but not in the when constraint
	private List<Variable> sourcevariables = new ArrayList<Variable>();
	// variables occuring in target domain and where constraint, but not in the when constraint or source domains
	private List<Variable> targetvariables = new ArrayList<Variable>();
	// separated target and source domains
	private RelationDomain targetdomain;
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();
	// the alloy signatures of each metamodel
	private List<Sig> modelsigs = new ArrayList<Sig>();
	// Declarations of quantified variables; needed for respective variable occurrences;
	private List<Decl> decls = new ArrayList<Decl>();
	// the Alloy expression rising from this relations
	final Expr fact;
	// the QVT relation being transformed
	private Relation rel;
	

	public QVTRelation2Alloy (TypedModel target, Relation rel, Transformation qvt, List<Sig> modelsigs) throws Exception {
		this.modelsigs = modelsigs;
		this.rel = rel;

		Expr fact,sourceexpr = Sig.NONE.no(),targetexpr = Sig.NONE.no(),whereexpr = Sig.NONE.no(), whenexpr = Sig.NONE.no();
		
		// separating target domain from the rest
		for (Object dom1 : rel.getDomain()) { // should be Domain
			Domain dom = (Domain) dom1;
			// "Domains" of QVT Relations must be "RelationDomains"
			if (!(dom instanceof RelationDomain)) 
				throw new ErrorTransform("Not a domain relation.","QVTRelation2Alloy",dom);
			else if (dom.getTypedModel().equals(target)) targetdomain = (RelationDomain) dom;
			else sourcedomains.add((RelationDomain) dom);
		}
		// calculates the variable lists (source variables, domain variables...)
		initVariableLists();
		

		// calculates the source variables declarations (quantifications)
		List<Decl> alloysourcevars = AlloyUtil.variableListToExpr(sourcevariables,modelsigs);
		decls.addAll(alloysourcevars);
		// calculates the when variables declarations (quantifications)
		List<Decl> alloywhenvars = AlloyUtil.variableListToExpr(whenvariables,modelsigs);
		decls.addAll(alloywhenvars);
		// calculates the target variables declarations (quantifications)
		List<Decl> alloytargetvars = AlloyUtil.variableListToExpr(targetvariables,modelsigs);
		decls.addAll(alloytargetvars);


		// calculates the target expression
		List<Decl> aux = new ArrayList<Decl>(alloytargetvars);
		aux.remove(0);
		Decl[] aux2 = new Decl[aux.size()];
		aux2 = aux.toArray(aux2);
		OCL2Alloy ocltrans = new OCL2Alloy(target,modelsigs,targetdomain.getTypedModel(),decls);
		if (rel.getWhere() != null)
			for (Object predicate : rel.getWhere().getPredicate()) {
				OclExpression oclwhere = ((Predicate) predicate).getConditionExpression();
				whereexpr = AlloyUtil.cleanAnd(whereexpr,ocltrans.oclExprToAlloy(oclwhere));
			}
		targetexpr = AlloyUtil.cleanAnd(patternToExpr(targetdomain),whereexpr).forSome(alloytargetvars.get(0),aux2);
		
		// calculates the source expression
		for (RelationDomain dom : sourcedomains) 
			sourceexpr = AlloyUtil.cleanAnd(sourceexpr,patternToExpr(dom));
		aux = new ArrayList<Decl>(alloysourcevars);
		aux.remove(0);
		aux2 = new Decl[aux.size()];
		aux2 = aux.toArray(aux2);
		
		// calculates the source => target expression
		fact = (sourceexpr.implies(targetexpr)).forAll(alloysourcevars.get(0),aux2);	
		
		// calculates the when expression
		if (rel.getWhen() != null){
			for (Object predicate : rel.getWhen().getPredicate()) {
				OclExpression oclwhen = ((Predicate) predicate).getConditionExpression();
				whenexpr = AlloyUtil.cleanAnd(whenexpr,ocltrans.oclExprToAlloy(oclwhen));
			}
			aux = new ArrayList<Decl>(alloywhenvars);
			aux.remove(0);
			aux2 = new Decl[aux.size()];
			aux2 = aux.toArray(aux2);
			
			// calculates the when => source => target expression
			fact = (whenexpr.implies(fact)).forAll(alloywhenvars.get(0),aux2);	
		}

		System.out.println("Fact relation "+rel.getName()+":" +fact.toString());
		this.fact = fact;
	}
	
	// separates source from target variables (eventually also when and where variables)
	private void initVariableLists() throws ErrorUnsupported{
		TemplateExp temp;
		List<PropertyTemplateItem> temps;
		
		if (rel.getWhen() != null)
			for (Object predicate : rel.getWhen().getPredicate()) {
				OclExpression oclwhen = ((Predicate) predicate).getConditionExpression();
				whenvariables.addAll(OCLUtil.variablesOCLExpression(oclwhen));
			}
		
		for (RelationDomain dom : sourcedomains) {
			temp = dom.getPattern().getTemplateExpression();
			// No support for CollectionTemplateExp
			if (!(temp instanceof ObjectTemplateExp)) 
				throw new ErrorUnsupported ("Template not an ObjectTemplate.","QVTRelation2Alloy",temp);
			temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
			for (PropertyTemplateItem item : temps)
				sourcevariables.addAll(OCLUtil.variablesOCLExpression(item.getValue()));
			sourcevariables.add(dom.getRootVariable());
		}
		sourcevariables.removeAll(whenvariables);
		
		temp = targetdomain.getPattern().getTemplateExpression();
		// No support for CollectionTemplateExp
		if (!(temp instanceof ObjectTemplateExp)) 
			throw new ErrorUnsupported ("Template not an ObjectTemplate.","QVTRelation2Alloy",temp);
		temps = new ArrayList<PropertyTemplateItem>(((ObjectTemplateExp) temp).getPart());
		for (PropertyTemplateItem item : temps)
			targetvariables.addAll(OCLUtil.variablesOCLExpression(item.getValue()));
		targetvariables.add(targetdomain.getRootVariable());
		if (rel.getWhere() != null)
			for (Object predicate : rel.getWhere().getPredicate()) {
				OclExpression oclwhere = ((Predicate) predicate).getConditionExpression();
				targetvariables.addAll(OCLUtil.variablesOCLExpression(oclwhere));
			}
		targetvariables.removeAll(sourcevariables);
		targetvariables.removeAll(whenvariables);
	}

	// calls OCL2Alloy on the domain pattern
	private Expr patternToExpr (RelationDomain domain) throws Exception {
		OCL2Alloy ocltrans = new OCL2Alloy(domain.getTypedModel(),modelsigs,targetdomain.getTypedModel(),decls);

		DomainPattern pattern = domain.getPattern();
		TemplateExp temp = pattern.getTemplateExpression(); 
		
		return ocltrans.oclExprToAlloy(temp);
	}
	
	public Expr getFact() {
		return fact;
	}
}
