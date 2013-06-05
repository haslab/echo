package pt.uminho.haslab.echo.plugin.wizards;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.markers.EchoMarker;
import pt.uminho.haslab.echo.plugin.properties.ProjectProperties;
import pt.uminho.haslab.echo.plugin.views.AlloyModelView;

public class AddQVTRelationWizard extends Wizard  {

	private AddQVTRelationWizardPage page;
	
	private String qvt;
	private ProjectProperties pp;
	private Shell shell;
	
	
	public AddQVTRelationWizard(String qvtPath, ProjectProperties p)
	{
		super();
		qvt = qvtPath;
		pp = p;
	}
	
	@Override
	public  void addPages()
	{
		page = new AddQVTRelationWizardPage(qvt);
		addPage(page);
	}
	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		shell = workbench.getModalDialogShellProvider().getShell();
		/*Object firstElement = selection.getFirstElement();
		
		if(firstElement instanceof IFile)
		{	
			IFile res = (IFile) firstElement;
			String qvt = res.getRawLocation().toString();
			page = new RelationPage(qvt);
			addPage(page);
		//}*/
	}

	
	
	@Override
	public boolean performFinish() {
		EchoRunner er = EchoPlugin.getInstance().getEchoRunner();
		
		int news = 0;
		int newp = 0;
		int orip = 0;
		for (int i = 0; i<page.getModels().size(); i++) {
			IResource f = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(i));
			if (f == null || !f.exists()) {
				news ++;
				newp = i;
			} else {
				orip = i;
			}
		}
		
		if (news == 1) {
			try {
				pp.addConformList(page.getModels().get(orip));
				pp.addQvtRule(page.getQvt());
				pp.addQvtRelation(page.getQvt(), page.getModels());
	
				RelationalTransformation trans = er.parser.getTransformation(page.getQvt());
				String metamodel = trans.getModelParameter().get(newp).getUsedPackage().get(0).getName();
				String metamodeluri = er.parser.getModelURI(metamodel);
	
				er.generateqvt(page.getQvt(),metamodeluri,page.getModels(),page.getModels().get(newp));
				AlloyModelView amv = EchoPlugin.getInstance().getAlloyView();
				amv.refresh();
				amv.setPathToWrite(page.getModels().get(newp));
				amv.setMetamodel(metamodeluri);
				amv.setProperties(pp);

			} catch (ErrorAlloy | ErrorUnsupported | ErrorTransform | ErrorParser e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (news == 0) {
			try {
				pp.addConformList(page.getModels().get(0));
				pp.addConformList(page.getModels().get(1));
				pp.addQvtRule(page.getQvt());
				pp.addQvtRelation(page.getQvt(), page.getModels());
				
				List<String> conformMeta = new ArrayList<String>(1);
				conformMeta.add(page.getModels().get(0));
				if(!er.conforms(conformMeta))
					EchoMarker.createIntraMarker(ResourcesPlugin.getWorkspace().getRoot().findMember(conformMeta.get(0)));
				conformMeta.remove(0);
				conformMeta.add(page.getModels().get(1));
				
				if(!er.conforms(conformMeta))
					EchoMarker.createIntraMarker(ResourcesPlugin.getWorkspace().getRoot().findMember(conformMeta.get(0)));
				
				
				
				boolean b =er.check(page.getQvt(), page.getModels());
				if(!b)
				{
					IResource modelA,modelB;
					modelA = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(0));
					modelB = ResourcesPlugin.getWorkspace().getRoot().findMember(page.getModels().get(1));
					EchoMarker.createInterMarker(modelA, modelB, page.getQvt());
				}
				//MessageDialog.openInformation(shell, "ok", "Check = " + b);
				
			} catch (ErrorAlloy | CoreException | ErrorUnsupported | ErrorTransform | ErrorParser e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(shell, "Invalid model URIs", "At least one of the model instances must already exist.");
		}
		
		return true;
	}

}
