package pt.uminho.haslab.echo.plugin.listeners;

import java.util.ArrayList;

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
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoMarker;
import pt.uminho.haslab.echo.plugin.EchoPlugin;




public class XMIChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
      		//System.out.println("Resources have changed.");
      		try {
      			event.getDelta().accept(new DeltaPrinter());
      			} catch (CoreException e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      		break;		
		}
      
	}
	
	
	class DeltaPrinter implements IResourceDeltaVisitor {
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
	        				String path = f.getFullPath().toString();
	        				System.out.println(path);
	        				ArrayList<String> list = new ArrayList<String>(1);
	        				list.add(path);
	        				try {
	        					EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
	        					er.addInstance(f.getFullPath().toString());
	        					Job j = new ConformsJob(f,er.conforms(list));
	   	        			 	j.setRule(f);
	   	        			 	j.schedule();	
	        				} catch (ErrorAlloy | ErrorUnsupported | ErrorTransform | ErrorParser e) {
	        					// TODO Auto-generated catch block
	        					e.printStackTrace();
	        				}
	        				 
	        			 }
	        		 }
	             break;
	       		}
	       return true; // visit the children
	    }
	    
	    class ConformsJob extends WorkspaceJob {
	    	private IResource res;
	    	private boolean bool;
	    	
	    	
	    	
	    	public ConformsJob(IResource r, boolean b)
	    	{
	    		super("Conforms Job");
	    		res = r;
	    		bool = b;
	    	}
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				if (res!=null && !bool) res.createMarker(EchoMarker.META_ERROR);
				else if (res !=null && bool)
					res.deleteMarkers(EchoMarker.META_ERROR, false, 0);
				return Status.OK_STATUS;
			}
	    }
	}
}
