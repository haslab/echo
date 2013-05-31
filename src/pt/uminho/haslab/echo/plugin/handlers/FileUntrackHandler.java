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

import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;

public class FileUntrackHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String path = res.getFullPath().toString();
			//String path = res.getRawLocation().toString();
			String extension = res.getFileExtension();
			
			ProjectProperties pp = ProjectProperties.getProjectProperties(res.getProject());
			if(extension.equals("xmi"))
			{			
				pp.removeFromConformList(path);
			}
			else if (extension.equals("ecore"))
			{
				pp.removeMetaModel(path);
			}else if (extension.equals("qvt") || extension.equals("qvtr"))
				MessageDialog.openInformation(shell, "Not Right",extension + "\n" + path);
			else
				MessageDialog.openInformation(shell, "Exception",extension + "not supported.");
			
			System.out.println("Unracked: "+extension + " at " + path);

		}
		return null;
	}

}
