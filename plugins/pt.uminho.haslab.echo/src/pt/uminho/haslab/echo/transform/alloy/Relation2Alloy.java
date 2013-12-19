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
import pt.uminho.haslab.echo.EchoRunner.Task;
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

	/** the parent transformation translator */
	public final Transformation2Alloy transformation_translator;

	/** the relation being transformed */
	public final Relation relation;
	
	/** the direction of the test being generated */
	public final Model direction;

	/** whether the relation is being called at top level or not
	 * this is not the same as being a top relation */
	public final boolean top;
	
	/** the parent (calling) relation, null if top */
	public final Relation2Alloy parent_translator;

	/** the root variables of the relation being translated*/
	private Map<Variable,String> rootvariables = new HashMap<Variable,String>();
	
	/** the target relation domain */
	private Domain targetdomain;
	
	/** the source relation domains */
	private List<Domain> sourcedomains = new ArrayList<Domain>();

	/** the Alloy declarations of the variables occurring in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,Decl> whenvar2alloydecl = new HashMap<String,Decl>();
	
	/** the Alloy declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,Decl> sourcevar2alloydecl = new HashMap<String,Decl>();
	
	/** the Alloy declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,Decl> targetvar2alloydecl = new HashMap<String,Decl>();
	
	/** the Alloy declarations of the root variables
	 * null if top relation */
	private Map<String, Decl> rootvar2alloydecl = new HashMap<String,Decl>();
	
	/** the model parameters variables of the current transformation */
	private Map<String,ExprHasName> modelparam2var = new LinkedHashMap<String,ExprHasName>();
	
	/** maps variable names to their Alloy representation 
	 * contains all variables, including model parameters */
	private Map<String,ExprHasName> var2var = new LinkedHashMap<String,ExprHasName>();

	/** maps variable names to the owning model */
	private Map<String,String> var2model = new LinkedHashMap<String,String>();
	
	/** the transformation model parameters Alloy declarations */
	private List<Decl> model_params_decls = new ArrayList<Decl>();

		
	
	/** 
	 * Creates a relation to Alloy translator for a non-top relation
	 * @param parent_translator the parent relation translator
	 * @param relation the relation to be translated
	 * @throws EchoError
	 */
	public Relation2Alloy (Relation2Alloy parent_translator, Relation relation) throws EchoError {
		this (parent_translator.transformation_translator, false, parent_translator, parent_translator.direction,relation);
	}

	/**
	 * Creates a relation to Alloy translator for a top relation
	 * @param transformation_translator the parent transformation translator
	 * @param direction the test direction
	 * @param relation the relation being translated
	 * @throws EchoError
	 */
	public Relation2Alloy (Transformation2Alloy transformation_translator, Model direction, Relation relation) throws EchoError {
		this (transformation_translator,true,null,direction,relation);
	}

	/** 
	 * Constructs a new relation to Alloy translator.
	 * Translates a relation (top or non top) to Alloy in a given direction.
	 * @param relation the relation being translated
	 * @param direction the target direction of the test
	 * @param top whether the relation is top or not
	 * @throws EchoError
	 */
	Relation2Alloy(Transformation2Alloy transformation_translator, Boolean top,
			Relation2Alloy parent_translator, Model direction, Relation relation)
			throws EchoError {
		this.relation = relation;
		this.direction = direction;
		this.top = top;
		this.transformation_translator = transformation_translator;
		this.parent_translator = top ? this : parent_translator;

		for (Model mdl : relation.getTransformation().getModels()) {
			Decl d;
			String metamodeluri = mdl.getMetamodelURI();
			try {
				d = AlloyEchoTranslator.getInstance()
						.getMetaModelStateSig(metamodeluri)
						.oneOf(mdl.getName());
			} catch (Err a) {
				throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
						"Failed to create transformation model variable: "
								+ mdl.getName(), a,
						Task.TRANSLATE_TRANSFORMATION);
			}
			model_params_decls.add(d);
			modelparam2var.put(mdl.getName(), d.get());
			var2model.put(d.get().label, null);
			var2var.put(d.get().label, d.get());
		}

		initVariableLists();
		EchoReporter.getInstance().debug("source var: "+sourcevar2alloydecl);
		EchoReporter.getInstance().debug("when var: "+whenvar2alloydecl);
		EchoReporter.getInstance().debug("target var: "+targetvar2alloydecl);
		EchoReporter.getInstance().debug("root var: "+rootvar2alloydecl);

		Field field = null;
		if (!top)
			field = addRelationField();

		Expr fact = calculateFact();
		AlloyOptimizations opt = new AlloyOptimizations();
		if (EchoOptionsSetup.getInstance().isOptimize() && true) {
			fact = opt.trading(fact);
			fact = opt.onePoint(fact);
		}
		EchoReporter.getInstance().debug("Post-opt: "+fact);
		
		if (top)
			addRelationPred(fact);
		else
			addRelationDef(fact, field);

	}

	/** 
	 * Calculates the Alloy constraint denoting the relation top test.
	 * Takes the shape "forall whenvars : when => (forall sourcevars : sourcedomain => (exists targetvars+wherevars : targetdomain && where))"
	 * Must be run after relation field is created (otherwise recursive calls will fail)
	 * @return the Alloy constraint representing the relation
	 * @throws EchoError
	 */
	private Expr calculateFact() throws EchoError {

		Expr fact = Sig.NONE.no(), sourceexpr = Sig.NONE.no(), postexpr = Sig.NONE.no(), whenexpr = Sig.NONE.no();
		Decl[] arraydecl;

		Condition postCondition = relation.getPost();
		Condition targetCondition = targetdomain.getCondition();
		try {
			if (postCondition != null) {
				postCondition.initTranslation(parent_translator, var2varmodel(),
						modelparam2var, null);
				postexpr = postCondition.translate();
			}

			targetCondition.initTranslation(parent_translator,var2varmodel(),modelparam2var,null);
			Expr targetexpr = targetCondition.translate();
			targetexpr = targetexpr.and(postexpr);

			if (targetvar2alloydecl.size() == 1)
				targetexpr = targetexpr.forSome(targetvar2alloydecl.values().iterator().next());
			else if (targetvar2alloydecl.size() > 1) {
				arraydecl = targetvar2alloydecl.values().toArray(
						new Decl[targetvar2alloydecl.size()]);
				targetexpr = targetexpr.forSome(arraydecl[0],
						Arrays.copyOfRange(arraydecl, 1, arraydecl.length));
			}

			for (Domain dom : sourcedomains) {
				Condition sourceCondition = dom.getCondition();
				sourceCondition.initTranslation(parent_translator,var2varmodel(),modelparam2var,null);
				sourceexpr = sourceexpr.and(sourceCondition.translate());
			}	
			fact = (sourceexpr.implies(targetexpr));


//			for (Decl d : sourcevar2alloydecl.values())
//				EchoReporter.getInstance().debug("Source var decl: "+d.names+"::"+d.expr);
//			for (Decl d : targetvar2alloydecl.values())
//				EchoReporter.getInstance().debug("Target var decl: "+d.names+"::"+d.expr);

			if (sourcevar2alloydecl.size() == 1) {
				fact = fact.forAll(sourcevar2alloydecl.values().iterator().next());
			}
			else if (sourcevar2alloydecl.size() > 1) {
				arraydecl = sourcevar2alloydecl.values().toArray(
						new Decl[sourcevar2alloydecl.size()]);
				fact = fact.forAll(arraydecl[0],
						Arrays.copyOfRange(arraydecl, 1, arraydecl.length));
			}

			Condition preCondition = relation.getPre();
			if (preCondition != null) {
				preCondition.initTranslation(parent_translator, var2varmodel(),
						modelparam2var, null);
				whenexpr = preCondition.translate();

				fact = (whenexpr.implies(fact));
				for (Decl d : whenvar2alloydecl.values())
					fact = fact.forAll(d);
			}
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
					"Failed to create relation constraint variables: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return fact;
	}
	
	/** 
	 * Initializes the variable lists and generates the respective Alloy declarations.
	 * @throws EchoError
	 * @todo Support fom <code>CollectionTemplateExp</code>
	 */
	private void initVariableLists() throws EchoError {
		Map<Variable,String> whenvar2model = new HashMap<Variable,String>();
		Map<Variable,String> sourcevar2model = new HashMap<Variable,String>();
		Map<Variable,String> targetvar2model = new HashMap<Variable,String>();
		
		for (Domain dom : relation.getDomains()) {
			rootvariables.put(dom.getVariable(),dom.getModel().getName());
			if (dom.getModel().equals(direction)) targetdomain = dom;
			else sourcedomains.add(dom);
		}
		
		Condition preCondition = relation.getPre();
		if (preCondition != null)
			whenvar2model = preCondition.getVariables(null);

		for (Domain dom : sourcedomains) {
			Condition cond = dom.getCondition();
			sourcevar2model = cond.getVariables(dom.getModel().getName());
		}

		for (Variable x : whenvar2model.keySet()) {
			whenvar2model.put(x, sourcevar2model.get(x));
			sourcevar2model.remove(x);
		}
		
		Condition temp = targetdomain.getCondition();
		targetvar2model = temp.getVariables(targetdomain.getModel().getName());
		
		Condition postCondition = relation.getPost();
		if (postCondition != null)
			for (Variable x : postCondition.getVariables(null).keySet())
				if (targetvar2model.get(x) == null) targetvar2model.put(x,null);
		
		for (Variable x : sourcevar2model.keySet()) {
			if (sourcevar2model.get(x) == null) 
				sourcevar2model.put(x, targetvar2model.get(x));
			targetvar2model.remove(x); 
		}
		
		for (Variable x : whenvar2model.keySet()) {
			if (whenvar2model.get(x) == null) 
				whenvar2model.put(x, targetvar2model.get(x));
			targetvar2model.remove(x); 
		}

		if (!top)
			for (Variable x : rootvariables.keySet()) {
				whenvar2model.remove(x);
				targetvar2model.remove(x);
				sourcevar2model.remove(x);
			}
	 
	    for (Variable s : sourcevar2model.keySet())
			var2model.put(s.getName(),sourcevar2model.get(s));
		for (Variable s : targetvar2model.keySet())
			var2model.put(s.getName(),targetvar2model.get(s));
		for (Variable s : whenvar2model.keySet())
			var2model.put(s.getName(),whenvar2model.get(s));
	    if (!top)
	    	for (Variable s : rootvariables.keySet())
				var2model.put(s.getName(),rootvariables.get(s));
		
		sourcevar2alloydecl = AlloyUtil.variableListToExpr(sourcevar2model.keySet(),var2varmodel(),modelparam2var);
	  	for (String s : sourcevar2alloydecl.keySet())
			var2var.put(s, sourcevar2alloydecl.get(s).get());
	  	targetvar2alloydecl = AlloyUtil.variableListToExpr(targetvar2model.keySet(),var2varmodel(),modelparam2var);
	  	for (String s : targetvar2alloydecl.keySet())
			var2var.put(s, targetvar2alloydecl.get(s).get());
	  	whenvar2alloydecl = AlloyUtil.variableListToExpr(whenvar2model.keySet(),var2varmodel(),modelparam2var);
	  	for (String s : whenvar2alloydecl.keySet())
			var2var.put(s, whenvar2alloydecl.get(s).get());
	  	rootvar2alloydecl = AlloyUtil.variableListToExpr(rootvariables.keySet(),var2varmodel(),modelparam2var);
	    if (!top)
		  	for (String s : rootvar2alloydecl.keySet())
				var2var.put(s, rootvar2alloydecl.get(s).get());

	}

	/** 
	 * Generates the relation field over the model sigs for called relations
	 * Must be run before the relation constraint is created (otherwise recursive calls will fail)
	 * @return the Alloy field for this relation
	 * @throws EchoError
	 * @todo Support for n models
	 */
	private Field addRelationFields() throws EchoError {
		Field field = null;
		Decl fst = rootvar2alloydecl.get(relation.getDomains().get(0)
				.getVariable().getName());
		/*Decl snd = rootvar2alloydecl.get(relation.getDomains().get(1)
				.getVariable().getName());*/
		try {
			Sig s = (Sig) fst.expr.type().toExpr();
			for (Field f : s.getFields()) {
				if (f.label.equals(AlloyUtil.relationFieldName(relation,
						direction)))
					field = f;
			}
			if (field == null) {
				field = s.addField(
						AlloyUtil.relationFieldName(relation, direction),
						/*type.setOf()*/Sig.UNIV.setOf());
			}
		} catch (Err a) { 
			throw new ErrorAlloy(
					ErrorAlloy.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "+relation.getName(),
					a,Task.TRANSLATE_TRANSFORMATION); 
		}
		return field;
	}
	
	/**
	 * adds to the transformation translator the constraint defining the field of this sub relation
	 * @param fact the constraint defining this relation
	 * @param field the field representing this sub relation
	 * @throws EchoError
	 */
	private void addRelationDef(Expr fact, Field field) throws EchoError {
		Decl fst = rootvar2alloydecl.get(relation.getDomains().get(0).getVariable().getName());
		Decl snd = rootvar2alloydecl.get(relation.getDomains().get(1).getVariable().getName());
		Func f;
		try {
			Expr e = field.equal(fact.comprehensionOver(fst,snd));
			f = new Func(null, field.label+"def",model_params_decls,null,e);
			transformation_translator.addSubRelationDef(f);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Failed to create sub relation field constraint: "
							+ relation.getName(), a,
							Task.TRANSLATE_TRANSFORMATION);
		}
	}
	
	/** 
	 * adds to the transformation translator the constraint defining this top relation
	 * @param fact the constraint defining this relation
	 * @throws EchoError
	 */
	private void addRelationPred(Expr fact) throws EchoError {
		try {
			transformation_translator.addTopRelationCall(new Func(null,AlloyUtil.relationPredName(relation,direction),
					model_params_decls, null, fact));
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Failed to create top relation constraint function: "
							+ relation.getName(), a,
							Task.TRANSLATE_TRANSFORMATION);
		}
	}
	
	
	/** 
	 * adds to the transformation translator the field representing this sub relation
	 * @throws EchoError
	 */
	private Field addRelationField() throws EchoError {
		Field field = null;
		try {
			field = addRelationFields();
			transformation_translator.addSubRelationCall(new Func(null,
					AlloyUtil.relationFieldName(relation, direction),
					model_params_decls, field.type().toExpr(), field));
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FUNC,
					"Failed to create sub relation constraint function: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return field;
	}

	/**
	 * Maps variable names to their Alloy representation and containing model
	 * Merges <code>var2var</code> with <code>var2model</code>
	 * @return the mapping
	 */
	private Map<String,Entry<ExprHasName,String>> var2varmodel() {
		Map<String,Entry<ExprHasName,String>> aux = new LinkedHashMap<String,Entry<ExprHasName,String>>();
		for (String s : var2model.keySet()) {
			aux.put(s, new SimpleEntry<ExprHasName,String>(var2var.get(s),var2model.get(s)));
		}
		return aux;
	}
	
}
