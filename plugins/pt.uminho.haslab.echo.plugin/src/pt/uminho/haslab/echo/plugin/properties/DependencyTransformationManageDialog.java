package pt.uminho.haslab.echo.plugin.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.mde.MDEManager;

public class DependencyTransformationManageDialog extends Dialog {

	private final String transformationID;
	
	public DependencyTransformationManageDialog(Shell parent, String transformationID) {
		super(parent);
		this.transformationID = transformationID;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	  protected Control createDialogArea(Composite parent) {
	    Composite container = (Composite) super.createDialogArea(parent);
	    TableViewer constraintlist = new TableViewer(container,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		constraintlist.getTable().setHeaderVisible(true);
		
		TableViewerColumn relcol = new TableViewerColumn(constraintlist, SWT.NONE);
		relcol.getColumn().setWidth(200);
		relcol.getColumn().setText("Relation");
		relcol.setLabelProvider(new ColumnLabelProvider());
		constraintlist.setContentProvider(new ArrayContentProvider());
		try {
			constraintlist.setInput(MDEManager.getInstance().getETransformationID(transformationID).getRelations());
		} catch (EError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		return container;
	  }
	
	
}
