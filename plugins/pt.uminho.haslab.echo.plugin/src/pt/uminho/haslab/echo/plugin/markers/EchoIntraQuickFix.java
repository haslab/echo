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
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.PluginMonitor;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EModel;

/**
 * Marker resolution for intra-model errors
 * 
 * @author nmm
 * 
 */
public class EchoIntraQuickFix  implements IMarkerResolution {
	/** The quick fix message */
	private String message;
	/** The model distance metric */
	private String metric;

	EchoIntraQuickFix(String metric) {
		this.metric = metric;
		if (metric.equals(EchoMarker.OBD))
			this.message = "Repair intra-model inconsistency through operation-based distance.";
		else
			this.message = "Repair intra-model inconsistency through graph edit distance.";
	}

	@Override
	public String getLabel() {
		return message;
	}

	@Override
	public void run(IMarker marker) {
		IResource res = marker.getResource();

		((PlugInOptions) EchoOptionsSetup.getInstance())
				.setOperationBased(metric.equals(EchoMarker.OBD));
		try {
			if (ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res)) {
				Job j = new ModelRepairJob(res);
				
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
			} else {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
					marker.delete();
				
			}
		} catch (CoreException | EError e) {
			e.printStackTrace();
		}
	}
	
	class ModelRepairJob extends Job {
		private IResource res = null;

		public ModelRepairJob(IResource r) {
			super("Repairing model "+r.getName()+".");
			res = r;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				EModel model = MDEManager.getInstance().getModel(res.getFullPath().toString(), false);
				EchoPlugin.getInstance().getRunner().repair(model.ID);
				EchoPlugin.getInstance().getGraphView().setTargetID(model.ID, false, null);
				EchoPlugin.getInstance().getGraphView().drawGraph();
			} catch (Exception e) {
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
		
	}

}
