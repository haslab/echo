package pt.uminho.haslab.echo.transform;

import java.util.List;

import pt.uminho.haslab.echo.ErrorTransform;

import net.sourceforge.qvtparser.model.qvtbase.Rule;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;
import net.sourceforge.qvtparser.model.qvtrelation.Relation;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVT2Alloy {

	public final Expr fact;
	
	public QVT2Alloy (TypedModel target, Transformation qvt, List<Sig> modelsigs) throws Exception {
		
		Expr fact = ExprConstant.TRUE;

		for (Object rel1 : qvt.getRule()){ // should be Rule
			Rule rel = (Rule) rel1;
			if (!(rel instanceof Relation)) throw new ErrorTransform ("Rule not a relation.","QVT2Alloy",rel);
			else {
				QVTRelation2Alloy trans = new QVTRelation2Alloy(target,(Relation) rel,qvt,modelsigs);
				fact = AlloyUtil.cleanAnd(fact,trans.getFact());
			}
		}
		
		this.fact = fact;
	}

	public Expr getFact() {
		return fact;
	}

}
