package pt.uminho.haslab.echo.plugin.properties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.wizards.ModelGenerateWizard;

public class ProjectModelsPage extends PropertyPage implements
IWorkbenchPropertyPage {

	private CheckboxTableViewer modellist;
	private IProject project;
	public final static String ID = "pt.uminho.haslab.echo.plugin.properties.models";

	@Override
	protected Control createContents(Composite parent) {
		project = (IProject) getElement().getAdapter(IProject.class);

		List<IResource> xmiresources = getAllXMI(project);	

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

		modellist = CheckboxTableViewer.newCheckList(argh,SWT.CHECK | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		modellist.setContentProvider(new ArrayContentProvider());
		modellist.setLabelProvider(new ViewLabelProvider());
		modellist.setCheckStateProvider(new CheckLabelProvider(project));
		modellist.setInput(xmiresources);

		Composite buttonscomposite = new Composite(tablecomposite, SWT.NONE);
		
		buttonscomposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		RowLayout rl_compositeb = new RowLayout(SWT.VERTICAL);
		rl_compositeb.fill = true;
		rl_compositeb.center = true;
		buttonscomposite.setLayout(rl_compositeb);

		Button addButton = new Button(buttonscomposite,SWT.PUSH);
		Button remButton = new Button(buttonscomposite,SWT.PUSH);
		addButton.setText("Select all");
		addButton.setEnabled(false);
		remButton.setText("Select none");
		remButton.setEnabled(false);

		Button genButton = new Button(buttonscomposite,SWT.PUSH);
		genButton.setText("Generate model");
		genButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WizardDialog wizardDialog = new WizardDialog(e.display.getActiveShell(), 
						new ModelGenerateWizard());
				wizardDialog.open();		
				//modellist.setInput(getAllXMI(e));
			}
		});

		return rootcomposite;	
	}

	/**
	 * Retrieves all XMI resources from a project
	 * @param p the project to be searched
	 * @return the XMI resources
	 */
	private List<IResource> getAllXMI(IContainer p) {
		List<IResource> modeluris = new ArrayList<IResource>();
		try {
			for (IResource res : p.members()) {
				if (res.getFileExtension() != null && res.getFileExtension().equals("xmi")) modeluris.add(res);
				else if (res instanceof IContainer) modeluris.addAll(getAllXMI((IContainer) res));
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return modeluris;
	}

	@Override
	protected void performApply() {
		for (Object x : modellist.getCheckedElements()) {
			if (!ProjectPropertiesManager.getProperties(project).isManagedModel((IResource) x))
				try {
					ProjectPropertiesManager.getProperties(project).addModel((IResource) x);
				} catch (ErrorUnsupported | ErrorInternalEngine | ErrorTransform
						| ErrorParser | ErrorAPI e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		for (IResource x : ProjectPropertiesManager.getProperties(project).getModels()) {
			if (!Arrays.asList(modellist.getCheckedElements()).contains(x))
				try {
					ProjectPropertiesManager.getProperties(project).remModel(x);
				} catch (ErrorParser | ErrorAPI e) {
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

	@Override
	protected void performDefaults() {
		super.performDefaults();
		modellist.setInput(getAllXMI(project));

	}
	
	/**
	 * Calculates table elements' text and images
	 * @author nmm
	 *
	 */
	private class ViewLabelProvider extends LabelProvider implements
	ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			IResource todo = (IResource) obj;
			return todo.getProjectRelativePath().toString();
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return EchoPlugin.getInstance().getImageRegistry().get(EchoPlugin.XMI_ICON);
		}

	}

	/**
	 * Calculates table elements' check state
	 * @author nmm
	 *
	 */
	class CheckLabelProvider implements ICheckStateProvider {

		IProject p;
		CheckLabelProvider (IProject p){
			this.p =p;
		}
		@Override
		public boolean isChecked(Object element) {
			return ProjectPropertiesManager.getProperties(p).isManagedModel((IResource) element);
		}

		@Override
		public boolean isGrayed(Object element) {
			return false;
		}


	}
}
