package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class EchoQuickFixer implements IMarkerResolutionGenerator {
    public IMarkerResolution[] getResolutions(IMarker mk) {
       try {
          String problem = mk.getType();
          if (problem.equals(EchoMarker.INTRA_ERROR))
	          return new IMarkerResolution[] {
	             new EchoIntraQuickFix(EchoMarker.GED),
	             new EchoIntraQuickFix(EchoMarker.OPS),
	          };
          else if (problem.equals(EchoMarker.INTER_ERROR)) {
        	  List<EchoInterQuickFix> fixes = new ArrayList<EchoInterQuickFix>();
        	  for (String dir : new ArrayList<String>()){
        		 fixes.add(new EchoInterQuickFix(EchoMarker.GED,dir));
        		 fixes.add(new EchoInterQuickFix(EchoMarker.OPS,dir));
 	          }
        	  return fixes.toArray(new IMarkerResolution[fixes.size()]);
          }
          else return new IMarkerResolution[0];
       }
       catch (CoreException e) {
          return new IMarkerResolution[0];
       }
    }
 }
