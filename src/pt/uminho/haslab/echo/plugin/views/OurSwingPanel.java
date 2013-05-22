package pt.uminho.haslab.echo.plugin.views;

import java.awt.Graphics;

import javax.swing.JPanel;

import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class OurSwingPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	VizGUI viz;
	
	
	public OurSwingPanel(){
		viz = new VizGUI(false, "", null,null,null,false);
	}
	
	public void paintComponent(Graphics g) {
		  viz.getViewer().paintComponent(g);
		 
		}
	
	public void loadXML(String filename){
		viz.loadXML(filename, true);
	}
}
