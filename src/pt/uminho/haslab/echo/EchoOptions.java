package pt.uminho.haslab.echo;

import java.util.Map;
import java.util.Map.Entry;

public interface EchoOptions {

	public boolean isVerbose();
	
	public boolean isHelp();
	
	public boolean isOverwrite();

	public boolean isOptimize();
			
	public Integer getSize();

	public Map<Entry<String,String>,Integer> getScopes();

	public Integer getMaxDelta();
	
}
