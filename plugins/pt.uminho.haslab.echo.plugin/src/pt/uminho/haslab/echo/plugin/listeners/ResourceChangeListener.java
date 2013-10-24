package pt.uminho.haslab.echo.plugin.listeners;

import java.rmi.server.UID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.properties.ProjectPropertiesManager;

/**
 * Handles changes on resources
 * @author nmm
 *
 */
public class ResourceChangeListener implements IResourceChangeListener {

	/**
	 * Reacts to resource changes (only post resource changes)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
			try {
				event.getDelta().accept(new DeltaIterator());
			} catch (CoreException e) {
				e.printStackTrace();
			}
			break;
		}

	}

	/**
	 * Manages change deltas
	 * Only reacts to resource modification and deletion
	 * @author nmm
	 *
	 */
	class DeltaIterator implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) {
			IResource res = delta.getResource();
			ResourceManager p = ProjectPropertiesManager.getProperties(res.getProject());
			switch (delta.getKind()) {
			case IResourceDelta.CHANGED:
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.MARKERS) == 0)
					if (res instanceof IFile) {
						IFile f = (IFile) res;
						if (p.isManagedModel(res)) {
							EchoReporter.getInstance().debug("Tracked model was changed");
							Job j = new ModelChangedJob(f);
							j.setRule(f);
							j.schedule();
						} else if (p.isManagedMetamodel(res)) {
							EchoReporter.getInstance().debug("Tracked metamodel was changed");
							Job j = new MetaModelChangedJob(f);
							j.setRule(f);
							j.schedule();
						} else if (p.isManagedQVT(res)) {
						    EchoReporter.getInstance().debug("Tracked qvt spec was changed");
							Job j = new QVTChangedJob(f);
							j.setRule(f);
							j.schedule();
						} 
					}
				break;
			case IResourceDelta.REMOVED:
				if (res instanceof IFile) {
					IFile f = (IFile) res;
					if (p.isManagedModel(res)) {
						EchoReporter.getInstance().debug("Tracked model was removed");
						Job j = new ModelDeletedJob(f);
						j.setRule(f);
						j.schedule();
					} else if (p.isManagedMetamodel(res)) {
						EchoReporter.getInstance().debug("Tracked metamodel was removed");
						Job j = new MetaModelChangedJob(f);
						j.setRule(f);
						j.schedule();
					} else if (p.isManagedQVT(res)) {
					    EchoReporter.getInstance().debug("Tracked qvt spec was removed");
						Job j = new QVTDeletedJob(f);
						j.setRule(f);
						j.schedule();
					} 
				}
				break;
			}
			return true; // visit the children
		}


		/**
		 * Model deleted job: untracks the model
		 * @author nmm
		 *
		 */
		class ModelDeletedJob extends WorkspaceJob {
			private IResource res = null;

			public ModelDeletedJob(IResource r) {
				super("Deleting model.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());

				try {
					resmanager.remModel(res);
				} catch (Exception e) {
					MessageDialog.openError(null,
							"Error removing model.",
							"Model failed to be untracked.");
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		}

		/**
		 * Metamodel deleted job: untracks meta-model (and consequently all dependant models)
		 * @author nmm
		 *
		 */
		class MetamodelDeletedJob extends WorkspaceJob {
			private IResource res = null;

			public MetamodelDeletedJob(IResource r) {
				super("Deleting meta-model.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());

				try {
					resmanager.remMetamodel(res);
				} catch (Exception e) {
					MessageDialog.openError(null,
							"Error removing meta-model.",
							"Meta-model failed to be untracked.");
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		}
		
		/**
		 * QVT resourced deleted job: removes all constraints
		 * @author nmm
		 *
		 */
		class QVTDeletedJob extends WorkspaceJob {
			private IResource res = null;

			public QVTDeletedJob(IResource r) {
				super("Deleting QVT resource.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());

				try {
					resmanager.removeAllQVTConstraint(res);
				} catch (Exception e) {
					MessageDialog.openError(null,
							"Error removing QVT resourced.",
							"Failed to remove associated QVT constraints.");
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		}

		/**
		 * Meta-model changed job: reloads meta-model
		 * @author nmm
		 *
		 */
		class MetaModelChangedJob extends WorkspaceJob {
			private IResource res = null;

			public MetaModelChangedJob(IResource r) {
				super("Meta-model reload.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());
				try {
					resmanager.reloadMetamodel(res);
				} catch (Exception e) {
					try {
						resmanager.remMetamodel(res);
					} catch (ErrorAPI | ErrorParser e1) {
						e1.printStackTrace();
					}
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
									"Error reloading meta-model.",
									"Meta-model has been untracked.");
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}

		}

		/**
		 * Model changed job: reloads model
		 * @author nmm
		 *
		 */
		class ModelChangedJob extends WorkspaceJob {
			private IResource res = null;

			public ModelChangedJob(IResource r) {
				super("Model reload.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());
				try {
					resmanager.reloadModel(res);
				} catch (Exception e) {
					try {
						resmanager.remModel(res);
					} catch (ErrorAPI | ErrorParser e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//MessageDialog.openError(null,"Error reloading model.","Error updating model.");
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}

		}
		
		/**
		 * QVT spec changed job: reloads constraints
		 * @author nmm
		 *
		 */
		class QVTChangedJob extends WorkspaceJob {
			private IResource res = null;

			public QVTChangedJob(IResource r) {
				super("QVT constraint reload.");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());
				try {
					resmanager.reloadQVTConstraint(res);
				} catch (Exception e) {
					MessageDialog.openError(null,
									"Error reloading model.",
									"Error updating model.");
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}

		}
	}
}
