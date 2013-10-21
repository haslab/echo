package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ModelGenerateWizardPage extends WizardPage {

	private Text modelPath;
	private Text scopes;
	private Text tMetamodel;
	private IResource metamodel;
	
	public ModelGenerateWizardPage(IResource metamodel) {
		super("Generate new model instance");
		setTitle("Generate new model instance");
		this.metamodel = metamodel;
	    setDescription("Generate a new model instance conformant to the "+metamodel+" meta-model.");
	}

	@Override
	public void createControl(Composite parent) {
	 	Composite container = new Composite(parent, SWT.NULL);
	    container.setLayout(new GridLayout(3,false));
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    KeyListener kl = new KeyListenerHelper();
	    
	    Label labelM = new Label(container, SWT.NULL);
	    labelM.setText("New model metamodel: ");

	    tMetamodel = new Text(container, SWT.BORDER | SWT.SINGLE);
	    tMetamodel.addKeyListener(kl);	    
	    tMetamodel.setLayoutData(gd);

	    Button bQVT = new Button(container, SWT.PUSH);
	    bQVT.addSelectionListener(new ButtonSelectionListener());
	    bQVT.setText("Browse");
	    
	    Label labelA = new Label(container, SWT.NULL);
	    labelA.setText("New model path: ");

	    modelPath = new Text(container, SWT.BORDER | SWT.SINGLE);
	    modelPath.addKeyListener(kl);	    
	    modelPath.setLayoutData(gd);

	    new Label(container, SWT.NULL);
	    
	    if (metamodel != null) {
		    tMetamodel.setText(metamodel.getFullPath().toString());
		    tMetamodel.setEnabled(false);
		    modelPath.setText(metamodel.getFullPath().toString().replace(".ecore", ".xmi"));
	    }
	    
	    Label labelB = new Label(container, SWT.NULL);
	    labelB.setText("Additional scopes:");
	    
	    scopes = new Text(container, SWT.BORDER | SWT.SINGLE);
	    scopes.setText("");
	    scopes.addKeyListener(kl);	    
	    scopes.setLayoutData(gd);
	    
	    new Label(container, SWT.NULL);
	    
	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(true);

	}
	
	public String getScopes()
	{
		return scopes.getText();
	}
	
	public String getPath()
	{
		return modelPath.getText();
	}
	
	public IResource getMetamodel()
	{
		return ResourcesPlugin.getWorkspace().getRoot().findMember(tMetamodel.getText());
	}
	
	
	class KeyListenerHelper implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
			isValid();			
		}
		
	}
	
	private boolean isValid() {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(modelPath.getText());
		
		IResource mresource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(tMetamodel.getText());
		
		if (mresource == null) {
			setMessage("Invalid meta-model.", ERROR);
			setPageComplete(false);
		} else if (modelPath.getText() != "" && modelPath.getText() != null && resource != null) {
			setMessage("Resource already exists, will be overwritten.", WARNING);
			setPageComplete(true);
		} else {
			setMessage(null,NONE);
			if (modelPath.getText() == null)
				setPageComplete(false);
			else
				setPageComplete(true);
		}
	return true;
	}

	class ButtonSelectionListener extends SelectionAdapter
	{
		public ButtonSelectionListener()
		{
			super();
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) 
		{
			// Handle the selection event
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				    new WorkbenchLabelProvider(),
				    new BaseWorkbenchContentProvider());
			dialog.setTitle("Tree Selection");
			dialog.setMessage("Select the elements from the tree:");
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setAllowMultiple(false);
			dialog.open();
			
			Object[] result = dialog.getResult();
			if (result != null)
			{
				IFile f = (IFile) result[0];
				tMetamodel.setText(f.getFullPath().toString());
			}
			isValid();
		}
	}
}
