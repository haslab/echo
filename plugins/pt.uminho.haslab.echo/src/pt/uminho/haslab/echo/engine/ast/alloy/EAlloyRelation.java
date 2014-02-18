package pt.uminho.haslab.echo.engine.ast.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.alloy.AlloyContext;
import pt.uminho.haslab.echo.engine.alloy.AlloyEchoTranslator;
import pt.uminho.haslab.echo.engine.alloy.AlloyOptimizations;
import pt.uminho.haslab.echo.engine.alloy.AlloyUtil;
import pt.uminho.haslab.echo.engine.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

public class EAlloyRelation extends EEngineRelation {
	
	/** the transformation model parameters declarations */
	private List<AlloyDecl> modelParamsDecls = new ArrayList<AlloyDecl>();

	public EAlloyRelation(EEngineRelation parentTranslator, ERelation relation)
			throws EchoError {
		super(relation, parentTranslator);
	}

	public EAlloyRelation(EAlloyTransformation eAlloyTransformation,
			EDependency dep, ERelation rel) throws EchoError {
		super(rel, dep, eAlloyTransformation);
	}

	/** {@inheritDoc} */
	@Override
	protected AlloyFormula simplify(IFormula formula) throws ErrorUnsupported {
		Expr afact = ((AlloyFormula)formula).formula;
		AlloyOptimizations opt = new AlloyOptimizations();
		afact = opt.trading(afact);
		afact = opt.onePoint(afact);
		EchoReporter.getInstance().debug("Post-opt: "+formula);
		return new AlloyFormula(afact);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void manageModelParams() throws ErrorAlloy, ErrorUnsupported, ErrorParser {
		// required because super class calls from constructor
		if (modelParamsDecls == null) modelParamsDecls = new ArrayList<AlloyDecl>();
		// creates declarations (variables) for the relation model parameters
		for (EModelParameter mdl : relation.getTransformation()
				.getModelParams()) {
			String metamodelID = mdl.getMetamodel().ID;

			Decl d;
			try {
				d = AlloyEchoTranslator.getInstance().getMetamodel(metamodelID).sig_metamodel
						.oneOf(mdl.getName());
			} catch (Err a) {
				throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_VAR,
						"Failed to create transformation model variable: "
								+ metamodelID, a, Task.TRANSLATE_TRANSFORMATION);
			}
			context.addParamExpression(false, mdl.getName(),
					new AlloyExpression(d.get()));
			context.addMetamodelExpression(false, metamodelID,
					new AlloyExpression(d.get()));
			AlloyDecl id = new AlloyDecl(d);
			context.addVar(id, mdl.getName());

			modelParamsDecls.add(id);
		}
	}

	/** {@inheritDoc}  */
	@Override
	protected AlloyExpression addNonTopRel(List<IDecl> rootVars) throws ErrorAlloy, ErrorUnsupported {
		if (rootVars.size() > 2) throw new ErrorUnsupported("Calls between more than 2 models not yet supported.");
				
		Field field = null;
		try {
			Sig s = (Sig) ((AlloyDecl) rootVars.get(0)).decl.expr.type().toExpr();
						for (Field f : s.getFields()) {
				if (f.label.equals(EchoHelper.relationFieldName(relation,
						dependency.target)))
					field = f;
			}
			if (field == null) {
				field = s.addField(EchoHelper.relationFieldName(relation,
						dependency.target),
				/* type.setOf() */Sig.UNIV.setOf());
			}
		} catch (Err a) {
			throw new ErrorAlloy(ErrorInternalEngine.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return new AlloyExpression(field);
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, IDecl> createVarDecls(Map<EVariable,String> set, boolean notTop) throws EchoError {
	    if (notTop)
	    	for (EVariable s : set.keySet())
	    		context.setVarModel(s.getName(),set.get(s));
		
		Map<String, Decl> vars = AlloyUtil.variableListToExpr(set.keySet(),(AlloyContext) context);
		Map<String, IDecl> ivars = new HashMap<String,IDecl>();
	  	for (String s : vars.keySet()) {
	  		IDecl d = new AlloyDecl(vars.get(s));
			if (notTop) context.addVar(d);
			ivars.put(s, d);
	  	}	  	
	  	return ivars;
	}

	@Override
	public void newRelation(EQVTRelation rel) throws EchoError {
		new EAlloyRelation(this, rel);
	}

	public List<AlloyDecl> getModelParams() {
		return modelParamsDecls;
	}

}
