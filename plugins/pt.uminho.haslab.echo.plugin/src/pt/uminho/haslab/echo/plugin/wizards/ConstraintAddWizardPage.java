package pt.uminho.haslab.echo.plugin.wizards;

import java.awt.Panel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.emf.EchoParser;

public class ConstraintAddWizardPage extends WizardPage {

	
	private Text qvttext;
	private IResource qvtresource;

	private Map<String,Text> model2text = new HashMap<String,Text>();
	
	private Map<String,Button> model2button = new HashMap<String,Button>();
	private Map<String,Label> model2label = new HashMap<String,Label>();

	private Map<String,IResource> model2resource = new HashMap<String,IResource>();

	private List<Composite> dependecies = new ArrayList<Composite>();
	private List<String> params = new ArrayList<String>();
	
	private final String DEFAULT_MESSAGE = "Select two model instances to be constrained by the QVT-R transformation.";
	private Composite container;
	
	public ConstraintAddWizardPage(IResource qvt_resource) {
		super("Add inter-model consistency");
		setTitle("Add inter-model consistency");
	    setDescription(DEFAULT_MESSAGE);
	    qvtresource = qvt_resource;
	}

	@Override
	public void createControl(Composite parent) {
	 	container = new Composite(parent, SWT.NULL);
	    container.setLayout(new GridLayout(3,false));
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    KeyListener kl = new KeyListenerHelper();
	    
	    Label labelQvt = new Label(container, SWT.NULL);
	    labelQvt.setText("QVT-R transformation:");
	    
	    qvttext = new Text(container, SWT.BORDER | SWT.SINGLE);
	    qvttext.addKeyListener(kl);	    
	    qvttext.setLayoutData(gd);
	    
	    Button bQVT = new Button(container, SWT.PUSH);
	    bQVT.addSelectionListener(new ButtonSelectionListener(null));
	    bQVT.setText("Browse");
	    
	    if (qvtresource != null) {
	    	qvttext.setText(qvtresource.getFullPath().toString());
	    	qvttext.setEnabled(false);
		    bQVT.setEnabled(false);
		    buildTexts();		    
	    }
	    
	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(false);
		container.layout(true);
	}
	
	private void buildTexts() {

		RelationalTransformation qvt;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		KeyListener kl = new KeyListenerHelper();

		try {
			
			for (Text c : model2text.values()) c.dispose();
			for (Button c : model2button.values()) c.dispose();
			for (Label c : model2label.values()) c.dispose();
			
			qvt = EchoParser.getInstance().loadQVT(qvtresource.getFullPath().toString());
			params = new ArrayList<String>();
			for (TypedModel mdl : qvt.getModelParameter()) {
				params.add(mdl.getName());
				Label label = new Label(container, SWT.NULL);
				label.setText(mdl.getName() + " model:");

				Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
				text.setText("");
				text.addKeyListener(kl);
				text.setLayoutData(gd);
				model2text.put(mdl.getName(),text);

				Button bModel = new Button(container, SWT.PUSH);
				bModel.addSelectionListener(new ButtonSelectionListener(mdl
						.getName()));
				bModel.setText("Browse");
				
				

				model2label.put(mdl.getName(),label);
				model2button.put(mdl.getName(),bModel);
				model2text.put(mdl.getName(),text);


			}
			addDependency();
			

		} catch (ErrorParser | ErrorTransform e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addDependency() {

		Label label = new Label(container, SWT.NULL);
		label.setText("Dependency:");

		Button check = new Button(container, SWT.CHECK);
		check.setText("Manage dependencies:");

		Button add = new Button(container, SWT.PUSH);
		add.setText("+");
		add.setEnabled(false);


		for (int i = 0; i < params.size(); i++) {
			for (int j = 0; j < params.size(); j++) {
				if (j != i) {
					GridData gd = new GridData(GridData.FILL_HORIZONTAL);
					GridLayout l = new GridLayout(3,false);
					l.marginTop = 0;
					l.marginBottom = 0;
					l.verticalSpacing = 0;
					l.marginHeight = 0;
					
					
					new Label(container, SWT.None);
					Composite teste = new Composite(container, SWT.None);
					
					teste.setLayout(l);
					teste.setLayoutData(gd);

					Button ad = new Button(teste, SWT.PUSH);
					ad.setText("+");
					ad.setEnabled(false);
					
					Combo aModel = new Combo(teste, SWT.DROP_DOWN | SWT.READ_ONLY);
					aModel.setItems(params.toArray(new String[params.size()]));
					aModel.setEnabled(false);
					aModel.select(i);
					aModel.setLayoutData(gd);
					
					Combo bModel = new Combo(teste, SWT.DROP_DOWN | SWT.READ_ONLY);
					bModel.setItems(params.toArray(new String[params.size()]));
					bModel.setEnabled(false);
					bModel.select(j);
					bModel.setLayoutData(gd);

					new Label(container, SWT.None);
					
					teste.setVisible(true);
					
					dependecies.add(teste); 
				}
			}
		}

	}
	
	/**
	 * Returns the QVT resource that specifies the constraint
	 * @return the QVT resource
	 */
	public IResource getQvt(){
		qvtresource = ResourcesPlugin.getWorkspace().getRoot().findMember(qvttext.getText());
		return qvtresource;
	}
	
	/**
	 * Returns the model paths specified in the text boxes
	 * Resources may still not exist
	 * @return the model paths
	 */
	public ArrayList<String> getModels()
	{
		ArrayList<String> al = new ArrayList<String>();
		for (Text t : model2text.values())
			al.add(t.getText());
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
		String res;

		public ButtonSelectionListener(String res)
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
				if (res == null) qvttext.setText(f.getFullPath().toString());
				else model2text.get(res).setText(f.getFullPath().toString());
			}
			isValid();
		}
	}

	/**
	 * Tests if the page is complete (currently, no text box is empty)
	 * @return if the page is complete
	 */
	private boolean isValid() {
		int invalid_res = 0;
		
		for (String s : model2text.keySet()) {
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(model2text.get(s).getText());
			model2resource.put(s, r);
			if (r == null)
				invalid_res++;
		}

		if (!qvtresource.equals(ResourcesPlugin.getWorkspace().getRoot()
				.findMember(qvttext.getText()))) {
			qvtresource = (ResourcesPlugin.getWorkspace().getRoot()
					.findMember(qvttext.getText()));
			if (qvtresource != null) 
				buildTexts();
		}
		
		if (qvtresource == null) {
			setMessage("Invalid QVT-R resource.", ERROR);
			setPageComplete(false);
			return false;
		} else if (invalid_res > 1) {
			setMessage("Invalid model resources: at least on model must already exist.", ERROR);
			setPageComplete(false);
			return false;
		} else if (invalid_res == 1) {
			setMessage("One of the model resources does not exist: will be generated.", WARNING);
			setPageComplete(true);
			return false;
		} else {
			setMessage(null,NONE);
			boolean complete = true;
			for (Text t : model2text.values())
				if (t.getText() == null) complete = false;
			setPageComplete(complete);
			return true;
		}
	}
}
