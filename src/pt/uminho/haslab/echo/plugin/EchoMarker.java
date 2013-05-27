package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EchoMarker {
	
	public final static String INTRA_ERROR = "pt.uminho.haslab.echo.plugin.intrainconsistency";
	public final static String INTER_ERROR = "pt.uminho.haslab.echo.plugin.interinconsistency";	

	public final static String GED = "ged";
	public final static String OPS = "ops";	

	public static IMarker createIntraMarker(IResource res) throws CoreException {
		IMarker mark = res.createMarker(EchoMarker.INTRA_ERROR);
		mark.setAttribute(IMarker.MESSAGE, "Model instance does not conform to the meta-model.");
		mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		return mark;
	}
	
}
