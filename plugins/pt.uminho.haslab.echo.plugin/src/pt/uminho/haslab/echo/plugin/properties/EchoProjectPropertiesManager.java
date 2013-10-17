package pt.uminho.haslab.echo.plugin.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.plugin.properties.EchoProjectProperties.QVTConstraintEntry;


public class EchoProjectPropertiesManager {
	
	private static QualifiedName ECHO_PROPERTIES = new QualifiedName("pt.uminho.haslab.echo","properties");
	private static Map<IProject,EchoProjectProperties> properties = new HashMap<IProject,EchoProjectProperties>();
	
	public static EchoProjectProperties loadProjectProperties(IProject project){
		String propertiesstring = null;
		try {
			propertiesstring = project.getPersistentProperty(ECHO_PROPERTIES);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EchoProjectProperties properties = new EchoProjectProperties();
		if (propertiesstring != null) {
		//	properties.readString(propertiesstring);
		}
		EchoProjectPropertiesManager.properties.put(project, properties);
		return properties;
	}

	private static void saveProjectProperties(IProject project) throws ErrorParser {
		try {
			project.setPersistentProperty(ECHO_PROPERTIES, properties.get(project).writeString());
		} catch(CoreException e) { throw new ErrorParser(e.getMessage()); }		
	}
	
	public static boolean hasModel(IProject project, String modeluri){
		EchoProjectProperties property = properties.get(project);
		return property.hasModel(modeluri);
	}
	
	public static boolean addModel(IProject project, String modeluri) throws ErrorParser{
		EchoProjectProperties property = properties.get(project);
		boolean res = property.addModel(modeluri);
		if (res) saveProjectProperties(project);
		return res;
	}
	
	public static boolean removeModel(IProject project, String modeluri) {
		EchoProjectProperties property = properties.get(project);
		boolean res =  property.removeModel(modeluri);
		if (res) try {saveProjectProperties(project);}
		catch(Exception e){}
		return res;
	}
	
	public static Set<String> getModels(IProject project){
		EchoProjectProperties property = properties.get(project);
		return property.getModels();
	}
	
	public static boolean addQVT(IProject project, String qvturi, List<String> modeluris) throws ErrorParser{
		EchoProjectProperties property = properties.get(project);
		boolean res = property.addQVT(qvturi,modeluris);
		if (res) saveProjectProperties(project);
		return res;
	}
	
	public static boolean remQVT(IProject project, String qvturi, List<String> modeluris) throws ErrorParser{
		EchoProjectProperties property = properties.get(project);
		boolean res = property.removeQVT(qvturi,modeluris);
		if (res) saveProjectProperties(project);
		return res;
	}
	
	public static Map<String,Set<String>> getQVTsModelFst (IProject project, String modeluri) {
		EchoProjectProperties property = properties.get(project);
		return property.getQVTsModelFst(modeluri);
	}

	public static Map<String,Set<String>> getQVTsModelSnd (IProject project, String modeluri) {
		EchoProjectProperties property = properties.get(project);
		return property.getQVTsModelSnd(modeluri);
	}
	
	public static List<QVTConstraintEntry> getQVTConstraints (IProject project) {
		EchoProjectProperties property = properties.get(project);
		return property.getQVTConstraints();
	}

}
