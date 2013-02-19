package pt.uminho.haslab.echo.transform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;

import pt.uminho.haslab.emof.ast.QVTBase.Rule;
import pt.uminho.haslab.emof.ast.QVTBase.Transformation;
import pt.uminho.haslab.emof.ast.QVTRelation.Relation;


public class QVT2Alloy {

	Transformation qvt;
	EPackage mm1, mm2;
	EObject sv1, sv2;
	
	Expr fact;
	
	public QVT2Alloy (EPackage mm1, EPackage mm2, EObject sv1, EObject sv2, Transformation qvt) {
		this.qvt = qvt;
		this.mm1 = mm1;
		this.mm2 = mm2;
		this.sv1 = sv1;
		this.sv2 = sv2;
		
		for (Rule rel : qvt.getRule())
			if (rel instanceof Relation)
				processRelation((Relation) rel);
			else throw new Error ("Rule not supported.");
	}
	
	private void processRelation (Relation rel) {
		if (!rel.getIsTopLevel()) return;
		
		
		
		
	}
	
}
