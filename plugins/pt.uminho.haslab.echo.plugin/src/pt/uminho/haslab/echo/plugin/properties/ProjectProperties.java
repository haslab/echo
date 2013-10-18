package pt.uminho.haslab.echo.plugin.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ConstraintManager.Constraint;


public class ProjectProperties {
	
	private static QualifiedName ECHO_PROPERTIES = new QualifiedName("pt.uminho.haslab.echo","properties");
	private static Map<IProject,ResourceManager> properties = new HashMap<IProject,ResourceManager>();
	
	public static ResourceManager getProperties(IProject project){
		ResourceManager res = properties.get(project);
		if (res == null) {
			res = new ResourceManager();
			properties.put(project,res);
		}
		return res;
	} 
	
	public static ResourceManager loadProperties2(IProject project){
		String propertiesstring = null;
		try {
			propertiesstring = project.getPersistentProperty(ECHO_PROPERTIES);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResourceManager properties = new ResourceManager();
		if (propertiesstring != null) {
		//	properties.readString(propertiesstring);
		}
		ProjectProperties.properties.put(project, properties);
		return properties;
	}

	private static void saveProjectProperties(IProject project) throws ErrorParser {
		try {
			project.setPersistentProperty(ECHO_PROPERTIES, properties.get(project).writeString());
		} catch(CoreException e) { throw new ErrorParser(e.getMessage()); }		
	}
	


}
