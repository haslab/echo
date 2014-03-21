package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;

import java.util.*;

import org.eclipse.ocl.examples.pivot.OCLExpression;

/**
 * An embedding of a model transformation relation in an abstract Echo engine.
 *
 * TODO: generalize to n-ary dependencies (as opposed to single target)
 * 
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class EEngineRelation {
	
	public final ITContext context;

	/** the relation being transformed */
	public final ERelation relation;
	
	/** the direction of the test being generated */
	public final EDependency dependency;

	/** the parent embedded transformation */
	public final EEngineTransformation transformation;

	/** whether the relation is being called at top level or not
	 * this is NOT the same as being a top relation */
	public final Mode mode;
	
	/** the parent (calling) relation, null if top */
	public final EEngineRelation callerRelation;
	
	/** the engine declarations of the root variables
	 * null if top call */
	protected Map<String, IDecl> rootVar2engineDecl = new LinkedHashMap<>();
	protected Map<String, IDecl> rootVarS2engineDecl = new LinkedHashMap<>();
	protected Map<String, IDecl> rootVarT2engineDecl = new LinkedHashMap<>();

	/** the root variables of the relation being translated*/
	private Map<EVariable,String> rootvariables = new LinkedHashMap<>();
	private Map<EVariable,String> rootvariablesS = new LinkedHashMap<>();
	private Map<EVariable,String> rootvariablesT = new LinkedHashMap<>();

	/** the target relation domain */
	private EModelDomain targetdomain;
	
	/** the source relation domains */
	private List<EModelDomain> sourcedomains = new ArrayList<>();

	/** the engine declarations of the variables occurring in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> whenVar2engineDecl = new LinkedHashMap<>();
	
	/** the engine declarations of the variables occurring in the source domain but not in the when constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> sourceVar2engineDecl = new LinkedHashMap<>();
	
	/** the engine declarations of the variables occurring in the target domain and where constraint but not in the source domains and the when constraint constraint
	 * if non-top relation, does not contain root variables*/
	private Map<String,IDecl> targetVar2engineDecl = new LinkedHashMap<>();

	public final IFormula constraint;

    protected IFormula extraRelConstraint;
	
	/** 
	 * Embeds a non-top relation into the engine representation.
	 * @param relation the relation to be translated
	 * @param parentRelation the already embedded parent relation
	 * @throws EchoError
	 */
	public EEngineRelation (ERelation relation, EEngineRelation parentRelation) throws EchoError {
		this (relation, Mode.SUB_TRACEABILITY, parentRelation.dependency, parentRelation,parentRelation.transformation);
	}

	/**
	 * Embeds a top relation into the engine representation.
	 * @param relation the relation being translated
	 * @param dependency the dependency (direction) of the relation
	 * @param transformation the already embedded parent transformation
	 * @throws EchoError
	 */
	public EEngineRelation (ERelation relation, EDependency dependency, EEngineTransformation transformation, boolean trace) throws EchoError {
		this (relation,trace?(relation.isTop()?Mode.TOP_TRACEABILITY:Mode.SUB_TRACEABILITY):Mode.TOP_QUANTIFIER,dependency,null,transformation);
	}

	/** 
	 * Embeds a relation into the engine representation.
	 * Translates a relation (top or non top) to engine representation in a given direction.
	 * Top relations are represented by a predicate.
	 * Non-top relations are represented by an relation and an associated definition.
	 * @param relation the relation being translated
	 * @param mode whether the relation is top or not
	 * @param dependency the dependency (direction) of the relation
	 * @param parentRelation the already embedded parent relation
	 * @param transformation the already embedded parent transformation
	 * @throws EchoError
	 */
	private EEngineRelation(ERelation relation, Mode mode,
			EDependency dependency, EEngineRelation parentRelation, EEngineTransformation transformation)
			throws EchoError {
		this.relation = relation;
		this.dependency = dependency;
		this.mode = mode;
		this.transformation = transformation;
		this.callerRelation = mode.isTop() ? this : parentRelation;
		this.context = EchoTranslator.getInstance().newContext();
		this.context.setCurrentRel(callerRelation);

		initVariableLists();
		
		IExpression sub = Constants.EMPTY();
        extraRelConstraint = Constants.TRUE();
		// must be created before calculating the constraint, as it may be recursively called
		if (!(mode == Mode.TOP_QUANTIFIER)) sub = addRelationField();
		
		IFormula temp = calculateConstraint();
		if (EchoOptionsSetup.getInstance().isOptimize())
			constraint = simplify(extraRelConstraint.and(temp));
		else 
			constraint = extraRelConstraint.and(temp);
		
		if (mode.isTop()) addRelationConstraint();
		else addRelationDef(sub);
		
	}

	/** 
	 * Initializes the variable lists and generates the respective engine declarations.
	 * TODO: assumes single target.
	 * @throws EchoError
	 * @todo Support for <code>CollectionTemplateExp</code>
	 */
	private void initVariableLists() throws EchoError {
		manageModelParams();
		
		// retrieve the variables occurring in the predicates and assign them owning domains (if possible)
		Map<EVariable,String> preVar2model = new LinkedHashMap<EVariable,String>();
		Map<EVariable,String> sourceVar2model = new LinkedHashMap<EVariable,String>();
		Map<EVariable,String> targetVar2model = new LinkedHashMap<EVariable,String>();
		
		for (EModelDomain dom : relation.getDomains()) {
			rootvariables.put(dom.getRootVariable(),dom.getModel().getName());
			if (dependency.target.equals(dom)) {
				rootvariablesT.put(dom.getRootVariable(),dom.getModel().getName());
				targetdomain = dom;
			}
			else {
				rootvariablesS.put(dom.getRootVariable(),dom.getModel().getName());
				sourcedomains.add(dom);
			}
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
		for (EVariable x : rootvariables.keySet()) {
			if (!mode.quantifyRoots()) {
				preVar2model.remove(x);
				targetVar2model.remove(x);
				sourceVar2model.remove(x);
			} else {
				if (preVar2model.keySet().contains(x) && preVar2model.get(x) == null) 
					preVar2model.put(x, rootvariables.get(x));
				if (targetVar2model.keySet().contains(x) && targetVar2model.get(x) == null) 
					targetVar2model.put(x, rootvariables.get(x));
				if (sourceVar2model.keySet().contains(x) && sourceVar2model.get(x) == null) 
					sourceVar2model.put(x, rootvariables.get(x));
			}
		}
	
//		EchoReporter.getInstance().debug("rootvarsS: "+rootvariablesS);
//		EchoReporter.getInstance().debug("rootvarsT: "+rootvariablesT);
//		EchoReporter.getInstance().debug("srcvars: "+sourceVar2model);
//		EchoReporter.getInstance().debug("trgvars: "+targetVar2model);
//		EchoReporter.getInstance().debug("prevars: "+preVar2model);
		
		// embed retrieved variable in engine representation
		sourceVar2engineDecl = createVarDecls(sourceVar2model,true);
		targetVar2engineDecl = createVarDecls(targetVar2model,true);
	  	whenVar2engineDecl = createVarDecls(preVar2model,true);
	  	// if non-top, root variables can't be added to the context
	  	rootVar2engineDecl = createVarDecls(rootvariables,!mode.quantifyRoots());
	  	for (EVariable v : rootvariablesS.keySet())
	  		rootVarS2engineDecl.put(v.getName(), rootVar2engineDecl.get(v.getName()));
	  	for (EVariable v : rootvariablesT.keySet())
	  		rootVarT2engineDecl.put(v.getName(), rootVar2engineDecl.get(v.getName()));
	
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

		// targetFormula := exists targetVars : targetPred & postPred
		EPredicate postPred = relation.getPost();
		if (postPred != null)
			postFormula = translateCondition(postPred);

		IFormula targetFormula = translateCondition(targetdomain.getCondition());
		targetFormula = targetFormula.and(postFormula);
		
		if (mode == Mode.TOP_TRACEABILITY) {
			// targetFormula := exists targetVars : targetPred & postPred & (srcRoot,trgRoot) in field
			List<IExpression> params = new ArrayList<>();
			for (IDecl d : rootVar2engineDecl.values())
				params.add(d.variable());
			targetFormula = targetFormula.and((IFormula) transformation.callRelation(relation, context, params));
		}

		if (targetVar2engineDecl.size() == 1)
			targetFormula = targetFormula.forSome(targetVar2engineDecl.values().iterator().next());
		else if (targetVar2engineDecl.size() > 1) {
			IDecl[] tempDecls = targetVar2engineDecl.values().toArray(
					new IDecl[targetVar2engineDecl.size()]);
			targetFormula = targetFormula.forSome(tempDecls[0],
					Arrays.copyOfRange(tempDecls, 1, tempDecls.length));
		}
		
		if (mode == Mode.TOP_TRACEABILITY) {
			// targetFormula := one rgtRoot : exists targetVars : targetPred & postPred & (srcRoot,trgRoot) in field
			for (IDecl d : rootVarT2engineDecl.values())
				targetFormula = targetFormula.forOne(d);
		}

		// sourceFormula := forall sourceVars : sourcePreds => targetFormula
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
		
		// formula := forall preVars : prePred => sourceFormula
		EPredicate preCondition = relation.getPre();
		if (preCondition != null) {
			whenFormula = translateCondition(preCondition);
			formula = (whenFormula.implies(formula));
			for (IDecl d : whenVar2engineDecl.values())
				formula = formula.forAll(d);
		}
		
		if (mode == Mode.TOP_TRACEABILITY) {
			// formula := forall srcRoot : forall preVars : prePred => sourceFormula
			for (IDecl d : rootVarS2engineDecl.values())
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
	private IFormula translateCondition(EPredicate predicate) throws EchoError {
		return predicate.translate(context);
	}
	/**
	 * Initializes anything related with the relation model parameters, if needed.
	 * @throws ErrorInternalEngine
	 * @throws ErrorParser 
	 * @throws ErrorUnsupported 
	 * @throws EchoError 
	 */
	protected abstract void manageModelParams() throws ErrorInternalEngine, ErrorUnsupported, ErrorParser, EchoError;

	/**
	 * Creates engine variable declarations from a set of EMF variables with an associated model.
	 * @param var2model the variables to translate associated with a model
	 * @param addContext if the variables should be added to the transformation context
	 * @return the new declarations
	 * @throws EchoError
	 */
	protected Map<String, IDecl> createVarDecls(
			Map<EVariable, String> var2model, boolean addContext)
			throws EchoError {

		if (addContext)
			for (EVariable s : var2model.keySet())
				context.setVarModel(s.getName(), var2model.get(s));

		Map<String, IDecl> ivars = new LinkedHashMap<>();
		for (EVariable var : var2model.keySet())
			ivars.put(var.getName(), context.getDecl(var, addContext));

		EchoReporter.getInstance().debug("Created vars: "+ivars);
		return ivars;
	}

	/** 
	 * Generates the relation field over the type of root variables.
	 * Must be run before the relation constraint is created (otherwise recursive calls will fail)
	 *
     * @param rootVars the root variable declarations
     * @return the field for this relation's sub-calls
	 * @throws EchoError
	 * TODO: Support for n models
	 */
	protected abstract IExpression addNonTopRel(List<? extends EModelDomain> rootVars) throws EchoError;

	/**
	 * Adds to the parent transformation the constraint defining the non-top call.
	 * @param field the field representing this sub relation
	 * @throws EchoError
	 * TODO: Support for n models
	 */
	private void addRelationDef(IExpression field) throws EchoError {
		if (relation.getDomains().size() > 2) throw new ErrorUnsupported("Calls between more than 2 models not yet supported.");
		IDecl fst = rootVar2engineDecl.get(relation.getDomains().get(0).getRootVariable().getName());
		IDecl snd = rootVar2engineDecl.get(relation.getDomains().get(1).getRootVariable().getName());
		EchoReporter.getInstance().debug(relation.getName() + " defined by comprehension.");
		IFormula e = field.eq(constraint.comprehension(fst,snd));
		transformation.defineSubRelationField(this,e);
	}
	
	/** 
	 * Adds to the parent transformation the constraint representing this top call.
	 * @throws EchoError
	 */
	private void addRelationConstraint() throws EchoError {
		transformation.addTopRelationConstraint(this);
	}
	
	/** 
	 * Adds to the parent transformation the field representing this non-top call.
	 * @throws EchoError
	 */
	private IExpression addRelationField() throws EchoError {
		if (relation.getDomains().size() > 2)
			throw new ErrorUnsupported(
					"Calls between more than 2 models not yet supported.");

		IExpression field = addNonTopRel(relation.getDomains());
		transformation.addSubRelationField(this, field);
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

	public abstract void newRelation(ERelation rel) throws EchoError;
	
	public enum Mode {
		TOP_QUANTIFIER,
		TOP_TRACEABILITY,
		SUB_TRACEABILITY;
		
		boolean isTop() {
			return (this == TOP_QUANTIFIER || this == TOP_TRACEABILITY);
		}

		boolean quantifyRoots() {
			return (this == TOP_QUANTIFIER);
		}
	}
	
}
