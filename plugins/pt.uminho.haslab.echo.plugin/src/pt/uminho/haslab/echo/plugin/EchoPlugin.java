package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.plugin.listeners.XMIChangeListener;
import pt.uminho.haslab.echo.plugin.properties.EchoProjectPropertiesManager;
import pt.uminho.haslab.echo.plugin.views.GraphView;

public class EchoPlugin extends Plugin {

	/** the current EchoPlugin instance **/
	private static EchoPlugin instance;

	/**
	 * Retrieves the EchoPlugin instance
	 * 
	 * @return the current EchoPlugin instance
	 */
	public static EchoPlugin getInstance() {
		return instance;
	}

	/** the graph visualizer View **/
	private GraphView graphView = null;

	/**
	 * Contructs a new Echo plugin
	 */
	public EchoPlugin() {
		super();
		instance = this;
		EchoOptionsSetup.init(new PlugInOptions());
		EchoReporter.init(new EchoReporter());
	}

	/**
	 * Starts the Echo plugin Loads the Echo properties of each project and
	 * starts the listeners
	 */
	@Override
	public void start(BundleContext bc) throws Exception {
		super.start(bc);

		for (IProject p : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects())
			if (p.isOpen())
				EchoProjectPropertiesManager.loadProjectProperties(p);

		IResourceChangeListener listener = new XMIChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Initializes (if necessary) and retrieves the graph visualizer View
	 * 
	 * @return the graph visualizer view
	 */
	public GraphView getGraphView() {
		if (graphView == null)
			try {
				graphView = (GraphView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.showView("pt.uminho.haslab.echo.alloymodelview");
			} catch (PartInitException e) {
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Error",
						"Could not find or launch Graph view.");
			}
		return graphView;
	}

	/**
	 * Sets a new graph visualizer view
	 * 
	 * @param graphView
	 *            the new graph visualizer view
	 */
	public void setGraphView(GraphView graphView) {
		this.graphView = graphView;
	}
}
