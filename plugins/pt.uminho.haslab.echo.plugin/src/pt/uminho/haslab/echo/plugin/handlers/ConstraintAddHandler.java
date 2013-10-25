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

import pt.uminho.haslab.echo.plugin.wizards.ConstraintAddWizard;

/**
 * Handles the "add new QVT constraint" event
 * @author nmm
 *
 */
public class ConstraintAddHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
	    
		WizardDialog wizardDialog;
	    if(selection != null && selection.getFirstElement() instanceof IFile)
		{	
	    	IFile res = (IFile) selection.getFirstElement();

			wizardDialog = new WizardDialog(shell.getShell(), 
					new ConstraintAddWizard(res));
		} else {
			 wizardDialog = new WizardDialog(shell.getShell(), 
				new ConstraintAddWizard());
		}	    
	    wizardDialog.open();

		return null;
	}

}