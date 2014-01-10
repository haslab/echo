package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Wizard for the generation of a new model
 * @author nmm
 *
 */
public class ModelGenerateWizard extends Wizard {

	private ModelGenerateWizardPage page;
	
	private Shell shell;
	private IResource res;
		
	public ModelGenerateWizard(IResource res)
	{
		super();
		this.res = res;
	}
	
	public ModelGenerateWizard()
	{
		super();
	}
	
	@Override
	public void addPages()
	{
		page = new ModelGenerateWizardPage(res);
		addPage(page);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		shell = workbench.getModalDialogShellProvider().getShell();
	}

	/**
	 * Parses the user-defined scopes and launches the generation of the new model
	 */
	@Override
	public boolean performFinish() {
		res = page.getMetamodel();
		Map<Entry<String,String>,Integer> scopes = new HashMap<Entry<String,String>,Integer>();
		if (page.getScopes() != null && ! page.getScopes().equals("")) {
			String[] args = page.getScopes().split(", ");
			if (args != null) {
				for (int i = 0; i < args.length ; i++) {
					String[] aux = args[i].split(" ");
					if (aux.length == 2)
						scopes.put(new SimpleEntry<String,String>(res.getFullPath().toString(),aux[1]),Integer.parseInt(aux[0]));					
					else MessageDialog.openInformation(shell, "Scope error", "Invalid scopes.");
				}
			}		
		}
		Job j = new ModelGenerateJob(scopes,page.getPath());
		j.setRule(new ResourceRules(res,ResourceRules.READ));
		j.schedule();
		return true;
	}
	
	
	class ModelGenerateJob extends WorkspaceJob {

		String path;
		Map<Entry<String,String>,Integer> scopes;
		
		public ModelGenerateJob(Map<Entry<String,String>,Integer> scopes, String path) {
			super("Generating model "+page.getPath()+".");
			this.path = path;
			this.scopes = scopes;
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

			try {
				ProjectPropertiesManager.getProperties(res.getProject()).generate(res,scopes,path);
			} catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
			}
		
			return Status.OK_STATUS;
		}
		
	}

}
