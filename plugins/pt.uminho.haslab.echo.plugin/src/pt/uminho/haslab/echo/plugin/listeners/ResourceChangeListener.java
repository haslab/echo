package pt.uminho.haslab.echo.plugin.listeners;

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

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.plugin.EchoPlugin;
import pt.uminho.haslab.echo.plugin.ResourceManager;
import pt.uminho.haslab.echo.plugin.ResourceRules;
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
			if (res.getProject() != null) {
				ResourceManager p = ProjectPropertiesManager.getProperties(res.getProject());
				switch (delta.getKind()) {
				case IResourceDelta.CHANGED:
					int flags = delta.getFlags();
					if ((flags & IResourceDelta.MARKERS) == 0)
						if (res instanceof IFile) {
							IFile f = (IFile) res;
							try {
								if (p.isManagedModel(res)) {
									EchoReporter.getInstance().debug("Tracked model was changed");
									WorkspaceJob j = new ModelChangedJob(f);
									j.setRule(new ResourceRules(f,ResourceRules.READ));
									j.schedule();
								} else if (p.isManagedMetamodel(res)) {
									EchoReporter.getInstance().debug("Tracked metamodel was changed");
									WorkspaceJob j = new MetaModelChangedJob(f);
									j.setRule(new ResourceRules(f,ResourceRules.READ));
									j.schedule();
								} else if (p.isManagedQVT(res)) {
								    EchoReporter.getInstance().debug("Tracked qvt spec was changed");
								    WorkspaceJob j = new QVTChangedJob(f);
									j.setRule(new ResourceRules(f,ResourceRules.READ));
									j.schedule();
								}
							} catch (EchoError e) {
								e.printStackTrace();
							}
						}
					break;
				case IResourceDelta.REMOVED:
					if (res instanceof IFile) {
						IFile f = (IFile) res;
						try {
							if (p.isManagedModel(res)) {
								EchoReporter.getInstance().debug("Tracked model was removed");
								WorkspaceJob j = new ModelDeletedJob(f);
								j.setRule(new ResourceRules(f,ResourceRules.READ));
								j.schedule();
							} else if (p.isManagedMetamodel(res)) {
								EchoReporter.getInstance().debug("Tracked metamodel was removed");
								WorkspaceJob j = new MetaModelChangedJob(f);
								j.setRule(new ResourceRules(f,ResourceRules.READ));
								j.schedule();
							} else if (p.isManagedQVT(res)) {
							    EchoReporter.getInstance().debug("Tracked qvt spec was removed");
							    WorkspaceJob j = new QVTDeletedJob(f);
								j.setRule(new ResourceRules(f,ResourceRules.READ));
								j.schedule();
							} 
						} catch (EchoError e) {
							e.printStackTrace();
						}
					}
					break;
				}
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
				super("Removing model "+r.getName()+".");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				ResourceManager resManager = ProjectPropertiesManager
						.getProperties(res.getProject());

				try {
					resManager.remModel(res);
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
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
				super("Removing metamodel "+r.getName()+".");
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
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
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
				super("Removing QVT resource "+r.getName()+".");
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
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
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
				super("Reloading metamodel "+r.getName()+".");
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
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
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
				super("Reloading model "+r.getName()+".");
				res = r;
			}

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				ResourceManager resmanager = ProjectPropertiesManager
						.getProperties(res.getProject());
				try {
					resmanager.reloadModel(res);
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
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
				super("Reloading QVT resource "+r.getName()+".");
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
					throw new CoreException(new Status(IStatus.ERROR, EchoPlugin.ID, e.getMessage(), e));
				}
				return Status.OK_STATUS;
			}

		}
	}
}
