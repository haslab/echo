package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

public class EchoInterQuickFix implements IMarkerResolution {
      String label;
      Object mode;
      String dir;
      EchoInterQuickFix(String mode, String dir) {
    	  this.mode = mode;
    	  this.dir = dir;
    	  if (mode.equals(EchoMarker.OPS))
    		  this.label = "Update "+dir+" to restore intra-model inconsistency through operation distance.";
    	  else
    		  this.label = "Update "+dir+" to restore intra-model inconsistency through graph edit distance.";
      }
      
      public String getLabel() {
         return label;
      }
      
      public void run(IMarker marker) {
    	  if (mode.equals(EchoMarker.OPS))
    		  MessageDialog.openInformation(null, "QuickFix Demo",
    				  "This quick-fix is not yet implemented");
    	  else 
    		  MessageDialog.openInformation(null, "QuickFix Demo",
    				  "This quick-fix is not yet implemented");
      }	
	
}
