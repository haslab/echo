package pt.uminho.haslab.echo.plugin.wizards;

import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;
import pt.uminho.haslab.echo.plugin.views.GraphView;

public class ModelGenerateWizard extends Wizard {

	private ModelGenerateWizardPage page;
	
	private Shell shell;
	private IResource res;
		
	public ModelGenerateWizard(IResource res)
	{
		super();
		this.res = res;
	}
	
	@Override
	public void addPages()
	{
		page = new ModelGenerateWizardPage(res.getFullPath().toString());
		addPage(page);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		shell = workbench.getModalDialogShellProvider().getShell();
		/*Object firstElement = selection.getFirstElement();
		
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String qvt = res.getRawLocation().toString();
			page = new RelationPage(qvt);
			addPage(page);
		//}*/
	}

	
	
	@Override
	public boolean performFinish() {
		EchoRunner er = EchoRunner.getInstance();
		try {
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
			try {
				ProjectPropertiesManager.getProperties(res.getProject()).generate(res,scopes,page.getPath());
			} catch (ErrorUnsupported | ErrorParser | ErrorTransform | ErrorAPI e) {
				// TODO Auto-generated catch block
				MessageDialog.openInformation(shell, "Error generating.", e.getMessage());
			}
		} catch (ErrorAlloy /*| ErrorUnsupported | ErrorParser*/ e) {
			MessageDialog.openInformation(shell, "Error generating instance", e.getMessage());
		}
		return true;
	}

}
