package pt.uminho.haslab.echo.plugin.listeners;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

public class XMIChangeListener implements IResourceChangeListener {

	Shell shell;
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		    public void run() {
		    	shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
		});
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
      		//EchoReporter.getInstance().debug("Resources have changed.");
      		try {
      			event.getDelta().accept(new DeltaIterator());
      			} catch (CoreException e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      		break;		
	}
      
	}
	
	
	class DeltaIterator implements IResourceDeltaVisitor {
	    public boolean visit(IResourceDelta delta) {
	       IResource res = delta.getResource();
	       switch (delta.getKind()) {
	          case IResourceDelta.CHANGED:
	             int flags = delta.getFlags();
	        	 if((flags & IResourceDelta.MARKERS) == 0)
	        		 if (res instanceof IFile)
	        		 {
	        			 IFile f = (IFile) res;
	        			 if(f!= null && f.getFileExtension().equals("xmi")){
	        				//EchoReporter.getInstance().debug(f+"");
	        				Job j = new ConformsJob(f);
							j.setRule(f);
							j.schedule();
	        			 } else if(f!= null && f.getFileExtension().equals("ecore")){
		        			//EchoReporter.getInstance().debug("Refreshing "+f);
		        			Job j = new RefreshMetaJob(f);
		        			j.setRule(f);
							j.schedule();
	        			 }

	        		 }
	             break;
	          case IResourceDelta.REMOVED:
	             if (res instanceof IFile) {
	    			 IFile f = (IFile) res;
	    			 if(f!= null && f.getFileExtension().equals("xmi")){
	    				//EchoReporter.getInstance().debug("Deleting "+f);
	    				Job j = new DeleteXMI(f);
						j.setRule(f);
						j.schedule();
	    			 } else if(f!= null && f.getFileExtension().equals("ecore")){
		    				//EchoReporter.getInstance().debug("Deleting "+f);
	        			Job j = new DeleteMeta(f);
	        			j.setRule(f);
						j.schedule();
	    			 }
	
	    		 }
		        	 break;
	       		}
	       return true; // visit the children
	    }
	    
	    class DeleteXMI extends WorkspaceJob {
	    	private IResource res =null;
	    	//private boolean bool;
	    	
	    	public DeleteXMI(IResource r)
	    	{
	    		super("Deleting instance.");
	    		res = r;
	    	}
			
	    	@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				try {
					EchoRunner.getInstance().remModel(res.getFullPath().toString());
					ProjectPropertiesManager.getProperties(res.getProject()).remModel(res);
				} catch (Exception e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
	    }
	    
	    class DeleteMeta extends WorkspaceJob {
	    	private IResource res =null;
	    	//private boolean bool;
	    	
	    	public DeleteMeta(IResource r)
	    	{
	    		super("Deleting meta-model.");
	    		res = r;
	    	}
			
	    	@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				try {
					EchoRunner.getInstance().remMetamodel(res.getFullPath().toString());
					//ProjectProperties.getProjectProperties(res.getProject()).removeMetaModel(res.getFullPath().toString());
				} catch (Exception e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
	    }
	    
	    
	    class RefreshMetaJob extends WorkspaceJob {
	    	private IResource res =null;
	    	//private boolean bool;
	    	
	    	public RefreshMetaJob(IResource r)
	    	{
	    		super("Refreshing meta-model.");
	    		res = r;
	    	}
			
	    	@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
	    		
				ResourceManager resmanager = ProjectPropertiesManager.getProperties(res.getProject());
				try {
					if (resmanager.hasMetamodel(res))
						resmanager.reloadMetamodel(res);
				} catch (Exception e) {
					try {
						resmanager.remMetamodel(res);
					} catch (ErrorAPI | ErrorParser e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
					//ProjectProperties.getProjectProperties(res.getProject()).removeMetaModel(res.getFullPath().toString());
					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							MessageDialog.openError(shell, "Error reloading meta-model.", "Meta-model has been untracked.");
						}
					});
					return Status.CANCEL_STATUS;
				}
	    		return Status.OK_STATUS;
			}
	    	
	    }
	    
	    class ConformsJob extends WorkspaceJob {
	    	private IResource res =null;
	    	//private boolean bool;	    	
	    	
	    	public ConformsJob(IResource r)
	    	{
	    		super("Conformity Tester");
	    		res = r;
	    	}
			
	    	@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				
				try {
					ResourceManager resmanager = ProjectPropertiesManager.getProperties(res.getProject());
					if (resmanager.hasModel(res))
						resmanager.reloadModel(res);
						
				} catch (Exception e) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(shell, "Error reloading model.", "Error updating model.");
						}
					});
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
	    	
	    	
	    		
	    	
	    }
	}
}
