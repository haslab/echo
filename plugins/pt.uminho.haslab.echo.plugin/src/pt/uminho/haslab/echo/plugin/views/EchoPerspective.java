package pt.uminho.haslab.echo.plugin.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class EchoPerspective implements IPerspectiveFactory {

	public static final String ID = "pt.uminho.haslab.echo.plugin.views.echoperspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		  defineActions(layout);
		   defineLayout(layout);
	}
	
	public void defineActions(IPageLayout layout) {
        layout.addNewWizardShortcut("org.eclipse.emf.codegen.ecore.ui.EmptyProjectWizard");
  		layout.addNewWizardShortcut("org.eclipse.emf.ecore.presentation.EcoreModelWizardID");
        layout.addNewWizardShortcut("org.eclipse.qvtd.examples.qvtrelation.empty");
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
           
        
        layout.addShowViewShortcut("pt.uminho.haslab.echo.plugin.toolbar");
        layout.addShowViewShortcut(GraphView.ID);
        layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        
        layout.addFastView(EchoPerspective.ID);
        layout.addPerspectiveShortcut(EchoPerspective.ID);
	}

	public void defineLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        
        IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.25, editorArea);
        left.addView(IPageLayout.ID_PROJECT_EXPLORER);
        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.5, editorArea);
        right.addView(GraphView.ID);
        IFolderLayout bot = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea);
        bot.addView(IPageLayout.ID_PROBLEM_VIEW);
        bot.addView(IPageLayout.ID_PROP_SHEET);
	}
}
