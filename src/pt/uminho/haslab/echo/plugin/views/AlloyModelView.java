package pt.uminho.haslab.echo.plugin.views;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pt.uminho.haslab.echo.plugin.EchoPlugin;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class AlloyModelView extends ViewPart {

	VizGUI viz;
	A4Solution sol;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    viz = new VizGUI(false, "", null,null,null,false);
	    
	    frame.add(viz.getPanel());
	    
	   sol = EchoPlugin.getInstance().getEchoRunner().getAInstance();
	   try {
		sol.writeXML("dummy.xml");
	} catch (Err e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   viz.loadXML("dummy.xml",true);
	    
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
