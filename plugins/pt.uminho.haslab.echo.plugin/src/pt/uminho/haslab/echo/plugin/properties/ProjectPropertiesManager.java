package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

/**
 * This class stores and manages the project properties assigned to each project
 * 
 * @author nmm
 * 
 */
public class ProjectPropertiesManager {

	private static final QualifiedName ECHO_PROPERTIES = new QualifiedName(
			"pt.uminho.haslab.echo", "properties");
	/** The project to properties map */
	private static Map<IProject, ResourceManager> properties = new HashMap<IProject, ResourceManager>();

	public static ResourceManager getProperties(IProject project) {
		ResourceManager res = properties.get(project);
		if (res == null
				&& ((PlugInOptions) EchoOptionsSetup.getInstance())
						.isPersistent()) {
			try {
				res = loadProperties(project);
			} catch (EchoError | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (res == null) {
			res = new ResourceManager();
		}
		properties.put(project, res);
		return res;
	}

	/**
	 * Reads persistent properties
	 * 
	 * @param project
	 * @return
	 * @throws ErrorAPI
	 * @throws ErrorParser
	 * @throws ErrorTransform
	 * @throws ErrorUnsupported
	 * @throws CoreException
	 * @throws ErrorInternalEngine
	 */
	public static ResourceManager loadProperties(IProject project)
			throws EchoError, CoreException {
		String propertiesstring = project.getPersistentProperty(ECHO_PROPERTIES);
		ResourceManager properties = null;
		if (propertiesstring != null) {
				properties = new ResourceManager();
			fromString(properties,propertiesstring);
		}
		ProjectPropertiesManager.properties.put(project, properties);
		return properties;
	}



	/**
	 * Writes persistent properties
	 * 
	 * @param project
	 * @throws ErrorParser
	 */
	public static void saveProjectProperties(IProject project)
			throws ErrorParser {
		try {
			project.setPersistentProperty(ECHO_PROPERTIES,
					toString(properties.get(project)));
		} catch (CoreException e) {
			throw new ErrorParser(e.getMessage());
		}
	}

	private static String toString(ResourceManager man) {
		StringBuilder builder = new StringBuilder();
		for (IResource res : man.getModels()) {
			builder.append(res.getFullPath().toString());
			builder.append(",");
		}
		builder.append(";");
		for (EConstraint c : man.getConstraints()) {
			builder.append(c.transformation);
			builder.append("@");
			builder.append(c.getModels().get(0));
			for (int i = 1; i < c.getModels().size(); i ++) {
				builder.append("@");
				builder.append(c.getModels().get(i));
			}
			builder.append(",");
		}
		return builder.toString();
	}
	
	private static void fromString(ResourceManager man, String propertiesstring) throws EchoError {
		String models = propertiesstring.split(";")[0];
		for (String model : models.split(",")) {
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(model);
			man.addModel(res);
		}

		String constraints = propertiesstring.split(",")[0];
		for (String constraint : constraints.split(",")) {
			String[] reses = constraint.split("@");
			IResource con = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(reses[0]);
			List<IResource> imodels = new ArrayList<IResource>();
			for (int i = 1; i < reses.length; i ++)
			imodels.add(ResourcesPlugin.getWorkspace().getRoot()
						.findMember(reses[i]));
		
			man.addQVTConstraint(con, imodels);
		}

	}

}
