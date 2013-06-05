package pt.uminho.haslab.echo.plugin.views;

import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class AlloyModelView extends ViewPart {
  
	VizGUI viz;
	A4Solution sol;
	String pathToWrite;
	String mmURI;
	ProjectProperties pp;
	
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
		File file = new File(".dummy.xml");
		try {
			file.createNewFile();
			FileWriter fileWritter = new FileWriter(file.getName(),false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write("<alloy><instance bitwidth=\"0\" maxseq=\"0\"></instance></alloy>");
	        bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		viz = new VizGUI(false, ".dummy.xml", null,null,null,false);
		viz.doShowViz();
		
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.add(viz.getPanel());

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
	
	public void setProperties(ProjectProperties pp)
	{
		this.pp = pp;
	}
	

	public void setMetamodel(String mm)
	{
		mmURI = mm;
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
	
	public void clean () {
		File file = new File(".dummy.xml");
		try {
			file.createNewFile();
			FileWriter fileWritter = new FileWriter(file.getName(),false);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write("<alloy><instance bitwidth=\"0\" maxseq=\"0\"></instance></alloy>");
	        bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viz.loadXML(".dummy.xml", true);
		viz.doShowViz();
	}

	public void saveInstance() {
		try {
			if (EchoPlugin.getInstance().getEchoRunner().hasInstance(pathToWrite))
				EchoPlugin.getInstance().getEchoRunner().writeInstance(pathToWrite);
			else{
				EchoPlugin.getInstance().getEchoRunner().writeAllInstances(mmURI,pathToWrite);
				pp.addConformList(pathToWrite);
			}

			pathToWrite = null;
		} catch (ErrorAlloy | ErrorTransform | ErrorUnsupported | ErrorParser e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
