package pt.uminho.haslab.echo.plugin;

import pt.uminho.haslab.echo.EchoOptions;

import org.eclipse.core.resources.ResourcesPlugin;

public class PlugInOptions implements EchoOptions {

	boolean operationbased = false;
	
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
		return true;
	}

	@Override
	public Integer getMaxDelta() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public Integer getOverallScope() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Integer getBitwidth() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
		return ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toString();
	}

}
