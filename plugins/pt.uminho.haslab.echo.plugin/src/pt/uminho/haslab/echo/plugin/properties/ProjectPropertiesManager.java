package pt.uminho.haslab.echo.plugin.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.plugin.ResourceManager;

/**
 * This class stores and manages the project properties assigned to each project
 * @author nmm
 *
 */
public class ProjectPropertiesManager {
	
	private static QualifiedName ECHO_PROPERTIES = new QualifiedName("pt.uminho.haslab.echo","properties");
	/** The project to properties map */
	private static Map<IProject,ResourceManager> properties = new HashMap<IProject,ResourceManager>();
	
	public static ResourceManager getProperties(IProject project){
		ResourceManager res = properties.get(project);
		if (res == null) {
			res = new ResourceManager();
			properties.put(project,res);
		}
		return res;
	} 
	
	/**
	 * Reads persistent properties
	 * @param project
	 * @return
	 */
	public static ResourceManager loadProperties(IProject project){
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
		ProjectPropertiesManager.properties.put(project, properties);
		return properties;
	}

	/**
	 * Writes persistent properties
	 * @param project
	 * @throws ErrorParser
	 */
	public static void saveProjectProperties(IProject project) throws ErrorParser {
		try {
			project.setPersistentProperty(ECHO_PROPERTIES, properties.get(project).writeString());
		} catch(CoreException e) { throw new ErrorParser(e.getMessage()); }		
	}
	


}
