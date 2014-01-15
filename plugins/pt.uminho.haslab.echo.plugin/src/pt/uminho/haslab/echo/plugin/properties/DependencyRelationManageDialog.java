package pt.uminho.haslab.echo.plugin.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

public class DependencyRelationManageDialog extends Dialog {

	private final ERelation relation;
	
	public DependencyRelationManageDialog(Shell parent, ERelation relation) {
		super(parent);
		this.relation = relation;
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
		//constraintlist.setInput(transformation.getRelations());

		
		
		return container;
	  }
	
	
}
