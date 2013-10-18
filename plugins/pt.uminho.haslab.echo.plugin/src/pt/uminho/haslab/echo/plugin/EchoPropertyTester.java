package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;

public class EchoPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "pt.uminho.haslab.echo.plugin.propertyTester";
	public static final String PROPERTY_TRACKED = "tracked";
	
	/**
	 * Class used by the plugin to test resource properties
	 * In particular, tests if a XMI model is being tracked by the system
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (PROPERTY_TRACKED.equals(property)) {
			return expectedValue == null
					? testTracked(receiver)
					: testTracked(receiver) == ((Boolean) expectedValue).booleanValue();
		}
		return false;
	}

	/**
	 * Tests if a XMI model is being tracked by the system
	 * @param receiver the XMI model
	 * @return either the XMI model is being tracked
	 */
	private boolean testTracked(Object receiver) {
		if (receiver instanceof IFile) {
			IFile res = (IFile) receiver;
			String ext = res.getFileExtension();
			if (ext == null)
				return false;
			if (ext.equals("xmi"))
				return ProjectProperties.getProperties(res.getProject()).hasModel(res);
		}
		return false;
	}


}
