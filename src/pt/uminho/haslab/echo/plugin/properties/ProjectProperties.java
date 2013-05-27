package pt.uminho.haslab.echo.plugin.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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


//TODO: THIS PACKAGE NEEDS TO BE REFACTORED!!!
public class ProjectProperties {
	
	
	private static Map<IProject,ProjectProperties> mapIProject = new HashMap<IProject,ProjectProperties>();
	
	public static ProjectProperties getProjectProperties(IProject project)
	{
		if (mapIProject.containsKey(project))
			return mapIProject.get(project);
		else
			return new ProjectProperties(project);
	}
	
	
	//QN for parsed models
	private static QualifiedName qnConformList = 
				new QualifiedName("pt.uminho.haslab.echo.properties.projectconformlist"
						,"projectconformlist");
	
	//QN for parsed Meta-models
	private static QualifiedName qnMetaModels = 
			new QualifiedName("pt.uminho.haslab.echo.properties.projectmetamodels"
					,"projectmetamodels");
	
	//QN for parsed QVTr files
	private static QualifiedName qnQvtRules = 
			new QualifiedName("pt.uminho.haslab.echo.properties.projectqvtrules"
					,"projectqvtrules");
	
	//QN for relations to keep;
	private static QualifiedName qnRelations = 
			new QualifiedName("pt.uminho.haslab.echo.properties.projectqvtrelations"
			,"projectqvtrelations");;
	
			
	private IProject project;
	private Set<String> conformList;
	private String conformString;
	
	private Set<String> metaModels;
	private String metaModelsString;
	
	private Set<String> qvtRules;
	private String qvtRulesString;
	
	private Set<QvtRelationProperty> qvtRelations;
	private String qvtRelationsString;
	
	public ProjectProperties(IProject project){
	
		
		this.project = project;
		metaModels = loadMetaModels();
		conformList = loadConformList();
		qvtRules = loadQvtRules();
		qvtRelations = loadRelations();
		mapIProject.put(project, this);
	}
	
	
	private Set<QvtRelationProperty> loadRelations(){
		
		//EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		Set<QvtRelationProperty> result = new HashSet<QvtRelationProperty>();
		try {
			qvtRelationsString = project.getPersistentProperty(qnRelations);
			System.out.println(qvtRelationsString);
			if (qvtRelationsString != null && qvtRelationsString != "")
				for(String s : qvtRelationsString.split(";"))
					if(s!=null)
					{
						result.add(QvtRelationProperty.parseString(s));
						//er.addQVT(s);
					}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} 
		
		return result;
	}
	
	private Set<String> loadQvtRules() {
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		Set<String> result = new HashSet<String>();
		try {
			qvtRulesString = project.getPersistentProperty(qnQvtRules);
			if (qvtRulesString != null)
				for(String s : qvtRulesString.split(";"))
					if(s!=null)
					{
						try {
							result.add(s);
							er.addQVT(s);
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

	private Set<String> loadMetaModels()
	{
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		Set<String> result = new HashSet<String>();
		try {
			metaModelsString = project.getPersistentProperty(qnMetaModels);
			if (metaModelsString != null && metaModelsString != "")
				for(String s : metaModelsString.split(";"))
					if(s!=null && s!= "")
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
	
	public Set<String> getQvtRules(){
		return qvtRules;
	}
	
	public Set<QvtRelationProperty> getQvtRelations()
	{
		return qvtRelations;
	}
	
	
	public void addQvtRelation(String qvtRule, List<String> models)
	{
		QvtRelationProperty qvtR = new QvtRelationProperty(qvtRule,models);
		System.out.println(qvtR);
		if(!qvtRelations.contains(qvtR)){
			//EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
			//er.addQVT(uri);
			if(qvtRelationsString != null)
				qvtRelationsString = qvtRelationsString + ";" + qvtR.toString();
			else
				qvtRelationsString = qvtR.toString();
			try {
				project.setPersistentProperty(qnRelations, qvtRelationsString);
				
				System.out.println("objecto: " + qvtR.toString() +"\nString: " + qvtRelationsString);
				qvtRelations.add(qvtR);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addQvtRelation(String relation) throws Exception
	{
		QvtRelationProperty qvtR = QvtRelationProperty.parseString(relation);
		if(!qvtRelations.contains(qvtR)){
			//EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
			//er.addQVT(uri);
			if(qvtRelationsString != null)
				qvtRelationsString = qvtRelationsString + ";" + qvtR.toString();
			else qvtRulesString = qvtR.toString();
			try {
				//asd
				project.setPersistentProperty(qnRelations, qvtRelationsString);
				qvtRelations.add(qvtR);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void addQvtRule(String uri) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser
	{
		if(!qvtRules.contains(uri)){
			EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
			er.addQVT(uri);
			if(qvtRulesString != null)
				qvtRulesString = qvtRulesString + ";" + uri;
			else qvtRulesString = uri;
			try {
				//asd
				project.setPersistentProperty(qnQvtRules, qvtRulesString);
				qvtRules.add(uri);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				//metaModelsString = metaModelsString + ";" + uri;
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
				//conformString = conformString + ";" + uri;
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
	
	public void removeFromConformList(String uri)
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
