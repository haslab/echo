package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EchoMarker {
	
	public final static String INTRA_ERROR = "pt.uminho.haslab.echo.plugin.intrainconsistency";
	public final static String INTER_ERROR = "pt.uminho.haslab.echo.plugin.interinconsistency";	

	
	public final static String QVTR = "qvtr";
	public final static String OPPOSITE = "opposite";
	


	public final static String GED = "ged";
	public final static String OPS = "ops";	


	public static IMarker createIntraMarker(IResource res) throws CoreException {
		res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
		IMarker mark = res.createMarker(EchoMarker.INTRA_ERROR);
		mark.setAttribute(IMarker.MESSAGE, "Model instance does not conform to the meta-model.");
		mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		return mark;
	}
	
	public static IMarker createInterMarker(IResource res,IResource partner,IResource qvtRule) throws CoreException {
		IMarker mark = createSingleInterMarker(res,partner,qvtRule.getFullPath().toString());
		createSingleInterMarker(partner,res,qvtRule.getFullPath().toString());
		return mark;
	}
	 private static IMarker createSingleInterMarker(IResource res,IResource partner,String qvtRule) throws CoreException{
		 IMarker mark = res.createMarker(EchoMarker.INTER_ERROR);
		 mark.setAttribute(IMarker.MESSAGE, "Model instance is not consistent with a QVT relation");
		 mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		 mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		 mark.setAttribute(EchoMarker.QVTR, qvtRule);
		 mark.setAttribute(EchoMarker.OPPOSITE, partner.getFullPath().toString());
		 return mark;
		}
	 }
	 
	
