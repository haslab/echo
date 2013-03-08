package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.Transformation;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class QVT2Alloy {

	public final List<Expr> fact = new ArrayList<Expr>();
	
	public QVT2Alloy (List<TypedModel> mdls, List<Sig> modelsigs, Transformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		
		for (Rule rel : qvt.getRule()){
			if (!(rel instanceof Relation)) throw new ErrorTransform ("Rule not a relation.","QVT2Alloy",rel);
			else {
				if (((Relation) rel).isIsTopLevel()) {
					for (TypedModel mdl : mdls) {
						QVTRelation2Alloy trans = new QVTRelation2Alloy(mdl,(Relation) rel,modelsigs,qvt);
						fact.add(trans.getFact());
						System.out.println("QVT relation "+rel.getName()+": "+trans.getFact());
					}
				}
			}
		}
	}

	public List<Expr> getFact() {
		return fact;
	}

}
