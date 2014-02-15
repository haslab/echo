package pt.uminho.haslab.echo.engine.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.alloy.AlloyContext;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

/**
 * An embedding of a model transformation relation in an abstract Echo engine.
 *
 * TODO: generalize to n-ary dependencies (as opposed to single target)
 * 
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class EEngineRelation {
	
	public final AlloyContext context;

	/** the relation being transformed */
	public final ERelation relation;
	
	/** the direction of the test being generated */
	public final EDependency dependency;

	/** the parent embedded transformation */
	public final EEngineTransformation transformation_translator;

	/** whether the relation is being called at top level or not
	 * this is NOT the same as being a top relation */
	public final boolean top;
	
	/** the parent (calling) relation, null if top */
	public final EEngineRelation parent_translator;
	
	/** the transformation model parameters declarations */
	private List<IDecl> modelParamsDecls = new ArrayList<IDecl>();

	/** the engine declarations of the root variables
	 * null if top call */
	private Map<String, IDecl> rootVar2engineDecl = new HashMap<String,IDecl>();

	/** the root variables of the relation being translated*/
	private Map<EVariable,String> rootvariables = new HashMap<EVariable,String>();

	/** the target relation domain */
	private EModelDomain targetdomain;
	
	/** the source relation domains */
	private List<EModelDomain> sourcedomains = new ArrayList<EModelDomain>();

	/** the engine declarations of the variables occurring in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> whenVar2engineDecl = new HashMap<String,IDecl>();
	
	/** the engine declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> sourceVar2engineDecl = new HashMap<String,IDecl>();
	
	/** the engine declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> targetVar2engineDecl = new HashMap<String,IDecl>();

	
	/** 
	 * Embeds a non-top relation into the engine representation.
	 * @param relation the relation to be translated
	 * @param parentRelation the already embedded parent relation
	 * @throws EchoError
	 */
	public EEngineRelation (ERelation relation, EEngineRelation parentRelation) throws EchoError {
		this (relation, false, parentRelation.dependency, parentRelation,parentRelation.transformation_translator);
	}

	/**
	 * Embeds a top relation into the engine representation.
	 * @param relation the relation being translated
	 * @param dependency the dependency (direction) of the relation
	 * @param transformation the already embedded parent transformation
	 * @throws EchoError
	 */
	public EEngineRelation (ERelation relation, EDependency dependency, EEngineTransformation transformation) throws EchoError {
		this (relation,true,dependency,null,transformation);
	}

	/** 
	 * Embeds a relation into the engine representation.
	 * Translates a relation (top or non top) to engine representation in a given direction.
	 * Top relations are represented by a predicate.
	 * Non-top relations are represented by an relation and an associated definition.
	 * @param relation the relation being translated
	 * @param top whether the relation is top or not
	 * @param dependency the dependency (direction) of the relation
	 * @param parentRelation the already embedded parent relation
	 * @param transformation the already embedded parent transformation
	 * @throws EchoError
	 */
	private EEngineRelation(ERelation relation, Boolean top,
			EDependency dependency, EEngineRelation parentRelation, EEngineTransformation transformation)
			throws EchoError {
		this.relation = relation;
		this.dependency = dependency;
		this.top = top;
		this.transformation_translator = transformation;
		this.parent_translator = top ? this : parentRelation;
		this.context = new AlloyContext();
		this.context.setCurrentRel(parentRelation);
		
		initVariableLists();

		IExpression field = null;
		// must be created before calculating the constraint, as it may be recursively called
		if (!top) field = addRelationField();

		IFormula constraint = calculateConstraint();
		if (EchoOptionsSetup.getInstance().isOptimize())
			constraint = simplify(constraint);
		
		if (top) addRelationConstraint(constraint);
		else addRelationDef(constraint, field);

	}

	/** 
	 * Initializes the variable lists and generates the respective engine declarations.
	 * TODO: assumes single target.
	 * @throws EchoError
	 * @todo Support fom <code>CollectionTemplateExp</code>
	 */
	private void initVariableLists() throws EchoError {
		// creates declarations (variables) for the relation model parameters
		for (EModelParameter mdl : relation.getTransformation().getModelParams()) {
			IDecl d = createDecl(mdl);
			modelParamsDecls.add(d);
		}
		
		// retrieve the variables occurring in the predicates and assign them owning domains (if possible)
		Map<EVariable,String> preVar2model = new HashMap<EVariable,String>();
		Map<EVariable,String> sourceVar2model = new HashMap<EVariable,String>();
		Map<EVariable,String> targetVar2model = new HashMap<EVariable,String>();
		
		for (EModelDomain dom : relation.getDomains()) {
			rootvariables.put(dom.getRootVariable(),dom.getModel().getName());
			if (dependency.target.equals(dom)) targetdomain = dom;
			else sourcedomains.add(dom);
		}
		
		EPredicate prePred = relation.getPre();
		if (prePred != null)
			preVar2model = prePred.getVariables(null);
	
		for (EModelDomain dom : sourcedomains) {
			EPredicate cond = dom.getCondition();
			sourceVar2model.putAll(cond.getVariables(dom.getModel().getName()));
		}
	
		// if a pre-var occurs in a source, get its domain and remove it
		for (EVariable x : preVar2model.keySet()) {
			preVar2model.put(x, sourceVar2model.get(x));
			sourceVar2model.remove(x);
		}
		
		EPredicate targetPred = targetdomain.getCondition();
		targetVar2model = targetPred.getVariables(targetdomain.getModel().getName());
		
		EPredicate postPred = relation.getPost();
		// add post-only-vars to the target vars
		if (postPred != null)
			for (EVariable x : postPred.getVariables(null).keySet())
				if (targetVar2model.get(x) == null) targetVar2model.put(x,null);
		
		// if a source-var occurs in a target, get its domain and remove it
		for (EVariable x : sourceVar2model.keySet()) {
			if (sourceVar2model.get(x) == null) 
				sourceVar2model.put(x, targetVar2model.get(x));
			targetVar2model.remove(x); 
		}
		
		// if a pre-var occurs in a target, get its domain and remove it
		for (EVariable x : preVar2model.keySet()) {
			if (preVar2model.get(x) == null) 
				preVar2model.put(x, targetVar2model.get(x));
			targetVar2model.remove(x); 
		}
	
		// if non-top call, root variables are discarded
		if (!top)
			for (EVariable x : rootvariables.keySet()) {
				preVar2model.remove(x);
				targetVar2model.remove(x);
				sourceVar2model.remove(x);
			}
	
		// embed retrieved variable in engine representation
		sourceVar2engineDecl = createVarDecls(sourceVar2model,true);
		targetVar2engineDecl = createVarDecls(targetVar2model,true);
	  	whenVar2engineDecl = createVarDecls(preVar2model,true);
	  	rootVar2engineDecl = createVarDecls(rootvariables,!top);
	
	}

	/** 
	 * Calculates the constraint denoting the relation top predicate.
	 * Takes the shape "forall whenvars : when => (forall sourcevars : sourcedomain => (exists targetvars+wherevars : targetdomain && where))"
	 * Must be run after relation field is created (otherwise recursive calls will fail)
	 * @return the constraint representing the relation top predicate
	 * @throws EchoError
	 */
	private IFormula calculateConstraint() throws EchoError {
		IFormula formula = EchoTranslator.getInstance().getTrueFormula();
		IFormula sourceFormula = EchoTranslator.getInstance().getTrueFormula();
		IFormula postFormula = EchoTranslator.getInstance().getTrueFormula();
		IFormula whenFormula = EchoTranslator.getInstance().getTrueFormula();

		// exists targetVars : targetPred & postPred
		EPredicate postPred = relation.getPost();
		if (postPred != null)
			postFormula = translateCondition(postPred);

		IFormula targetFormula = translateCondition(targetdomain.getCondition());
		targetFormula = targetFormula.and(postFormula);

		if (targetVar2engineDecl.size() == 1)
			targetFormula = targetFormula.forSome(targetVar2engineDecl.values().iterator().next());
		else if (targetVar2engineDecl.size() > 1) {
			IDecl[] tempDecls = targetVar2engineDecl.values().toArray(
					new IDecl[targetVar2engineDecl.size()]);
			targetFormula = targetFormula.forSome(tempDecls[0],
					Arrays.copyOfRange(tempDecls, 1, tempDecls.length));
		}

		// forall sourceVars : sourcePreds => targetFormula
		for (EModelDomain dom : sourcedomains) {
			EPredicate sourceCondition = dom.getCondition();
			IFormula temp = translateCondition(sourceCondition);
			sourceFormula = sourceFormula.and(temp);
		}	
		formula = (sourceFormula.implies(targetFormula));

		if (sourceVar2engineDecl.size() == 1) {
			formula = formula.forAll(sourceVar2engineDecl.values().iterator().next());
		}
		else if (sourceVar2engineDecl.size() > 1) {
			IDecl[] tempDecls = sourceVar2engineDecl.values().toArray(
					new IDecl[sourceVar2engineDecl.size()]);
			formula = formula.forAll(tempDecls[0],
					Arrays.copyOfRange(tempDecls, 1, tempDecls.length));
		}
		
		// forall preVars : prePred => sourceFormula
		EPredicate preCondition = relation.getPre();
		if (preCondition != null) {
			whenFormula = translateCondition(preCondition);
			formula = (whenFormula.implies(formula));
			for (IDecl d : whenVar2engineDecl.values())
				formula = formula.forAll(d);
		}
		
		return formula;
	}	
	
	/**
	 * Translates a predicate to an engine formula.
	 * @param predicate the predicate to be translated
	 * @return the engine embedding
	 * @throws EchoError
	 */
	private IFormula translateCondition(EPredicate pred) throws EchoError {
		return pred.translate(context);
	}
	/**
	 * Creates an engine declaration
	 * @param mdl the metamodel ID of the model parameter
	 * @return the model parameter declaration
	 * @throws ErrorInternalEngine
	 */
	protected abstract IDecl createDecl(EModelParameter mdl) throws ErrorInternalEngine;

	/**
	 * 
	 * @param sourcevar2model
	 * @param b
	 * @return
	 * @throws EchoError
	 */
	protected abstract Map<String, IDecl> createVarDecls(Map<EVariable, String> sourcevar2model, boolean b) throws EchoError;

	/**
	 * Creates the relation representing a non-top relation call.
	 * @param fst
	 * @return the relation representing the non-top call
	 * @throws ErrorInternalEngine
	 */
	protected abstract IExpression createNonTopRel(IDecl fst) throws ErrorInternalEngine;

	/** 
	 * Generates the relation field over the model sigs for called relations
	 * Must be run before the relation constraint is created (otherwise recursive calls will fail)
	 * @return the field for this relation
	 * @throws EchoError
	 * @todo Support for n models
	 */
	private IExpression addNonTopRel() throws EchoError {
		IExpression field = null;
		IDecl fst = rootVar2engineDecl.get(relation.getDomains().get(0)
				.getRootVariable().getName());
		/*Decl snd = rootVar2engineDecl.get(relation.getDomains().get(1)
				.getVariable().getName());*/
		
		field = createNonTopRel(fst);
		
		return field;
	}

	/**
	 * Adds to the parent transformation the constraint defining the non-top call
	 * @param fact the constraint defining this relation
	 * @param field the field representing this sub relation
	 * @throws EchoError
	 */
	private void addRelationDef(IFormula fact, IExpression field) throws EchoError {
		IDecl fst = rootVar2engineDecl.get(relation.getDomains().get(0).getRootVariable().getName());
		IDecl snd = rootVar2engineDecl.get(relation.getDomains().get(1).getRootVariable().getName());
		IFormula e = field.eq(fact.comprehension(fst,snd));
		transformation_translator.addSubRelationDef(this,modelParamsDecls,e);
	}
	
	
	/** 
	 * Adds to the parent transformation the constraint representing this top call
	 * @param fact the constraint defining this relation
	 * @throws EchoError
	 */
	private void addRelationConstraint(IFormula fact) throws EchoError {
		transformation_translator.addTopRelationCall(this, modelParamsDecls, fact);
	}
	
	
	/** 
	 * Adds to the parent transformation the field representing this non-top call
	 * @throws EchoError
	 */
	private IExpression addRelationField() throws EchoError {
		IExpression field = addNonTopRel();
		transformation_translator.addSubRelationCall(this, modelParamsDecls, field);
		return field;
	}

	/**
	 * Simplifies a formula.
	 * TODO: should be an EchoEngineTranslator method.
	 * @param formula the formula to be simplified
	 * @return the simplified formula
	 * @throws ErrorUnsupported
	 */
	abstract protected IFormula simplify(IFormula formula) throws ErrorUnsupported;

	public abstract void newRelation(EQVTRelation rel) throws EchoError;
}
