package pt.uminho.haslab.echo.engine.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

class EAlloyTransformation extends EEngineTransformation {

	/** the Alloy expression rising from this QVT Transformation*/
	private Func func;
	
	/** the Alloy functions defining sub-relation consistency */
	private Map<String,Func> subRelationCallDefs;

	/** the Alloy functions defining sub-relation calls */
	private Map<String,Func> subRelationCallFuncs;
	
	/** the Alloy functions defining top-relation calls */
	private Map<String,Func> topRelationCallFuncs;
	
	/** {@inheritDoc} */
	EAlloyTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
	}

	/** {@inheritDoc} */
	@Override
	protected void createRelation(ERelation rel, EDependency dep) throws EchoError {
		new EAlloyRelation(this,dep,rel);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void createParams(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorAlloy {
		for (EModelParameter mdl : transformation.getModelParams()) {
			Decl d;
			String metamodelID = mdl.getMetamodel().ID;
			try {
				d = AlloyEchoTranslator.getInstance().getMetamodel(metamodelID).sig_metamodel.oneOf(mdl.getName());
			} catch (Err a) { 
				throw new ErrorAlloy(
						ErrorInternalEngine.FAIL_CREATE_VAR,
						"Failed to create transformation model variable: "+mdl.getName(),
						a,Task.TRANSLATE_TRANSFORMATION); 
			}
			model_params_decls.add(new AlloyDecl(d));
			model_params_vars.add(new AlloyExpression(d.get()));
		}		
	}
	
	/** {@inheritDoc} */
	@Override
	protected void generateConstraints(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorAlloy {
		
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);

		Expr[] vars = new Expr[model_params_vars.size()];
		for (int i = 0; i<model_params_vars.size(); i++)
			vars[i] = ((AlloyExpression) model_params_vars.get(i)).EXPR;

		Expr fact = Sig.NONE.no();
		for (Func f : topRelationCallFuncs.values()) {
			fact = fact.and(f.call(vars));
		}
		
		for (Func f : subRelationCallDefs.values()) {
			fact = fact.and(f.call(vars));
		}
		
		try {
			func = new Func(null, transformation.getName(), decls,
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
	protected AlloyFormula getTransformationConstraint(List<IExpression> vars) {
		List<Expr> sigs = new ArrayList<Expr>();
		for (IExpression v : vars)
			sigs.add(((AlloyExpression) v).EXPR);
		return new AlloyFormula(func.call(sigs.toArray(new Expr[sigs.size()])));
	}

	/** {@inheritDoc} */
	@Override
	protected void addSubRelationDef(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IFormula e) throws ErrorAlloy {
		if (subRelationCallDefs == null) subRelationCallDefs = new HashMap<String,Func>();

		Func f;
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			f = new Func(null, EchoHelper.relationFieldName(
					eAlloyRelation.relation, eAlloyRelation.dependency.target)
					+ "def", decls, null, ((AlloyFormula) e).formula);
			subRelationCallDefs.put(f.label, f);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation field constraint: "
							+ eAlloyRelation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addSubRelationCall(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IExpression exp) throws ErrorAlloy {
		
		if (subRelationCallFuncs == null) subRelationCallFuncs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		Field field = (Field) ((AlloyExpression) exp).EXPR;
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			Func x = new Func(null, EchoHelper.relationFieldName(
					eAlloyRelation.relation, eAlloyRelation.dependency.target),
					decls, field.type().toExpr(), field);
			subRelationCallFuncs.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation constraint function: "
							+ eAlloyRelation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected void addTopRelationCall(EEngineRelation arelation,
			List<IDecl> model_params_decls, IFormula fact) throws ErrorAlloy {
		
		if (topRelationCallFuncs == null) topRelationCallFuncs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			Func x = new Func(null, EchoHelper.relationPredName(
					arelation.relation, arelation.dependency.target),
					decls, null, ((AlloyFormula) fact).formula);
			topRelationCallFuncs.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create top relation constraint function: "
							+ arelation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlloyFormula callRelation(ERelation n, ITContext context, List<IExpression> params) {
		if (subRelationCallFuncs == null) return null;
		Func f = subRelationCallFuncs.get(EchoHelper.relationFieldName(n,context.getCurrentRel().dependency.target));
		if (f == null) return null;

		// applies the model parameters to the relation function
		Expr[] vars = new Expr[context.getModelParams().size()];
		for (int i = 0; i<context.getModelParams().size(); i++)
			vars[i] = ((AlloyExpression) context.getModelParams().get(i)).EXPR;
		Expr exp = f.call(vars);
		IExpression expp = new AlloyExpression(exp);
		
		// applies the parameters to the relation function
		IExpression insig = params.get(params.size() - 1);
		params.remove(insig);
		for (IExpression param : params)
			expp = param.join(expp);
		IFormula form = insig.in(expp);
		
		return (AlloyFormula) form;
	}

}
