package pt.uminho.haslab.echo.plugin.views;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class AlloyModelView extends ViewPart {
  
	VizGUI viz;
	A4Solution sol;
	String pathToWrite;
	
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		viz = new VizGUI(true, "", null,null,null,false);
		viz.doShowViz();
		
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.add(viz.getViewer());

	    viz.loadXML(".dummy.xml",true);

	    //loadGraph();
	    

	    EchoPlugin.getInstance().setAlloyView(this);
	}

	
	public void refresh()
	{
		loadGraph();
		pathToWrite = null;
	}

	public void setPathToWrite(String path)
	{
		pathToWrite = path;
	}
	

	public void loadGraph()
	{
		sol = EchoPlugin.getInstance().getEchoRunner().getAInstance();
		   try {
			   if(sol != null && sol.satisfiable())
			   {
				   sol.writeXML(".dummy.xml");
				   viz.loadXML(".dummy.xml",true);
				   EchoPlugin.getInstance().getEchoRunner().generateTheme(viz.getVizState());
				   viz.doShowViz();
			   } else {
				   viz.loadXML("alloy_output.xml",true);
				   viz.doShowViz();				   
			   }
		   }catch (Err e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	}
	public void dispose(){
		EchoPlugin.getInstance().deleteView();
		super.dispose();
	}
	
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void saveInstance() {
		try {
			EchoPlugin.getInstance().getEchoRunner().writeInstance(pathToWrite);
			pathToWrite = null;
		} catch (ErrorAlloy | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
