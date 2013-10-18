package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;

/**
 * Manages a set of inter-model constraints
 * @author nmm
 *
 */
public class ConstraintManager {

	private Map<IResource,List<Constraint>> cnsconstraints = new HashMap<IResource, List<Constraint>>();
	private Map<IResource,List<Constraint>> mdlconstraints = new HashMap<IResource, List<Constraint>>();
	
	public Constraint addConstraint(IResource constraint, IResource fstmodel, IResource sndmodel){
		List<Constraint> cs = cnsconstraints.get(constraint);
		if (cs == null) cs = new ArrayList<Constraint>();
		for (Constraint c : cs) {
			if (c.fstmodel.equals(fstmodel) && c.sndmodel.equals(sndmodel)) return c;
			if (c.fstmodel.equals(sndmodel) && c.sndmodel.equals(fstmodel)) return c;
		}
		Constraint c = new Constraint(fstmodel, sndmodel, constraint);
		cs.add(c);
		cnsconstraints.put(constraint, cs);

		cs = mdlconstraints.get(fstmodel);
		if (cs == null) cs = new ArrayList<Constraint>();
		cs.add(c);
		mdlconstraints.put(fstmodel, cs);
		
		cs = mdlconstraints.get(sndmodel);
		if (cs == null) cs = new ArrayList<Constraint>();
		cs.add(c);
		mdlconstraints.put(sndmodel, cs);	
		
		return c;
	}
	
	public List<Constraint> getAllConstraints() {
		List<Constraint> aux = new ArrayList<Constraint>();
		for (List<Constraint> x : cnsconstraints.values())
			aux.addAll(x);
		return aux;	
		}
	
	public List<Constraint> getAllConstraintsModel(String model) {
		List<Constraint> res = mdlconstraints.get(model);
		return res == null? new ArrayList<Constraint>() : res;
	}

	public List<Constraint> getAllConstraintsConstraint(String constraint) {
		return cnsconstraints.get(constraint);
	}
	
	public Constraint removeConstraint(IResource fstmodel, IResource sndmodel, IResource constraint) {
		Constraint c = new Constraint(fstmodel, sndmodel, constraint);
		removeConstraint(c);
		return c;
	}


	public void removeConstraint(Constraint constraint) {
		List<Constraint> cs = new ArrayList<Constraint>();
		for (Constraint c : cnsconstraints.get(constraint.constraint)) {
			if (!(c.equals(constraint)))
				cs.add(c);
		}
		cnsconstraints.put(constraint.constraint, cs);

		cs = new ArrayList<Constraint>();
		for (Constraint c : mdlconstraints.get(constraint.fstmodel)) {
			if (!(c.equals(constraint)))
				cs.add(c);
		}
		mdlconstraints.put(constraint.fstmodel, cs);
		
		cs = new ArrayList<Constraint>();
		for (Constraint c : mdlconstraints.get(constraint.sndmodel)) {
			if (!(c.equals(constraint)))
				cs.add(c);
		}
		mdlconstraints.put(constraint.sndmodel, cs);
		
		
}

	public class Constraint {
		public final IResource fstmodel;
		public final IResource sndmodel;
		public final IResource constraint;

		public Constraint(IResource fstmodel, IResource sndmodel, IResource constraint) {
			this.fstmodel = fstmodel;
			this.sndmodel = sndmodel;
			this.constraint = constraint;
		}

		@Override
		public boolean equals(Object cons) {
			if (!(cons instanceof Constraint))
				return false;
			Constraint constraint = (Constraint) cons;
			return (this.constraint.equals(constraint.constraint) && ((this.fstmodel
					.equals(constraint.fstmodel) && this.sndmodel
					.equals(constraint.sndmodel)) || (this.fstmodel
					.equals(constraint.sndmodel) && this.sndmodel
					.equals(constraint.fstmodel))));
		}	
		
	}
}
