package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EExceptMaxDelta;
import pt.uminho.haslab.echo.EException;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.PlugInOptions;
import pt.uminho.haslab.echo.plugin.PluginMonitor;
import pt.uminho.haslab.echo.plugin.ResourceRules;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;

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
	private IResource metamodelRes;
		
	public ModelGenerateWizard(IResource res)
	{
		super();
		this.metamodelRes = res;
	}
	
	public ModelGenerateWizard()
	{
		super();
	}
	
	@Override
	public void addPages()
	{
		page = new ModelGenerateWizardPage(metamodelRes);
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
		metamodelRes = page.getMetamodel();
		EMetamodel metamodel;
		try {
			metamodel = MDEManager.getInstance().getMetamodel(metamodelRes.getFullPath().toString(), false);
			Map<Entry<String,String>,Integer> scopes = new HashMap<Entry<String,String>,Integer>();
			if (page.getScopes() != null && ! page.getScopes().equals("")) {
				String[] args = page.getScopes().split(", ");
				if (args != null) {
					for (int i = 0; i < args.length ; i++) {
						String[] aux = args[i].split(" ");
						if (aux.length == 2)
							scopes.put(new SimpleEntry<String,String>(metamodel.ID,aux[1]),Integer.parseInt(aux[0]));					
						else MessageDialog.openInformation(shell, "Scope error", "Invalid scopes.");
					}
				}		
			}
			if (page.getMetric().equals("OBD"))
				((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(true);
			else
				((PlugInOptions) EchoOptionsSetup.getInstance()).setOperationBased(false);

			Job j = new ModelGenerateJob(scopes,page.getPath(),page.getRoot());

			IJobManager manager = Job.getJobManager();
			
			final PluginMonitor p = new PluginMonitor();
			ProgressProvider provider = new ProgressProvider() {
			  @Override
			  public IProgressMonitor createMonitor(Job job) {
			    return p;
			  }
			};
			manager.setProgressProvider(provider);
			j.setRule(new ResourceRules(metamodelRes,ResourceRules.READ));
			j.schedule();
		} catch (EError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return true;
	}
	
	class ModelGenerateJob extends Job {

		String path;
		String root;
		Map<Entry<String,String>,Integer> scopes;
		
		public ModelGenerateJob(Map<Entry<String,String>,Integer> scopes, String path, String root) {
			super("Generating model "+page.getPath()+".");
			this.path = path;
			this.scopes = scopes;
			this.root = root;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				ProjectPropertiesManager.getProperties(metamodelRes.getProject()).generate(metamodelRes,scopes,path,root);
			} catch (EExceptMaxDelta e) {
				return new Status(Status.ERROR, EchoPlugin.ID, "Maximum delta reached.", e);
			} catch (EErrorParser e) {
				return new Status(Status.ERROR, EchoPlugin.ID, "Failed to parse scopes.", e);
			} catch (EException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
		
	}


}
