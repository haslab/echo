package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;

public class EchoPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "pt.uminho.haslab.echo.plugin.propertyTester";
	public static final String PROPERTY_HAS_MARK = "hasmark";
	public static final String PROPERTY_TRACKED = "tracked";
	public static final String PROPERTY_DEPENDENCIES = "dependencies";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (PROPERTY_HAS_MARK.equals(property))
				return testMark(receiver);
		else if (PROPERTY_DEPENDENCIES.equals(property))
			return testMetaTracked(receiver);
		else if(PROPERTY_TRACKED.equals(property))
			if(expectedValue != null && expectedValue instanceof Boolean)
				return testTracked(receiver,((Boolean) expectedValue).booleanValue());
			else
				return testTracked(receiver, true);
		return false;
	}

	private boolean testTracked(Object receiver, boolean expected) {
		if(receiver instanceof IFile)
		{
			IFile res = (IFile) receiver;
			String uri = res.getFullPath().toString();
			String ext = res.getFileExtension();
			if(ext.equals("xmi"))
				return ProjectProperties.getProjectProperties(res.getProject()).getConformList().contains(uri) == expected;
		
			else if (ext.equals("ecore"))
				return ProjectProperties.getProjectProperties(res.getProject()).getMetaModels().contains(uri) == expected;
			
		}
		return false;
	}
	

	private boolean testMetaTracked(Object receiver) {
		if(receiver instanceof IFile)

{
			IFile res = (IFile) receiver;
			String uri = res.getFullPath().toString();
			String ext = res.getFileExtension();
			if(ext.equals("xmi")) {
				EchoRunner echo = EchoPlugin.getInstance().getEchoRunner();
				EObject obj = echo.parser.loadInstance(res.getFullPath().toString());
				String name = obj.eClass().getEPackage().getName();
				return echo.parser.getModelURI(name) != null;

			}
		
		}
		return true;
	}


	private boolean testMark(Object receiver)
	{
		if(receiver instanceof IResource)
		{
			IResource res = (IResource) receiver;
			try {
				if (res.findMarkers(EchoMarker.INTRA_ERROR, true, 0).length != 0) 
					return true;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	
	
}
