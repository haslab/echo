package pt.uminho.haslab.echo.plugin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Plugin;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;

public class EchoPlugin extends Plugin {
	private static EchoPlugin instance;
	
	private EchoRunner echoRunner;
	private Set<String> Models;
	private Set<String> MetaModels;
	
	
	public EchoPlugin()
	{
		super();
		initializeModels();
		instance = this;
		try {
			echoRunner = new EchoRunner(new PlugInOptions());
		} catch (ErrorAlloy | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initializeModels(){
		Models = new HashSet<String>();
		MetaModels = new HashSet<String>();
	}
	
	public Set<String> getModels(){return Models;}
	
	public Set<String> getMetaModels(){return MetaModels;}
	
	public static EchoPlugin getInstance(){
		return instance;
	}
	
	public EchoRunner getEchoRunner(){
		return echoRunner;
	}
}
