package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.PluginMonitor;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

/**
 * Marker resolution for inter-model errors
 * @author nmm
 *
 */
public class EchoInterQuickFix implements IMarkerResolution {
	/** The quick fix message */  
	private String message;
	/** The model distance metric */
	private String metric;

	EchoInterQuickFix(String metric) {
		this.metric = metric;
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
				
				Job j = new ModelRepairJob(res, list, marker.getAttribute(EchoMarker.CONSTRAINT).toString());
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

	class ModelRepairJob extends Job {
		private IResource res = null;
		private String constraint = null;
		private List<String> list = null;
		
		public ModelRepairJob(IResource r, List<String> list, String constraint) {
			super("Repairing model "+r.getName()+".");
			res = r;
			this.constraint = constraint;
			this.list = list;
		}

		@Override
		public IStatus run(IProgressMonitor monitor)  {
			boolean suc  = false;
			try {
				suc = EchoPlugin.getInstance().getRunner().enforce(constraint,list, res.getFullPath().toString());
				if (suc) {
					EchoPlugin.getInstance().getGraphView().setTargetPath(res.getFullPath().toString(),false,null);
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
