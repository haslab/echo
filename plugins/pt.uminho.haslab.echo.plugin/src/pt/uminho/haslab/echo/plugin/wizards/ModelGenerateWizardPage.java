package pt.uminho.haslab.echo.plugin.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ModelGenerateWizardPage extends WizardPage {

	private Text modelPath;
	private Text scopes;
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
	    container.setLayout(new GridLayout(2,false));
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    KeyListener kl = new KeyListenerHelper();
	    
	    Label labelA = new Label(container, SWT.NULL);
	    labelA.setText("New model path: ");

	    modelPath = new Text(container, SWT.BORDER | SWT.SINGLE);
	    modelPath.setText(metamodel.getFullPath().toString().replace(".ecore", ".xmi"));
	    modelPath.addKeyListener(kl);	    
	    modelPath.setLayoutData(gd);
	    
	    Label labelB = new Label(container, SWT.NULL);
	    labelB.setText("Additional scopes:");
	    
	    scopes = new Text(container, SWT.BORDER | SWT.SINGLE);
	    scopes.setText("");
	    scopes.addKeyListener(kl);	    
	    scopes.setLayoutData(gd);
	    
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
		
		if (resource != null) {
			setMessage("Resource already exists, will be overwritten.", WARNING);
		} else {
			setMessage(null,NONE);
			if (modelPath.getText() == null)
				setPageComplete(false);
			else
				setPageComplete(true);
		}
	return true;
	}

}
