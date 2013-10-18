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
import pt.uminho.haslab.echo.plugin.PlugInOptions;

public class ProjectPropertiesPage extends PropertyPage implements
IWorkbenchPropertyPage {
	
	private Spinner scopetext;
	private Spinner bitwidthtext;
	private Button optimizationsbutton;
	private Spinner maxiterations;
	
	@Override
	protected Control createContents(Composite parent) {

		Composite rootcomposite = new Composite(parent, SWT.NONE);		
		rootcomposite.setLayout(new GridLayout(1, false));
		rootcomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label grouptitle = new Label(rootcomposite, SWT.NONE);
		grouptitle.setText("Overall model finding options.");
		
		Group group = new Group(rootcomposite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		Label iterationslabel = new Label(group, SWT.NONE);
		iterationslabel.setText("Max iterations: ");
		maxiterations = new Spinner(group, SWT.SINGLE);
		maxiterations.setSelection(EchoOptionsSetup.getInstance().getMaxDelta());
		Label scopelabel = new Label(group, SWT.NONE);
		scopelabel.setText("Default scope: ");
		scopetext = new Spinner(group, SWT.SINGLE);
		scopetext.setSelection(EchoOptionsSetup.getInstance().getOverallScope());
		Label bitwidthlabel = new Label(group, SWT.NONE);
		bitwidthlabel.setText("Default bitwidth: ");
		bitwidthtext = new Spinner(group, SWT.SINGLE);
		bitwidthtext.setSelection(EchoOptionsSetup.getInstance().getBitwidth());
		optimizationsbutton = new Button(group, SWT.CHECK);
		optimizationsbutton.setText("Formula simplification ");
		optimizationsbutton.setSelection(EchoOptionsSetup.getInstance().isOptimize());

		return rootcomposite;	
	}
	
	@Override
	protected void performDefaults() {
		bitwidthtext.setSelection(EchoOptionsSetup.DEFAULT_BITWIDTH);
		maxiterations.setSelection(EchoOptionsSetup.DEFAULT_DELTA);
		optimizationsbutton.setSelection(EchoOptionsSetup.DEFAULT_OPTIMIZE);
		scopetext.setSelection(EchoOptionsSetup.DEFAULT_SCOPE);
	}
	
	@Override
	protected void performApply() {
		PlugInOptions op = (PlugInOptions) EchoOptionsSetup.getInstance();
		op.setBitwidth(bitwidthtext.getSelection());
		op.setDelta(maxiterations.getSelection());
		op.setOptimize(optimizationsbutton.getSelection());
		op.setScope(scopetext.getSelection());
	}
	
	@Override
	public boolean performOk() {
		performApply();
		return true;
	}

}
