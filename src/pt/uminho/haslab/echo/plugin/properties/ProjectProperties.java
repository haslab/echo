package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectProperties {
	
	
	private static QualifiedName qnConformList = 
				new QualifiedName("pt.uminho.haslab.echo.properties.projectconformlist"
						,"projectconformlist");
	
	private IProject project;
	private List<String> conformList;
	
	public ProjectProperties(IProject project){
		this.project = project;
		conformList = loadConformList();
		
	}
	
	private List<String> loadConformList()
	{
		List<String> result = new ArrayList<String>();
		try {
			String property = project.getPersistentProperty(qnConformList);
			if (property != null)
				for(String s : property.split(";"))
					result.add(s);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public List<String> getConformList(){
		return conformList;
	}
	
	public void addConformList(String uri)
	{
		;
	}
	
	public void removeConformList(String uri)
	{
		;
	}
	
}
