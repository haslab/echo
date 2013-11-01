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

import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

public class ModelViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile) {	
			IFile res = (IFile) firstElement;
			String extension = res.getFileExtension();
			
			if(extension.equals("xmi")) {			
				try {
					ProjectPropertiesManager.getProperties(res.getProject()).show(res);
				} catch(Exception e) {
					MessageDialog.openError(shell, "Failed to show resource.", e.getMessage());
					e.printStackTrace();
				}
			}
			else
				MessageDialog.openInformation(shell, "Exception",extension + "not supported.");		
			
			EchoPlugin.getInstance().getGraphView().drawGraph();

		}
		return null;
	
	}

}
