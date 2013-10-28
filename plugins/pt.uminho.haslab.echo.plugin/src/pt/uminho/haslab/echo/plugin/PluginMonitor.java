package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.runtime.IProgressMonitor;

import pt.uminho.haslab.echo.Monitor;

public class PluginMonitor implements Monitor {

	private IProgressMonitor monitor;
	
	public PluginMonitor(IProgressMonitor m) {
		monitor = m;
	}

	@Override
	public void cancel() {
		if (monitor != null)
			monitor.setCanceled(true);
	}

	@Override
	public boolean isCancelled() {
		if (monitor != null)
			return monitor.isCanceled();
		return false;
	}
	

}
