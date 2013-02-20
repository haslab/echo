package pt.uminho.haslab.echo.transform;


import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import pt.uminho.haslab.emof.ast.QVTBase.Rule;
import pt.uminho.haslab.emof.ast.QVTBase.Transformation;
import pt.uminho.haslab.emof.ast.QVTBase.TypedModel;
import pt.uminho.haslab.emof.ast.QVTRelation.Relation;


public class QVT2Alloy {

	private Transformation qvt;
	private TypedModel target;
	
	public final Expr fact;
	
	public QVT2Alloy (TypedModel target, Transformation qvt) throws Err {
		this.qvt = qvt;
		this.target = target;
		
		Expr fact = Sig.NONE.no();

		for (Rule rel : qvt.getRule()){
			if (!(rel instanceof Relation)) throw new Error ("Rule not supported: "+rel.toString());
			else {
				QVTRelation2Alloy trans = new QVTRelation2Alloy(target,(Relation) rel,qvt);
				fact = fact.and(trans.getFact());
			}
		}
		this.fact = fact;
	}

	public Expr getFact() {
		return fact;
	}

}
