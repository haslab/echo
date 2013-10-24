package pt.uminho.haslab.echo.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.views.GraphView;

/**
 * Handles the "select solution" event
 * @author nmm
 *
 */
public class SolutionSelectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		GraphView amv = EchoPlugin.getInstance().getGraphView();
		amv.saveInstance();
		
		return null;
	}

}
