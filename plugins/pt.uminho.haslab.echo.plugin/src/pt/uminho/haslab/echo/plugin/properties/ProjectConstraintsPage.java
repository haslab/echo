package pt.uminho.haslab.echo.plugin.properties;


import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.properties.ConstraintManager.Constraint;

public class ProjectConstraintsPage extends PropertyPage implements
IWorkbenchPropertyPage {

	private TableViewer constraintlist;
	private IProject project;

	@Override
	protected Control createContents(Composite parent) {
		project = (IProject) getElement().getAdapter(IProject.class);

		List<Constraint> constraints = ProjectProperties.getProperties(project).getConstraints();

		Composite rootcomposite = new Composite(parent, SWT.NONE);

		rootcomposite.setLayout(new GridLayout(1, false));
		rootcomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label grouptitle = new Label(rootcomposite, SWT.NONE);
		grouptitle.setText("Tracked model resources.");

		Composite tablecomposite = new Composite(rootcomposite, SWT.NONE);
		tablecomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tablecomposite.setLayout(new GridLayout(2, false));

		Composite argh = new Composite(tablecomposite, SWT.NONE);
		argh.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		argh.setLayout(new FillLayout(SWT.VERTICAL));

		constraintlist = new TableViewer(argh,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		constraintlist.getTable().setHeaderVisible(true);
		TableViewerColumn qvtcol = new TableViewerColumn(constraintlist, SWT.NONE);
		qvtcol.getColumn().setWidth(200);
		qvtcol.getColumn().setText("Constraint");
		qvtcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.constraint.getProjectRelativePath().toString();
			}
		});
		TableViewerColumn fstcol = new TableViewerColumn(constraintlist, SWT.NONE);
		fstcol.getColumn().setWidth(200);
		fstcol.getColumn().setText("First model");
		fstcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.fstmodel.getProjectRelativePath().toString();
			}
		});
		TableViewerColumn sndcol = new TableViewerColumn(constraintlist, SWT.NONE);
		sndcol.getColumn().setWidth(200);
		sndcol.getColumn().setText("Second model");
		sndcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.sndmodel.getProjectRelativePath().toString();
			}
		});

		constraintlist.setContentProvider(new ArrayContentProvider());
		constraintlist.setInput(constraints);

		Composite buttonscomposite = new Composite(tablecomposite, SWT.NONE);

		GridData gd_compositeb = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		//gd_compositeb.widthHint = 150;
		buttonscomposite.setLayoutData(gd_compositeb);
		RowLayout rl_compositeb = new RowLayout(SWT.VERTICAL);
		rl_compositeb.fill = true;
		rl_compositeb.center = true;
		//rl_compositeb.justify = true;
		buttonscomposite.setLayout(rl_compositeb);

		Button addButton = new Button(buttonscomposite,SWT.PUSH);
		Button remButton = new Button(buttonscomposite,SWT.PUSH);
		Button allButton = new Button(buttonscomposite,SWT.PUSH);
		addButton.setText("Add constraint");
		addButton.setEnabled(false);
		remButton.setText("Remove constraint");
		remButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				constraintlist.getTable().remove(constraintlist.getTable().getSelectionIndex());
			}
		});
		allButton.setText("Remove all");
		allButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				constraintlist.getTable().removeAll();
			}
		});

		return rootcomposite;	

	}
	
	@Override
	protected void performApply() {
		super.performApply();
		for (TableItem x : constraintlist.getTable().getItems()) {
			Constraint c = (Constraint) x.getData();
			if (!ProjectProperties.getProperties(project).getConstraints().contains(c))
				try {
					ProjectProperties.getProperties(project).addQVTConstraint(c.constraint, c.fstmodel, c.sndmodel);;
				} catch (ErrorUnsupported | ErrorAlloy | ErrorTransform
						| ErrorParser | ErrorAPI e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		for (Constraint x : ProjectProperties.getProperties(project).getConstraints()) {
			boolean has = false;
			for (TableItem y : constraintlist.getTable().getItems()) 
				if (x.equals((Constraint) y.getData())) has = true;
			if (!has) 
				try {
					ProjectProperties.getProperties(project).removeQVTConstraint(x.constraint, x.fstmodel, x.sndmodel);
				} catch (ErrorParser | ErrorAPI e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}	
	}

}
