package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

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
		String path = res.getFullPath().toString();

		((PlugInOptions) EchoOptionsSetup.getInstance())
				.setOperationBased(metric.equals(EchoMarker.OBD));

		if (ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res)) {
			Job j = new ModelRepairJob(res);
			j.setRule(new ResourceRules(res));
			j.schedule();
		} else {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
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
				EchoPlugin.getInstance().getRunner().repair(res.getFullPath().toString());
				EchoPlugin.getInstance().getGraphView().setTargetPath(res.getFullPath().toString(), false, null);
				EchoPlugin.getInstance().getGraphView().drawGraph();
			} catch (Exception e) {
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
		
	}

}
