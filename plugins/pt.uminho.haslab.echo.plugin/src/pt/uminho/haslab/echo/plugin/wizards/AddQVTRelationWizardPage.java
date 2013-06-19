package pt.uminho.haslab.echo.plugin.wizards;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
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

public class AddQVTRelationWizardPage extends WizardPage {

	private String qvt = "";
	//private String modelA = "";
	//private String modelB = "";
	
	private Text tQvt;
	private Text tModelA;
	private Text tModelB;
	
	public AddQVTRelationWizardPage(String qvtPath) {
		super("Add inter-model consistency");
		setTitle("Add inter-model consistency");
	    setDescription("Select two model instances to be constrained by the QVT-R transformation.");
	    qvt = qvtPath;
	    
	}

	@Override
	public void createControl(Composite parent) {
	 	Composite container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 3;
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    KeyListener kl = new KeyListenerHelper();
	    
	    Label labelA = new Label(container, SWT.NULL);
	    labelA.setText("First Model:");

	    tModelA = new Text(container, SWT.BORDER | SWT.SINGLE);
	    tModelA.setText("");
	    tModelA.addKeyListener(kl);	    
	    tModelA.setLayoutData(gd);
	    
	    Button bModelA = new Button(container, SWT.PUSH);
	    bModelA.addSelectionListener(new ButtonSelectionListener(tModelA));
	    bModelA.setText("Browse");
	    
	    Label labelB = new Label(container, SWT.NULL);
	    labelB.setText("Second Model:");
	    
	    tModelB = new Text(container, SWT.BORDER | SWT.SINGLE);
	    tModelB.setText("");
	    tModelB.addKeyListener(kl);	    
	    tModelB.setLayoutData(gd);
	    
	    Button bModelB = new Button(container, SWT.PUSH);
	    bModelB.addSelectionListener(new ButtonSelectionListener(tModelB));
	    bModelB.setText("Browse");
	    
	    Label labelQvt = new Label(container, SWT.NULL);
	    labelQvt.setText("QVT-R transformation:");
	    
	    tQvt = new Text(container, SWT.BORDER | SWT.SINGLE);
	    tQvt.setText(qvt);
	    tQvt.addKeyListener(kl);	    
	    tQvt.setLayoutData(gd);
	    
	    Button bQVT = new Button(container, SWT.PUSH);
	    bQVT.addSelectionListener(new ButtonSelectionListener(tQvt));
	    bQVT.setText("Browse");
	    
	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(false);


	}
	
	public String getQvt(){
		return tQvt.getText();
	}
	
	public ArrayList<String> getModels()
	{
		ArrayList<String> al = new ArrayList<String>(2);
		al.add(tModelA.getText());
		al.add(tModelB.getText());
		return al;
	}
	
	class KeyListenerHelper implements KeyListener{

		
		@Override
		public void keyPressed(KeyEvent e) {	
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (!tModelA.getText().isEmpty() &&
					!tModelB.getText().isEmpty() &&
					!tQvt.getText().isEmpty()) {
		          setPageComplete(true);
		        }
			else setPageComplete(false);
			
		}
		
	}
	
	class ButtonSelectionListener extends SelectionAdapter
	{
		Text textBox;

		public ButtonSelectionListener(Text tb)
		{
			super();
			textBox = tb;
			
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
				IFile res = (IFile) result[0];
				textBox.setText(res.getFullPath().toString());
			}
			if (!tModelA.getText().isEmpty() &&
					!tModelB.getText().isEmpty() &&
					!tQvt.getText().isEmpty()) {
		          setPageComplete(true);
		        }
			else setPageComplete(false);
		}
	}

}
