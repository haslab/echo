package pt.uminho.haslab.echo.plugin.wizards;

import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.views.AlloyModelView;

public class ModelGenerateWizard extends Wizard {

	private ModelGenerateWizardPage page;
	
	private String metamodel;
	private Shell shell;
	private ProjectProperties pp;
	
	
	public ModelGenerateWizard(String metamodel, ProjectProperties pp)
	{
		super();
		this.metamodel = metamodel;
		this.pp = pp;
	}
	
	@Override
	public void addPages()
	{
		page = new ModelGenerateWizardPage(metamodel);
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
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		
		try {
			Map<Entry<String,String>,Integer> scopes = new HashMap<Entry<String,String>,Integer>();
			if (page.getScopes() != null && ! page.getScopes().equals("")) {
				String[] args = page.getScopes().split(", ");
				if (args != null) {
					for (int i = 0; i < args.length ; i++) {
						String[] aux = args[i].split(" ");
						if (aux.length == 2)
							scopes.put(new SimpleEntry<String,String>(er.parser.getModelsFromUri(metamodel).getName(),aux[1]),Integer.parseInt(aux[0]));					
						else MessageDialog.openInformation(shell, "Scope error", "Invalid scopes.");

					}
				}		
			}
			er.generate(metamodel, scopes);
			
			AlloyModelView amv = EchoPlugin.getInstance().getAlloyView();
			amv.refresh();
			amv.setPathToWrite(page.getPath());
			amv.setMetamodel(metamodel);
			amv.setProperties(pp);
		} catch (ErrorAlloy | ErrorTransform /*| ErrorUnsupported | ErrorParser*/ e) {
			MessageDialog.openInformation(shell, "Error generating instance", e.getMessage());
		}
		return true;
	}

}
