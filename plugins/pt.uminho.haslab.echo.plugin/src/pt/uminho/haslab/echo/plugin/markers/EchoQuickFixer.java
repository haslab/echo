package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Generates resolutions for Echo error markers
 * @author nmm
 *
 */
public class EchoQuickFixer implements IMarkerResolutionGenerator {
	public IMarkerResolution[] getResolutions(IMarker mk) {
       try {
          String problem = mk.getType();
          if (problem.equals(EchoMarker.INTRA_ERROR))
	          return new IMarkerResolution[] {
	             new EchoIntraQuickFix(EchoMarker.GED),
	             new EchoIntraQuickFix(EchoMarker.OBD),
	          };
          else if (problem.equals(EchoMarker.INTER_ERROR)) {
        	  return new IMarkerResolution[] {
        	             new EchoInterQuickFix(mk,EchoMarker.GED),
        	             new EchoInterQuickFix(mk,EchoMarker.OBD),
        	   	  };
          }
          else return new IMarkerResolution[0];
       }
       catch (CoreException e) {
          return new IMarkerResolution[0];
       }
    }
 }
