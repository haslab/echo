package pt.uminho.haslab.echo.transform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.qvtd.pivot.qvtbase.impl.TransformationImpl;
import org.eclipse.qvtd.pivot.qvtrelation.impl.RelationImpl;


public class QVT2Alloy {

	TransformationImpl qvt;
	EObject mm1, mm2;
	
	
	public QVT2Alloy (EObject mm1, EObject mm2,TransformationImpl qvt) {
		this.qvt = qvt;
		this.mm1 = mm1;
		this.mm2 = mm2;
	}
	
}
