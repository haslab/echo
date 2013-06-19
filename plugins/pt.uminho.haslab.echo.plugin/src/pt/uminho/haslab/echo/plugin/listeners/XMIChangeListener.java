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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.emf.EMFParser;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.properties.QvtRelationProperty;

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
	        			 } else if(f!= null && f.getFileExtension().equals("ecore")){
		        			System.out.println("Refreshing "+f);
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
	    				System.out.println("Deleting "+f);
	    				Job j = new DeleteXMI(f);
						j.setRule(f);
						j.schedule();
	    			 } else if(f!= null && f.getFileExtension().equals("ecore")){
		    				System.out.println("Deleting "+f);
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
					EchoPlugin.getInstance().getEchoRunner().remModel(res.getFullPath().toString());
					ProjectProperties.getProjectProperties(res.getProject()).removeFromConformList(res.getFullPath().toString());
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
					EchoPlugin.getInstance().getEchoRunner().remMetamodel(res.getFullPath().toString());
					ProjectProperties.getProjectProperties(res.getProject()).removeMetaModel(res.getFullPath().toString());
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
				
	    		EchoRunner runner = EchoPlugin.getInstance().getEchoRunner();
	    		EMFParser parser = EchoPlugin.getInstance().getEchoParser();
	    		if (runner.hasMetamodel(res.getFullPath().toString())){
	    			System.out.println("Actually refreshing.");
					try {
						runner.remMetamodel(res.getFullPath().toString());
						EPackage metamodel = parser.loadMetamodel(res.getFullPath().toString());
						runner.addMetamodel(metamodel);
					} catch (Exception e) {
						runner.remMetamodel(res.getFullPath().toString());
						ProjectProperties.getProjectProperties(res.getProject()).removeMetaModel(res.getFullPath().toString());
						Display.getDefault().syncExec(new Runnable()
						{
							public void run()
							{
								MessageDialog.openError(shell, "Error reloading meta-model.", "Meta-model has been untracked.");
							}
						});
						return Status.CANCEL_STATUS;
					}
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
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				try {
					EchoRunner runner = EchoPlugin.getInstance().getEchoRunner();
					EMFParser parser = EchoPlugin.getInstance().getEchoParser();
					if (runner.hasModel(res.getFullPath().toString())) {
						EObject model = parser.loadModel(res.getFullPath().toString());
						runner.addModel(model);
						conformMeta(runner);
						conformQVT(runner);
					}
						
				} catch (Exception e) {
					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							MessageDialog.openError(shell, "Error reloading meta-model.", "Meta-model has been untracked.");
						}
					});
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
	    	
	    	private void conformMeta(EchoRunner er) throws ErrorAlloy, CoreException
	    	{
				EchoPlugin.getInstance().getAlloyView().clean();
	    		System.out.println("conformmeta");
	    		String path = res.getFullPath().toString();
				ArrayList<String> list = new ArrayList<String>(1);
				list.add(path);
				System.out.println(er.conforms(list));
				if(er.conforms(list))
					res.deleteMarkers(EchoMarker.INTRA_ERROR, false, 0);
				else {
					EchoMarker.createIntraMarker(res);		
				}
	    	}
	    	
	    	private void conformQVT(EchoRunner er) throws ErrorAlloy, CoreException
	    	{
	    		ProjectProperties pp = ProjectProperties.getProjectProperties(res.getProject());
				EchoPlugin.getInstance().getAlloyView().clean();
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
	    				}
	    			}
	    		
	    	}
	    }
	}
}
