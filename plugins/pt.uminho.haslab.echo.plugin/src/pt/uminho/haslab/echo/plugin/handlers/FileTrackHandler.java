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

public class FileTrackHandler extends AbstractHandler {

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
			if(extension.equals("xmi")) {			
				try {
					pp.addConformList(path);
				}catch(Exception e)	{
					e.printStackTrace();
					MessageDialog.openError(shell, "Error loading resource.", e.getMessage());
				}
			}
			else if (extension.equals("ecore")) {
				try {
					pp.addMetaModel(path);
				}catch(Exception e)	{
					e.printStackTrace();
					MessageDialog.openError(shell, "Error loading resource.", "Error loading meta-model.\n"+e.getMessage());
				}
			}
			else if (extension.equals("qvt") || extension.equals("qvtr")) {
				try {
					pp.addQvtRule(res.getRawLocation().toString());
				}catch(Exception e)	{
					e.printStackTrace();
					MessageDialog.openError(shell, "Error loading resource.", e.getMessage());
				}
			}
			else
				MessageDialog.openInformation(shell, "Exception",extension + "not supported.");
			
			System.out.println("Tracked: "+extension + " at " + path);
		
			
		}
		return null;
	}

}
