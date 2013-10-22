package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.transform.EchoTranslator;

/**
 * Marker resolution for inter-model errors
 * @author nmm
 *
 */
public class EchoInterQuickFix extends WorkbenchMarkerResolution implements IMarkerResolution {
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
		EchoRunner echo = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		ArrayList<String> list = new ArrayList<String>(1);
		IResource res = marker.getResource();
		String path = res.getFullPath().toString();

		try {
			RelationalTransformation trans = parser.getTransformation(marker.getAttribute(EchoMarker.CONSTRAINT).toString());
			String metadir = EchoTranslator.getInstance().getModelStateSig(path).parent.label;
			if (metadir.equals(URIUtil.resolveURI(trans.getModelParameter().get(0).getUsedPackage().get(0).getEPackage().eResource()))) {
				list.add(path);
				list.add(marker.getAttribute(EchoMarker.OPPOSITE).toString());
			} else {
				list.add(marker.getAttribute(EchoMarker.OPPOSITE).toString());
				list.add(path);
			}
		} catch (Exception e1) {
			MessageDialog.openError(null, "Error loading QVT-R.",e1.getMessage());
			e1.printStackTrace();
			return;
		}

		((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(metric.equals(EchoMarker.OBD));
		boolean b;
		try {
			b = echo.enforce(marker.getAttribute(EchoMarker.CONSTRAINT).toString(),list, path);
			while(!b) b = echo.increment();
		} catch (Exception e) {
			MessageDialog.openError(null, "Error loading QVT-R.",e.getMessage());
			e.printStackTrace();
			return;
		}
		EchoPlugin.getInstance().getGraphView().setTargetPath(path,false,null);
		EchoPlugin.getInstance().getGraphView().drawGraph();
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
