package pt.uminho.haslab.echo.plugin.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import pt.uminho.haslab.echo.ErrorAPI;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

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
	public final static String MODELS = "opposite";
	/** inter-model error marker model parameter model attribute */
	public final static String PARAM = "parameter";

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
				List<IResource> related = oppositeFromString(res,(String) pre.getAttribute(MODELS));
				for (IResource res2 : related)
					for (IMarker pre2 : res2.findMarkers(INTER_ERROR, false, 0))
						if (pre2.getAttribute(MODELS).equals(pre.getAttribute(MODELS)))
							pre2.delete();
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
	public static List<IMarker> createInterMarker(EConstraint constraint) throws ErrorAPI {
		List<IMarker> marks = new ArrayList<IMarker>();
		IMarker mark;
		try {
			for (int i = 0; i < constraint.models.size(); i++)
				if (constraint.models.get(i).findMarkers(INTRA_ERROR,false,0).length == 0) {
					mark = createSingleInterMarker(i, constraint.models, constraint.constraint
							.getFullPath().toString(),constraint.params.get(0));
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
	private static IMarker createSingleInterMarker(int targetmodel,
			List<IResource> models, String qvtRule, String name) throws ErrorAPI {
		IMarker mark;
		
		
		try {
			for (IMarker pre : models.get(targetmodel).findMarkers(INTER_ERROR, false, 0)) {
				if (pre.getAttribute(CONSTRAINT).equals(qvtRule) && pre.getAttribute(MODELS).equals(oppositeToString(models)))
					pre.delete();
			}
			
			mark = models.get(targetmodel).createMarker(EchoMarker.INTER_ERROR);
			mark.setAttribute(IMarker.MESSAGE,
					"Model instance is not consistent with a QVT relation");
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			mark.setAttribute(EchoMarker.CONSTRAINT, qvtRule);
			mark.setAttribute(EchoMarker.MODELS, oppositeToString(models));
			mark.setAttribute(EchoMarker.PARAM, name);
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
			for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR,false, 0)) 
				for (IResource related : oppositeFromString(res, (String) mk.getAttribute(EchoMarker.MODELS))) {
					for (IMarker mk1 : related.findMarkers(EchoMarker.INTER_ERROR,false, 0)) 
						if (mk1.getAttribute(EchoMarker.MODELS).equals(mk.getAttribute(EchoMarker.MODELS)))
	                        mk1.delete();
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
	public static void removeRelatedInterMarker(EConstraint constraint) throws ErrorAPI {
		String constrainturi = constraint.constraint.getFullPath().toString();

		try {
			for (IResource res : constraint.models)
				if(res.isAccessible())
					for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR,false, 0))
						if (mk.getAttribute(EchoMarker.MODELS).equals(oppositeToString(constraint.models))
								&& mk.getAttribute(EchoMarker.CONSTRAINT).equals(constrainturi))
							mk.delete();
			

		} catch (CoreException e) {
			throw new ErrorAPI("\nFailed to remove markers.\n");
		}
	}
	
	private static String oppositeToString(List<IResource> models) {
		StringBuffer s = new StringBuffer();
		for (IResource res : models) {
			s.append(res.getFullPath().toString());
			s.append(";");
		}
		return s.toString();
	}
	
	private static List<IResource> oppositeFromString(IResource src, String models) {
		List<IResource> res = new ArrayList<IResource>();
		for (String s : models.split(";"))
			res.add(src.getWorkspace().getRoot().findMember(s));
		return res;
	}

}
