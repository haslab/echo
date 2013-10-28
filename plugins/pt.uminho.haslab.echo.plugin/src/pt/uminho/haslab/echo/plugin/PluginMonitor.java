package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.runtime.IProgressMonitor;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
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
		EchoReporter.getInstance().debug("I'v just been tested if cancelled!!");
		if (monitor != null)
			return monitor.isCanceled();
		return false;
	}
	

}
