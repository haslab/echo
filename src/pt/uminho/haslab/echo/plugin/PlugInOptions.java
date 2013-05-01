package pt.uminho.haslab.echo.plugin;

import java.util.Map;
import java.util.Map.Entry;

import pt.uminho.haslab.echo.EchoOptions;

public class PlugInOptions implements EchoOptions {

	public PlugInOptions(){
		
	}
	
	@Override
	public boolean isVerbose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOverwrite() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isOptimize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<Entry<String, String>, Integer> getScopes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getMaxDelta() {
		// TODO Auto-generated method stub
		return 0;
	}

}
