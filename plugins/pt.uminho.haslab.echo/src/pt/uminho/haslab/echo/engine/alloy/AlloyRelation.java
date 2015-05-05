package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ast.CoreRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

import java.util.ArrayList;
import java.util.List;

class AlloyRelation extends CoreRelation {
	
	/** the transformation model parameters declarations */
	private List<AlloyDecl> modelParamsDecls = new ArrayList<>();

	AlloyRelation(CoreRelation parentTranslator, ERelation relation)
			throws EError {
		super(relation, parentTranslator);
	}

	AlloyRelation(AlloyTransformation eAlloyTransformation,
			EDependency dep, ERelation rel, boolean trace) throws EError {
		super(rel, dep, eAlloyTransformation, trace);
	}

	/** {@inheritDoc} */
	@Override
	protected AlloyFormula simplify(IFormula formula) throws EErrorUnsupported {
		Expr afact = ((AlloyFormula)formula).FORMULA;
		AlloyOptimizations opt = new AlloyOptimizations();
		afact = opt.trading(afact);
		afact = opt.onePoint(afact);
		EchoReporter.getInstance().debug("Post-opt: "+formula);
		return new AlloyFormula(afact);
	}
	
	/** {@inheritDoc} 
	 * @throws EError */
	@Override
	protected void manageModelParams() throws EError {
		// required because super class calls from constructor
		if (modelParamsDecls == null) modelParamsDecls = new ArrayList<>();
		// creates declarations (variables) for the relation model parameters
		for (EModelParameter mdl : relation.getTransformation()
				.getModelParams()) {
			String metamodelID = mdl.getMetamodel().ID;

			Decl d;
			try {
				d = AlloyTranslator.getInstance().getMetamodel(metamodelID).SIG
						.oneOf(mdl.getName());
			} catch (Err a) {
				throw new EErrorAlloy(EErrorCore.FAIL_CREATE_VAR,
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
	protected AlloyExpression addNonTopRel(List<? extends EModelDomain> eModelDomains) throws EErrorAlloy, EErrorUnsupported {
		if (eModelDomains.size() > 2) throw new EErrorUnsupported(EErrorUnsupported.MULTIDIRECTIONAL,"Calls between more than 2 models not yet supported.",Task.TRANSLATE_TRANSFORMATION);
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
			throw new EErrorAlloy(EErrorCore.FAIL_CREATE_FIELD,
					"Failed to create relation field representation: "
							+ relation.getName(), a,
					Task.TRANSLATE_TRANSFORMATION);
		}
		return new AlloyExpression(field);
	}

	@Override
	public void newRelation(ERelation rel) throws EError {
		new AlloyRelation(this, rel);
	}

	List<AlloyDecl> getModelParams() {
		return modelParamsDecls;
	}

}
