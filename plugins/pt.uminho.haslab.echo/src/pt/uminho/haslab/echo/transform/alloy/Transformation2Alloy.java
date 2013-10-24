package pt.uminho.haslab.echo.transform.alloy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Relation;
import pt.uminho.haslab.echo.consistency.Transformation;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

class Transformation2Alloy {

	/** the Alloy expression rising from this QVT Transformation*/
	private Func func;
	private Transformation transformation;
	
	/** the additional facts, defining the fields of internal QVT calls */
	private Map<String,Func> recRelationDefs = new HashMap<String,Func>();
	private Map<String,Func> recRelationRecCalls = new HashMap<String,Func>();
	private Map<String,Func> recRelationTopCalls = new HashMap<String,Func>();

	
	
	/** Constructs a new QVT Transformation to Alloy translator.
	 * A {@code QVTRelation2Alloy} is called for every top QVT Relation and direction.
	 * 
	 * @param transformation the QVT Transformation being translated
	 * @param statesigs maps transformation arguments (or metamodels) to the respective Alloy singleton signature (or abstract signature)
	 * @param modelsigs maps metamodels to the set of Alloy signatures
	 * 
	 * @throws ErrorTransform, 
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws Err 
	 */
	Transformation2Alloy (Transformation transformation) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr fact = Sig.NONE.no();
		this.transformation = transformation;
		Map<String,Decl> argsdecls = new HashMap<String, Decl>();
		List<Decl> decls = new ArrayList<Decl>();
		List<ExprHasName> vars = new ArrayList<ExprHasName>();

		for (Model mdl : transformation.getModels()) {
			Decl d;
			try {
				String metamodeluri = mdl.getMetamodelURI();
				d = EchoTranslator.getInstance().getMetamodelStateSig(metamodeluri).oneOf(mdl.getName());
			} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
			argsdecls.put(mdl.getName(), d);
			decls.add(d);
			vars.add(d.get());
		}

		for (Relation rel : transformation.getRelations())
			if (rel.isTop()) {
				for (Model mdl : transformation.getModels()) {
					//TypedModel mdl = qvt.getModelParameter().get(0);
					new Relation2Alloy(this,mdl,rel);
				}
			}
		for (Func f : recRelationTopCalls.values()) {
			fact = fact.and(f.call(vars.toArray(new ExprVar[vars.size()])));
		}		
		for (Func f : recRelationDefs.values()) {
			fact = fact.and(f.call(vars.toArray(new ExprVar[vars.size()])));
		}

		try {
			func = new Func(null, transformation.getName(), decls, null, fact);		
		} catch (Err a) { throw new ErrorAlloy(a.getMessage()); } 
	}
	
	/** Returns the Alloy fact corresponding to this QVT Transformation
	 * 
	 * @return this.fact
	 */	
	Func getFunc() {
		return func;
	}

	Transformation getTransformation() {
		return transformation;
	}
	
	/** 
	 * Adds a new Alloy function defining a non-top QVT relation field
	 * should be used by descendants on parent
	 */
	void addRecRelationDef(Func x) {
		recRelationDefs.put(x.label, x);
	}
	
	void addRecRelationCall(Func x) {
		recRelationRecCalls.put(x.label, x);
	}
	
	void addTopRelationCall(Func x) {
		recRelationTopCalls.put(x.label, x);
	}
	
	
	Func getRecRelationCall(Relation n, Model dir) {
		Func f = recRelationRecCalls.get(AlloyUtil.relationFieldName(n,dir));
		return f;
	}

}
