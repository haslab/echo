package pt.uminho.haslab.echo.engine;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;

/**
 * Helper class for Echo's naming conventions.
 * Should be followed in order be able to retrieve meta-model information from the engine artifacts.
 *
 * @author nmm
 * @version 0.4 21/03/2014
 */
public class EchoHelper {
	
	/** the name of the top-level state artifact */	
	public static String STATESIGNAME = "State@";
	/** the name of the state ordering artifact */
	public static String ORDNAME = "ord@";
	/** the name of the set representing new elements */
	public static String NEWSNAME = "news@";
	/** engine String type name */
	public static String STRINGNAME = "String";
	/** engine Integer type name */
	public static String INTNAME = "Int";

	/**
	 * Calculates the unique identifier for an object.
	 * @param prefix the label prefix
	 * @param obj the object for which the identifier is being calculated
	 * @return the unique identifier
	 */
	public static String makeID(String prefix, Object obj) {
		return prefix + obj.hashCode();
	}
	
	/**
	 * Retrieves the meta-model ID from an artifact label.
	 * @param label the label of the artifact
	 * @return the meta-model ID
	 */
	public static String getMetamodelIDfromLabel(String label) {
		return label.split("@")[0];
	}

	/**
	 * Calculates the label for a classifier.
	 * @param metamodel the parent meta-model
	 * @param classifier the classifier
	 * @return the classifier label
	 */
	public static String classifierLabel (EMetamodel metamodel, EClassifier classifier) {
		return (metamodel.ID + "@" + classifier.getName());
	}

	/**
	 * Retrieves the class name from an artifact label (if calculated by <code>classifierLabel</code>).
	 * @param label the label of the artifact
	 * @return the class name
	 */
	public static String getClassifierName(String label) {
		String res = null;
		String[] aux = label.split("@");
		if (aux.length > 1) {
			if (isElement(label)) res = aux[1].split("#")[0];
			else res = aux[1];
		}
		return res;
	}
	
	/** 
	 * Tests if a label may represent a class (if calculated by <code>classifierLabel</code>).
	 * May return false positives.
	 * @param label the label of the artifact
	 * @return if the label may represent a class
	 */
	public static boolean mayBeClass(String label) {
		return label.split("@").length == 2;
	}

	/**
	 * Calculates the label for a feature.
	 * @param metamodel the parent meta-model
	 * @param feature the feature
	 * @return the feature label
	 */
	public static String featureLabel(EMetamodel metamodel, EStructuralFeature feature) {
		return (metamodel.ID + "@" + feature.getEContainingClass().getName() + "@" + feature.getName());
	}

	/**
	 * Retrieves the feature name from an artifact label (if calculated by <code>featureLabel</code>).
	 * @param label the label of the artifact
	 * @return the feature name
	 */
	public static String getFeatureName(String label) {
		String res = null;
		String[] aux = label.split("@");
		if (aux.length > 2) {
			if (isElement(label)) res = aux[2].split("#")[0];
			else res = aux[2];
		}
		return res;
	}
	
	/** 
	 * Tests if a label may represent a feature (if calculated by <code>featureLabel</code>).
	 * May return false positives.
	 * @param label the label of the artifact
	 * @return if the label may represent a feature
	 */
	public static boolean mayBeFeature(String label) {
		return label.split("@").length == 3;
	}

	/**
	 * Calculates the label for an enumeration literal.
	 * @param metamodel the parent meta-model
	 * @param feature the enumeration literal
	 * @return the enumeration literal label
	 */
	public static String literalLabel(EMetamodel metamodel, EEnumLiteral elit) {
		return (metamodel.ID + "@" + elit.getEEnum().getName() + "@" + elit.getName());
	}
	
	/**
	 * Retrieves the enumeration literal name from an artifact label (if calculated by <code>literalLabel</code>).
	 * @param label the label of the artifact
	 * @return the enumeration literal name
	 */
	public static String getEnumeLitName(String label) {
		String res = null;
		String[] aux = label.split("@");
		if (aux.length > 2) {
			if (isElement(label)) res = aux[2].split("#")[0];
			else res = aux[2];
		}
		return res;
	}

	/** 
	 * Tests if a label may represent an enumeration literal (if calculated by <code>literalLabel</code>).
	 * May return false positives.
	 * @param label the label of the artifact
	 * @return if the label may represent an enumeration literal
	 */
	public static boolean mayBeEnumeLit(String label) {
		return label.split("@").length == 3;
	}
	
	/**
	 * Calculates the label of the state field of a class.
	 * @param metamodel the parent meta-model
	 * @param cl the class
	 * @return the state field label
	 */
	public static String stateFieldName (EMetamodel metamodel, EClass cl) {
		return metamodel.ID +"@"+ cl.getName() +"@";
	}
	
	/** 
	 * Tests if a label may represent a state field (if calculated by <code>stateFieldName</code>).
	 * @param label the label of the artifact
	 * @return if the label may represent a state field
	 */
	public static boolean isStateField(String label) {
		return mayBeClass(label) && label.endsWith("@");
	}
	
	/**
	 * Calculates the label for a target mode.
	 * @param model the target model label
	 * @return the label of the target model
	 */
	public static String targetName(String label) {
		return "'"+label;
	}
	
	/**
	 * Calculates the presentation label for a model artifact.
	 * Assumes targets were created with <code>targetName</code>.
	 * @param label the label of the artifact
	 * @return the presentation label
	 */
	public static String getModelName(String label) {
		String[] aux = label.split("/");
		if (label.charAt(0) == '\'') return "target@"+aux[aux.length-1];
		else return "source@"+aux[aux.length-1];
	}

	/**
	 * Calculates the presentation label for a meta-model artifact.
	 * @param label the label of the artifact
	 * @return the presentation label
	 */
	public static String getMetaModelName(String label) {
		String[] aux = label.split("/");
		return aux[aux.length-1];
	}

	public static boolean isElement(String label) {
		return mayBeClass(label) && label.split("#").length == 2 && label.endsWith("#");
	}

	/**
	 * Defines the name of a relation field
	 * @param rel the relation
	 * @param dir the direction (target) being run
	 * @return the name of the field
	 */
	public static String relationFieldName (ERelation rel, EModelDomain dir) {
		try {
			EModelParameter model = dir.getModel();
			return rel.getName() +"@"+model.getName()+"@";
		} catch (EError e) {
			return "Err";
		}
	}

	/**
	 * Defines the name of a relation predicate
	 * @param rel the relation
	 * @param dir the direction (target) being run
	 * @return the name of the predicate
	 */
	public static String relationPredName (ERelation rel, EModelDomain dir) {
		try {
			EModelParameter model = dir.getModel();
			return rel.getName() +"_"+model.getName();
		} catch (EError e) {
			return "Err";
		}		
	}
	
}
