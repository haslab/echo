package pt.uminho.haslab.echo.plugin.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ETransformation;
import pt.uminho.haslab.mde.transformation.qvt.EQVTTransformation;

public class ConstraintAddWizardPage extends WizardPage {

//	private boolean magage_deps = false;
	
	private Text qvttext;
	private IResource qvtresource;

	private Map<String,Text> model2text = new HashMap<String,Text>();
	
	private Map<String,Button> model2button = new HashMap<String,Button>();
	private Map<String,Label> model2label = new HashMap<String,Label>();

	private Map<String,IResource> model2resource = new HashMap<String,IResource>();

//	private List<Group> dependecies = new ArrayList<Group>();
//	private List<Button> dependecies_rem = new ArrayList<Button>();
//	private List<Label> dependecies_filler = new ArrayList<Label>();

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

		ETransformation trans = null;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		KeyListener kl = new KeyListenerHelper();

		for (Text c : model2text.values()) c.dispose();
		for (Button c : model2button.values()) c.dispose();
		for (Label c : model2label.values()) c.dispose();
		
		try {
			trans = MDEManager.getInstance().getETransformation(qvtresource.getFullPath().toString(),false);
		} catch (EchoError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params = new ArrayList<String>();
		for (EModelParameter mdl : trans.getModelParams()) {
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
		//addDependency();
	}
	
//	private void addDependency() {
//
//		Label label = new Label(container, SWT.NULL);
//		label.setText("Dependency:");
//
//		Button check = new Button(container, SWT.CHECK);
//		check.setText("Manage dependencies:");
//		check.addSelectionListener(new ManageDependenciesListener());
//
//		Button add = new Button(container, SWT.PUSH);
//		add.setText("+");
//		add.setEnabled(true);
//		add.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				addDependencyGroup(0);			
//				container.layout(true);
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//
//		for (int i = 0; i < params.size(); i++) {
//			
//			addDependencyGroup(i);
//			
//		}
//		
//		container.layout(true);
//
//	}
//	
//	private void addDependencyGroup(int index) {
//		dependecies_filler.add(new Label(container,SWT.None));
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		GridLayout l = new GridLayout(2,false);
//		l.marginTop = 0;
//		l.marginBottom = 0;
//		l.verticalSpacing = 0;
//		l.marginHeight = 0;
//	
//		Group teste = new Group(container, SWT.SHADOW_ETCHED_IN);
//		
//		teste.setLayout(l);
//		teste.setLayoutData(gd);
//
//		Combo aModel = new Combo(teste, SWT.DROP_DOWN | SWT.READ_ONLY);
//		aModel.setItems(params.toArray(new String[params.size()]));
//		aModel.select(index);
//		aModel.setLayoutData(gd);
//		
//		boolean fst = true;
//		for (int j = 0; j < params.size(); j++) {
//			if (j != index) {
//				Combo bModel = new Combo(teste, SWT.DROP_DOWN | SWT.READ_ONLY);
//				bModel.setItems(params.toArray(new String[params.size()]));
//				bModel.select(j);
//				bModel.setLayoutData(gd);
//				
//				if (fst) {				
//					Composite bts = new Composite(teste,SWT.None);
//					bts.setLayout(new GridLayout(2,false));
//					Button ad = new Button(bts, SWT.PUSH);
//					ad.setText("+");
//					ad.setVisible(true);
//					Button re = new Button(bts, SWT.PUSH);
//					re.setText("-");
//					re.setVisible(true);
//					fst = false;
//					bts.setVisible(true);
//				} else {
//					if (!(j == params.size() - 1 || (j == index - 1 && index == params.size() - 1)))
//					new Label(teste,SWT.None);
//				}
//			}
//		}
//		Button addi = new Button(container, SWT.PUSH);
//		addi.setText("-");
//		addi.setEnabled(magage_deps);
//		addi.addSelectionListener(new RemoveDependencyListener(dependecies.size()));
//		setEnableRecursive(teste, magage_deps);
//		dependecies_rem.add(addi);
//		teste.setVisible(true);
//		dependecies.add(teste); 
//
//	}
	
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
	
//	class ManageDependenciesListener implements SelectionListener{
//
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			Button button = (Button) e.widget;
//			magage_deps = button.getSelection();
//			for (Group g : dependecies)
//				setEnableRecursive(g,magage_deps);
//			for (Button b : dependecies_rem)
//				b.setEnabled(magage_deps);
//		}
//
//		@Override
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}
//		
//		
//	}
	
	private void setEnableRecursive (Control c, boolean e) {
		c.setEnabled(e);
		if (c instanceof Composite)
			for (Control x : ((Composite) c).getChildren())
				setEnableRecursive(x, e);
	}
	
//	class RemoveDependencyListener implements SelectionListener {
//		private int i;
//		
//		RemoveDependencyListener (int i) {
//			this.i = i;
//		}
//		
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			dependecies.get(i).dispose();
//			dependecies_filler.get(i).dispose();
//			e.widget.dispose();
//			container.layout(true);
//		}
//
//		@Override
//		public void widgetDefaultSelected(SelectionEvent e) {
//			return;
//		}
//		
//	}
//	
	
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
	
	
//	Map<String,List<List<String>>> getDependencies() {
//		Map<String,List<List<String>>> deps = new HashMap<String,List<List<String>>>();
//		for (Group g : dependecies) {
//			List<String> sources = new ArrayList<String>();
//			Control[] controls = g.getChildren();
//			String target = params.get(((Combo) controls[0]).getSelectionIndex());
//			for (int i = 1; i < controls.length; i ++)
//				if (controls[i] instanceof Combo)
//					sources.add(params.get(((Combo) controls[i]).getSelectionIndex()));
//			List<List<String>> ex = deps.get(target);
//			if (ex == null) ex = new ArrayList<List<String>>();
//			ex.add(sources);
//			deps.put(target,ex);
//		}
//		return deps;
//	}
}
