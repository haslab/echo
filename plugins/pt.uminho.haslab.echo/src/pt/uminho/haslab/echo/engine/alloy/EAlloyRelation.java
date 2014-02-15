package pt.uminho.haslab.echo.engine.alloy;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
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
	
	public EAlloyRelation(EEngineRelation parent_translator, ERelation relation)
			throws EchoError {
		super(relation, parent_translator);
	}

	public EAlloyRelation(EAlloyTransformation eAlloyTransformation,
			EDependency dep, ERelation rel) throws EchoError {
		super(rel, dep, eAlloyTransformation);
	}

	protected AlloyFormula simplify(IFormula formula) throws ErrorUnsupported {
		Expr afact = ((AlloyFormula)formula).formula;
		AlloyOptimizations opt = new AlloyOptimizations();
		afact = opt.trading(afact);
		afact = opt.onePoint(afact);
		EchoReporter.getInstance().debug("Post-opt: "+formula);
		return new AlloyFormula(afact);
	}
	
	protected AlloyDecl createDecl(EModelParameter model) throws ErrorAlloy {
		String metamodelID = model.getMetamodel().ID;

		Decl d;
		try {
			d = AlloyEchoTranslator.getInstance()
					.getMetamodel(metamodelID).sig_metamodel
					.oneOf(metamodelID);
		} catch (Err a) {
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_VAR,
					"Failed to create transformation model variable: "
							+ metamodelID, a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		context.addModelParam(false, model.getName(), new AlloyExpression(d.get()));
		AlloyDecl id = new AlloyDecl(d);
		context.addVar(id);
		return id;

	}

	protected AlloyExpression createNonTopRel(IDecl fst) throws ErrorAlloy {
		Field field = null;

		try {
			Sig s = (Sig) ((AlloyDecl) fst).decl.expr.type().toExpr();
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
			throw new ErrorAlloy(ErrorAlloy.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return new AlloyExpression(field);
	}

	@Override
	protected Map<String, IDecl> createVarDecls(Map<EVariable,String> set, boolean notTop) throws EchoError {
	    if (notTop)
	    	for (EVariable s : set.keySet())
	    		context.setVarModel(s.getName(),set.get(s));
		
		Map<String, Decl> vars = AlloyUtil.variableListToExpr(set.keySet(),context);
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
	

}
