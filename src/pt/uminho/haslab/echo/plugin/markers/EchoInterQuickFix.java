package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;

public class EchoInterQuickFix implements IMarkerResolution {
      String label;
      Object mode;
      EchoInterQuickFix(String mode) {
    	  this.mode = mode;
    	  if (mode.equals(EchoMarker.OPS))
    		  this.label = "Repair inter-model inconsistency through operation distance.";
    	  else
    		  this.label = "Repair inter-model inconsistency through graph edit distance.";
      }
      
      public String getLabel() {
         return label;
      }
      
      public void run(IMarker marker) {
    	  EchoRunner echo = EchoPlugin.getInstance().getEchoRunner();
    	  
    	  IResource res = marker.getResource();
    	  String path = res.getFullPath().toString();
    	  ArrayList<String> list = new ArrayList<String>(1);
    	  list.add(path);
    	  try {
			list.add(marker.getAttribute(EchoMarker.OPPOSITE).toString());
    	  } catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
    	  }
    	 
    	  EchoPlugin.getInstance().options.setOperationBased(mode.equals(EchoMarker.OPS));
		  boolean b;
		  try {
			  b = echo.enforce(marker.getAttribute(EchoMarker.QVTR).toString(),list, res.getFullPath().toString());
			  while(!b) b = echo.increment();
		  } catch (ErrorAlloy | CoreException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  EchoPlugin.getInstance().getAlloyView().refresh();
		  EchoPlugin.getInstance().getAlloyView().setPathToWrite(path);
      }
}
