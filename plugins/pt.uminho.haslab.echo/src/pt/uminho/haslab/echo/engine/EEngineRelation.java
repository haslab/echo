package pt.uminho.haslab.echo.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

public abstract class EEngineRelation {

	/** the relation being transformed */
	public final ERelation relation;
	
	/** the direction of the test being generated */
	public final EDependency dependency;

	/** the parent transformation translator */
	public final EEngineTransformation transformation_translator;

	/** whether the relation is being called at top level or not
	 * this is not the same as being a top relation */
	public final boolean top;
	
	/** the parent (calling) relation, null if top */
	public final EEngineRelation parent_translator;
	
	/** the transformation model parameters Alloy declarations */
	private List<IDecl> model_params_decls = new ArrayList<IDecl>();

	/** the Alloy declarations of the root variables
	 * null if top relation */
	private Map<String, IDecl> rootvar2alloydecl = new HashMap<String,IDecl>();

	/** the root variables of the relation being translated*/
	private Map<EVariable,String> rootvariables = new HashMap<EVariable,String>();

	/** the target relation domain */
	private EModelDomain targetdomain;
	
	/** the source relation domains */
	private List<EModelDomain> sourcedomains = new ArrayList<EModelDomain>();

	/** the Alloy declarations of the variables occurring in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> whenvar2alloydecl = new HashMap<String,IDecl>();
	
	/** the Alloy declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> sourcevar2alloydecl = new HashMap<String,IDecl>();
	
	/** the Alloy declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> targetvar2alloydecl = new HashMap<String,IDecl>();

	
	/** 
	 * Creates a relation to Alloy translator for a non-top relation
	 * @param parent_translator the parent relation translator
	 * @param relation the relation to be translated
	 * @throws EchoError
	 */
	public EEngineRelation (EEngineRelation parent_translator, ERelation relation) throws EchoError {
		this (parent_translator.transformation_translator, false, parent_translator, parent_translator.dependency,relation);
	}

	/**
	 * Creates a relation to Alloy translator for a top relation
	 * @param transformation_translator the parent transformation translator

	 * @param relation the relation being translated
	 * @throws EchoError
	 */
	public EEngineRelation (EEngineTransformation transformation_translator, EDependency dependency, ERelation relation) throws EchoError {
		this (transformation_translator,true,null,dependency,relation);
	}

	/** 
	 * Constructs a new relation to Alloy translator.
	 * Translates a relation (top or non top) to Alloy in a given direction.
	 * @param relation the relation being translated

	 * @param top whether the relation is top or not
	 * @throws EchoError
	 */
	EEngineRelation(EEngineTransformation transformation_translator, Boolean top,
			EEngineRelation parent_translator, EDependency dependency, ERelation relation)
			throws EchoError {
		this.relation = relation;
		this.dependency = dependency;
		this.top = top;
		this.transformation_translator = transformation_translator;
		this.parent_translator = top ? this : parent_translator;

		initVariableLists();

		IExpression field = null;
		if (!top)
			field = addRelationField();

		IFormula fact = calculateFact();
		if (EchoOptionsSetup.getInstance().isOptimize())
			fact = optimize(fact);
		
		if (top)
			addRelationPred(fact);
		else
			addRelationDef(fact, field);

	}

	abstract protected IFormula optimize(IFormula fact) throws ErrorUnsupported;
	
	/** 
	 * Calculates the Alloy constraint denoting the relation top test.
	 * Takes the shape "forall whenvars : when => (forall sourcevars : sourcedomain => (exists targetvars+wherevars : targetdomain && where))"
	 * Must be run after relation field is created (otherwise recursive calls will fail)
	 * @return the Alloy constraint representing the relation
	 * @throws EchoError
	 */
	private IFormula calculateFact() throws EchoError {
		IFormula fact = EchoTranslator.getInstance().getTrueFormula();
		IFormula sourceexpr = EchoTranslator.getInstance().getTrueFormula();
		IFormula postexpr = EchoTranslator.getInstance().getTrueFormula();
		IFormula whenexpr = EchoTranslator.getInstance().getTrueFormula();

		IDecl[] arraydecl;

		EPredicate postCondition = relation.getPost();
		EPredicate targetCondition = targetdomain.getCondition();

		if (postCondition != null)
			postexpr = translateCondition(postCondition);

		IFormula targetexpr = translateCondition(targetCondition);
	
		targetexpr = targetexpr.and(postexpr);

		if (targetvar2alloydecl.size() == 1)
			targetexpr = targetexpr.forSome(targetvar2alloydecl.values().iterator().next());
		else if (targetvar2alloydecl.size() > 1) {
			arraydecl = targetvar2alloydecl.values().toArray(
					new IDecl[targetvar2alloydecl.size()]);
			targetexpr = targetexpr.forSome(arraydecl[0],
					Arrays.copyOfRange(arraydecl, 1, arraydecl.length));
		}

		for (EModelDomain dom : sourcedomains) {
			EPredicate sourceCondition = dom.getCondition();
			IFormula temp = translateCondition(sourceCondition);
			sourceexpr = sourceexpr.and(temp);
		}	
		fact = (sourceexpr.implies(targetexpr));

		if (sourcevar2alloydecl.size() == 1) {
			fact = fact.forAll(sourcevar2alloydecl.values().iterator().next());
		}
		else if (sourcevar2alloydecl.size() > 1) {
			arraydecl = sourcevar2alloydecl.values().toArray(
					new IDecl[sourcevar2alloydecl.size()]);
			fact = fact.forAll(arraydecl[0],
					Arrays.copyOfRange(arraydecl, 1, arraydecl.length));
		}

		EPredicate preCondition = relation.getPre();
		if (preCondition != null) {
			whenexpr = translateCondition(preCondition);

			fact = (whenexpr.implies(fact));
			for (IDecl d : whenvar2alloydecl.values())
				fact = fact.forAll(d);
		}
		
		return fact;
	}	
	
	protected abstract IFormula translateCondition(EPredicate targetCondition) throws EchoError;

	/** 
	 * Initializes the variable lists and generates the respective Alloy declarations.
	 * @throws EchoError
	 * @todo Support fom <code>CollectionTemplateExp</code>
	 */
	private void initVariableLists() throws EchoError {
		for (EModelParameter mdl : relation.getTransformation().getModelParams()) {
			String metamodelID = mdl.getMetamodel().ID;
			IDecl d = createDecl(metamodelID);
			model_params_decls.add(d);
		}
		
		Map<EVariable,String> whenvar2model = new HashMap<EVariable,String>();
		Map<EVariable,String> sourcevar2model = new HashMap<EVariable,String>();
		Map<EVariable,String> targetvar2model = new HashMap<EVariable,String>();
		
		for (EModelDomain dom : relation.getDomains()) {
			rootvariables.put(dom.getRootVariable(),dom.getModel().getName());
			if (dependency.target.equals(dom)) targetdomain = dom;
			else sourcedomains.add(dom);
		}
		
		EPredicate preCondition = relation.getPre();
		if (preCondition != null)
			whenvar2model = preCondition.getVariables(null);

		for (EModelDomain dom : sourcedomains) {
			EPredicate cond = dom.getCondition();
			sourcevar2model.putAll(cond.getVariables(dom.getModel().getName()));
		}

		for (EVariable x : whenvar2model.keySet()) {
			whenvar2model.put(x, sourcevar2model.get(x));
			sourcevar2model.remove(x);
		}
		
		EPredicate temp = targetdomain.getCondition();
		targetvar2model = temp.getVariables(targetdomain.getModel().getName());
		
		EPredicate postCondition = relation.getPost();
		if (postCondition != null)
			for (EVariable x : postCondition.getVariables(null).keySet())
				if (targetvar2model.get(x) == null) targetvar2model.put(x,null);
		
		for (EVariable x : sourcevar2model.keySet()) {
			if (sourcevar2model.get(x) == null) 
				sourcevar2model.put(x, targetvar2model.get(x));
			targetvar2model.remove(x); 
		}
		
		for (EVariable x : whenvar2model.keySet()) {
			if (whenvar2model.get(x) == null) 
				whenvar2model.put(x, targetvar2model.get(x));
			targetvar2model.remove(x); 
		}

		if (!top)
			for (EVariable x : rootvariables.keySet()) {
				whenvar2model.remove(x);
				targetvar2model.remove(x);
				sourcevar2model.remove(x);
			}
	 
		sourcevar2alloydecl = createVarDecls(sourcevar2model,true);
		targetvar2alloydecl = createVarDecls(targetvar2model,true);
	  	whenvar2alloydecl = createVarDecls(whenvar2model,true);
	  	rootvar2alloydecl = createVarDecls(rootvariables,!top);

	}	
	
	protected abstract IDecl createDecl(String metamodelID) throws ErrorInternalEngine;

	protected abstract Map<String, IDecl> createVarDecls(Map<EVariable, String> sourcevar2model, boolean b) throws EchoError;

	/** 
	 * Generates the relation field over the model sigs for called relations
	 * Must be run before the relation constraint is created (otherwise recursive calls will fail)
	 * @return the Alloy field for this relation
	 * @throws EchoError
	 * @todo Support for n models
	 */
	private IExpression addRelationFields() throws EchoError {
		IExpression field = null;
		IDecl fst = rootvar2alloydecl.get(relation.getDomains().get(0)
				.getRootVariable().getName());
		/*Decl snd = rootvar2alloydecl.get(relation.getDomains().get(1)
				.getVariable().getName());*/
		
		field = createField(fst);
		
		return field;
	}

	protected abstract IExpression createField(IDecl fst) throws ErrorInternalEngine;
	
	/**
	 * adds to the transformation translator the constraint defining the field of this sub relation
	 * @param fact the constraint defining this relation
	 * @param field the field representing this sub relation
	 * @throws EchoError
	 */
	private void addRelationDef(IFormula fact, IExpression field) throws EchoError {
		IDecl fst = rootvar2alloydecl.get(relation.getDomains().get(0).getRootVariable().getName());
		IDecl snd = rootvar2alloydecl.get(relation.getDomains().get(1).getRootVariable().getName());
		IFormula e = field.eq(fact.comprehension(fst,snd));
		transformation_translator.addSubRelationDef(this,model_params_decls,e);
	}
	
	
	/** 
	 * adds to the transformation translator the constraint defining this top relation
	 * @param fact the constraint defining this relation
	 * @throws EchoError
	 */
	private void addRelationPred(IFormula fact) throws EchoError {
		transformation_translator.addTopRelationCall(this, model_params_decls, fact);
	}
	
	
	/** 
	 * adds to the transformation translator the field representing this sub relation
	 * @throws EchoError
	 */
	private IExpression addRelationField() throws EchoError {
		IExpression field = addRelationFields();
		transformation_translator.addSubRelationCall(this, model_params_decls, field);
		return field;
	}
	
}
