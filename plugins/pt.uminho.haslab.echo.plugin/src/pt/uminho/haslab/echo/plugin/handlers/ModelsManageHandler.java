package pt.uminho.haslab.echo.plugin.handlers;

import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import pt.uminho.haslab.echo.plugin.properties.ProjectModelsPage;

public class ModelsManageHandler extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart iworkbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (iworkbench != null) {
			IEditorInput input = iworkbench.getEditorInput();
			IFile res = ((IFileEditorInput)input).getFile();
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			PreferenceDialog x = PreferencesUtil.createPropertyDialogOn(shell, res.getProject(), ProjectModelsPage.ID, new String[] { ProjectModelsPage.ID}, Collections.EMPTY_MAP);
			x.open();
		}
		return null;
	}

}
