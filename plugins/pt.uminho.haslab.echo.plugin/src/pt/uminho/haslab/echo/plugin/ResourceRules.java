package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

public class ResourceRules implements ISchedulingRule{

	public final IResource res;
	static public final String READ = "read";
	static public final String WRITE = "read";
	
	public final String mode;
	
	public ResourceRules(String mode) {
		this.mode = mode;
		this.res = null;
	}
	
	public ResourceRules(IResource res, String mode) {
		this.mode = mode;
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
			if (this.mode.equals(WRITE) && ((ResourceRules)rule).mode.equals(WRITE)) return true;
			res2 = ((ResourceRules) rule).res;
		} else if (rule instanceof IFile) {
			res2 = (IFile) rule;
		} 
		if (res2 != null && res2.getFileExtension() != null	) {			
			switch (res.getFileExtension()) {
				case "xmi" :
					switch (res2.getFileExtension()) {
						case "xmi" : return res.equals(res2);
						case "ecore" : return manager.getMetamodel(res).equals(res2);
						case "qvtr" : 
							for (EConstraint c : manager.getConstraints(res2))
								if (c.getModels().get(0).equals(res) || c.getModels().get(1).equals(res))
									return true;
					}
					break;
				case "ecore" :
					switch (res2.getFileExtension()) {
						case "ecore" : return res.equals(res2);
						case "xmi" : return manager.getMetamodel(res2).equals(res);
						case "qvtr" : 
							for (EConstraint c : manager.getConstraints(res2))
								if (ResourcesPlugin.getWorkspace().getRoot().findMember(c.getModels().get(0).getMetamodel().getURI()).equals(res) || 
										ResourcesPlugin.getWorkspace().getRoot().findMember(c.getModels().get(1).getMetamodel().getURI()).equals(res))
									return true;
					}
					break;
				case "qvtr" :
					switch (res2.getFileExtension()) {
						case "ecore" :
							for (EConstraint c : manager.getConstraints(res))
								if (ResourcesPlugin.getWorkspace().getRoot().findMember(c.getModels().get(0).getMetamodel().getURI()).equals(res2) || 
										ResourcesPlugin.getWorkspace().getRoot().findMember(c.getModels().get(1).getMetamodel().getURI()).equals(res2))
									return true;
						case "xmi" : 
							for (EConstraint c : manager.getConstraints(res))
								if (c.getModels().get(0).equals(res2) || c.getModels().get(1).equals(res2))
									return true;
						case "qvtr" : return res.equals(res2);
					}
					break;
				}
		}
		return false;
	}
	
}