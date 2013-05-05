package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class ProjectPropertiesPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	
	private ListViewer viewer;

	@Override
	protected Control createContents(Composite parent) {
		ArrayList<String> l = new ArrayList<String>();
		l.add("asd");
		l.add("ddddd");
		
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		
	    myComposite.setLayout(layout);
	    
	    viewer = new ListViewer(myComposite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
	    
	    viewer.setContentProvider(new ArrayContentProvider());
	    // Get the content for the viewer, setInput will call getElements in the
	    // contentProvider
	    viewer.setInput(l);
	    
	    Button addButton = new Button(myComposite,SWT.PUSH );
	    Button remButton = new Button(myComposite,SWT.PUSH);
	    addButton.setText("Add");
	    remButton.setText("Remove");
		
		return myComposite;	
		}

}
