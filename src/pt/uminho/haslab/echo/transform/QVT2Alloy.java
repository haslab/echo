package pt.uminho.haslab.echo.transform;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.qvtparser.model.qvtbase.Rule;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;
import net.sourceforge.qvtparser.model.qvtrelation.Relation;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;


public class QVT2Alloy {

	private Transformation qvt;
	private TypedModel target;
	
	private Map<String,List<Sig>> modelsigs = new HashMap<String,List<Sig>>();

	public final Expr fact;
	
	public QVT2Alloy (TypedModel target, Transformation qvt, Map<String,List<Sig>> modelsigs) throws Err {
		this.qvt = qvt;
		this.target = target;
		this.modelsigs = modelsigs;
		
		Expr fact = Sig.NONE.no();

		for (Object rel1 : qvt.getRule()){ // should be Rule
			Rule rel = (Rule) rel1;
			if (!(rel instanceof Relation)) throw new Error ("Rule not supported: "+rel.toString());
			else {
				QVTRelation2Alloy trans = new QVTRelation2Alloy(target,(Relation) rel,qvt,this.modelsigs);
				fact = fact.and(trans.getFact());
			}
		}
		this.fact = fact;
	}

	public Expr getFact() {
		return fact;
	}

}
