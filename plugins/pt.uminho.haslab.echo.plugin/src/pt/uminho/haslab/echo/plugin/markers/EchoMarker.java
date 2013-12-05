package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.plugin.ConstraintManager.Constraint;

/**
 * Manages Echo error markers
 * @author nmm
 *
 */
public class EchoMarker {

	/** intra-model error marker */
	public final static String INTRA_ERROR = "pt.uminho.haslab.echo.plugin.intrainconsistency";
	/** inter-model error marker */
	public final static String INTER_ERROR = "pt.uminho.haslab.echo.plugin.interinconsistency";

	/** inter-model error marker constraint attribute */
	public final static String CONSTRAINT = "constraint";
	/** inter-model error marker opposite model attribute */
	public final static String OPPOSITE = "opposite";

	/** graph edit distance */
	public final static String GED = "ged";
	/** operation-based distance */
	public final static String OBD = "obd";

	/**
	 * Creates a intra-model error marker in a model resource
	 * @param res the model to add the marker
	 * @return the created marker
	 * @throws ErrorAPI 
	 */
	public static IMarker createIntraMarker(IResource res) throws ErrorAPI {
		IMarker mark = null;
		try {
			res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
			mark = res.createMarker(EchoMarker.INTRA_ERROR);
			mark.setAttribute(IMarker.MESSAGE,
					"Model instance does not conform to the meta-model.");
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			
			for (IMarker pre : res.findMarkers(INTER_ERROR, false, 0)) {
				IResource related = res.getWorkspace().getRoot().findMember((String) pre.getAttribute(OPPOSITE));
				for (IMarker pre2 : related.findMarkers(INTER_ERROR, false, 0)) {
					if (pre2.getAttribute(OPPOSITE).equals(res.getFullPath().toString()))
						pre2.delete();
				}
			}
				
			
		} catch (CoreException e) {
			throw new ErrorAPI("Failed to create marker.");
		}
		return mark;
	}
	
	/**
	 * Removes intra-model errors from a model resource
	 * @param res the model to remove the markers
	 * @throws ErrorAPI
	 */
	public static void removeIntraMarkers(IResource res) throws ErrorAPI {
		try {
			if(res.isAccessible())
				res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
		} catch (CoreException e) {
			throw new ErrorAPI("Failed to delete marker.");
		}
	}
	
	/**
	 * Creates inter-model error markers between two model resources
	 * @param constraint the constraint that raised the error
	 * @return the list of created markers
	 * @throws ErrorAPI 
	 */
	public static List<IMarker> createInterMarker(Constraint constraint) throws ErrorAPI {
		List<IMarker> marks = new ArrayList<IMarker>();
		IMarker mark;
		try {
			if (constraint.sndmodel.findMarkers(INTRA_ERROR,false,0).length == 0) {
				mark = createSingleInterMarker(constraint.fstmodel, constraint.sndmodel, constraint.constraint
						.getFullPath().toString());
				marks.add(mark);
			}
			if (constraint.fstmodel.findMarkers(INTRA_ERROR,false,0).length == 0) {
				mark = createSingleInterMarker(constraint.sndmodel, constraint.fstmodel, constraint.constraint
					.getFullPath().toString());
			marks.add(mark);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return marks;
	}

	/**
	 * Creates an individual inter-model error marker on a model resource
	 * @param relatedres the related model
	 * @return the created marker
	 * @throws ErrorAPI 
	 */
	private static IMarker createSingleInterMarker(IResource modelres,
			IResource relatedres, String qvtRule) throws ErrorAPI {
		IMarker mark;
		
		
		try {
			for (IMarker pre : modelres.findMarkers(INTER_ERROR, false, 0)) {
				if (pre.getAttribute(CONSTRAINT).equals(qvtRule) && pre.getAttribute(OPPOSITE).equals(relatedres.getFullPath().toString()))
					pre.delete();
			}
			
			mark = modelres.createMarker(EchoMarker.INTER_ERROR);
			mark.setAttribute(IMarker.MESSAGE,
					"Model instance is not consistent with a QVT relation");
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			mark.setAttribute(EchoMarker.CONSTRAINT, qvtRule);
			mark.setAttribute(EchoMarker.OPPOSITE, relatedres.getFullPath().toString());
		}
		catch (CoreException e) {
			throw new ErrorAPI("Failed to create marker.");
		}
		return mark;
	}
	
	/**
	 * Removes inter-model errors from a model resource
	 * @param res the model to remove the markers
	 * @throws ErrorAPI
	 */
	public static void removeInterMarkers(IResource res) throws ErrorAPI {
		try {
			for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR,false, 0)) {
                System.out.println("no ciclo");
				String relatedUri = (String) mk.getAttribute(EchoMarker.OPPOSITE);
                System.out.println(relatedUri);
				IResource related = res.getWorkspace().getRoot().findMember(relatedUri);
                System.out.println(related);
				for (IMarker mk1 : related.findMarkers(EchoMarker.INTER_ERROR,false, 0)) 
					if (mk1.getAttribute(EchoMarker.OPPOSITE).equals(res.getFullPath().toString()))
                    {
                        System.out.println("deleting");
                        mk1.delete();
                    }
				mk.delete();
			}
		} catch (CoreException e) {
			throw new ErrorAPI("Failed to delete marker.");
		}
	}

	/**
	 * Removes the inter-model error markers of two model resources related by a particular constraint
	 * @param constraint the constraint to be removed
	 * @throws ErrorAPI
	 */
	public static void removeRelatedInterMarker(Constraint constraint) throws ErrorAPI {
		String fstmodeluri = constraint.fstmodel.getFullPath().toString();
		String sndmodeluri = constraint.sndmodel.getFullPath().toString();
		String constrainturi = constraint.constraint.getFullPath().toString();

		try {
			if(constraint.fstmodel.isAccessible())
				for (IMarker mk : constraint.fstmodel.findMarkers(EchoMarker.INTER_ERROR,false, 0))
					if (mk.getAttribute(EchoMarker.OPPOSITE).equals(sndmodeluri)
							&& mk.getAttribute(EchoMarker.CONSTRAINT).equals(constrainturi))
						mk.delete();
			
			if(constraint.sndmodel.isAccessible())
				for (IMarker mk : constraint.sndmodel.findMarkers(EchoMarker.INTER_ERROR,false, 0))
					if (mk.getAttribute(EchoMarker.OPPOSITE).equals(fstmodeluri)
							&& mk.getAttribute(EchoMarker.CONSTRAINT).equals(constrainturi))
						mk.delete();

		} catch (CoreException e) {
			throw new ErrorAPI("\nFailed to remove markers.\n");
		}
	}

}
