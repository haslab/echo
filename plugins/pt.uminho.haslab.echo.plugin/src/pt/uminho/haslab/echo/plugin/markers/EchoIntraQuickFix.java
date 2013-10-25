package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

/**
 * Marker resolution for intra-model errors
 * 
 * @author nmm
 * 
 */
public class EchoIntraQuickFix extends WorkbenchMarkerResolution implements IMarkerResolution {
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
		EchoRunner echo = EchoRunner.getInstance();
		IResource res = marker.getResource();
		String path = res.getFullPath().toString();

		((PlugInOptions) EchoOptionsSetup.getInstance())
				.setOperationBased(metric.equals(EchoMarker.OBD));

		if (ProjectPropertiesManager.getProperties(res.getProject()).isManagedModel(res)) {
			try {
		        echo.repair(path);
			} catch (ErrorAlloy e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error loading QVT-R.",e.getMessage());
				e.printStackTrace();
			}
			EchoPlugin.getInstance().getGraphView()
					.setTargetPath(path, false, null);
			EchoPlugin.getInstance().getGraphView().drawGraph();
		} else {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error repairing resource.","Resource is no longer tracked.");
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getDescription() {
		return "teste";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMarker[] findOtherMarkers(IMarker[] markers) {
		return new IMarker[0];
	}

}
