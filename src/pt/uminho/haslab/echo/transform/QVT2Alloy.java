package pt.uminho.haslab.echo.transform;

import java.util.List;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import net.sourceforge.qvtparser.model.qvtbase.Rule;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;
import net.sourceforge.qvtparser.model.qvtrelation.Relation;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVT2Alloy {

	public final Expr fact;
	
	public QVT2Alloy (List<TypedModel> mdls, List<Sig> modelsigs, Transformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		
		Expr fact = ExprConstant.TRUE;

		for (Object rel1 : qvt.getRule()){ // should be Rule
			Rule rel = (Rule) rel1;
			if (!(rel instanceof Relation)) throw new ErrorTransform ("Rule not a relation.","QVT2Alloy",rel);
			else {
				if (((Relation) rel).getIsTopLevel() != null && ((Relation) rel).getIsTopLevel()) { // apparently, non-top is null
					for (TypedModel mdl : mdls) {
						QVTRelation2Alloy trans = new QVTRelation2Alloy(mdl,(Relation) rel,modelsigs,qvt);
						fact = AlloyUtil.cleanAnd(fact,trans.getFact());
						System.out.println("QVT: "+trans.getFact());
					}
				}
			}
		}
		
		this.fact = fact;
	}

	public Expr getFact() {
		return fact;
	}

}
