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

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.PluginMonitor;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.MDEManager;

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
		List<String> modelIDs = new ArrayList<String>(1);
		IResource res = marker.getResource();

		try {

			if (ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res)) {

				modelIDs = Arrays.asList(((String) marker.getAttribute(EchoMarker.MODELS)).split(";"));
				((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(metric.equals(EchoMarker.OBD));
				List<String> aux = new ArrayList<String>();
				aux.add(MDEManager.getInstance().getModel(res.getFullPath().toString(),false).ID);
				Job j = new ModelRepairJob(aux, modelIDs, marker.getAttribute(EchoMarker.TRANSFORMATION).toString());
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
		} catch (EchoError | CoreException e1) {
			MessageDialog.openError(null, "Error loading QVT-R.",e1.getMessage());
			e1.printStackTrace();
			return;
		}	
	}



	public void run(IMarker[] markers,
            IProgressMonitor monitor) {
		List<String> modelsID = new ArrayList<String>();
		String constraintID;
		
		
		for(IMarker marker : markers)
			try {
				if (!ProjectPropertiesManager.getProperties(marker.getResource().getProject()).isManagedModel(marker.getResource())) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
					marker.delete();
				}
			} catch (CoreException | EchoError e) {
				e.printStackTrace();
			}
			
		try {
			modelsID =  Arrays.asList(((String) markers[0].getAttribute(EchoMarker.MODELS)).split(";"));
			constraintID = markers[0].getAttribute(EchoMarker.TRANSFORMATION).toString();

			((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(metric.equals(EchoMarker.OBD));
			List<String> targetIDs = new ArrayList<String>();
			for (IMarker marker : markers) {
				targetIDs.add(MDEManager.getInstance().getModel(marker.getResource().getFullPath().toString(),false).ID);
				//test if marker over same constraint
			}
			Job j = new ModelRepairJob(targetIDs, modelsID, constraintID);
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
						marker.getAttribute(EchoMarker.TRANSFORMATION).equals(caller.getAttribute(EchoMarker.TRANSFORMATION)))
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
		private List<String> targetIDs;
		private String transformationID = null;
		private List<String> modelIDs = null;
		
		public ModelRepairJob(List<String> targetIDs, List<String> modelIDs, String transformationID) {
			super("Repairing model "+targetIDs+".");
			this.targetIDs = targetIDs;
			this.transformationID = transformationID;
			this.modelIDs = modelIDs;
		}

		@Override
		public IStatus run(IProgressMonitor monitor)  {
			boolean suc  = false;
			try {
				suc = EchoPlugin.getInstance().getRunner().enforce(transformationID,modelIDs,targetIDs);
				List<String> targetURIs = new ArrayList<String>();
				for (String targetID : targetIDs)
					targetURIs.add(MDEManager.getInstance().getModelID(targetID).getURI());
				if (suc) {
					EchoPlugin.getInstance().getGraphView().setTargetPath(targetURIs,false,null);
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
