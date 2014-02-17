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
	
	List<IDecl> modelParamDecls = new ArrayList<IDecl>();
	List<IExpression> modelParamVars = new ArrayList<IExpression>();


	/** {@inheritDoc} */
	@Override
	protected void createRelation(ERelation rel, EDependency dep) throws EchoError {
		new EAlloyRelation(this,dep,rel);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void manageModelParams() throws ErrorAlloy {
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
			modelParamDecls.add(new AlloyDecl(d));
			modelParamVars.add(new AlloyExpression(d.get()));
		}		
	}
	
	/** {@inheritDoc} */
	@Override
	protected void generateConstraints() throws ErrorAlloy {
		
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : modelParamDecls)
			decls.add(((AlloyDecl) d).decl);

		Expr[] vars = new Expr[modelParamVars.size()];
		for (int i = 0; i<modelParamVars.size(); i++)
			vars[i] = ((AlloyExpression) modelParamVars.get(i)).EXPR;

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
	protected void defineSubRelationCall(EEngineRelation relation, IFormula e) throws ErrorAlloy {
		if (subRelationCallDefs == null) subRelationCallDefs = new HashMap<String,Func>();

		Func f;
		List<Decl> decls = new ArrayList<Decl>();
		for (AlloyDecl d : ((EAlloyRelation) relation).getModelParams())
			decls.add(d.decl);
		try {
			f = new Func(null, EchoHelper.relationFieldName(
					relation.relation, relation.dependency.target)
					+ "def", decls, null, ((AlloyFormula) e).formula);
			subRelationCallDefs.put(f.label, f);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation field constraint: "
							+ relation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addSubRelationCall(EEngineRelation relation, IExpression exp) throws ErrorAlloy {
		
		if (subRelationCallFuncs == null) subRelationCallFuncs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		Field field = (Field) ((AlloyExpression) exp).EXPR;
		for (AlloyDecl d : ((EAlloyRelation) relation).getModelParams())
			decls.add(d.decl);
		try {
			Func x = new Func(null, EchoHelper.relationFieldName(
					relation.relation, relation.dependency.target),
					decls, field.type().toExpr(), field);
			subRelationCallFuncs.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create sub relation constraint function: "
							+ relation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected void addTopRelationCall(EEngineRelation relation) throws ErrorAlloy {
		
		if (topRelationCallFuncs == null) topRelationCallFuncs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		for (AlloyDecl d : ((EAlloyRelation) relation).getModelParams())
			decls.add(d.decl);
		try {
			Func x = new Func(null, EchoHelper.relationPredName(
					relation.relation, relation.dependency.target),
					decls, null, ((AlloyFormula) relation.constraint).formula);
			topRelationCallFuncs.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create top relation constraint function: "
							+ relation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlloyFormula callRelation(ERelation n, ITContext context, List<IExpression> params) {
		if (subRelationCallFuncs == null) return null;
		Func f = subRelationCallFuncs.get(EchoHelper.relationFieldName(n,context.getCallerRel().dependency.target));
		if (f == null) return null;

		// applies the model parameters to the relation function
		Expr[] vars = new Expr[context.getModelExpressions().size()];
		for (int i = 0; i<context.getModelExpressions().size(); i++)
			vars[i] = ((AlloyExpression) context.getModelExpressions().get(i)).EXPR;
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
