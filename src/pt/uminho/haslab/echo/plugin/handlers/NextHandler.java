package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.views.AlloyModelView;

public class NextHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Shell shell = HandlerUtil.getActiveShell(event);
		try {
			if (!EchoPlugin.getInstance().getEchoRunner().next())
			{
				MessageDialog.openInformation(shell, "Info", "A close intance couldn't be found.\nIncremented search range");
				while(!EchoPlugin.getInstance().getEchoRunner().increment());
			}
			
			AlloyModelView amv = EchoPlugin.getInstance().getAlloyView();
			if(amv!= null)
				amv.loadGraph();
				
		} catch (ErrorAlloy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
