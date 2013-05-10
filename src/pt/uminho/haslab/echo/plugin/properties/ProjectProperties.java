package pt.uminho.haslab.echo.plugin.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;

public class ProjectProperties {
	
	
	private static Map<IProject,ProjectProperties> mapIProject = new HashMap<IProject,ProjectProperties>();
	
	public static ProjectProperties getProjectProperties(IProject project)
	{
		if (mapIProject.containsKey(project))
			return mapIProject.get(project);
		else
			return new ProjectProperties(project);
	}
	
	
	private static QualifiedName qnConformList = 
				new QualifiedName("pt.uminho.haslab.echo.properties.projectconformlist"
						,"projectconformlist");
	private static QualifiedName qnMetaModels = 
			new QualifiedName("pt.uminho.haslab.echo.properties.projectmetamodels"
					,"projectmetamodels");
	
	
	
	private IProject project;
	private Set<String> conformList;
	private String conformString;
	
	private Set<String> metaModels;
	private String metaModelsString;
	
	public ProjectProperties(IProject project){
		this.project = project;
		metaModels = loadMetaModels();
		conformList = loadConformList();
		mapIProject.put(project, this);		
	}
	
	private Set<String> loadMetaModels()
	{
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		Set<String> result = new HashSet<String>();
		try {
			metaModelsString = project.getPersistentProperty(qnMetaModels);
			if (metaModelsString != null)
				for(String s : metaModelsString.split(";"))
					if(s!=null)
					{
						try {
							result.add(s);
							er.addModel(s);
						} catch (ErrorUnsupported | ErrorAlloy | ErrorTransform
								| ErrorParser e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
		} catch (CoreException e) {
			e.printStackTrace();
		} 
		
		return result;	
	}
	
	private Set<String> loadConformList()
	{
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		Set<String> result = new HashSet<String>();
		try {
			conformString = project.getPersistentProperty(qnConformList);
			if (conformString != null)
				for(String s : conformString.split(";"))
					if(s!=null)
					{
						try {
							result.add(s);
							er.addInstance(s);
						} catch (ErrorUnsupported | ErrorAlloy | ErrorTransform
								| ErrorParser e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
		} catch (CoreException e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public Set<String> getConformList(){
		return conformList;
	}
	
	public Set<String> getMetaModels(){
		return metaModels;
	}
	
	public void addMetaModel(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser
	{
		if(!metaModels.contains(uri)){
			EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
			er.addModel(uri);
			if(metaModelsString != null)
				metaModelsString = metaModelsString + ";" + uri;
			else metaModelsString = uri;
			try {
				metaModelsString = metaModelsString + ";" + uri;
				project.setPersistentProperty(qnMetaModels, metaModelsString);
				metaModels.add(uri);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addConformList(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser
	{
		if(!conformList.contains(uri)){
			EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
			er.addInstance(uri);
			if(conformString != null)
				conformString = conformString + ";" + uri;
			else conformString = uri;
			try {
				conformString = conformString + ";" + uri;
				project.setPersistentProperty(qnConformList, conformString);
				conformList.add(uri);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String makeStringFromCollection(Collection<String> c)
	{
		String result = null;
		for(String s : c)
		{
			if(result != null)
				result = result + ";" + s;
			else
				result = s;
				
		}
		return result;
	}
	
	
	public void removeMetaModel(String uri)
	{
		metaModels.remove(uri);
		metaModelsString = makeStringFromCollection(metaModels);
		try {
			project.setPersistentProperty(qnMetaModels, metaModelsString);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeConformList(String uri)
	{
		conformList.remove(uri);
		conformString = makeStringFromCollection(conformList);
		try {
			project.setPersistentProperty(qnConformList, conformString);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
