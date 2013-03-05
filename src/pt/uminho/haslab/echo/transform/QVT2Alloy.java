package pt.uminho.haslab.echo.transform;

import java.util.List;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.Transformation;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;


import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVT2Alloy {

	public final Expr fact;
	
	public QVT2Alloy (List<TypedModel> mdls, List<Sig> modelsigs, Transformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		
		Expr fact = ExprConstant.TRUE;

		for (Rule rel : qvt.getRule()){
			if (!(rel instanceof Relation)) throw new ErrorTransform ("Rule not a relation.","QVT2Alloy",rel);
			else {
				System.out.println("isTOP: "+((Relation) rel).isIsTopLevel());
				if (!((Relation) rel).isIsTopLevel() && !rel.getName().equals("A2C")) {
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
