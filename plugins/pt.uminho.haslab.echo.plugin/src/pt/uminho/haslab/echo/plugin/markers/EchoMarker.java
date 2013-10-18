package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.echo.plugin.ConstraintManager.Constraint;

public class EchoMarker {

	public final static String INTRA_ERROR = "pt.uminho.haslab.echo.plugin.intrainconsistency";
	public final static String INTER_ERROR = "pt.uminho.haslab.echo.plugin.interinconsistency";

	public final static String CONSTRAINT = "constraint";
	public final static String OPPOSITE = "opposite";

	public final static String GED = "ged";
	public final static String OPS = "ops";

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
			res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
		} catch (CoreException e) {
			throw new ErrorAPI("Failed to create marker.");
		}
	}
	


	/**
	 * Creates inter-model error markers between two model resources
	 * @param fstmodelres the first model
	 * @param partner the second model
	 * @param constraintres the constraint relating them
	 * @return the list of created markers
	 * @throws ErrorAPI 
	 */
	public static List<IMarker> createInterMarker(Constraint c) throws ErrorAPI {
		List<IMarker> marks = new ArrayList<IMarker>();
		IMarker mark;
		mark = createSingleInterMarker(c.fstmodel, c.sndmodel, c.constraint
				.getFullPath().toString());
		marks.add(mark);
		mark = createSingleInterMarker(c.sndmodel, c.fstmodel, c.constraint
				.getFullPath().toString());
		marks.add(mark);
		return marks;
	}

	/**
	 * Creates an individual inter-model error marker on a model resource
	 * @param fstmodelres the model to add the marker
	 * @param relatedres the related model
	 * @param constraintres the constraint relating them
	 * @return the created marker
	 * @throws ErrorAPI 
	 */
	private static IMarker createSingleInterMarker(IResource modelres,
			IResource relatedres, String qvtRule) throws ErrorAPI {
		IMarker mark;
		try {
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
				String relateduri = (String) mk.getAttribute(EchoMarker.OPPOSITE);
				IResource related = res.getWorkspace().getRoot().findMember(relateduri);
				for (IMarker mk1 : related.findMarkers(EchoMarker.INTER_ERROR,false, 0)) 
					if (mk1.getAttribute(EchoMarker.OPPOSITE).equals(res.getFullPath().toString()))
						mk1.delete();
				mk.delete();
			}
		} catch (CoreException e) {
			throw new ErrorAPI("Failed to create marker.");
		}
	}

	/**
	 * Removes the inter-model error markers of two model resources related by a particular constraint
	 * @param fstmodelres the first model to remove marker
	 * @param sndmodelres the second model to remove marker
	 * @param constraintres the constraint relating them
	 * @throws ErrorAPI
	 */
	public static void removeRelatedInterMarker(Constraint c) throws ErrorAPI {
		String fstmodeluri = c.fstmodel.getFullPath().toString();
		String sndmodeluri = c.sndmodel.getFullPath().toString();
		String constrainturi = c.constraint.getFullPath().toString();

		try {
			for (IMarker mk : c.fstmodel.findMarkers(EchoMarker.INTER_ERROR,false, 0))
				if (mk.getAttribute(EchoMarker.OPPOSITE).equals(sndmodeluri)
						&& mk.getAttribute(EchoMarker.CONSTRAINT).equals(constrainturi))
					mk.delete();

			for (IMarker mk : c.sndmodel.findMarkers(EchoMarker.INTER_ERROR,false, 0))
				if (mk.getAttribute(EchoMarker.OPPOSITE).equals(fstmodeluri)
						&& mk.getAttribute(EchoMarker.CONSTRAINT).equals(constrainturi))
					mk.delete();

		} catch (CoreException e) {
			throw new ErrorAPI("Failed to remove markers.");
		}
	}

}
