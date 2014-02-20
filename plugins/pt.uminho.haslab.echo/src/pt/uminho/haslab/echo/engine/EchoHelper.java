package pt.uminho.haslab.echo.engine;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

public class EchoHelper {
	public static String ORDNAME = "ord@";
	public static String NEWSNAME = "news@";
	public static String STATESIGNAME = "State@";


	public static String relationFieldName (ERelation rel, EModelDomain dir) {
		try {
			EModelParameter model = dir.getModel();
			return rel.getName() +"@"+model.getName()+"@";
		} catch (EchoError e) {
			return "Err";
		}
	}


	public static String getMetamodelIDfromLabel(String label) {
		return label.split("@")[0];
	}

	public static String getClassifierName(String label) {
		String res = null;
		String[] aux = label.split("@");
		if (aux.length > 1) {
			if (isElement(label)) res = aux[1].split("#")[0];
			else res = aux[1];
		}
		return res;
	}

	public static String getFeatureName(String label) {
		String res = null;
		String[] aux = label.split("@");
		if (aux.length > 2) {
			if (isElement(label)) res = aux[2].split("#")[0];
			else res = aux[2];
		}
		return res;
	}

	public static String getModelName(String label) {
		String[] aux = label.split("/");
		if (label.charAt(0) == '\'') return "target@"+aux[aux.length-1];
		else return "source@"+aux[aux.length-1];
	}

	public static String getMetaModelName(String label) {
		String[] aux = label.split("/");
		return aux[aux.length-1];
	}



	public static boolean mayBeClass(String label) {
		return label.split("@").length == 2;
	}

	public static boolean mayBeFeature(String label) {
		return label.split("@").length == 3;
	}

	public static boolean mayBeStateOrLiteral(String label) {
		return label.split("@").length == 1 || label.startsWith(ORDNAME);
	}

	public static boolean isElement(String label) {
		return mayBeClass(label) && label.split("#").length == 2 && label.endsWith("#");
	}


	public static boolean isStateField(String label) {
		return mayBeClass(label) && label.endsWith("@");
	}


	public static String classifierKey (EMetamodel pck, EClassifier ec) {
		return (pck.ID + "@" + ec.getName());
	}

	public static String featureKey (EMetamodel pck, EStructuralFeature ec) {
		return (pck.ID + "@" + ec.getEContainingClass().getName() + "@" + ec.getName());
	}

	public static String literalKey (EMetamodel pck, EEnumLiteral ec) {
		return (pck.ID + "@" + ec.getEEnum().getName() + "@" + ec.getName());
	}


	public static String stateFieldName (EMetamodel pck, EClass cls) {
		return pck.ID +"@"+ cls.getName() +"@";
	}

	public static String relationPredName (ERelation rel, EModelDomain dir) {
		try {
			EModelParameter model = dir.getModel();
			return rel.getName() +"_"+model.getName();
		} catch (EchoError e) {
			return "Err";
		}		
	}

}
