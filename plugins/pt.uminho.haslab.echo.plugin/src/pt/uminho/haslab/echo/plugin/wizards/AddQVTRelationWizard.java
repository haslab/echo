package pt.uminho.haslab.echo.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.emf.EchoParser;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.properties.ConstraintManager.Constraint;
import pt.uminho.haslab.echo.plugin.views.GraphView;

public class AddQVTRelationWizard extends Wizard  {

	private AddQVTRelationWizardPage page;
	
	private String qvt;
	private IProject project;
	private Shell shell;
	
	
	public AddQVTRelationWizard(String qvtPath, IProject project)
	{
		super();
		this.qvt = qvtPath;
		this.project = project;
	}
	
	@Override
	public  void addPages()
	{
		page = new AddQVTRelationWizardPage(qvt);
		addPage(page);
	}
	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		shell = workbench.getModalDialogShellProvider().getShell();
	}
	
	
	@Override
	public boolean performFinish() {
		EchoRunner er = EchoRunner.getInstance();
		EchoParser parser = EchoParser.getInstance();
		
		int news = 0;
		int newp = 0;
		int orip = 0;
		for (int i = 0; i<page.getModels().size(); i++) {
			IResource f = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(i));
			if (f == null || !f.exists()) {
				news ++;
				newp = i;
			} else {
				orip = i;
			}
		}

		if (news == 1) {
			try {
				IResource ressource = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(orip));
				IResource resqvt = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getQvt());

				ProjectProperties.getProperties(resqvt.getProject()).addQVTgenerate(resqvt, ressource, page.getModels().get(newp), newp);
			} catch (Exception e) {
				MessageDialog.openInformation(shell, "Error creating constraint", e.getMessage());
				e.printStackTrace();
			}
			
		} else if (news == 0) {
			try {
				IResource modelA = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(0));
				IResource modelB = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(1));
				IResource resqvt = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getQvt());
				ProjectProperties.getProperties(resqvt.getProject()).addQVTConstraint(resqvt,modelA,modelB);
					
			} catch (Exception e) {
				MessageDialog.openError(shell, "Error translating QVT-R", e.getMessage());
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(shell, "Invalid model URIs", "At least one of the model instances must already exist.");
		}
		
		return true;
	}

}
