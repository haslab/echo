package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.plugin.listeners.XMIChangeListener;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.views.AlloyModelView;

public class EchoPlugin extends Plugin {
	private static EchoPlugin instance;
	
	private EchoRunner echoRunner;
	private EMFParser echoParser;
	public final PlugInOptions options;
	
	
	private AlloyModelView AlloyView = null;
	
	
	public EchoPlugin()
	{
		super();
		instance = this;
		options = new PlugInOptions();
		try {
			echoParser= new EMFParser(options);
			echoRunner = new EchoRunner(options);
		} catch (ErrorAlloy | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public void start(BundleContext bc) throws Exception{
		super.start(bc);
		
		
		for (IProject p :ResourcesPlugin.getWorkspace().getRoot().getProjects())
			if(p.isOpen())
				ProjectProperties.getProjectProperties(p);
		
		IResourceChangeListener listener = new XMIChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			      listener, IResourceChangeEvent.POST_CHANGE);
	}

	
	public void refreshView()
	{
		if(AlloyView != null)
			AlloyView.refresh();
	}
	
	
	
	public static EchoPlugin getInstance(){
		return instance;
	}
	
	public EchoRunner getEchoRunner(){
		return echoRunner;
	}
	
	public EMFParser getEchoParser(){
		return echoParser;
	}

	public void deleteView() {
		AlloyView = null;
	}
	
	public AlloyModelView getAlloyView()
	{
		if (AlloyView == null)
			try {
				AlloyView = (AlloyModelView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("pt.uminho.haslab.echo.alloymodelview");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return AlloyView;
	}

	public void setAlloyView(AlloyModelView alloyView) {
		AlloyView = alloyView;
	}
}
