package pt.uminho.haslab.echo.plugin;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoOptionsSetup.EchoOptions;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Implementation of the Echo options interface for the plugin
 * 
 * @author nmm
 * 
 */
public class PlugInOptions implements EchoOptions {

	private boolean operationbased = false;
	private int bitwidth = EchoOptionsSetup.DEFAULT_BITWIDTH;
	private int scope = EchoOptionsSetup.DEFAULT_SCOPE;
	private int delta = EchoOptionsSetup.DEFAULT_DELTA;
	private boolean verbose = EchoOptionsSetup.DEFAULT_VERBOSE;
	private boolean overwrite = EchoOptionsSetup.DEFAULT_OVERWRITE;
	private boolean optimize = EchoOptionsSetup.DEFAULT_OPTIMIZE;

	public PlugInOptions() {
	}

	@Override
	public boolean isVerbose() {
		return verbose;
	}

	@Override
	public boolean isOverwrite() {
		return overwrite;
	}

	@Override
	public boolean isOptimize() {
		return optimize;
	}

	@Override
	public Integer getMaxDelta() {
		return delta;
	}

	@Override
	public Integer getOverallScope() {
		return scope;
	}

	@Override
	public Integer getBitwidth() {
		return bitwidth;
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

	public void setBitwidth(int n) {
		bitwidth = n;
	}

	public void setScope(int n) {
		scope = n;
	}

	public void setDelta(int n) {
		delta = n;
	}

	public void setOptimize(boolean n) {
		optimize = n;
	}

}
