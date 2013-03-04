package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.qvtd.pivot.qvtbase.Domain;
import org.eclipse.qvtd.pivot.qvtbase.Predicate;
import org.eclipse.qvtd.pivot.qvtbase.Transformation;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;
import org.eclipse.qvtd.pivot.qvttemplate.TemplateExp;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;




import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTRelation2Alloy {

	// variables occuring in the when constraint
	private List<Decl> alloywhenvars = new ArrayList<Decl>();
	// variables occuring in source domains but not in the when constraint
	private List<Decl> alloysourcevars = new ArrayList<Decl>();
	// variables occuring in target domain and where constraint, but not in the when constraint or source domains
	private List<Decl> alloytargetvars = new ArrayList<Decl>();
	// Declarations of quantified variables; needed for respective variable occurrences (union of the above)
	private List<Decl> decls = new ArrayList<Decl>();
		
	// the alloy signatures of each metamodel
	private List<Sig> modelsigs = new ArrayList<Sig>();
	
	// separated target and source domains
	private RelationDomain targetdomain;
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();
	// the QVT relation being transformed
	private Relation rel;
	// the QVT transformation being applied
	private Transformation qvt;
	// tue target metamodel
	private TypedModel target;

	// the Alloy expression rising from this relations
	final Expr fact;

	public QVTRelation2Alloy (TypedModel target, Relation rel, List<Sig> modelsigs, Transformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		this.modelsigs = modelsigs;
		this.qvt = qvt;
		this.rel = rel;
		this.target = target;
		
		initDomains ();
		
		initVariableDeclarationLists(true);
		
		fact = calculateFact();
		//System.out.println("Fact relation "+rel.getName()+":" +fact.toString());
	}
	
	// this one takes a list of declarations as an extra argument: used with relation calls, since some variables are already quantified
	public QVTRelation2Alloy (TypedModel target, Relation rel, List<Sig> modelsigs, Transformation qvt, List<Decl> prevdecls) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		this.modelsigs = modelsigs;
		this.qvt = qvt;
		this.rel = rel;
		this.target = target;
		
		initDomains ();
		
		decls.addAll(prevdecls);
		
		initVariableDeclarationLists(false);
		
		fact = calculateFact();
		
	}
	
	// separating target domain from the rest
	private void initDomains () throws ErrorTransform {
		for (Object dom1 : rel.getDomain()) { // should be Domain
			Domain dom = (Domain) dom1;
			// "Domains" of QVT Relations must be "RelationDomains"
			if (!(dom instanceof RelationDomain)) 
				throw new ErrorTransform("Not a domain relation.","QVTRelation2Alloy",dom);
			else if (dom.getTypedModel().equals(target)) targetdomain = (RelationDomain) dom;
			else sourcedomains.add((RelationDomain) dom);
		}
	}
	// calculates the final fact
	private Expr calculateFact() throws ErrorAlloy, ErrorTransform, ErrorUnsupported {

		Expr fact,sourceexpr = Sig.NONE.no(),targetexpr = Sig.NONE.no(),whereexpr = Sig.NONE.no(), whenexpr = Sig.NONE.no();

		// calculates the target expression
		OCL2Alloy ocltrans = new OCL2Alloy(target,modelsigs,decls,qvt);
		try {
			if (rel.getWhere() != null)
				for (Object predicate : rel.getWhere().getPredicate()) {
					OCLExpression oclwhere = ((Predicate) predicate).getConditionExpression();
					whereexpr = AlloyUtil.cleanAnd(whereexpr,ocltrans.oclExprToAlloy(oclwhere));
				}
			targetexpr = AlloyUtil.cleanAnd(patternToExpr(targetdomain),whereexpr);
			for (Decl d : alloytargetvars)
				targetexpr = targetexpr.forSome(d);
			
			// calculates the source expression
			for (RelationDomain dom : sourcedomains) 
				sourceexpr = AlloyUtil.cleanAnd(sourceexpr,patternToExpr(dom));
			
			// calculates the source => target expression
			fact = (sourceexpr.implies(targetexpr));
			for (Decl d : alloysourcevars)
				fact = fact.forAll(d);	
			
			// calculates the when expression
			if (rel.getWhen() != null){
				for (Object predicate : rel.getWhen().getPredicate()) {
					OCLExpression oclwhen = ((Predicate) predicate).getConditionExpression();
					whenexpr = AlloyUtil.cleanAnd(whenexpr,ocltrans.oclExprToAlloy(oclwhen));
				}
	
				// calculates the when => source => target expression
				fact = (whenexpr.implies(fact));	
				for (Decl d : alloywhenvars)
					fact = fact.forAll(d);
			}
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"QVTRelation2Alloy",rel);}
		
		return fact;
	}
	
	// separates source from target variables (eventually also when and where variables)
	private void initVariableDeclarationLists(boolean top) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		TemplateExp temp;
		List<PropertyTemplateItem> temps;
		List<VariableDeclaration> whenvariables = new ArrayList<VariableDeclaration>();
		List<VariableDeclaration> sourcevariables = new ArrayList<VariableDeclaration>();
		List<VariableDeclaration> targetvariables = new ArrayList<VariableDeclaration>();
		List<VariableDeclaration> rootvariables = new ArrayList<VariableDeclaration>();
		
		if (rel.getWhen() != null)
			for (Object predicate : rel.getWhen().getPredicate()) {
				OCLExpression oclwhen = ((Predicate) predicate).getConditionExpression();
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
			rootvariables.add(dom.getRootVariable());
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
		rootvariables.add(targetdomain.getRootVariable());
		if (rel.getWhere() != null)
			for (Object predicate : rel.getWhere().getPredicate()) {
				OCLExpression oclwhere = ((Predicate) predicate).getConditionExpression();
				targetvariables.addAll(OCLUtil.variablesOCLExpression(oclwhere));
			}
		targetvariables.removeAll(sourcevariables);
		targetvariables.removeAll(whenvariables);

		if (!top) {
			whenvariables.removeAll(rootvariables);
			targetvariables.removeAll(rootvariables);
			sourcevariables.removeAll(rootvariables);
		}
		
		// calculates the source variables declarations (quantifications)
		alloysourcevars = OCL2Alloy.variableListToExpr(sourcevariables,modelsigs);
		decls.addAll(alloysourcevars);
		// calculates the when variables declarations (quantifications)
		alloywhenvars = OCL2Alloy.variableListToExpr(whenvariables,modelsigs);
		decls.addAll(alloywhenvars);
		// calculates the target variables declarations (quantifications)
		alloytargetvars = OCL2Alloy.variableListToExpr(targetvariables,modelsigs);
		decls.addAll(alloytargetvars);
		
	}

	// calls OCL2Alloy on the domain pattern
	private Expr patternToExpr (RelationDomain domain) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		OCL2Alloy ocltrans = new OCL2Alloy(target,modelsigs,decls,qvt);

		DomainPattern pattern = domain.getPattern();
		TemplateExp temp = pattern.getTemplateExpression(); 
		
		return ocltrans.oclExprToAlloy(temp);
	}
	
	public Expr getFact() {
		return fact;
	}
}
