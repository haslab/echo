package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;

public class AddQVTRelationWizard extends Wizard implements INewWizard {

	private AddQVTRelationWizardPage page;
	
	private String qvt;
	private ProjectProperties pp;
	private Shell shell;
	
	
	public AddQVTRelationWizard(String qvtPath, ProjectProperties p)
	{
		super();
		qvt = qvtPath;
		pp = p;
	}
	
	@Override
	public  void addPages()
	{
		page = new AddQVTRelationWizardPage(qvt);
		addPage(page);
	}
	
	@Override
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
			pp.addQvtRelation(page.getQvt(), page.getModels());
			boolean b =er.check(page.getQvt(), page.getModels());
			MessageDialog.openInformation(shell, "ok", "Check = " + b);
			
		} catch (ErrorAlloy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
