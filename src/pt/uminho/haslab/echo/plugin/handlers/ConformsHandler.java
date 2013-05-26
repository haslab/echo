package pt.uminho.haslab.echo.plugin.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoMarker;
import pt.uminho.haslab.echo.plugin.EchoPlugin;

public class ConformsHandler extends AbstractHandler {

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
			ArrayList<String> list = new ArrayList<String>(1);
			list.add(path);
			try {
				boolean b = er.conforms(list);

				if (!b) {
					EchoMarker.createIntraMarker(res);
				}
				else
					res.deleteMarkers(EchoMarker.INTRA_ERROR, false, 0);
				MessageDialog.openInformation(shell, "ok", "Conforms = " + b);
				EchoPlugin.getInstance().refreshView();
			} catch (ErrorAlloy | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageDialog.openInformation(shell, "Error", e.getMessage());
			}
			
		}
		
		
		return null;
	}

}
