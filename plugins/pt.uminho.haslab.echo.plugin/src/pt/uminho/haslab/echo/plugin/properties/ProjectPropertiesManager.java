package pt.uminho.haslab.echo.plugin.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
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
			try {
				res = loadProperties(project);
			} catch (ErrorAlloy | ErrorUnsupported | ErrorTransform
					| ErrorParser | ErrorAPI | CoreException e) {
				e.printStackTrace();
			}
		}
		if (res == null) {
			res = new ResourceManager();
		}
		properties.put(project,res);
		return res;
	} 
	
	/**
	 * Reads persistent properties
	 * @param project
	 * @return
	 * @throws ErrorAPI 
	 * @throws ErrorParser 
	 * @throws ErrorTransform 
	 * @throws ErrorUnsupported 
	 * @throws ErrorAlloy 
	 * @throws CoreException 
	 */
	public static ResourceManager loadProperties(IProject project) throws ErrorAlloy, ErrorUnsupported, ErrorTransform, ErrorParser, ErrorAPI, CoreException{
		String propertiesstring = null;
		propertiesstring = project.getPersistentProperty(ECHO_PROPERTIES);
		ResourceManager properties = null;
		if (propertiesstring != null) {
			properties = new ResourceManager(propertiesstring);
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
			project.setPersistentProperty(ECHO_PROPERTIES, properties.get(project).toString());
		} catch(CoreException e) { throw new ErrorParser(e.getMessage()); }		
	}
	


}
