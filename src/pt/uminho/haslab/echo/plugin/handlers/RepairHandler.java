package pt.uminho.haslab.echo.plugin.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.views.AlloyModelView;


public class RepairHandler extends AbstractHandler {



	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
				boolean b = er.repair(list, res.getFullPath().toString());
				while(!b) b = er.increment();
					
				AlloyModelView amv = (AlloyModelView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView("pt.uminho.haslab.echo.alloymodelview");
				amv.refresh();
				amv.setPathToWrite(path);
				//er.writeInstance(path);
			} catch (ErrorAlloy | PartInitException e) {
		
				e.printStackTrace();
				MessageDialog.openInformation(shell, "Alloy Error, or view....", e.getMessage());
			}
			
		}
		
		
		return null;
	}
}
