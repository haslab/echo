package pt.uminho.haslab.echo.plugin.properties;


import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.ConstraintManager.Constraint;
import pt.uminho.haslab.echo.plugin.wizards.ConstraintAddWizard;
import pt.uminho.haslab.echo.plugin.EchoPlugin;

public class ProjectConstraintsPage extends PropertyPage implements
IWorkbenchPropertyPage {

	private TableViewer constraintlist;
	private IProject project;
	public final static String ID = "pt.uminho.haslab.echo.plugin.properties.constraints";

	@Override
	protected Control createContents(Composite parent) {
		project = (IProject) getElement().getAdapter(IProject.class);

		List<Constraint> constraints = ProjectPropertiesManager.getProperties(project).getConstraints();

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
		qvtcol.setLabelProvider(new ViewLabelProvider(0));

		TableViewerColumn fstcol = new TableViewerColumn(constraintlist, SWT.NONE);
		fstcol.getColumn().setWidth(200);
		fstcol.getColumn().setText("First model");
		fstcol.setLabelProvider(new ViewLabelProvider(1));

		TableViewerColumn sndcol = new TableViewerColumn(constraintlist, SWT.NONE);
		sndcol.getColumn().setWidth(200);
		sndcol.getColumn().setText("Second model");
		sndcol.setLabelProvider(new ViewLabelProvider(2));

		constraintlist.setContentProvider(new ArrayContentProvider());
		constraintlist.setInput(constraints);

		Composite buttonscomposite = new Composite(tablecomposite, SWT.NONE);

		buttonscomposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		RowLayout rl_compositeb = new RowLayout(SWT.VERTICAL);
		rl_compositeb.fill = true;
		rl_compositeb.center = true;
		buttonscomposite.setLayout(rl_compositeb);

		Button addButton = new Button(buttonscomposite,SWT.PUSH);
		Button remButton = new Button(buttonscomposite,SWT.PUSH);
		Button allButton = new Button(buttonscomposite,SWT.PUSH);
		addButton.setText("Add constraint");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WizardDialog wizardDialog = new WizardDialog(e.display.getActiveShell(), 
						new ConstraintAddWizard());
				wizardDialog.open();		
				constraintlist.setInput(ProjectPropertiesManager.getProperties(project).getConstraints());
			}
		});
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
		for (TableItem x : constraintlist.getTable().getItems()) {
			System.out.println("BUH?");
			Constraint c = (Constraint) x.getData();
			if (!ProjectPropertiesManager.getProperties(project).getConstraints().contains(c))
				try {
					ProjectPropertiesManager.getProperties(project).addQVTConstraint(c.constraint, c.fstmodel, c.sndmodel);
				} catch (EchoError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};

		}
		for (Constraint x : ProjectPropertiesManager.getProperties(project).getConstraints()) {
			System.out.println("BUH?");
			boolean has = false;
			for (TableItem y : constraintlist.getTable().getItems()) 
				if (x.equals((Constraint) y.getData())) has = true;
			if (!has) 
				try {
					ProjectPropertiesManager.getProperties(project).removeQVTConstraint(x);
				} catch (EchoError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}	
		try {
			ProjectPropertiesManager.saveProjectProperties(project);
		} catch (ErrorParser e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public boolean performOk() {
		performApply();
		return true;
	}

	
	/**
	 * Calculates column elements' text and image
	 * @author nmm
	 *
	 */
	private class ViewLabelProvider extends ColumnLabelProvider  {
		
		private int i;
		public ViewLabelProvider(int i) {
			this.i = i;
		}

		public String getText(Object obj) {
			Constraint qvt = (Constraint) obj;
			switch (i) {
			case 0:
				return qvt.constraint.getProjectRelativePath().toString();
			case 1:
				return qvt.fstmodel.getProjectRelativePath().toString();
			case 2:
				return qvt.sndmodel.getProjectRelativePath().toString();
			}
			return null;
		}

		public Image getImage(Object obj) {
			if (i==0)
				return EchoPlugin.getInstance().getImageRegistry().get(EchoPlugin.QVT_ICON);			
			else
				return EchoPlugin.getInstance().getImageRegistry().get(EchoPlugin.XMI_ICON);
		}

	}
}
