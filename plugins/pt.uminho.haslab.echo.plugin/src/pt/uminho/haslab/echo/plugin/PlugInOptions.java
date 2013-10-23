package pt.uminho.haslab.echo.plugin;

import pt.uminho.haslab.echo.EchoOptions;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Implementation of the Echo options interface for the plugin
 * 
 * @author nmm
 * 
 */
public class PlugInOptions implements EchoOptions {

	boolean operationbased = false;

	public PlugInOptions() {
	}

	@Override
	public boolean isVerbose() {
		return true;
	}

	@Override
	public boolean isOverwrite() {
		return true;
	}

	@Override
	public boolean isOptimize() {
		return true;
	}

	@Override
	public Integer getMaxDelta() {
		return 20;
	}

	@Override
	public Integer getOverallScope() {
		return 1;
	}

	@Override
	public Integer getBitwidth() {
		return 2;
	}

	@Override
	public boolean isOperationBased() {
		return operationbased;
	}

	public void setOperationBased(boolean b) {
		operationbased = b;
	}

	@Override
	public String getWorkspacePath() {
		return ResourcesPlugin.getWorkspace().getRoot().getRawLocation()
				.toString();
	}

	@Override
	public boolean isStandalone() {
		return false;
	}

}
