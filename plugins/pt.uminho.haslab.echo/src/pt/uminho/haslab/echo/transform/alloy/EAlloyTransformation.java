package pt.uminho.haslab.echo.transform.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.transform.EEngineRelation;
import pt.uminho.haslab.echo.transform.EEngineTransformation;
import pt.uminho.haslab.echo.transform.EchoHelper;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;
import pt.uminho.haslab.echo.transform.ast.IFormula;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

class EAlloyTransformation extends EEngineTransformation {

	/** the Alloy expression rising from this QVT Transformation*/
	private Func func;
	
	/** the Alloy functions defining sub-relation consistency */
	private Map<String,Func> subrelationcall_defs;

	/** the Alloy functions defining sub-relation calls */
	private Map<String,Func> subrelationcall_funcs;
	
	/** the Alloy functions defining top-relation calls */
	private Map<String,Func> toprelationcall_funcs;
	
	/** {@inheritDoc} */
	EAlloyTransformation(ETransformation transformation,
			Map<String, List<EDependency>> dependencies) throws EchoError {
		super(transformation, dependencies);
	}

	/** {@inheritDoc} */
	@Override
	protected void createRelation(EDependency dep, ERelation rel) throws EchoError {
		new EAlloyRelation(this,dep,rel);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void createParams(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorAlloy {
		for (EModelParameter mdl : transformation.getModels()) {
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
		for (Func f : toprelationcall_funcs.values()) {
			fact = fact.and(f.call(vars));
		}
		
		for (Func f : subrelationcall_defs.values()) {
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
		if (subrelationcall_defs == null) subrelationcall_defs = new HashMap<String,Func>();

		Func f;
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			f = new Func(null, EchoHelper.relationFieldName(
					eAlloyRelation.relation, eAlloyRelation.dependency.target)
					+ "def", decls, null, ((AlloyFormula) e).formula);
			subrelationcall_defs.put(f.label, f);
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
		
		if (subrelationcall_funcs == null) subrelationcall_funcs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		Field field = (Field) ((AlloyExpression) exp).EXPR;
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			Func x = new Func(null, EchoHelper.relationFieldName(
					eAlloyRelation.relation, eAlloyRelation.dependency.target),
					decls, field.type().toExpr(), field);
			subrelationcall_funcs.put(x.label, x);
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
		
		if (toprelationcall_funcs == null) toprelationcall_funcs = new HashMap<String,Func>();
		
		List<Decl> decls = new ArrayList<Decl>();
		for (IDecl d : model_params_decls)
			decls.add(((AlloyDecl) d).decl);
		try {
			Func x = new Func(null, EchoHelper.relationPredName(
					arelation.relation, arelation.dependency.target),
					decls, null, ((AlloyFormula) fact).formula);
			toprelationcall_funcs.put(x.label, x);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FUNC,
					"Failed to create top relation constraint function: "
							+ arelation.relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlloyFormula callRelation(ERelation n, EDependency dep, List<IExpression> aux) {
		if (subrelationcall_funcs == null) return null;
		Func f = subrelationcall_funcs.get(EchoHelper.relationFieldName(n,dep.target));
		if (f == null) return null;
		Expr[] vars = new Expr[aux.size()];
		for (int i = 0; i<aux.size(); i++)
			vars[i] = ((AlloyExpression) aux.get(i)).EXPR;
		Expr exp = f.call(vars);
		return new AlloyFormula(exp);
	}

}
