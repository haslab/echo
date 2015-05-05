package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorAPI;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

/**
 * Handles the "untrack model" event
 * @author nmm
 *
 */
public class FileUntrackHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String extension = res.getFileExtension();
			
			if(extension.equals("xmi"))
				try {
					ProjectPropertiesManager.getProperties(res.getProject()).remModel(res);
				} catch (EError e) {
					MessageDialog.openInformation(shell, "Failed to untrack resource",e.getMessage());
					e.printStackTrace();
				}
			else
				MessageDialog.openInformation(shell, "Exception",extension + "not supported.");
			
			try {
				ProjectPropertiesManager.saveProjectProperties(res.getProject());
			} catch (EErrorAPI e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

}
