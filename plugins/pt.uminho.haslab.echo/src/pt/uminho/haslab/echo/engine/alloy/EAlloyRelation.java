package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EEngineRelation;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.model.ECondition;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

public class EAlloyRelation extends EEngineRelation {

	public EAlloyRelation(EEngineRelation parent_translator, ERelation relation)
			throws EchoError {
		super(parent_translator, relation);
	}

	public EAlloyRelation(EAlloyTransformation eAlloyTransformation,
			EDependency dep, ERelation rel) throws EchoError {
		super(eAlloyTransformation, dep, rel);
	}

	/** maps variable names to the owning model */
	private Map<String,String> var2model;

	/** the model parameters variables of the current transformation */
	private Map<String,ExprHasName> modelparam2var;

	/** maps variable names to their Alloy representation 
	 * contains all variables, including model parameters */
	private Map<String,ExprHasName> var2var;

	

	protected AlloyFormula optimize(IFormula fact) throws ErrorUnsupported {
		Expr afact = ((AlloyFormula)fact).formula;
		AlloyOptimizations opt = new AlloyOptimizations();
		afact = opt.trading(afact);
		afact = opt.onePoint(afact);
		EchoReporter.getInstance().debug("Post-opt: "+fact);
		return new AlloyFormula(afact);
	}
	
	protected AlloyDecl createDecl(String metamodelID) throws ErrorAlloy {
		if (modelparam2var == null) modelparam2var = new LinkedHashMap<String,ExprHasName>();
		if (var2model == null) var2model = new LinkedHashMap<String,String>();
		if (var2var == null) var2var = new LinkedHashMap<String,ExprHasName>();
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
		modelparam2var.put(metamodelID, d.get());
		var2model.put(d.get().label, null);
		var2var.put(d.get().label, d.get());
		return new AlloyDecl(d);

	}

	protected AlloyExpression createField(IDecl fst) throws ErrorAlloy {
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

	
	/**
	 * Maps variable names to their Alloy representation and containing model
	 * Merges <code>var2var</code> with <code>var2model</code>
	 * @return the mapping
	 */
	private Map<String,Entry<ExprHasName,String>> var2varmodel() {
		Map<String,Entry<ExprHasName,String>> aux = new LinkedHashMap<String,Entry<ExprHasName,String>>();
		for (String s : var2model.keySet()) {
			aux.put(s, new SimpleEntry<ExprHasName,String>(var2var.get(s),var2model.get(s)));
		}
		return aux;
	}

	@Override
	protected Map<String, IDecl> createVarDecls(Map<EVariable,String> set, boolean b) throws EchoError {
	    if (b)
	    	for (EVariable s : set.keySet())
	    		var2model.put(s.getName(),set.get(s));
		
		Map<String, Decl> vars = AlloyUtil.variableListToExpr(set.keySet(),var2varmodel(),modelparam2var);
		Map<String, IDecl> ivars = new HashMap<String,IDecl>();
	  	for (String s : vars.keySet()) {
			if (b) var2var.put(s, vars.get(s).get());
			ivars.put(s, new AlloyDecl(vars.get(s)));
	  	}	  	
	  	return ivars;
	}
	
	protected AlloyFormula translateCondition(ECondition targetCondition) throws EchoError {
		targetCondition.initTranslation((EAlloyRelation) parent_translator,var2varmodel(),modelparam2var,null);
		return new AlloyFormula(targetCondition.translate());
	}

}
