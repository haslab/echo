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

	public static final String ISPERSISTENT = "persistent";

	public static final boolean DEFAULT_ISPERSISTENT = false;
	public static final boolean DEFAULT_OBD = false;

	private boolean operationbased = DEFAULT_OBD;
	private boolean verbose = EchoOptionsSetup.DEFAULT_VERBOSE;
	
	public PlugInOptions() {
		EchoPlugin.getInstance().getPreferenceStore().setDefault(EchoOptionsSetup.BITWIDTH, EchoOptionsSetup.DEFAULT_BITWIDTH);
		EchoPlugin.getInstance().getPreferenceStore().setDefault(EchoOptionsSetup.SCOPE, EchoOptionsSetup.DEFAULT_SCOPE);
		EchoPlugin.getInstance().getPreferenceStore().setDefault(EchoOptionsSetup.DELTA, EchoOptionsSetup.DEFAULT_DELTA);
		EchoPlugin.getInstance().getPreferenceStore().setDefault(EchoOptionsSetup.OPTIMIZE, EchoOptionsSetup.DEFAULT_OPTIMIZE);
		EchoPlugin.getInstance().getPreferenceStore().setDefault(EchoOptionsSetup.OVERWITE, EchoOptionsSetup.DEFAULT_OVERWRITE);
		EchoPlugin.getInstance().getPreferenceStore().setDefault(ISPERSISTENT, DEFAULT_ISPERSISTENT);
	}

	@Override
	public boolean isVerbose() {
		return verbose;
	}

	@Override
	public boolean isOverwrite() {
		return EchoPlugin.getInstance().getPreferenceStore().getBoolean(EchoOptionsSetup.OVERWITE);
	}

	@Override
	public boolean isOptimize() {
		return EchoPlugin.getInstance().getPreferenceStore().getBoolean(EchoOptionsSetup.OPTIMIZE);
	}

	@Override
	public Integer getMaxDelta() {
		return EchoPlugin.getInstance().getPreferenceStore().getInt(EchoOptionsSetup.DELTA);
	}

	@Override
	public Integer getOverallScope() {
		return EchoPlugin.getInstance().getPreferenceStore().getInt(EchoOptionsSetup.SCOPE);
	}

	@Override
	public Integer getBitwidth() {
		return EchoPlugin.getInstance().getPreferenceStore().getInt(EchoOptionsSetup.BITWIDTH);
	}

	@Override
	public boolean isOperationBased() {
		return operationbased;
	}

	public void setOperationBased(boolean b) {
		operationbased = b;
	}

	public boolean isPersistent() {
		return EchoPlugin.getInstance().getPreferenceStore().getBoolean(ISPERSISTENT);
	}

	public void setIsPersistent(boolean b) {
		EchoPlugin.getInstance().getPreferenceStore().setValue(ISPERSISTENT,b);
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
		EchoPlugin.getInstance().getPreferenceStore().setValue(EchoOptionsSetup.BITWIDTH,n);
	}

	public void setScope(int n) {
		EchoPlugin.getInstance().getPreferenceStore().setValue(EchoOptionsSetup.SCOPE,n);
	}

	public void setDelta(int n) {
		EchoPlugin.getInstance().getPreferenceStore().setValue(EchoOptionsSetup.DELTA,n);
	}

	public void setOptimize(boolean n) {
		EchoPlugin.getInstance().getPreferenceStore().setValue(EchoOptionsSetup.OPTIMIZE,n);
	}

	public void setOverwrite(boolean n) {
		EchoPlugin.getInstance().getPreferenceStore().setValue(EchoOptionsSetup.OVERWITE,n);
	}

	public void goDefault() {
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(EchoOptionsSetup.OVERWITE);
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(EchoOptionsSetup.BITWIDTH);
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(EchoOptionsSetup.DELTA);
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(EchoOptionsSetup.OPTIMIZE);
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(EchoOptionsSetup.SCOPE);
		EchoPlugin.getInstance().getPreferenceStore().setToDefault(ISPERSISTENT);		
	}

}
