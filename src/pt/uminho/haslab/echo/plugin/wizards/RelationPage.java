package pt.uminho.haslab.echo.plugin.wizards;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RelationPage extends WizardPage {

	private String qvt = "";
	//private String modelA = "";
	//private String modelB = "";
	
	private Text tQvt;
	private Text tModelA;
	private Text tModelB;
	
	public RelationPage(String qvtPath) {
		super("Relation Page");
		setTitle("Relation Page");
	    setDescription("Use this page to add new Relations");
	    qvt = qvtPath;
	}

	@Override
	public void createControl(Composite parent) {
		 	Composite container = new Composite(parent, SWT.NULL);
		    GridLayout layout = new GridLayout();
		    container.setLayout(layout);
		    layout.numColumns = 2;
		    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		    KeyListener kl = new KeyListenerHelper();
		    
		    
		    Label labelA = new Label(container, SWT.NULL);
		    labelA.setText("First Model:");

		    tModelA = new Text(container, SWT.BORDER | SWT.SINGLE);
		    tModelA.setText("");
		    tModelA.addKeyListener(kl);	    
		    tModelA.setLayoutData(gd);
		    
		    Label labelB = new Label(container, SWT.NULL);
		    labelB.setText("Second Model:");
		    
		    tModelB = new Text(container, SWT.BORDER | SWT.SINGLE);
		    tModelB.setText("");
		    tModelB.addKeyListener(kl);	    
		    tModelB.setLayoutData(gd);
		    
		    Label labelQvt = new Label(container, SWT.NULL);
		    labelQvt.setText("QVTr file:");
		    
		    tQvt = new Text(container, SWT.BORDER | SWT.SINGLE);
		    tQvt.setText(qvt);
		    tQvt.addKeyListener(kl);	    
		    tQvt.setLayoutData(gd);
		    
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

}
