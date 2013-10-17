package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IMarkerResolution;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoOptionsSetup.EchoOptions;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;

public class EchoIntraQuickFix implements IMarkerResolution {
      String label;
      Object mode;
      EchoIntraQuickFix(String mode) {
    	  this.mode = mode;
    	  if (mode.equals(EchoMarker.OPS))
    		  this.label = "Repair intra-model inconsistency through operation-based distance.";
    	  else
    		  this.label = "Repair intra-model inconsistency through graph edit distance.";
      }
      
      public String getLabel() {
         return label;
      }
      
      public void run(IMarker marker) {
    	  EchoRunner echo = EchoRunner.getInstance();
    	  IResource res = marker.getResource();
    	  String path = res.getFullPath().toString();
       
    	  ((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(mode.equals(EchoMarker.OPS));
 
		  boolean b;
		  try {
			  b = echo.repair(res.getFullPath().toString());
			  while(!b) b = echo.increment();
		  } catch (ErrorAlloy e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  EchoPlugin.getInstance().getGraphView().setTargetPath(path,false,null);
		  EchoPlugin.getInstance().getGraphView().drawGraph();
      }	
	
}
