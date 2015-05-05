package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

public class EchoPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "pt.uminho.haslab.echo.plugin.propertyTester";
	public static final String PROPERTY_TRACKED = "tracked";
	public static final String PROPERTY_HASPROJECT = "has_project";

	/**
	 * Class used by the plugin to test resource properties
	 * In particular, tests if a XMI model is being tracked by the system
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (PROPERTY_TRACKED.equals(property)) {
			return (expectedValue == null)
					? testTracked(receiver)
							: (testTracked(receiver) == ((Boolean) expectedValue).booleanValue());
		} else if (PROPERTY_HASPROJECT.equals(property)) {
			return (expectedValue == null)
					? testHasProject(receiver)
							: (testHasProject(receiver) == ((Boolean) expectedValue).booleanValue());
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
				try {
					return ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res);
				} catch (EError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}
	
	private boolean testHasProject(Object receiver) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) return false;
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) return false;
		
		IEditorInput input = editor.getEditorInput();
		if (input == null) return false;
		
		IFile res = ((IFileEditorInput)input).getFile();
		return res != null;
	}


}
