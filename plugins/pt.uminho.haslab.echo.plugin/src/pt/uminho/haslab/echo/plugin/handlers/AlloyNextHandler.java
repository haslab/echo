package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.views.GraphView;

public class AlloyNextHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		//Shell shell = HandlerUtil.getActiveShell(event);
		try {
			if (!EchoRunner.getInstance().next())
			{
				System.out.println("A close intance couldn't be found.\nIncremented search range");
				while(!EchoRunner.getInstance().increment());
			}
			
			GraphView amv = EchoPlugin.getInstance().getGraphView();
			if(amv!= null)
				amv.drawGraph();
				
		} catch (ErrorAlloy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
