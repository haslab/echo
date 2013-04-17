package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVTTransformation2Alloy {

	/** the Alloy expression rising from this QVT Transformation*/
	private Func func;
	private RelationalTransformation qvt;
	
	/** Constructs a new QVT Transformation to Alloy translator.
	 * A {@code QVTRelation2Alloy} is called for every top QVT Relation and direction.
	 * 
	 * @param qvt the QVT Transformation being translated
	 * @param statesigs maps transformation arguments (or metamodels) to the respective Alloy singleton signature (or abstract signature)
	 * @param modelsigs maps metamodels to the set of Alloy signatures
	 * 
	 * @throws ErrorTransform, 
	 * @throws ErrorUnsupported
	 * @throws ErrorAlloy
	 * @throws Err 
	 */
	public QVTTransformation2Alloy (EMF2Alloy translator, RelationalTransformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr fact = Sig.NONE.no();
		this.qvt = qvt;
		Map<String,Decl> argsdecls = new HashMap<String, Decl>();
		List<Decl> decls = new ArrayList<Decl>();
		List<ExprHasName> vars = new ArrayList<ExprHasName>();
		for (TypedModel mdl : qvt.getModelParameter()) {
			Decl d;
			try {
				d = translator.getModelStateSig(mdl.getUsedPackage().get(0).getName()).oneOf(mdl.getName());
			} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
			argsdecls.put(mdl.getName(), d);
			decls.add(d);
			vars.add(d.get());
		}
		for (Rule rel : qvt.getRule())
			if (!(rel instanceof Relation)) throw new ErrorTransform ("Rule not a relation.","QVT2Alloy",rel);
			else if (((Relation) rel).isIsTopLevel()) {

				for (TypedModel mdl : qvt.getModelParameter()) {
				//TypedModel mdl = qvt.getModelParameter().get(0);
						QVTRelation2Alloy trans = new QVTRelation2Alloy(null,(Relation)rel,mdl,true,translator);
					fact = fact.and(trans.getFunc().call(vars.toArray(new ExprVar[vars.size()])));
					for (Func f : trans.getFieldFunc()) {

						fact = fact.and(f.call(vars.toArray(new ExprVar[vars.size()])));
					}
				}
			}
		try {
			func = new Func(null, qvt.getName(), decls, null, fact);		
		} catch (Err a) { throw new ErrorAlloy(a.getMessage()); }
	}
	
	/** Returns the Alloy fact corresponding to this QVT Transformation
	 * 
	 * @return this.fact
	 */	
	public Func getFunc() {
		return func;
	}

	public RelationalTransformation getQVTTransformation() {
		return qvt;
	}

}
