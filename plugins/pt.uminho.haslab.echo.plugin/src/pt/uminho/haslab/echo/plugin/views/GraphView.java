package pt.uminho.haslab.echo.plugin.views;

import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.alloy.AlloyTuple;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

public class GraphView extends ViewPart {

	/** the main windows of the graph visualizer */
	private VizGUI viz;
	/** The path of the generated target instances */
	private String targetPath;
	/** The metamodel of the newly generated instances */
	private IResource resmetamodel;
	/** If the generated target model is a new model */
	private boolean isNewModel;
	
	public final static String ID = "pt.uminho.haslab.echo.graphviewer";

	/**
	 * Initializes a graph visualizer View with a dummy xml instance
	 */
	public GraphView() {
		super();
		try {
			viz = new VizGUI(false, emptyDummy(), null, null, null, false);
			viz.doShowViz();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Clears the graph visualizer (loads a dummy empty instance)
	 */
	public void clearGraph() {
		resmetamodel = null;
		
		try {
			viz.loadXML(emptyDummy(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viz.doShowViz();
	}
	
	/**
	 * Creates an empty solution dummy file
	 * @return the path to the dummy file
	 * @throws IOException
	 */
	private String emptyDummy() throws IOException {
		File file = new File(".dummy.xml");
		file.createNewFile();
		FileWriter fileWritter = new FileWriter(file.getName(), false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter
				.write("<alloy><instance bitwidth=\"0\" maxseq=\"0\"></instance></alloy>");
		bufferWritter.close();
		return ".dummy.xml";
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.EMBEDDED
				| SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.add(viz.getPanel());
	}

	@Override
	public void dispose() {
		EchoPlugin.getInstance().setGraphView(null);
		super.dispose();
	}

	/**
	 * Draws the graph of the current Alloy solution
	 */
	public void drawGraph() {
		EchoRunner runner = EchoRunner.getInstance();
		A4Solution sol = ((AlloyTuple) runner.getAInstance().getContents()).getSolution();
		try {
			if (sol != null && sol.satisfiable()) {
				sol.writeXML(".dummy.xml");
				viz.loadXML(".dummy.xml", true);
				runner.generateTheme(viz.getVizState());
				viz.doShowViz();
			}
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates the target model with the new solution
	 * If the target model is new, adds it to the managed list
	 */
	public void saveInstance() {
		EchoRunner runner = EchoRunner.getInstance();

		try {
			if (targetPath == null) {
				Shell shell = this.getSite().getShell();
				MessageDialog.openInformation(shell, "No instance to save.",
						"No instance to save.");
			} 
			else if (!isNewModel)
				runner.writeInstance(targetPath);
			else {
				runner.writeAllInstances(resmetamodel.getFullPath().toString(),
						targetPath);
				IResource modelA = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(targetPath);
				ProjectPropertiesManager.getProperties(modelA.getProject()).modelGenerated(modelA);

				try {
					ProjectPropertiesManager.saveProjectProperties(modelA.getProject());
				} catch (ErrorParser e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
			
			targetPath = null;
		} catch (Exception e) {
			Shell shell = this.getSite().getShell();
			MessageDialog.openError(shell, "Error saving update.",
					e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void setFocus() {
		return;
	}

	/**
	 * Sets the path of the generated target instance
	 * @param path the path of the target instance
	 * @param isNew if the generated instance is a new model
	 * @param mm the metamodel of the target model (may be null if already exists)
	 */
	public void setTargetPath(String path, boolean isNew, IResource mm) {
		targetPath = path;
		isNewModel = isNew;
		resmetamodel = mm;
	}
}
