package pt.uminho.haslab.echo.engine.kodkod;

import java.util.HashMap;
import java.util.Map;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.EEngineTransformation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

public class EKodkodRelation extends EEngineRelation {

	public EKodkodRelation(ERelation relation,
			EDependency dependency, EEngineTransformation transformation) throws EchoError {
		super(relation, dependency, transformation);
	}

	/** {@inheritDoc} */
	@Override
	protected KodkodFormula simplify(IFormula formula) throws ErrorUnsupported {
		return (KodkodFormula) formula;
	}
	
	/** {@inheritDoc} */
	@Override
	protected KodkodDecl createDecl(EModelParameter model) throws ErrorKodkod {
		String metamodelID = model.getMetamodel().ID;

		Decl d;
		try {
			d = KodkodEchoTranslator.getInstance()
					.getMetamodel(metamodelID).sig_metamodel
					.oneOf(model.getName());
		} catch (Err a) {
			throw new ErrorKodkod(ErrorInternalEngine.FAIL_CREATE_VAR,
					"Failed to create transformation model variable: "
							+ metamodelID, a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		context.addModelParamX(false, model.getName(), new KodkodExpression(d.get()));
		context.addModelParam(false, metamodelID, new KodkodExpression(d.get()));
		KodkodDecl id = new KodkodDecl(d);
		context.addVar(id,model.getName());
		return id;

	}

	/** {@inheritDoc} */
	@Override
	protected KodkodExpression createNonTopRel(IDecl fst) throws ErrorKodkod {
		Field field = null;

		try {
			Sig s = (Sig) ((KodkodDecl) fst).decl.expr.type().toExpr();
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
			throw new ErrorKodkod(ErrorInternalEngine.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return new KodkodExpression(field);
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, IDecl> createVarDecls(Map<EVariable,String> set, boolean notTop) throws EchoError {
	    if (notTop)
	    	for (EVariable s : set.keySet())
	    		context.setVarModel(s.getName(),set.get(s));
		
		Map<String, Decl> vars = KodkodUtil.variableListToExpr(set.keySet(),context);
		Map<String, IDecl> ivars = new HashMap<String,IDecl>();
	  	for (String s : vars.keySet()) {
	  		IDecl d = new KodkodDecl(vars.get(s));
			if (notTop) context.addVar(d);
			ivars.put(s, d);
	  	}	  	
	  	return ivars;
	}

	@Override
	public void newRelation(EQVTRelation rel) throws EchoError {
		new EKodkodRelation(this, rel);
	}
	
}
