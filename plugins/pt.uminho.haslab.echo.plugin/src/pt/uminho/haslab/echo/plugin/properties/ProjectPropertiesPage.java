package pt.uminho.haslab.echo.plugin.properties;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import pt.uminho.haslab.echo.EchoOptionsSetup;

public class ProjectPropertiesPage extends PropertyPage implements
IWorkbenchPropertyPage {

	@Override
	protected Control createContents(Composite parent) {

		Composite rootcomposite = new Composite(parent, SWT.NONE);
		GridLayout rootlayout = new GridLayout(1, false);
		rootlayout.marginHeight = 1;
		rootlayout.marginWidth = 1;
		
		rootcomposite.setLayout(rootlayout);
		rootcomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label grouptitle = new Label(rootcomposite, SWT.NONE);
		grouptitle.setText("Overall model finding options.");
		
		Group group = new Group(rootcomposite, SWT.NONE);
		GridLayout grouplayout = new GridLayout(2, false);
		grouplayout.marginHeight = 1;
		grouplayout.marginWidth = 1;
		group.setLayout(grouplayout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		Label iterationslabel = new Label(group, SWT.NONE);
		iterationslabel.setText("Max iterations: ");
		Spinner maxiterations = new Spinner(group, SWT.SINGLE);
		maxiterations.setSelection(EchoOptionsSetup.getInstance().getMaxDelta());
		Label scopelabel = new Label(group, SWT.NONE);
		scopelabel.setText("Default scope: ");
		Spinner scopetext = new Spinner(group, SWT.SINGLE);
		scopetext.setSelection(EchoOptionsSetup.getInstance().getOverallScope());
		Label bitwidthlabel = new Label(group, SWT.NONE);
		bitwidthlabel.setText("Default bitwidth: ");
		Spinner bitwidthtext = new Spinner(group, SWT.SINGLE);
		bitwidthtext.setSelection(EchoOptionsSetup.getInstance().getBitwidth());
		Button optimizationsbutton = new Button(group, SWT.CHECK);
		optimizationsbutton.setText("Formula simplification ");

		return rootcomposite;	
	}

}
