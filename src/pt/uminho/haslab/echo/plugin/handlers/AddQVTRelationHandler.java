package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.wizards.AddQVTRelationWizard;

public class AddQVTRelationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Shell shell = HandlerUtil.getActiveShell(event);
		
		
		
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String path = res.getRawLocation().toString();

			ProjectProperties p = ProjectProperties.getProjectProperties(res.getProject());
			WizardDialog wizardDialog = new WizardDialog(shell.getShell(), 
					new AddQVTRelationWizard(path,p));
			
			wizardDialog.open();
			
			
		}
		
		return null;
	}

}
