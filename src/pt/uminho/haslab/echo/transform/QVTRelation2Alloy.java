package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.qvtd.pivot.qvtbase.Domain;
import org.eclipse.qvtd.pivot.qvtbase.Predicate;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.DomainPattern;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;
import org.eclipse.qvtd.pivot.qvtrelation.RelationDomain;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.TemplateExp;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprBinary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprQt;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

public class QVTRelation2Alloy {

	/** the QVT Relation being transformed*/
	private Relation rel;
	/** the direction of the QVT Relation*/
	private TypedModel direction;
	/** whether the QVT Relation is top or not*/
	private boolean top;
	/** the Alloy state signatures of the instances*/
	private Map<String,Expr> statesigs = new HashMap<String,Expr>();
	/** the Alloy signatures of the metamodels*/
	private Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();
	
	/** the root variables of the QVT Relation being translated*/
	private List<VariableDeclaration> rootvariables = new ArrayList<VariableDeclaration>();
	/** the target relation domain */
	private RelationDomain targetdomain;
	/** the source relation domains */
	private List<RelationDomain> sourcedomains = new ArrayList<RelationDomain>();

	/** the Alloy declarations of the variables occurring in the when constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Set<Decl> alloywhenvars = new HashSet<Decl>();
	/** the Alloy declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Set<Decl> alloysourcevars = new HashSet<Decl>();
	/** the Alloy declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Set<Decl> alloytargetvars = new HashSet<Decl>();
	/** the Alloy declarations of the root variables
	 * null if top QVT Relation */
	private List<Decl> alloyrootvars = new ArrayList<Decl>();
	/** the Alloy declarations of all variables (union of the previous sets) */ 
	private Set<Decl> decls = new HashSet<Decl>();
	
	/** the Alloy expression rising from this QVT Relation*/
	private Expr fact;
	/** the Alloy field representing the this QVT Relation (null if top QVT Relation)*/
	private Field field;
	
	/** Constructs a new QVT Relation to Alloy translator.
	 * Translates a QVT Relation (top or non top) to Alloy in a given direction.
	 * 
	 * @param rel the QVT Relation being translated
	 * @param direction the target direction of the transformation
	 * @param top whether the QVT Relation is top or not
	 * @param statesigs maps transformation arguments (or metamodels) to the respective Alloy singleton signature (or abstract signature)
	 * @param modelsigs maps metamodels to the set of Alloy signatures
	 * 
	 * @throws ErrorTransform, 
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 */
	public QVTRelation2Alloy (Relation rel, TypedModel direction, boolean top, Map<String,Expr> statesigs, Map<String,List<Sig>> modelsigs) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		this.modelsigs = modelsigs;
		this.statesigs = statesigs;
		this.rel = rel;
		this.direction = direction;
		this.top = top;
		initDomains();
		initVariableDeclarationLists();
		calculateFact();
		//fact = trading(fact);
		field = top?null:addRelationFields();
	}
	
	/** Initializes the domain variables {@code this.sourcedomains}, {@code this.targetdomain} and {@code this.rootvariables}
	 * 
	 * @throws ErrorTransform if some {@code Domain} is not {@code RelationDomain}
	 */
	private void initDomains () throws ErrorTransform {
		for (Domain dom : rel.getDomain())
			if (!(dom instanceof RelationDomain)) throw new ErrorTransform("Not a domain relation.","QVTRelation2Alloy",dom);
			else {
				rootvariables.add(((RelationDomain) dom).getRootVariable());
				if (dom.getTypedModel().equals(direction)) targetdomain = (RelationDomain) dom;
				else sourcedomains.add((RelationDomain) dom);
			}
	}
	
	/** Calculates the Alloy expression denoting the QVT Relation.
	 * Takes the shape "forall whenvars : when => (forall sourcevars : sourcedomain => (exists targetvars+wherevars : targetdomain && where))"
	 * 
	 * @return the Alloy expression representing the QVT Relation
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorUnsupported
	 */
	private void calculateFact() throws ErrorAlloy, ErrorTransform, ErrorUnsupported {

		Expr fact,sourceexpr = Sig.NONE.no(),targetexpr = Sig.NONE.no(),whereexpr = Sig.NONE.no(), whenexpr = Sig.NONE.no();
		Decl[] arraydecl;
		try {
			if (rel.getWhere() != null){
				OCL2Alloy ocltrans = new OCL2Alloy(direction,statesigs,modelsigs,decls);
				for (Predicate predicate : rel.getWhere().getPredicate()) {
					OCLExpression oclwhere = predicate.getConditionExpression();
					whereexpr = AlloyUtil.cleanAnd(whereexpr,ocltrans.oclExprToAlloy(oclwhere));
				}
			}
			targetexpr = AlloyUtil.cleanAnd(patternToExpr(targetdomain),whereexpr);
			if (alloytargetvars.size() == 1)
				targetexpr = targetexpr.forSome(alloytargetvars.iterator().next());	
			else if (alloytargetvars.size() > 1) {
				arraydecl = (Decl[]) alloytargetvars.toArray(new Decl[alloytargetvars.size()]);
				targetexpr = targetexpr.forSome(arraydecl[0],Arrays.copyOfRange(arraydecl, 1, arraydecl.length));	
			}

			for (RelationDomain dom : sourcedomains) 
				sourceexpr = AlloyUtil.cleanAnd(sourceexpr,patternToExpr(dom));
			fact = (sourceexpr.implies(targetexpr));
			
			if (alloysourcevars.size() == 1)
				fact = fact.forAll(alloysourcevars.iterator().next());	
			else if (alloysourcevars.size() > 1) {
				arraydecl = (Decl[]) alloysourcevars.toArray(new Decl[alloysourcevars.size()]);
				fact = fact.forAll(arraydecl[0],Arrays.copyOfRange(arraydecl, 1, arraydecl.length));	
			}
			
			if (rel.getWhen() != null){
				OCL2Alloy ocltrans = new OCL2Alloy(direction,statesigs,modelsigs,decls);
				for (Predicate predicate : rel.getWhen().getPredicate()) {
					OCLExpression oclwhen = predicate.getConditionExpression();
					whenexpr = AlloyUtil.cleanAnd(whenexpr,ocltrans.oclExprToAlloy(oclwhen));
				}
	
				fact = (whenexpr.implies(fact));	
				for (Decl d : alloywhenvars)
					fact = fact.forAll(d);
			}
			
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"QVTRelation2Alloy",sourceexpr);}
		
		this.fact = fact;
	}
	
	/** Initializes the variable lists and generates the respective Alloy declarations.
	 * 
	 * @throws ErrorTransform
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 * @todo Support fom <code>CollectionTemplateExp</code>
	 */
	private void initVariableDeclarationLists() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		TemplateExp temp;
		Set<VariableDeclaration> whenvariables = new HashSet<VariableDeclaration>();
		Set<VariableDeclaration> sourcevariables = new HashSet<VariableDeclaration>();
		Set<VariableDeclaration> targetvariables = new HashSet<VariableDeclaration>();
		
		if (rel.getWhen() != null)
			for (Predicate predicate : rel.getWhen().getPredicate()) {
				OCLExpression oclwhen = predicate.getConditionExpression();
				whenvariables.addAll(OCLUtil.variablesOCLExpression(oclwhen));
			}
		
		for (RelationDomain dom : sourcedomains) {
			temp = dom.getPattern().getTemplateExpression();
			sourcevariables.addAll(OCLUtil.variablesOCLExpression(temp));
		}
		sourcevariables.removeAll(whenvariables);
		
		temp = targetdomain.getPattern().getTemplateExpression();
		targetvariables.addAll(OCLUtil.variablesOCLExpression(temp));
		if (rel.getWhere() != null)
			for (Predicate predicate : rel.getWhere().getPredicate()) {
				OCLExpression oclwhere = predicate.getConditionExpression();
				targetvariables.addAll(OCLUtil.variablesOCLExpression(oclwhere));
			}
		targetvariables.removeAll(sourcevariables);
		targetvariables.removeAll(whenvariables);

		if (!top) {
			whenvariables.removeAll(rootvariables);
			targetvariables.removeAll(rootvariables);
			sourcevariables.removeAll(rootvariables);
		}
		
		alloysourcevars = (Set<Decl>) OCL2Alloy.variableListToExpr(sourcevariables,modelsigs,true,statesigs);
		decls.addAll(alloysourcevars);
		alloywhenvars =  (Set<Decl>) OCL2Alloy.variableListToExpr(whenvariables,modelsigs,true,statesigs);
		decls.addAll(alloywhenvars);
		alloytargetvars = (Set<Decl>) OCL2Alloy.variableListToExpr(targetvariables,modelsigs,true,statesigs);
		decls.addAll(alloytargetvars);
		alloyrootvars = (List<Decl>) OCL2Alloy.variableListToExpr(rootvariables,modelsigs,false,statesigs);
	    if (!top) decls.addAll(alloyrootvars);
	}

	/** Translates a {@code RelationDomain} to the correspondent Alloy expression through {@code OCL2Alloy} translator.
	 * 
	 * @param domain The {@code RelationDomain} to be translated
	 * @return the Alloy expression representing {@code domain}
	 * @throws ErrorTransform
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 */
	private Expr patternToExpr (RelationDomain domain) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		OCL2Alloy ocltrans = new OCL2Alloy(domain.getTypedModel(),statesigs,modelsigs,decls);

		DomainPattern pattern = domain.getPattern();
		ObjectTemplateExp temp = (ObjectTemplateExp) pattern.getTemplateExpression(); 
		
		return ocltrans.oclExprToAlloy(temp);
	}
	
	/** Generates the QVT Relation field.
	 * 
	 * @return the Alloy field for this QVT Relation
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @todo Support for n models
	 */
	private Field addRelationFields() throws ErrorAlloy, ErrorTransform{
		try {
			Sig type = (Sig) alloyrootvars.get(1).expr.type().toExpr();
			Sig s = (Sig) alloyrootvars.get(0).expr.type().toExpr();
			Field field = null;
			for (Field f : s.getFields()) {
				if (f.label.equals(AlloyUtil.relationFieldName(rel,direction)))
					field = f;
			}
			if (field == null) {
				field = s.addField(AlloyUtil.relationFieldName(rel,direction), type.setOf());
				s.addFact(field.equal(fact.comprehensionOver(alloyrootvars.get(0), alloyrootvars.get(1))));
			}
			return field;
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"QVTRelation2Alloy",fact);}
	}
	
	private Expr trading(Expr expr){
		Expr res = expr;
		Expr ebody = null;
		List<Decl> aux = new ArrayList<Decl>();
		if (expr instanceof ExprQt){
			ebody = trading(((ExprQt) expr).sub);
			if (((ExprQt) expr).op.equals(ExprQt.Op.ALL) && (ebody instanceof ExprBinary) && 
					((ExprBinary)ebody).op.equals(ExprBinary.Op.IMPLIES)) {
				Expr abody = ((ExprBinary)ebody).left;
				for (Decl d : ((ExprQt) expr).decls) {
					if (d.names.size()==1){
						Entry<List<Expr>,Expr> es = findTrades(d.get(),abody);
						System.out.println(d.get() +" cast to "+es.getKey());
						if (es.getKey().size() > 0) {
							d = new Decl(null,null,null,d.names,es.getKey().get(0));
							abody = es.getValue();
						} 
						aux.add(d);
						ebody = ExprBinary.Op.IMPLIES.make(null, null, abody, ((ExprBinary)ebody).right);
					}
				}
				aux = AlloyUtil.ordDecls(aux);
				res = ((ExprQt) expr).op.make(null, null,aux, ebody);
			} else if (((ExprQt) expr).op.equals(ExprQt.Op.SOME)) {
				for (Decl d : ((ExprQt) expr).decls) {
					if (d.names.size()==1){
						Entry<List<Expr>,Expr> es = findTrades(d.get(),ebody);
						if (es.getKey().size() > 0) {
							d = new Decl(null,null,null,d.names,es.getKey().get(0));
							ebody = es.getValue();
						} 
						aux.add(d);
					}
				}
				//aux = AlloyUtil.ordDecls(aux);
				res = ((ExprQt) expr).op.make(null, null,aux, ebody);
			}
		} else if (expr instanceof ExprBinary){
			res = ((ExprBinary)expr).op.make(null, null, trading(((ExprBinary) expr).left),trading(((ExprBinary) expr).right));			
		}

		
		return res;
	}
	
	private Entry<List<Expr>,Expr> findTrades(ExprHasName v, Expr e){
		List<Expr> resl = new ArrayList<Expr>();
		Expr rese = e;
		if (e instanceof ExprBinary){
			if (((ExprBinary)e).op.equals(ExprBinary.Op.IN)){
				Expr inleft = ((ExprBinary)e).left;
				Expr inright = ((ExprBinary)e).right;				
				if (inleft.isSame(v)) {
					resl.add(inright);
					rese = Sig.NONE.no();
				}else if (inright instanceof ExprBinary && ((ExprBinary)inright).op.equals(ExprBinary.Op.JOIN) ) {
					if (((ExprBinary) inright).left.isSame(v)) {
						resl.add((((ExprBinary) inright).right).join(inleft));
						rese = Sig.NONE.no();
					}
				}
			
			} 
		} else if ((e instanceof ExprList) && ((ExprList)e).op.equals(ExprList.Op.AND)) {
			List<Expr> exps = new ArrayList<Expr>();
			for (Expr arg : ((ExprList) e).args) {
				Entry<List<Expr>,Expr> auxr = findTrades(v,arg);
				resl.addAll(auxr.getKey());
				exps.add(auxr.getValue());
			}
			rese = ExprList.make(null, null, ExprList.Op.AND, exps);
		}
		return new SimpleEntry<List<Expr>,Expr>(resl,rese);
		
	}
	
	/** Returns the Alloy fact corresponding to this QVT Relation
	 * 
	 * @return this.fact
	 */
	public Expr getFact() {
		return fact;
	}

	/** Returns the Alloy field corresponding to this QVT Relation
	 * 
	 * @return this.field
	 */
	public Field getField() {
		return field;
	}
}
