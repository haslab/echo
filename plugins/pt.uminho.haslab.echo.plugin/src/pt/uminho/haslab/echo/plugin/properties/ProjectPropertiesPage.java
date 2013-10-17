package pt.uminho.haslab.echo.plugin.properties;


import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import pt.uminho.haslab.echo.plugin.properties.EchoProjectProperties.QVTConstraintEntry;

public class ProjectPropertiesPage extends PropertyPage implements
IWorkbenchPropertyPage {

	private ListViewer modellist;
	private TableViewer qvtlist;

	@Override
	protected Control createContents(Composite parent) {
		IProject p = (IProject) getElement().getAdapter(IProject.class);
		Set<String> modeluris = EchoProjectPropertiesManager.getModels(p);
		List<QVTConstraintEntry> qvturis = EchoProjectPropertiesManager.getQVTConstraints(p);

		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;

		myComposite.setLayout(layout);

		modellist = new ListViewer(myComposite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

		modellist.setContentProvider(new ArrayContentProvider());
		modellist.setInput(modeluris);

		qvtlist = new TableViewer(myComposite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		TableViewerColumn qvtcol = new TableViewerColumn(qvtlist, SWT.NONE);
		qvtcol.getColumn().setWidth(200);
		qvtcol.getColumn().setText("QVT-R");
		qvtcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QVTConstraintEntry qvt = (QVTConstraintEntry) element;
				return qvt.getQvt();
			}
		});
		TableViewerColumn fstcol = new TableViewerColumn(qvtlist, SWT.NONE);
		fstcol.getColumn().setWidth(200);
		fstcol.getColumn().setText("First model");
		fstcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QVTConstraintEntry qvt = (QVTConstraintEntry) element;
				return qvt.getFst();
			}
		});
		TableViewerColumn sndcol = new TableViewerColumn(qvtlist, SWT.NONE);
		sndcol.getColumn().setWidth(200);
		sndcol.getColumn().setText("Second model");
		sndcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QVTConstraintEntry qvt = (QVTConstraintEntry) element;
				return qvt.getSnd();
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
