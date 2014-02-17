package pt.uminho.haslab.echo.engine.kodkod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Relation;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

public class EKodkodRelation extends EEngineRelation {

	public EKodkodRelation(EEngineRelation parentTranslator, ERelation relation)
			throws EchoError {
		super(relation, parentTranslator);
	}

	public EKodkodRelation(EKodkodTransformation eAlloyTransformation,
			EDependency dep, ERelation rel) throws EchoError {
		super(rel, dep, eAlloyTransformation);
	}

	/** {@inheritDoc} */
	@Override
	protected KodkodFormula simplify(IFormula formula) throws ErrorUnsupported {
		return (KodkodFormula) formula;
	}
	

	/** {@inheritDoc} */
	@Override
	protected KodkodExpression addNonTopRel(List<IDecl> rootVars) throws ErrorKodkod {
		Relation field = null;

		// Creates a relation between the TYPES of the root variables (only two for now)
		
//		try {
//			Sig s = (Sig) ((KodkodDecl) fst).decl.expr.type().toExpr();
//			for (Field f : s.getFields()) {
//				if (f.label.equals(EchoHelper.relationFieldName(relation,
//						dependency.target)))
//					field = f;
//			}
//			if (field == null) {
//				field = s.addField(EchoHelper.relationFieldName(relation,
//						dependency.target),
//				/* type.setOf() */Sig.UNIV.setOf());
//			}
//		} catch (Err a) {
//			throw new ErrorKodkod(ErrorInternalEngine.FAIL_CREATE_FIELD,
//					"Failed to create relation field representation: "
//							+ relation.getName(),
//					Task.TRANSLATE_TRANSFORMATION);
//		}
//		
		return new KodkodExpression(field);
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, IDecl> createVarDecls(Map<EVariable,String> set, boolean notTop) throws EchoError {
		Map<String, IDecl> ivars = new HashMap<String,IDecl>();
		
		// Creates declarations from EMF variables (adds extra information to the context if necessary)

//		if (notTop)
//	    	for (EVariable s : set.keySet())
//	    		context.setVarModel(s.getName(),set.get(s));
//		
//		Map<String, Decl> vars = KodkodUtil.variableListToExpr(set.keySet(),context);
//	  	for (String s : vars.keySet()) {
//	  		IDecl d = new KodkodDecl(vars.get(s));
//			if (notTop) context.addVar(d);
//			ivars.put(s, d);
//	  	}	  	

	  	return ivars;
	}

	@Override
	public void newRelation(EQVTRelation rel) throws EchoError {
		new EKodkodRelation(this, rel);
	}

	@Override
	protected void manageModelParams() throws ErrorInternalEngine,
			ErrorUnsupported, ErrorParser {
		// TODO Is there need to do anything about this in Kodkod?
	}
	
}
