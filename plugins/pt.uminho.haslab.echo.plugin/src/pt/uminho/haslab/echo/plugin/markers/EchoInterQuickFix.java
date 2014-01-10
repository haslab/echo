package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.PluginMonitor;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.emf.EMFParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Marker resolution for inter-model errors
 * @author nmm
 *
 */
public class EchoInterQuickFix extends	WorkbenchMarkerResolution {
	/** The quick fix message */  
	private String message;
	/** The model distance metric */
	private String metric;
	private IMarker caller;

	EchoInterQuickFix(IMarker caller, String metric) {
		this.metric = metric;
		this.caller = caller;
		if (metric.equals(EchoMarker.OBD))
			this.message = "Repair inter-model inconsistency through operation-based distance.";
		else
			this.message = "Repair inter-model inconsistency through graph edit distance.";
	}

	@Override
	public String getLabel() {
		return message;
	}

	/**
	 * Calls the Echo runner to fix a marker 
	 */
	@Override
	public void run(IMarker marker) {
		List<String> list = new ArrayList<String>(1);
		IResource res = marker.getResource();

		if (ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res)) {

			try {
				list =  Arrays.asList(((String) marker.getAttribute(EchoMarker.MODELS)).split(";"));

				((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(metric.equals(EchoMarker.OBD));
				List<String> aux = new ArrayList<String>();
				aux.add(res.getFullPath().toString());
				Job j = new ModelRepairJob(aux, list, marker.getAttribute(EchoMarker.CONSTRAINT).toString());
				IJobManager manager = Job.getJobManager();
				
				final PluginMonitor p = new PluginMonitor();
				ProgressProvider provider = new ProgressProvider() {
				  @Override
				  public IProgressMonitor createMonitor(Job job) {
				    return p;
				  }
				};
				manager.setProgressProvider(provider);
				j.setRule(new ResourceRules(res,ResourceRules.WRITE));
				j.schedule();

			} catch (Exception e1) {
				MessageDialog.openError(null, "Error loading QVT-R.",e1.getMessage());
				e1.printStackTrace();
				return;
			}			
			
		} else {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}



	public void run(IMarker[] markers,
            IProgressMonitor monitor) {
		EchoReporter.getInstance().debug("run: "+markers);
		List<String> constraintmodels = new ArrayList<String>();
		String constraint;
		
		
		for(IMarker marker : markers)
			if (!ProjectPropertiesManager.getProperties(marker.getResource().getProject()).isManagedModel(marker.getResource())) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
				try {
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}	
				}
			
		try {
			constraintmodels =  Arrays.asList(((String) markers[0].getAttribute(EchoMarker.MODELS)).split(";"));
			constraint = markers[0].getAttribute(EchoMarker.CONSTRAINT).toString();

			((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(metric.equals(EchoMarker.OBD));
			List<String> aux = new ArrayList<String>();
			for (IMarker marker : markers) {
				aux.add(marker.getResource().getFullPath().toString());
				//test if marker over same constraint
			}
			Job j = new ModelRepairJob(aux, constraintmodels, constraint);
			IJobManager manager = Job.getJobManager();
			
			final PluginMonitor p = new PluginMonitor();
			ProgressProvider provider = new ProgressProvider() {
			  @Override
			  public IProgressMonitor createMonitor(Job job) {
			    return p;
			  }
			};
			manager.setProgressProvider(provider);
			for (IMarker marker : markers)
				j.setRule(new ResourceRules(marker.getResource(),ResourceRules.WRITE));
			j.schedule();

		} catch (Exception e1) {
			MessageDialog.openError(null, "Error loading QVT-R.",e1.getMessage());
			e1.printStackTrace();
			return;
		}			

	}
	
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMarker[] findOtherMarkers(IMarker[] markers) {
		ArrayList<IMarker> res = new ArrayList<IMarker>();
		for (IMarker marker : markers) {
			try {
				if (marker.getType().equals(EchoMarker.INTER_ERROR))
					if (marker.getAttribute(EchoMarker.MODELS).equals(caller.getAttribute(EchoMarker.MODELS)) &&
						marker.getAttribute(EchoMarker.CONSTRAINT).equals(caller.getAttribute(EchoMarker.CONSTRAINT)))
					res.add(marker);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		res.remove(caller);

		return res.toArray(new IMarker[res.size()]);
	}



	class ModelRepairJob extends Job {
		private List<String>  targets;
		private String constraint = null;
		private List<String> list = null;
		
		public ModelRepairJob(List<String> targets, List<String> list, String constraint) {
			super("Repairing model "+targets+".");
			this.targets = targets;
			this.constraint = constraint;
			this.list = list;
		}

		@Override
		public IStatus run(IProgressMonitor monitor)  {
			boolean suc  = false;
			try {
				suc = EchoPlugin.getInstance().getRunner().enforce(constraint,list,targets);
				if (suc) {
					EchoPlugin.getInstance().getGraphView().setTargetPath(targets,false,null);
					EchoPlugin.getInstance().getGraphView().drawGraph();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
		
	}

}
