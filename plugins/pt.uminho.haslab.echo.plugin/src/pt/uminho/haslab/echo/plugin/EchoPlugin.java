package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.engine.TransformFactory;
import pt.uminho.haslab.echo.plugin.listeners.ResourceChangeListener;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.echo.plugin.views.GraphView;
import pt.uminho.haslab.mde.EMFParser;

import java.net.URL;

public class EchoPlugin extends AbstractUIPlugin {
	
    public final static String ID = "pt.uminho.haslab.echo.plugin";
    public final static String ICONS_PATH = "icons/";
    public final static String QVT_ICON = "qvt.gif";
    public final static String XMI_ICON = "xmi.gif";

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


    private EchoRunner runner;

	/**
	 * Contructs a new Echo plugin
	 */
	public EchoPlugin() {
		super();
		instance = this;
        runner = new EchoRunner(TransformFactory.ALLOY);
		EchoOptionsSetup.init(new PlugInOptions());
		EchoReporter.init(new EchoReporter());
	}

	/**
	 * Starts the Echo plugin Loads the 
	 * Echo properties of each project and
	 * starts the listeners
	 */
	@Override
	public void start(BundleContext bc) throws Exception {
		super.start(bc);

		for (IProject p : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects())
			if (p.isOpen())
				ProjectPropertiesManager.getProperties(p);

		IResourceChangeListener listener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				IResourceChangeEvent.POST_CHANGE);
		getGraphView();
	}

	/**
	 * Initializes (if necessary) and retrieves the graph visualizer View
	 * 
	 * @return the graph visualizer view
	 */
	public GraphView getGraphView() {
		if (graphView == null)
			Display.getDefault().asyncExec(new Runnable() {
			    @Override
			    public void run() {
			        try {
						graphView = (GraphView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(GraphView.ID);
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
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
	
	/**
	 * Initializes and populates the Image Registry
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
        Bundle bundle = Platform.getBundle(ID);
        URL url; ImageDescriptor desc;
        
        url = FileLocator.findEntries(bundle, new Path(ICONS_PATH+QVT_ICON))[0];
		desc = ImageDescriptor.createFromURL(url);
		reg.put(QVT_ICON, desc);
        url = FileLocator.findEntries(bundle, new Path(ICONS_PATH+XMI_ICON))[0];
		desc = ImageDescriptor.createFromURL(url);
		reg.put(XMI_ICON, desc);

	}

    public EchoRunner getRunner() {
        return runner;
    }
}
