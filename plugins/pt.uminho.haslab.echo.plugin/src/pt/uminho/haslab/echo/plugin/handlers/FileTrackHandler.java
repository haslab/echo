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

import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

public class FileTrackHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile) {	
			IFile res = (IFile) firstElement;
			String extension = res.getFileExtension();
			
			if(extension.equals("xmi")) {			
				try {
					ProjectPropertiesManager.getProperties(res.getProject()).addModel(res);
				} catch(Exception e) {
					e.printStackTrace();
					MessageDialog.openError(shell, "Error loading resource.", e.getMessage());
				}
			}
			else
				MessageDialog.openInformation(shell, "Exception",extension + "not supported.");					
		}
		return null;
	}

}
