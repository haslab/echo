package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTRelation;

import java.util.ArrayList;
import java.util.List;

class EAlloyRelation extends EEngineRelation {
	
	/** the transformation model parameters declarations */
	private List<AlloyDecl> modelParamsDecls = new ArrayList<>();

	EAlloyRelation(EEngineRelation parentTranslator, ERelation relation)
			throws EchoError {
		super(relation, parentTranslator);
	}

	EAlloyRelation(EAlloyTransformation eAlloyTransformation,
			EDependency dep, ERelation rel, boolean trace) throws EchoError {
		super(rel, dep, eAlloyTransformation, trace);
	}

	/** {@inheritDoc} */
	@Override
	protected AlloyFormula simplify(IFormula formula) throws ErrorUnsupported {
		Expr afact = ((AlloyFormula)formula).FORMULA;
		AlloyOptimizations opt = new AlloyOptimizations();
		afact = opt.trading(afact);
		afact = opt.onePoint(afact);
		EchoReporter.getInstance().debug("Post-opt: "+formula);
		return new AlloyFormula(afact);
	}
	
	/** {@inheritDoc} 
	 * @throws EchoError */
	@Override
	protected void manageModelParams() throws EchoError {
		// required because super class calls from constructor
		if (modelParamsDecls == null) modelParamsDecls = new ArrayList<>();
		// creates declarations (variables) for the relation model parameters
		for (EModelParameter mdl : relation.getTransformation()
				.getModelParams()) {
			String metamodelID = mdl.getMetamodel().ID;

			Decl d;
			try {
				d = AlloyEchoTranslator.getInstance().getMetamodel(metamodelID).SIG
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

	/** {@inheritDoc}
     * @param eModelDomains*/
	@Override
	protected AlloyExpression addNonTopRel(List<? extends EModelDomain> eModelDomains) throws ErrorAlloy, ErrorUnsupported {
		if (eModelDomains.size() > 2) throw new ErrorUnsupported("Calls between more than 2 models not yet supported.");
        List<IDecl> rootVars = new ArrayList<>();
        for (EModelDomain d : eModelDomains)

            rootVars.add(rootVar2engineDecl.get(d.getRootVariable().getName()));

		Field field = null;
		try {
			Sig s = (Sig) ((AlloyDecl) rootVars.get(0)).DECL.expr.type().toExpr();
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

	@Override
	public void newRelation(EQVTRelation rel) throws EchoError {
		new EAlloyRelation(this, rel);
	}

	List<AlloyDecl> getModelParams() {
		return modelParamsDecls;
	}

}
