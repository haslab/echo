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

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;

public class AddInfoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		
		Shell shell = HandlerUtil.getActiveShell(event);
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String path = res.getFullPath().toString();
			String extension = res.getFileExtension();
			
			try {
				if(extension.equals("xmi"))
					er.addInstance(path);
				else if (extension.equals("ecore"))
					er.addModel(path);
				else if (extension.equals("qvt") || extension.equals("qvtr"))
					er.addQVT(path);
				else
					MessageDialog.openInformation(shell, "Not Right",extension + "\n" + path);
				
				MessageDialog.openInformation(shell, "Info",extension + "\n" + path);
			}catch(ErrorUnsupported | ErrorAlloy | ErrorTransform | ErrorParser e)
			{
				e.printStackTrace();
				MessageDialog.openInformation(shell, "Exception", e.getMessage()+ "\n" + e.toString());
			}
			
		}
		return null;
	}

}
