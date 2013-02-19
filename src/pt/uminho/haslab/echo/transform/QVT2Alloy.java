package pt.uminho.haslab.echo.transform;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.emof.ast.QVTBase.impl.TransformationImpl;


public class QVT2Alloy {

	TransformationImpl qvt;
	EObject mm1, mm2;
	
	
	public QVT2Alloy (EObject mm1, EObject mm2,TransformationImpl qvt) {
		this.qvt = qvt;
		this.mm1 = mm1;
		this.mm2 = mm2;
	}
	
}
