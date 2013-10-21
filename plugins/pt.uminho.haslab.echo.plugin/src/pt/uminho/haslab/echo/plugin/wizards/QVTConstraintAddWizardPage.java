package pt.uminho.haslab.echo.plugin.wizards;

import java.util.ArrayList;

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

public class QVTConstraintAddWizardPage extends WizardPage {

	private Text[] text_boxes = new Text[3];
	private IResource[] resources = new IResource[3];
	private final int FST = 0, SND = 1, QVT = 2;
	private final String DEFAULT_MESSAGE = "Select two model instances to be constrained by the QVT-R transformation.";
	
	public QVTConstraintAddWizardPage(IResource qvt_resource) {
		super("Add inter-model consistency");
		setTitle("Add inter-model consistency");
	    setDescription(DEFAULT_MESSAGE);
	    this.resources[QVT] = qvt_resource;
	}

	@Override
	public void createControl(Composite parent) {
	 	Composite container = new Composite(parent, SWT.NULL);
	    container.setLayout(new GridLayout(3,false));
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    KeyListener kl = new KeyListenerHelper();
	    
	    Label labelA = new Label(container, SWT.NULL);
	    labelA.setText("First Model:");

	    text_boxes[FST] = new Text(container, SWT.BORDER | SWT.SINGLE);
	    text_boxes[FST].setText("");
	    text_boxes[FST].addKeyListener(kl);	    
	    text_boxes[FST].setLayoutData(gd);
	    
	    Button bModelA = new Button(container, SWT.PUSH);
	    bModelA.addSelectionListener(new ButtonSelectionListener(FST));
	    bModelA.setText("Browse");
	    
	    Label labelB = new Label(container, SWT.NULL);
	    labelB.setText("Second Model:");
	    
	    text_boxes[SND] = new Text(container, SWT.BORDER | SWT.SINGLE);
	    text_boxes[SND].setText("");
	    text_boxes[SND].addKeyListener(kl);	    
	    text_boxes[SND].setLayoutData(gd);
	    
	    Button bModelB = new Button(container, SWT.PUSH);
	    bModelB.addSelectionListener(new ButtonSelectionListener(SND));
	    bModelB.setText("Browse");
	    
	    Label labelQvt = new Label(container, SWT.NULL);
	    labelQvt.setText("QVT-R transformation:");
	    
	    text_boxes[QVT] = new Text(container, SWT.BORDER | SWT.SINGLE);
	    text_boxes[QVT].addKeyListener(kl);	    
	    text_boxes[QVT].setLayoutData(gd);
	    
	    Button bQVT = new Button(container, SWT.PUSH);
	    bQVT.addSelectionListener(new ButtonSelectionListener(QVT));
	    bQVT.setText("Browse");
	    
	    if (resources[QVT] != null) {
	    	text_boxes[QVT].setText(resources[QVT].getFullPath().toString());
	    	text_boxes[QVT].setEnabled(false);
		    bQVT.setEnabled(false);
	    }
	    
	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(false);
	}
	
	/**
	 * Returns the QVT resource that specifies the constraint
	 * @return the QVT resource
	 */
	public IResource getQvt(){
		resources[QVT] = ResourcesPlugin.getWorkspace().getRoot().findMember(text_boxes[QVT].getText());
		return resources[QVT];
	}
	
	/**
	 * Returns the model paths specified in the text boxes
	 * Resources may still not exist
	 * @return the model paths
	 */
	public ArrayList<String> getModels()
	{
		ArrayList<String> al = new ArrayList<String>(2);
		al.add(text_boxes[FST].getText());
		al.add(text_boxes[SND].getText());
		return al;
	}
	
	class KeyListenerHelper implements KeyListener{
		
		@Override
		public void keyPressed(KeyEvent e) {	
			return;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			isValid();
		}
		
	}
	
	
	/**
	 * Opens a file browser dialog box for the corresponding resource
	 * @author nmm
	 *
	 */
	class ButtonSelectionListener extends SelectionAdapter
	{
		int res;

		public ButtonSelectionListener(int res)
		{
			super();
			this.res = res;
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
				text_boxes[res].setText(f.getFullPath().toString());
			}
			isValid();
		}
	}

	/**
	 * Tests if the page is complete (currently, no text box is empty)
	 * @return if the page is complete
	 */
	private boolean isValid() {
		IResource qvt_resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(text_boxes[QVT].getText());
		IResource fst_resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(text_boxes[FST].getText());
		IResource snd_resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(text_boxes[SND].getText());

		if (qvt_resource == null) {
			setMessage("Invalid QVT-R resource.", ERROR);
			setPageComplete(false);
			return false;
		} else if (fst_resource == null && snd_resource == null) {
			setMessage("Invalid model resources: at least on model must already exist.", ERROR);
			setPageComplete(false);
			return false;
		} else if (fst_resource == null || snd_resource == null) {
			setMessage("One of the model resources does not exist: will be generated.", WARNING);
			setPageComplete(true);
			return false;
		} else {
			setMessage(null,NONE);
			if (text_boxes[QVT].getText() == null || text_boxes[FST].getText() == null || text_boxes[SND].getText() == null)
				setPageComplete(false);
			else
				setPageComplete(true);
			return true;
		}
	}
}
