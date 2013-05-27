package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

public class EchoIntraQuickFix implements IMarkerResolution {
      String label;
      Object mode;
      EchoIntraQuickFix(String mode) {
    	  this.mode = mode;
    	  if (mode.equals(EchoMarker.OPS))
    		  this.label = "Repair intra-model inconsistency through operation distance.";
    	  else
    		  this.label = "Repair intra-model inconsistency through graph edit distance.";
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
