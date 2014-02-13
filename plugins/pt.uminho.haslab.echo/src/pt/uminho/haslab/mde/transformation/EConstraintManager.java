package pt.uminho.haslab.mde.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.mde.model.EModel;

/**
 * Manages the current set of inter-model constraints
 * A constraint consists of a set of models related by a transformation
 * @author nmm
 */
public class EConstraintManager {

	private static EConstraintManager instance = new EConstraintManager();

	public static EConstraintManager getInstance() {
		return instance;
	}


	/** maps transformations to related existing inter-model constraints */
	private Map<String,List<EConstraint>> trans2constraints = new HashMap<String, List<EConstraint>>();
	/** maps models to related existing inter-model constraints */
	private Map<String,List<EConstraint>> model2constraints = new HashMap<String, List<EConstraint>>();

	/**
	 * Creates a new inter-model constraint
	 * If <code>transformation</code> is already relating <code>models</code>, returns the existing one
	 * @param transformation the transformation to related the models
	 * @param models the models to be related
	 * @return the newly created constraint
	 */
	public EConstraint addConstraint(ETransformation transformation, List<EModel> models){
		List<EConstraint> cs = trans2constraints.get(transformation.ID);
		if (cs == null) cs = new ArrayList<EConstraint>();
		for (EConstraint c : cs) {
			boolean same = true;
			for (int i = 0; i < models.size() && same; i ++)
				same = same && c.models.get(i).equals(models.get(i));
			if (same) return c;
		}
		EConstraint c = new EConstraint(models, transformation);
		cs.add(c);
		trans2constraints.put(transformation.ID, cs);

		for (EModel model : models) {
			cs = model2constraints.get(model.ID);
			if (cs == null) cs = new ArrayList<EConstraint>();
			cs.add(c);
			model2constraints.put(model.ID, cs);
		}
		return c;
	}

	/**
	 * Returns all currently existing constraints
	 * @return the set of constraints
	 */
	public List<EConstraint> getAllConstraints() {
		List<EConstraint> aux = new ArrayList<EConstraint>();
		for (List<EConstraint> x : trans2constraints.values())
			aux.addAll(x);
		return aux;
	}

	/**
	 * Returns all constraints involving a given model
	 * @param model the model
	 * @return constraints involving <code>model</code>
	 */
	public List<EConstraint> getConstraintsModel(String modelID) {
		List<EConstraint> res = model2constraints.get(modelID);
		//EchoReporter.getInstance().debug(modelID + " at "+model2constraints.keySet()+ " so "+res);
		return res == null? new ArrayList<EConstraint>() : res;
	}

	/**
	 * Returns all constraints involving a given transformation
	 * @param transformation the transformation
	 * @return constraints involving <code>transformation</code>
	 */
	public List<EConstraint> getConstraintsTransformation(String transformationID) {
		List<EConstraint> res = trans2constraints.get(transformationID);
		return res == null? new ArrayList<EConstraint>() : res;
	}

	/**
	 * Removes a constraint relating a set of models by a given transformation
	 * @param models the related models
	 * @param transformation the relating transformation
	 * @return the removed constraint
	 */
	public EConstraint removeConstraint(List<EModel> models, ETransformation transformation) {
		EConstraint c = new EConstraint(models, transformation);
		removeConstraint(c);
		return c;
	}

	/**
	 * Removes the given constraint from the system
	 * @param constraint the constraint to be removed
	 */
	public void removeConstraint(EConstraint constraint) {
		List<EConstraint> cs = new ArrayList<EConstraint>();
		for (EConstraint c : trans2constraints.get(constraint.transformation.ID)) {
			if (!(c.equals(constraint)))
				cs.add(c);
		}
		trans2constraints.put(constraint.transformation.ID, cs);

		for (EModel model : constraint.models) {
			cs = new ArrayList<EConstraint>();
			for (EConstraint c : model2constraints.get(model.ID)) {
				if (!(c.equals(constraint)))
					cs.add(c);
			}
			model2constraints.put(model.ID, cs);
		}
	}

	/**
	 * Represents a particular inter-model constraint
	 * Consists of a set of models and the relating transformation
	 * @author nmm
	 */
	public class EConstraint {
		/** the related models */
		private List<EModel> models;
		/** the relating transformation */
		public final ETransformation transformation;

		/**
		 * Creates a constraint given a set of models and a transformation
		 * @param models the models to be related
		 * @param transformation the relating transformation
		 */
		private EConstraint(List<EModel> models, ETransformation transformation) {
			this.models = new ArrayList<EModel>(models);
			this.transformation = transformation;
		}

		public List<EModel> getModels() {
			return models;
		}

		@Override
		public boolean equals(Object cons) {
			if (!(cons instanceof EConstraint))
				return false;
			EConstraint constraint = (EConstraint) cons;
			if (!this.transformation.equals(constraint.transformation)) return false;
			if (this.models.size() != constraint.models.size()) return false;
			boolean same = true;
			for (int i = 0; i < this.models.size() && same; i++)
				same = same && this.models.get(i).equals(constraint.models.get(i));
			return same;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder(transformation.getName());
			s.append(": ");
			s.append(models.get(0).toString());
			for (int i = 1; i < models.size(); i ++) {
				s.append(" <-> ");
				s.append(models.get(i).toString());
			}
			return s.toString();
		}

	}
}
