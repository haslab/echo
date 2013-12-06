package pt.uminho.haslab.echo.transform.alloy;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.alloy.AlloyOptimizations;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.consistency.Condition;
import pt.uminho.haslab.echo.consistency.Domain;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Relation;
import pt.uminho.haslab.echo.consistency.Variable;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

public class Relation2Alloy {

	/** the QVT Relation being transformed*/
	private Relation rel;
	/** the direction of the QVT Relation*/
	private Model direction;
	/** whether the QVT Relation is being called at top level or not
	 * this is not the same as being a top relation */
	private boolean top;
	/** the parent top QVT Relation, null if top */
	private final Relation2Alloy parentq;

	/** the root variables of the QVT Relation being translated*/
	private Map<Variable,String> rootvariables = new HashMap<Variable,String>();
	/** the target relation domain */
	private Domain targetdomain;
	/** the source relation domains */
	private List<Domain> sourcedomains = new ArrayList<Domain>();

	/** the Alloy declarations of the variables occurring in the when constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Map<String,Decl> alloywhenvars = new HashMap<String,Decl>();
	/** the Alloy declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Map<String,Decl> alloysourcevars = new HashMap<String,Decl>();
	/** the Alloy declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top QVT Relation, does not contain root variables*/
	private Map<String,Decl> alloytargetvars = new HashMap<String,Decl>();
	/** the Alloy declarations of the root variables
	 * null if top QVT Relation */
	private Map<String, Decl> alloyrootvars = new HashMap<String,Decl>();
	/** the Alloy declarations of all variables (union of the previous sets) */ 
	
	/** the current QVT function */
	/** the variables of the current QVT function */
	private Map<String,ExprHasName> statevars = new LinkedHashMap<String,ExprHasName>();
	private Map<String,ExprHasName> varvar = new LinkedHashMap<String,ExprHasName>();
	private Map<String,String> varsstate = new LinkedHashMap<String,String>();
		
	public final Transformation2Alloy transformation_trans;
	
	public Relation2Alloy (Relation2Alloy q2a, Relation rel) throws EchoError {
		this (q2a.transformation_trans, false,q2a,q2a.getDirection(),rel);
	}

	public Relation2Alloy (Transformation2Alloy t, Model mdl, Relation rel) throws EchoError {
		this (t,true,null,mdl,rel);
	}

	/** 
	 * Constructs a new QVT Relation to Alloy translator.
	 * Translates a QVT Relation (top or non top) to Alloy in a given direction.
	 * @param rel the QVT Relation being translated
	 * @param direction the target direction of the transformation
	 * @param top whether the QVT Relation is top or not
	 * @throws ErrorTransform, 
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 */
	Relation2Alloy (Transformation2Alloy t, Boolean top, Relation2Alloy q2a, Model direction, Relation rel) throws EchoError {
		this.rel = rel;
		this.direction = direction;
		this.top = top;
		this.transformation_trans = t;
		this.parentq = top?this:q2a;
		List<Decl> mdecls = new ArrayList<Decl>();
				
		for (Model mdl : rel.getTransformation().getModels()) {
			Decl d;
			String metamodeluri = mdl.getMetamodelURI();
			try {
				d = AlloyEchoTranslator.getInstance().getMetaModelStateSig(metamodeluri).oneOf(mdl.getName());
			} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
			mdecls.add(d);
			statevars.put(mdl.getName(),d.get());
		}
		for (Decl d : mdecls) {
			varsstate.put(d.get().label,null);
			varvar.put(d.get().label, d.get());
		}
		initDomains();
		initVariableLists();
		Field field = null;
		if (!top) {
			try {
				field = addRelationFields(mdecls);
				transformation_trans.addRecRelationCall(new Func(null, AlloyUtil.relationFieldName(rel,direction), mdecls, field.type().toExpr(), field));
			} catch (Err e) {
				e.printStackTrace();
			}	
		}
		Expr fact = calculateFact();
		AlloyOptimizations opt = new AlloyOptimizations();
		if(EchoOptionsSetup.getInstance().isOptimize()&&true) {
			//EchoReporter.getInstance().debug("Pre-onepoint "+fact);
			fact = opt.trading(fact);
			//EchoReporter.getInstance().debug("Mid-onepoint "+fact);
			fact = opt.onePoint(fact);
		}
		EchoReporter.getInstance().debug("Pos-onepoint "+fact);
		try {
			if(top) {
				transformation_trans.addTopRelationCall(new Func(null, rel.getName()+"_"+direction.getName(), mdecls, null, fact));	
			}
			else {
				addRelationDef(fact, field, mdecls);
			}
		} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }	
	}
	
	/** 
	 * Initializes the domain variables {@code this.sourcedomains}, {@code this.targetdomain} and {@code this.rootvariables}
	 * @throws ErrorTransform if some {@code Domain} is not {@code RelationDomain}
	 */
	private void initDomains () throws EchoError {
		for (Domain dom : rel.getDomains()) {
			rootvariables.put(dom.getVariable(),dom.getModel().getName());
			if (dom.getModel().equals(direction)) targetdomain = dom;
			else sourcedomains.add(dom);
		}
	}
	
	/** 
	 * Calculates the Alloy expression denoting the QVT Relation.
	 * Takes the shape "forall whenvars : when => (forall sourcevars : sourcedomain => (exists targetvars+wherevars : targetdomain && where))"
	 * @return the Alloy expression representing the QVT Relation
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @throws ErrorUnsupported
	 */
	private Expr calculateFact() throws EchoError {

		Expr fact = Sig.NONE.no(),sourceexpr = Sig.NONE.no(),targetexpr = Sig.NONE.no(),whereexpr = Sig.NONE.no(), whenexpr = Sig.NONE.no();
		Decl[] arraydecl;
		Condition postCondition = rel.getPost();
		try {
			if (postCondition != null){
				postCondition.initTranslation(parentq,auxMap(),statevars, null);
				whereexpr = postCondition.translate();
			}
			targetexpr = AlloyUtil.cleanAnd(patternToExpr(targetdomain),whereexpr);
			if (alloytargetvars.size() == 1)
				targetexpr = targetexpr.forSome(alloytargetvars.values().iterator().next());	
			else if (alloytargetvars.size() > 1) {
				arraydecl = alloytargetvars.values().toArray(new Decl[alloytargetvars.size()]);
				targetexpr = targetexpr.forSome(arraydecl[0],Arrays.copyOfRange(arraydecl, 1, arraydecl.length));	
			}

			for (Domain dom : sourcedomains) 
				sourceexpr = AlloyUtil.cleanAnd(sourceexpr,patternToExpr(dom));
			fact = (sourceexpr.implies(targetexpr));
			
			
			for (Decl d : alloysourcevars.values()) {
				System.out.println("Universal: "+d.toString() + " : " + d.expr);
			}
			
			if (alloysourcevars.size() == 1)
				fact = fact.forAll(alloysourcevars.values().iterator().next());	
			else if (alloysourcevars.size() > 1) {
				arraydecl = alloysourcevars.values().toArray(new Decl[alloysourcevars.size()]);
				fact = fact.forAll(arraydecl[0],Arrays.copyOfRange(arraydecl, 1, arraydecl.length));	
			}
			
			Condition preCondition = rel.getPre();
			if (preCondition != null){
				preCondition.initTranslation(parentq,auxMap(),statevars, null);
				whenexpr = preCondition.translate();
	
				fact = (whenexpr.implies(fact));	
				for (Decl d : alloywhenvars.values())
					fact = fact.forAll(d);
			}
			
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		
		return fact;
	}
	
	/** 
	 * Initializes the variable lists and generates the respective Alloy declarations.
	 * @throws ErrorTransform
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 * @todo Support fom <code>CollectionTemplateExp</code>
	 */
	private void initVariableLists() throws EchoError {
		Condition temp;
		Map<Variable,String> whenvariables = new HashMap<Variable,String>();
		Map<Variable,String> sourcevariables = new HashMap<Variable,String>();
		Map<Variable,String> targetvariables = new HashMap<Variable,String>();
		
		Condition preCondition = rel.getPre();
		if (preCondition != null)
			whenvariables = preCondition.getVariables(null);

		for (Domain dom : sourcedomains) {
			Condition cond = dom.getCondition();
			sourcevariables = cond.getVariables(dom.getModel().getName());
		}

		for (Variable x : whenvariables.keySet()) {
			whenvariables.put(x, sourcevariables.get(x));
			sourcevariables.remove(x);
		}
		
		temp = targetdomain.getCondition();
		targetvariables.putAll(temp.getVariables(targetdomain.getModel().getName()));
		Condition postCondition = rel.getPost();
		if (postCondition != null)
			for (Variable x : postCondition.getVariables(null).keySet())
					if (targetvariables.get(x) == null) targetvariables.put(x,null);
		for (Variable x : sourcevariables.keySet()) {
			if (sourcevariables.get(x) == null) sourcevariables.put(x, targetvariables.get(x));
			targetvariables.remove(x); 
		}
		for (Variable x : whenvariables.keySet()) {
			if (whenvariables.get(x) == null) whenvariables.put(x, targetvariables.get(x));
			targetvariables.remove(x); 
		}

		if (!top) {
			for (Variable x : rootvariables.keySet()) {
				whenvariables.remove(x);
				targetvariables.remove(x);
				sourcevariables.remove(x);
			}
		}
	    /*EchoReporter.getInstance().debug(top+"");

	    EchoReporter.getInstance().debug("sourcevariables: "+sourcevariables);
	    EchoReporter.getInstance().debug("whenvariables: "+whenvariables);
	    EchoReporter.getInstance().debug("targetvariables: "+targetvariables);
	    EchoReporter.getInstance().debug("rootvariables: "+rootvariables);*/

	    for (Variable s : sourcevariables.keySet())
			varsstate.put(s.getName(),sourcevariables.get(s));
		for (Variable s : targetvariables.keySet())
			varsstate.put(s.getName(),targetvariables.get(s));
		for (Variable s : whenvariables.keySet())
			varsstate.put(s.getName(),whenvariables.get(s));
	    if (!top) {
	    	for (Variable s : rootvariables.keySet())
				varsstate.put(s.getName(),rootvariables.get(s));
	    }
		
		alloysourcevars = AlloyUtil.variableListToExpr(sourcevariables.keySet(),auxMap(),statevars);
	  	for (String s : alloysourcevars.keySet())
			varvar.put(s, alloysourcevars.get(s).get());
	  	alloytargetvars = AlloyUtil.variableListToExpr(targetvariables.keySet(),auxMap(),statevars);
	  	for (String s : alloytargetvars.keySet())
			varvar.put(s, alloytargetvars.get(s).get());
	  	alloywhenvars = AlloyUtil.variableListToExpr(whenvariables.keySet(),auxMap(),statevars);
	  	for (String s : alloywhenvars.keySet())
			varvar.put(s, alloywhenvars.get(s).get());
	  	alloyrootvars = AlloyUtil.variableListToExpr(rootvariables.keySet(),auxMap(),statevars);
	    if (!top) {
		  	for (String s : alloyrootvars.keySet())
				varvar.put(s, alloyrootvars.get(s).get());
	    }
	    //EchoReporter.getInstance().debug("vasstate: "+varvar);
	}

	

	/** 
	 * Translates a {@code RelationDomain} to the correspondent Alloy expression through {@code Expression2Alloy} translator.
	 * @param domain The {@code RelationDomain} to be translated
	 * @return the Alloy expression representing {@code domain}
	 * @throws ErrorTransform
	 * @throws ErrorAlloy
	 * @throws ErrorUnsupported
	 */
	private Expr patternToExpr (Domain domain) throws EchoError {		
		Condition cond = domain.getCondition();
		cond.initTranslation(parentq,auxMap(),statevars,null);
		Expr res = cond.translate();
		return res;
	}
	
	/** 
	 * Generates the QVT Relation field over the model sigs.
	 * @return the Alloy field for this QVT Relation
	 * @throws ErrorAlloy
	 * @throws ErrorTransform
	 * @todo Support for n models
	 */
	private Field addRelationFields(List<Decl> mdecls) throws EchoError {
		Field field = null;
		Decl fst = alloyrootvars.get(rel.getDomains().get(0).getVariable().getName());
		Decl snd = alloyrootvars.get(rel.getDomains().get(1).getVariable().getName());
		try {
			Sig s = (Sig) fst.expr.type().toExpr();
			for (Field f : s.getFields()) {
				if (f.label.equals(AlloyUtil.relationFieldName(rel,direction)))
					field = f;
			}
			if (field == null) {
				field = s.addField(AlloyUtil.relationFieldName(rel,direction), /*type.setOf()*/Sig.UNIV.setOf());
			}
			} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		return field;
	}
	
	private void addRelationDef(Expr fact, Field field, List<Decl> mdecls) throws EchoError {
		Decl fst = alloyrootvars.get(rel.getDomains().get(0).getVariable().getName());
		Decl snd = alloyrootvars.get(rel.getDomains().get(1).getVariable().getName());
		Func f;
		try {
			Expr e = field.equal(fact.comprehensionOver(fst,snd));
			f = new Func(null, field.label+"def",mdecls,null,e);
			transformation_trans.addRecRelationDef(f);
		} catch (Err e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	

	Model getDirection() {
		return direction;
	}

	private Map<String,Entry<ExprHasName,String>> auxMap () {
		Map<String,Entry<ExprHasName,String>> aux = new LinkedHashMap<String,Entry<ExprHasName,String>>();
		for (String s : varsstate.keySet()) {
			aux.put(s, new SimpleEntry<ExprHasName,String>(varvar.get(s),varsstate.get(s)));
		}
		return aux;
	}
	
}
