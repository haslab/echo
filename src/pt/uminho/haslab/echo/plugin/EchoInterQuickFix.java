package pt.uminho.haslab.echo.plugin;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;

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
			list.add(marker.getAttribute(EchoMarker.PARTNER_MODEL).toString());
    	  } catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
    	  }
    	  if (mode.equals(EchoMarker.OPS))
    		  MessageDialog.openInformation(null, "QuickFix Demo",
    				  "This quick-fix is not yet implemented");
    	  else {
    		  boolean b;
    		  try {
    			  b = echo.enforce(marker.getAttribute(EchoMarker.QVR_RULE).toString(),list, res.getFullPath().toString());
    			  while(!b) b = echo.increment();
    		  } catch (ErrorAlloy | CoreException e) {
    			  // TODO Auto-generated catch block
    			  e.printStackTrace();
    		  }
    		  EchoPlugin.getInstance().getAlloyView().refresh();
    		  EchoPlugin.getInstance().getAlloyView().setPathToWrite(path);
    	  }
      }
}
