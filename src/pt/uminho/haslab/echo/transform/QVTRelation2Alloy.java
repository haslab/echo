package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import pt.uminho.haslab.echo.alloy.AlloyOptimizations;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.emf.OCLUtil;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

public class QVTRelation2Alloy {

	/** the QVT Relation being transformed*/
	private Relation rel;
	/** the direction of the QVT Relation*/
	private TypedModel direction;
	/** whether the QVT Relation is being called at top level or not
	 * this is not the same as being a top relation */
	private boolean top;
	
	EMF2Alloy translator;
	
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
	public QVTRelation2Alloy (Relation rel, TypedModel direction, boolean top, EMF2Alloy translator) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		this.rel = rel;
		this.direction = direction;
		this.top = top;
		this.translator = translator;
		
		initDomains();
		initVariableDeclarationLists();
		calculateFact();
		AlloyOptimizations opt = new AlloyOptimizations(translator);
		if(translator.options.isOptimize()) {
			fact = opt.trading(fact);
			System.out.println("Pre-onepoint "+fact);
			fact = opt.onePoint(fact);
			System.out.println("Pos-onepoint "+fact);
		}
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
				OCL2Alloy ocltrans = new OCL2Alloy(direction,translator,decls);
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
				OCL2Alloy ocltrans = new OCL2Alloy(direction,translator,decls);
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
		
		alloysourcevars = (Set<Decl>) OCL2Alloy.variableListToExpr(sourcevariables,true,translator);
		decls.addAll(alloysourcevars);
		alloywhenvars =  (Set<Decl>) OCL2Alloy.variableListToExpr(whenvariables,true,translator);
		decls.addAll(alloywhenvars);
		alloytargetvars = (Set<Decl>) OCL2Alloy.variableListToExpr(targetvariables,true,translator);
		decls.addAll(alloytargetvars);
		alloyrootvars = (List<Decl>) OCL2Alloy.variableListToExpr(rootvariables,false,translator);
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
		OCL2Alloy ocltrans = new OCL2Alloy(domain.getTypedModel(),translator,decls);

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
