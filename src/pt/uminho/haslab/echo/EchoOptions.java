package pt.uminho.haslab.echo;

import java.util.Map;
import java.util.Map.Entry;

public interface EchoOptions {

	/** if the output should be verbose */
	public boolean isVerbose();
	
	/** if the generated instances should overwrite the original ones */
	public boolean isOverwrite();

	/** if Echo should try to simplify expressions */
	public boolean isOptimize();
			
	/** the overall Alloy scope */
	public Integer getSize();

	/** the concrete scope for particular classes ((model,class) -> scope) */
	public Map<Entry<String,String>,Integer> getScopes();

	/** the maximum delta for an instance generation run */
	public Integer getMaxDelta();
	
}
