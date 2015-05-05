package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
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
							for (String cID : manager.getConstraints(res2)) {
								EConstraint c = EConstraintManager.getInstance().getConstraintID(cID);
								if (c.getModels().get(0).equals(res) || c.getModels().get(1).equals(res))
									return true;
							}
					}
					break;
				case "ecore" :
					switch (res2.getFileExtension()) {
						case "ecore" : return res.equals(res2);
						case "xmi" : return manager.getMetamodel(res2).equals(res);
						case "qvtr" : 
							for (String cID : manager.getConstraints(res2)) {
								EConstraint c = EConstraintManager.getInstance().getConstraintID(cID);
								EModel m1=null,m2=null;
								try {
									m1 = MDEManager.getInstance().getModel(c.getModels().get(0),false);
									m2 = MDEManager.getInstance().getModel(c.getModels().get(1),false);
								} catch (EErrorParser | EErrorUnsupported e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (ResourcesPlugin.getWorkspace().getRoot().findMember(m1.getMetamodel().getURI()).equals(res) || 
										ResourcesPlugin.getWorkspace().getRoot().findMember(m2.getMetamodel().getURI()).equals(res))
									return true;
							}
					}
					break;
				case "qvtr" :
					switch (res2.getFileExtension()) {
						case "ecore" :
							for (String cID : manager.getConstraints(res)) {
								EConstraint c = EConstraintManager.getInstance().getConstraintID(cID);
								EModel m1=null,m2=null;
								try {
									m1 = MDEManager.getInstance().getModel(c.getModels().get(0),false);
									m2 = MDEManager.getInstance().getModel(c.getModels().get(1),false);
								} catch (EErrorParser | EErrorUnsupported e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (ResourcesPlugin.getWorkspace().getRoot().findMember(m1.getMetamodel().getURI()).equals(res2) || 
										ResourcesPlugin.getWorkspace().getRoot().findMember(m2.getMetamodel().getURI()).equals(res2))
									return true;
							}
						case "xmi" : 
							for (String cID : manager.getConstraints(res)) {
								EConstraint c = EConstraintManager.getInstance().getConstraintID(cID);
								if (c.getModels().get(0).equals(res2) || c.getModels().get(1).equals(res2))
									return true;
							}
						case "qvtr" : return res.equals(res2);
					}
					break;
				}
		}
		return false;
	}
	
}