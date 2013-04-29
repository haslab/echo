package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.runtime.Plugin;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;

public class EchoPlugin extends Plugin {
	private static EchoPlugin instance;
	
	private EchoRunner echoRunner;	
	
	public EchoPlugin()
	{
		super();
		instance = this;
		try {
			echoRunner = new EchoRunner(new PlugInOptions());
		} catch (ErrorParser | ErrorAlloy | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static EchoPlugin getInstance(){
		return instance;
	}
	
	public EchoRunner getEchoRunner(){
		return echoRunner;
	}
}
