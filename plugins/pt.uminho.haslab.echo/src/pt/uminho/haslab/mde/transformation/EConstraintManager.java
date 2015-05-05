package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.ast.CoreTransformation;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EArtifact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

/**
 * Manages the current set of inter-model constraints.
 * A constraint consists of a set of models related by a transformation.
 * 
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EConstraintManager {

	private static EConstraintManager instance = new EConstraintManager();

	public static EConstraintManager getInstance() {
		return instance;
	}
	
	
	private Map<String,EConstraint> constraints = new HashMap<String,EConstraint>();
	/** maps transformations to related existing inter-model constraints */
	private Map<String,List<String>> trans2constraints = new HashMap<String, List<String>>();
	/** maps models to related existing inter-model constraints */
	private Map<String,List<String>> model2constraints = new HashMap<String, List<String>>();

	/**
	 * Creates a new inter-model constraint
	 * If <code>transformation</code> is already relating <code>models</code>, returns the existing one
	 * @param transformation the transformation to related the models
	 * @param models the models to be related
	 * @return the newly created constraint
	 * @throws EError 
	 */
	public EConstraint addConstraint(String transformationID, List<String> models) throws EError{
		EConstraint c = new EConstraint(models, transformationID);

		List<String> cs = trans2constraints.get(transformationID);
		if (cs == null) cs = new ArrayList<String>();
		cs.add(c.ID);
		trans2constraints.put(transformationID, cs);

		for (String id : models) {
			cs = model2constraints.get(id);
			if (cs == null) cs = new ArrayList<String>();
			cs.add(c.ID);
			model2constraints.put(id, cs);
		}
		constraints.put(c.ID,c);
		return c;
	}

	/**
	 * Returns all currently existing constraints
	 * @return the set of constraints
	 */
	public List<String> getAllConstraints() {
		return new ArrayList<String>(constraints.keySet());
	}

	/**
	 * Returns all constraints involving a given model
	 * @param model the model
	 * @return constraints involving <code>model</code>
	 */
	public List<String> getConstraintsModel(String modelID) {
		List<String> res = model2constraints.get(modelID);
		return res == null? new ArrayList<String>() : res;
	}

	/**
	 * Returns all constraints involving a given transformation
	 * @param transformation the transformation
	 * @return constraints involving <code>transformation</code>
	 */
	public List<String> getConstraintsTransformation(String transformationID) {
		List<String> res = trans2constraints.get(transformationID);
		return res == null? new ArrayList<String>() : res;
	}

	/**
	 * Removes the given constraint from the system
	 * @param constraint the constraint to be removed
	 */
	public void removeConstraint(String constraintID) {
		EConstraint c = constraints.remove(constraintID);

		trans2constraints.get(c.transformationID).remove(constraintID);

		for (String id : c.getModels())
			model2constraints.get(id).remove(constraintID);
	}

	/**
	 * Represents a particular inter-model constraint
	 * Consists of a set of models and the relating transformation
	 * @author nmm
	 */
	public class EConstraint extends EArtifact {
		/** the related models */
		private List<String> models;
		/** the relating transformation */
		public final String transformationID;

		/**
		 * Creates a constraint given a set of models and a transformation
		 * @param models the models to be related
		 * @param transformation the relating transformation
		 * @throws EError 
		 */
		private EConstraint(List<String> models, String transformationID) throws EError {
			super(transformationID+models,MDEManager.getInstance().getETransformationID(transformationID).getEObject());
			this.models = new ArrayList<String>(models);
			this.transformationID = transformationID;
		}

		public List<String> getModels() {
			return models;
		}

		@Override
		public boolean equals(Object cons) {
			if (!(cons instanceof EConstraint))
				return false;
			EConstraint constraint = (EConstraint) cons;
			if (!this.transformationID.equals(constraint.transformationID)) return false;
			if (this.models.size() != constraint.models.size()) return false;
			boolean same = true;
			for (int i = 0; i < this.models.size() && same; i++)
				same = same && this.models.get(i).equals(constraint.models.get(i));
			return same;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder(transformationID);
			s.append(": ");
			s.append(models.get(0).toString());
			for (int i = 1; i < models.size(); i++) {
				s.append(" <-> ");
				s.append(models.get(i).toString());
			}
			return s.toString();
		}
		
		public IFormula getConstraint() {
			CoreTransformation t = CoreTranslator.getInstance().getTransformation(transformationID);
			return t.getConstraint(models);
		}

		@Override
		public EObject getEObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void process(EObject artifact) throws EErrorUnsupported,
				EErrorParser {
			// TODO Auto-generated method stub
			
		}


	}

	public EConstraint getConstraintID(String constraintID) {
		return constraints.get(constraintID);
	}
}
