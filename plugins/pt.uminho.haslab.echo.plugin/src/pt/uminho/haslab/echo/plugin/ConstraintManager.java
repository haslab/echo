package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.emf.EchoParser;

/**
 * Manages a set of inter-model constraints
 * @author nmm
 *
 */
public class ConstraintManager {

	/** maps constraint resources to related existing inter-model constraints */
	private Map<IResource,List<Constraint>> cres2constraints = new HashMap<IResource, List<Constraint>>();
	/** maps model resources to related existing inter-model constraints */
	private Map<IResource,List<Constraint>> mres2constraints = new HashMap<IResource, List<Constraint>>();

	public Constraint addConstraint(IResource constraint, List<IResource> models){
		List<Constraint> cs = cres2constraints.get(constraint);
		if (cs == null) cs = new ArrayList<Constraint>();
		for (Constraint c : cs) {
			boolean same = true;
			for (int i = 0; i < models.size() && same; i ++)
				same = same && c.models.get(i).equals(models.get(i));
			if (same) return c;
		}
		Constraint c = new Constraint(models, constraint);
		cs.add(c);
		cres2constraints.put(constraint, cs);

		for (IResource model : models) {
			cs = mres2constraints.get(model);
			if (cs == null) cs = new ArrayList<Constraint>();
			cs.add(c);
			mres2constraints.put(model, cs);
		}

		return c;
	}

	public List<Constraint> getAllConstraints() {
		List<Constraint> aux = new ArrayList<Constraint>();
		for (List<Constraint> x : cres2constraints.values())
			aux.addAll(x);
		return aux;	
	}

	public List<Constraint> getAllConstraintsModel(IResource model) {
		List<Constraint> res = mres2constraints.get(model);
		return res == null? new ArrayList<Constraint>() : res;
	}

	public List<Constraint> getAllConstraintsConstraint(IResource constraint) {
		return cres2constraints.get(constraint);
	}

	public Constraint removeConstraint(List<IResource> models, IResource constraint) {
		Constraint c = new Constraint(models, constraint);
		removeConstraint(c);
		return c;
	}


	public void removeConstraint(Constraint constraint) {
		List<Constraint> cs = new ArrayList<Constraint>();
		for (Constraint c : cres2constraints.get(constraint.constraint)) {
			if (!(c.equals(constraint)))
				cs.add(c);
		}
		cres2constraints.put(constraint.constraint, cs);

		for (IResource model : constraint.models) {
			cs = new ArrayList<Constraint>();
			for (Constraint c : mres2constraints.get(model)) {
				if (!(c.equals(constraint)))
					cs.add(c);
			}
			mres2constraints.put(model, cs);
		}
	}

	/**
	 * Represents a particular constraint
	 * @author nmm
	 */
	public class Constraint {
		public final List<IResource> models;
		public final List<String> params = new ArrayList<String>();
		public final IResource constraint;

		private Constraint(List<IResource> models, IResource constraint) {
			this.models = new ArrayList<IResource>(models);
			this.constraint = constraint;
			RelationalTransformation rel = EchoParser.getInstance().getTransformation(constraint.getFullPath().toString());
			for (TypedModel mdl : rel.getModelParameter())
				params.add(mdl.getName());
		}

		@Override
		public boolean equals(Object cons) {
			if (!(cons instanceof Constraint))
				return false;
			Constraint constraint = (Constraint) cons;
			if (!this.constraint.equals(constraint.constraint)) return false;
			if (this.models.size() != constraint.models.size()) return false;
			boolean same = true;
			for (int i = 0; i < this.models.size() && same; i++)
				same = same && this.models.get(i).equals(constraint.models.get(i));
			return same;
		}
		
		@Override
		public String toString() {
			StringBuilder s = new StringBuilder(constraint.getName());
			s.append(": ");
			s.append(models.get(0).getName());
			for (int i = 1; i < models.size(); i ++) {
	 			s.append(" <-> ");
				s.append(models.get(i).getName()); 
			}		
			return s.toString();
		}

	}
}
