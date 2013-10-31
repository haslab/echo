package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

/**
 * Wizard for the creation of a new QVT constraint
 * 
 * @author nmm
 * 
 */
public class ConstraintAddWizard extends Wizard {

	private ConstraintAddWizardPage page;

	private final IResource qvt_resource;
	private Shell shell;

	public ConstraintAddWizard() {
		super();
		qvt_resource = null;
	}

	public ConstraintAddWizard(IResource qvt_resource) {
		super();
		this.qvt_resource = qvt_resource;
	}

	@Override
	public void addPages() {
		page = new ConstraintAddWizardPage(qvt_resource);
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		shell = workbench.getModalDialogShellProvider().getShell();
	}

	/**
	 * Creates a new constraint over the models specified in the form
	 * If one of the specified models does not exists, generate that resource
	 */
	@Override
	public boolean performFinish() {

		int new_count = 0, new_pos = 0, existing_pos = 0;
		for (int i = 0; i < page.getModels().size(); i++) {
			IResource f = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(page.getModels().get(i));
			if (f == null || !f.exists()) {
				new_count++;
				new_pos = i;
			} else {
				existing_pos = i;
			}
		}

		if (new_count == 1) {
			try {
				IResource existing_resource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(page.getModels().get(existing_pos));
				IResource qvt_resource = page.getQvt();

				ProjectPropertiesManager.getProperties(qvt_resource.getProject())
						.addQVTgenerate(qvt_resource, existing_resource,
								page.getModels().get(new_pos), new_pos);
			} catch (Exception e) {
				MessageDialog.openInformation(shell,
						"Error creating constraint", e.getMessage());
				e.printStackTrace();
			}

		} else if (new_count == 0) {
			try {
				IResource fst_resource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(page.getModels().get(0));
				IResource snd_resource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(page.getModels().get(1));
				IResource qvt_resource = page.getQvt();
				ProjectPropertiesManager.getProperties(qvt_resource.getProject())
						.addQVTConstraint(qvt_resource, fst_resource, snd_resource);
			} catch (Exception e) {
				MessageDialog.openError(shell, "Error translating QVT-R",
						e.getMessage());
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(shell, "Invalid model URIs",
					"At least one of the model instances must already exist.");
		}

		return true;
	}

}
