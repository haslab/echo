package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.QVTBase.Domain;
import pt.uminho.haslab.emof.ast.QVTBase.Transformation;
import pt.uminho.haslab.emof.ast.QVTBase.TypedModel;
import pt.uminho.haslab.emof.ast.QVTRelation.DomainPattern;
import pt.uminho.haslab.emof.ast.QVTRelation.Relation;
import pt.uminho.haslab.emof.ast.QVTRelation.RelationDomain;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTRelation2Alloy {

	private Relation rel;
	private TypedModel target;
	// targetvariables are variables from target domain that do not occur in the source domains
	private List<Variable> sourcevariables, targetvariables = new ArrayList<Variable>();
	private RelationDomain targetdomain;
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();
	// the Alloy expression rising from this relations
	final Expr result;
	

	public QVTRelation2Alloy (TypedModel target, Relation rel, Transformation qvt) throws Err {
		this.target = target;
		this.rel = rel;

		Expr fact;
		
		for (Domain dom : rel.getDomain())
			if (!(dom instanceof RelationDomain)) throw new Error("QVT2Alloy: Not a domain relation: "+dom.toString());
			else if (dom.getTypedModel().equals(target)) targetdomain = (RelationDomain) dom;
			else sourcedomains.add((RelationDomain) dom);

		initVariableLists();

		List<Decl> alloytargetvars = AlloyUtil.variableListToExpr(targetvariables);
		List<Decl> aux = new ArrayList<Decl>(alloytargetvars);
		aux.remove(0);
		Decl[] aux2 = new Decl[aux.size()];
		aux2 = targetvariables.toArray(aux2);
		fact = patternToExpr(targetdomain.getPattern()).forSome(aux.get(0),aux2);	

		Expr sourceExpr = Sig.NONE.no();
		for (RelationDomain dom : sourcedomains)
			sourceExpr.and(patternToExpr(dom.getPattern()));
		List<Decl> alloysourcevars = AlloyUtil.variableListToExpr(sourcevariables);
		aux = new ArrayList<Decl>(alloysourcevars);
		aux.remove(0);
		aux2 = new Decl[aux.size()];
		aux2 = targetvariables.toArray(aux2);

		fact = sourceExpr.forAll(aux.get(0),aux2).implies(fact);	
		
		result = fact;
	}
	
	public Expr getFact() {
		return result;
	}
	
	// separates source from target variables (eventually also when and where variables)
	private void initVariableLists(){
		for (RelationDomain dom : sourcedomains) {
			sourcevariables.addAll(dom.getPattern().getBindsTo());
			sourcevariables.add(dom.getRootVariable());
		}
		targetvariables = targetdomain.getPattern().getBindsTo();
		targetvariables.add(targetdomain.getRootVariable());
		targetvariables.removeAll(sourcevariables);
	}
	
	
	private Expr patternToExpr (DomainPattern pattern) {
		return null;
	}
}
