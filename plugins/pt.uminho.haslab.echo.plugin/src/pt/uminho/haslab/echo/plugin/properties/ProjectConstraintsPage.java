package pt.uminho.haslab.echo.plugin.properties;


import org.eclipse.swt.graphics.Image;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ConstraintManager.Constraint;

public class ProjectConstraintsPage extends PropertyPage implements
IWorkbenchPropertyPage {

	private TableViewer modellist;
	private TableViewer qvtlist;

	@Override
	protected Control createContents(Composite parent) {
		IProject p = (IProject) getElement().getAdapter(IProject.class);
		List<IResource> modeluris = ProjectProperties.getProperties(p).getModels();
		List<Constraint> qvturis = ProjectProperties.getProperties(p).getConstraints();

		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;

		myComposite.setLayout(layout);


		
		qvtlist = new TableViewer(myComposite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		TableViewerColumn qvtcol = new TableViewerColumn(qvtlist, SWT.NONE);
		qvtcol.getColumn().setWidth(200);
		qvtcol.getColumn().setText("QVT-R");
		qvtcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.constraint;
			}
		});
		TableViewerColumn fstcol = new TableViewerColumn(qvtlist, SWT.NONE);
		fstcol.getColumn().setWidth(200);
		fstcol.getColumn().setText("First model");
		fstcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.fstmodel;
			}
		});
		TableViewerColumn sndcol = new TableViewerColumn(qvtlist, SWT.NONE);
		sndcol.getColumn().setWidth(200);
		sndcol.getColumn().setText("Second model");
		sndcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Constraint qvt = (Constraint) element;
				return qvt.sndmodel;
			}
		});

		qvtlist.setContentProvider(new ArrayContentProvider());
		qvtlist.setInput(qvturis);

		
		Button addButton = new Button(myComposite,SWT.PUSH );
		Button remButton = new Button(myComposite,SWT.PUSH);
		addButton.setText("Add");
		remButton.setText("Remove");

		return myComposite;	
	}

}
