package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;

public class EchoPlugin extends Plugin {
	private static EchoPlugin instance;
	
	private EchoRunner echoRunner;
	
	
	
	
	
	
	public EchoPlugin()
	{
		super();
		instance = this;
		try {
			echoRunner = new EchoRunner(new PlugInOptions());
		} catch (ErrorAlloy | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (IProject p :ResourcesPlugin.getWorkspace().getRoot().getProjects())
			if(p.isOpen())
				ProjectProperties.getProjectProperties(p);
		
	}
	
	
	public static EchoPlugin getInstance(){
		return instance;
	}
	
	public EchoRunner getEchoRunner(){
		return echoRunner;
	}
}
