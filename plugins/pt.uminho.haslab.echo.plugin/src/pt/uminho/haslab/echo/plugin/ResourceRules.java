package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.plugin.ConstraintManager.Constraint;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

public class ResourceRules implements ISchedulingRule{

	public final IResource res;
	
	public ResourceRules() {
		this.res = null;
	}
	
	public ResourceRules(IResource res) {
		this.res = res;
	}
	
	@Override
	public boolean contains(ISchedulingRule rule) {
		return isConflicting(rule);
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		ResourceManager manager = ProjectPropertiesManager.getProperties(res.getProject());
		IResource res2 = null;
		if (rule instanceof ResourceRules) {
			res2 = ((ResourceRules) rule).res;
		} else if (rule instanceof IFile) {
			res2 = (IFile) rule;
		} 
		EchoReporter.getInstance().debug("Comparing "+res+" with "+rule);
		if (res2 != null && res2.getFileExtension() != null	) {			
			switch (res.getFileExtension()) {
				case "xmi" :
					switch (res2.getFileExtension()) {
						case "xmi" : return res.equals(res2);
						case "ecore" : return manager.getMetamodel(res).equals(res2);
						case "qvtr" : 
							for (Constraint c : manager.getConstraints(res2))
								if (c.fstmodel.equals(res) || c.sndmodel.equals(res))
									return true;
					}
					break;
				case "ecore" :
					switch (res2.getFileExtension()) {
						case "ecore" : return res.equals(res2);
						case "xmi" : return manager.getMetamodel(res2).equals(res);
						case "qvtr" : 
							for (Constraint c : manager.getConstraints(res2))
								if (manager.getMetamodel(c.fstmodel).equals(res) || manager.getMetamodel(c.sndmodel).equals(res))
									return true;
					}
					break;
				case "qvtr" :
					switch (res2.getFileExtension()) {
						case "ecore" :
							for (Constraint c : manager.getConstraints(res))
								if (manager.getMetamodel(c.fstmodel).equals(res2) || manager.getMetamodel(c.sndmodel).equals(res2))
									return true;
						case "xmi" : 
							for (Constraint c : manager.getConstraints(res))
								if (c.fstmodel.equals(res2) || c.sndmodel.equals(res2))
									return true;
						case "qvtr" : return res.equals(res2);
					}
					break;
				}
		}
		return false;
	}
	
}