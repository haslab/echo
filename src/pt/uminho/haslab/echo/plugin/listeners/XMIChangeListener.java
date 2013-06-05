package pt.uminho.haslab.echo.plugin.listeners;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
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
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.properties.QvtRelationProperty;




public class XMIChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
      		//System.out.println("Resources have changed.");
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
	        				System.out.println(f);
	        				Job j = new ConformsJob(f);
							j.setRule(f);
							j.schedule();
	        				 
	        			 }
	        		 }
	             break;
	       		}
	       return true; // visit the children
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
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				try {
					EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
					er.addInstance(res.getFullPath().toString());
					System.out.println(res);
					conformMeta(er);
					conformQVT(er);
					
						
				} catch (ErrorAlloy | ErrorUnsupported | ErrorTransform | ErrorParser e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
	    	
	    	private void conformMeta(EchoRunner er) throws ErrorAlloy, CoreException
	    	{
	    		System.out.println("conformmeta");
	    		String path = res.getFullPath().toString();
				ArrayList<String> list = new ArrayList<String>(1);
				list.add(path);
				System.out.println(er.conforms(list));
				if(er.conforms(list))
					res.deleteMarkers(EchoMarker.INTRA_ERROR, false, 0);
				else {
					EchoMarker.createIntraMarker(res);		
					EchoPlugin.getInstance().getAlloyView().clean();
				}
	    	}
	    	
	    	private void conformQVT(EchoRunner er) throws ErrorAlloy, CoreException
	    	{
	    		ProjectProperties pp = ProjectProperties.getProjectProperties(res.getProject());
	    		
	    		for(QvtRelationProperty qrp : pp.getQvtRelations())
	    			if(qrp.getModels().contains(res.getFullPath().toString()))
	    			{
	    				String path;
    					if(res.getFullPath().toString().equals(qrp.getModelA()))
    						path = qrp.getModelB();
    					else
    						path = qrp.getModelA();
    					IResource partner = res.getWorkspace().getRoot().findMember(path);
	    				if(er.check(qrp.getQVTrule(), qrp.getModels()))
	    				{	
	    					for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR, false, 0))
	    						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(path) &&
	    								mk.getAttribute(EchoMarker.QVTR).equals(qrp.getQVTrule()))
	    							mk.delete();
	    					for(IMarker mk : partner.findMarkers(EchoMarker.INTER_ERROR, false, 0))
	    						if(mk.getAttribute(EchoMarker.OPPOSITE).equals(res.getFullPath().toString()) &&
	    								mk.getAttribute(EchoMarker.QVTR).equals(qrp.getQVTrule()))
	    							mk.delete();
	    				}
	    				else {
	    					EchoMarker.createInterMarker(res,partner,qrp.getQVTrule());
	    					EchoPlugin.getInstance().getAlloyView().clean();
	    				}
	    			}
	    		
	    	}
	    }
	}
}
