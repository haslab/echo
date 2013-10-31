package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.views.GraphView;

/**
 * Handles the "show next solution" event
 * @author nmm
 *
 */
public class SolutionNextHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			EchoRunner.getInstance().next();
			
			GraphView amv = EchoPlugin.getInstance().getGraphView();
			if(amv!= null) amv.drawGraph();
				
		} catch (ErrorInternalEngine e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
