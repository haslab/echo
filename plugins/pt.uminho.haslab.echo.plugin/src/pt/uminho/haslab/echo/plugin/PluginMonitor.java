package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.runtime.IProgressMonitor;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;

public class PluginMonitor implements IProgressMonitor {

	@Override
	public void beginTask(String name, int totalWork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalWorked(double work) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		// TODO Auto-generated method stub
		EchoReporter.getInstance().debug("CANCEL!!!!");		
	}

	@Override
	public void setTaskName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subTask(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void worked(int work) {
		// TODO Auto-generated method stub
		
	}
	

}
