package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.EchoTranslator;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation.Mode;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.INode;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedding of a model transformation in Alloy.
 * 
 * @author nmm
 * @version 0.4 17/02/2014
 */
class EAlloyTransformation extends EEngineTransformation {

	/** the Alloy function denoting the constraint of this Transformation */
	private Func func;

	/** the Alloy functions defining sub-relation constraint fields */
	private Map<String, Func> subRelationDefs;

	/** the Alloy functions denoting sub-relation calls */
	private Map<String, Func> subRelationFields;

	/** the Alloy functions defining top-relation constraints */
	private Map<String, Func> topRelationConstraints;
	
	private Field trace;

	/**
	 * the Alloy declarations denoting the transformation parameters used in the
	 * above defined functions
	 */
	private List<Decl> modelParamDecls = new ArrayList<Decl>();

	/** {@inheritDoc} */
	EAlloyTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
	}

	/** {@inheritDoc} */
	@Override
	protected void createRelation(ERelation rel, EDependency dep, boolean trace)
			throws EchoError {
		new EAlloyRelation(this, dep, rel, trace);
	}

	/** {@inheritDoc} */
	@Override
	protected void initModelParams() throws ErrorAlloy {
		// required because super class calls from constructor
		if (modelParamDecls == null) modelParamDecls = new ArrayList<Decl>();
		// Creates model parameters variables to be used as constraint
		// parameters
		for (EModelParameter mdl : transformation.getModelParams()) {
			Decl d;
			String metamodelID = mdl.getMetamodel().ID;
			try {
				d = AlloyEchoTranslator.getInstance().getMetamodel(metamodelID).SIG
						.oneOf(mdl.getName());
			} catch (Err a) {
				throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_VAR,
						"Failed to create transformation model variable: "
								+ mdl.getName(), a,
						Task.TRANSLATE_TRANSFORMATION);
			}
			modelParamDecls.add(d);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void processConstraint() throws ErrorAlloy {
		Expr[] vars = new Expr[modelParamDecls.size()];
		for (int i = 0; i < modelParamDecls.size(); i++)
			vars[i] = modelParamDecls.get(i).get();

		// calls top relations with transformation parameters
		Expr fact = Sig.NONE.no();
		if (topRelationConstraints != null)
			for (Func f : topRelationConstraints.values())
				fact = fact.and(f.call(vars));

		// calls sub relation fields definitions with transformation parameters
		if (subRelationDefs != null)
			for (Func f : subRelationDefs.values())
				fact = fact.and(f.call(vars));
		
		if (subRelationFields != null && trace != null) {
			Expr aux = Sig.NONE.product(Sig.NONE);
			for (Func f : subRelationFields.values())
				aux = aux.plus(f.call(vars));
			fact = fact.and((vars[1].join(vars[0].join(trace))).equal(aux));
		}

		EchoReporter.getInstance().debug("Transformation: "+fact);

		// creates functions with transformation parameters
		try {
			func = new Func(null, transformation.getName(), modelParamDecls,
					null, fact);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create transformation function: "
							+ transformation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected AlloyFormula getConstraint(List<String> modelIDs) {
		// calls constraint function with the model's state sig
		List<Expr> sigs = new ArrayList<Expr>();
		for (String modelID : modelIDs) {
			EAlloyModel mdl = (EAlloyModel) EchoTranslator.getInstance().getModel(modelID);
			sigs.add(mdl.getModelSig());
		}
		return new AlloyFormula(func.call(sigs.toArray(new Expr[sigs.size()])));
	}

	/** {@inheritDoc} */
	@Override
	protected void defineSubRelationField(EEngineRelation rel, IFormula def)
			throws ErrorAlloy {
		if (subRelationDefs == null)
			subRelationDefs = new HashMap<String, Func>();

		// creates a function that defines the sub-relation field with
		// appropriate model parameters
		List<Decl> decls = new ArrayList<Decl>();
		for (AlloyDecl d : ((EAlloyRelation) rel).getModelParams())
			decls.add(d.DECL);
		try {
			Func f = new Func(null, EchoHelper.relationFieldName(rel.relation,
					rel.dependency.target) + "def", decls, null,
					((AlloyFormula) def).FORMULA);
			subRelationDefs.put(f.label, f);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation field constraint: "
							+ rel.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void addSubRelationField(EEngineRelation relation, IExpression exp)
			throws ErrorAlloy {
		if (subRelationFields == null)
			subRelationFields = new HashMap<String, Func>();

		if (relation.mode == Mode.TOP_TRACEABILITY) {
			String op = EchoHelper.relationFieldName(relation.relation, relation.dependency.getSources().get(0));
			if (subRelationFields.get(op) != null) {
				subRelationFields.put(EchoHelper.relationFieldName(
						relation.relation, relation.dependency.target), subRelationFields.get(op));
				return;
			}
		}
		
		// creates a function that calls the sub-relation field with appropriate
		// model parameters
		List<Decl> decls = new ArrayList<Decl>();
		Field field = (Field) ((AlloyExpression) exp).EXPR;
		for (AlloyDecl d : ((EAlloyRelation) relation).getModelParams())
			decls.add(d.DECL);
		try {
			Func x = new Func(null, EchoHelper.relationFieldName(
					relation.relation, relation.dependency.target), decls,
					field.type().toExpr(), field);
			subRelationFields.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation constraint function: "
							+ relation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void addTopRelationConstraint(EEngineRelation relation)
			throws ErrorAlloy {
		if (topRelationConstraints == null)
			topRelationConstraints = new HashMap<String, Func>();

		// creates a function that calls tests the top relation with appropriate
		// model parameters
		List<Decl> decls = new ArrayList<Decl>();
		for (AlloyDecl d : ((EAlloyRelation) relation).getModelParams())
			decls.add(d.DECL);
		try {
			Func x = new Func(null, EchoHelper.relationPredName(
					relation.relation, relation.dependency.target), decls,
					null, ((AlloyFormula) relation.constraint).FORMULA);
			topRelationConstraints.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create top relation constraint function: "
							+ relation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public INode callRelation(ERelation n, ITContext context,
			List<IExpression> params) {
		if (subRelationFields == null) return null;
		
		// applies the model parameters to the relation function
		Expr[] vars = new Expr[context.getModelExpressions().size()];
		for (int i = 0; i < context.getModelExpressions().size(); i++)
			vars[i] = ((AlloyExpression) context.getModelExpressions().get(i)).EXPR;
		
		// retrieves the relation field 		
		Func f = subRelationFields.get(EchoHelper.relationFieldName(n,
				context.getCallerRel().dependency.target));
		if (f == null) return null;

		Expr exp = f.call(vars);
		
		IExpression expp = new AlloyExpression(exp);
		INode form = null;
		// applies the relation parameters to the relation function
		if (params.size() == 1) {
			form = params.get(0).join(expp);
		}
		else {
			IExpression insig = params.get(params.size() - 1);
			params.remove(insig);
			for (IExpression param : params)
				expp = param.join(expp);
			form = insig.in(expp);
		}
		return form;
	}

	@Override
	public IExpression callAllRelation(ITContext context, IExpression param) throws ErrorAlloy, ErrorUnsupported {

		if (trace == null) addTrace();

		Expr[] vars = new Expr[context.getModelExpressions().size()];
		for (int i = 0; i < context.getModelExpressions().size(); i++)
			vars[i] = ((AlloyExpression) context.getModelExpressions().get(i)).EXPR;

		Expr exp = vars[1].join(vars[0].join(trace));
		
		IExpression expp = new AlloyExpression(exp);

		return param.join(expp);
	}
	
	protected void addTrace() throws ErrorAlloy, ErrorUnsupported {
		if (modelParamDecls.size() > 2) throw new ErrorUnsupported("Traces between more than 2 models not yet supported.");
		try {
			Sig s0 = (Sig) (modelParamDecls.get(0).expr.type().toExpr());
			Sig s1 = (Sig) (modelParamDecls.get(1).expr.type().toExpr());
			trace = s0.addField("trace", s1.product(Sig.UNIV.product(Sig.UNIV)));
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "
							+"trace", a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}


}
