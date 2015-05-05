package pt.uminho.haslab.echo.plugin.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorAPI;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EConstraintManager;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.List;

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

	/** inter-model error marker model parameter model attribute */
	public final static String PARAM = "parameter";

	/** graph edit distance */
	public final static String GED = "ged";
	/** operation-based distance */
	public final static String OBD = "obd";

	public static final String CONSTRAINT = "constraint";

	/**
	 * Creates a intra-model error marker in a model resource
	 * @param res the model to add the marker
	 * @return the created marker
	 * @throws EErrorAPI 
	 * @throws EErrorUnsupported 
	 * @throws EErrorParser 
	 */
	public static IMarker createIntraMarker(IResource res) throws EErrorAPI, EErrorParser, EErrorUnsupported {
		IMarker mark = null;
		try {
			res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
			mark = res.createMarker(EchoMarker.INTRA_ERROR);
			mark.setAttribute(IMarker.MESSAGE,
					"Model instance does not conform to the meta-model.");
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			
			// inter-model inconsistencies cannot be fixed with source inconsistent
			for (IMarker pre : res.findMarkers(INTER_ERROR, false, 0)) {
				List<IResource> related = oppositeFromString(res,pre);
				for (IResource res2 : related)
					for (IMarker pre2 : res2.findMarkers(INTER_ERROR, false, 0))
						if (pre2.getAttribute(CONSTRAINT).equals(pre.getAttribute(CONSTRAINT)))
							pre2.delete();
			}
				
			
		} catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to create marker.",Task.PLUGIN);
		}
		return mark;
	}
	
	/**
	 * Removes intra-model errors from a model resource
	 * @param res the model to remove the markers
	 * @throws EErrorAPI
	 */
	public static void removeIntraMarkers(IResource res) throws EErrorAPI {
		try {
			if(res.isAccessible())
				res.deleteMarkers(EchoMarker.INTRA_ERROR, true, 0);
		} catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to delete marker.",Task.PLUGIN);
		}
	}
	
	/**
	 * Creates inter-model error markers between two model resources
	 * @param constraint the constraint that raised the error
	 * @return the list of created markers
	 * @throws EError 
	 */
	public static List<IMarker> createInterMarker(String constraintID) throws EError {
		List<IMarker> marks = new ArrayList<IMarker>();
		IMarker mark;
		try {
			EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
			for (int i = 0; i < constraint.getModels().size(); i++) {
				String modelID = constraint.getModels().get(i);
				EModel model = MDEManager.getInstance().getModelID(modelID);
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(model.getURI());
 				if (res.findMarkers(INTRA_ERROR,false,0).length == 0) {
 					ETransformation t = MDEManager.getInstance().getETransformationID(constraint.transformationID);
					mark = createSingleInterMarker(i, constraint,t.getModelParams().get(0).getName());
					marks.add(mark);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return marks;
	}

	/**
	 * Creates an individual inter-model error marker on a model resource
	 *
	 * @return the created marker
	 * @throws EErrorAPI 
	 * @throws EErrorUnsupported 
	 * @throws EErrorParser 
	 */
	private static IMarker createSingleInterMarker(int targetmodel,
			EConstraint constraint, String name) throws EErrorAPI, EErrorParser, EErrorUnsupported {
		IMarker mark;
		
		try {
			EModel trgmodel = MDEManager.getInstance().getModelID(constraint.getModels().get(targetmodel));
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(trgmodel.getURI());
			for (IMarker pre : res.findMarkers(INTER_ERROR, false, 0)) {
				if (pre.getAttribute(CONSTRAINT).equals(constraint.ID))
					pre.delete(); 
			}
			
			mark = res.createMarker(EchoMarker.INTER_ERROR);
			mark.setAttribute(IMarker.MESSAGE,
					"Model instance is not consistent with a QVT relation");
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			mark.setAttribute(EchoMarker.CONSTRAINT, constraint.ID);
			mark.setAttribute(EchoMarker.PARAM, name);
		}
		catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to create marker.",Task.PLUGIN);
		}
		return mark;
	}
	
	/**
	 * Removes inter-model errors from a model resource.
	 * Will also remove the equivalent markers from related resources.
	 * @param res the model to remove the markers
	 * @throws EErrorAPI
	 * @throws EErrorUnsupported 
	 * @throws EErrorParser 
	 */
	public static void removeInterMarkers(IResource res) throws EErrorAPI, EErrorParser, EErrorUnsupported {
		try {
			for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR,false, 0)) {
				for (IResource related : oppositeFromString(res, mk)) {
					for (IMarker mk1 : related.findMarkers(EchoMarker.INTER_ERROR,false, 0)) 
						if (mk1.getAttribute(EchoMarker.CONSTRAINT).equals(mk.getAttribute(EchoMarker.CONSTRAINT)))
	                        mk1.delete();
					mk.delete();
				}
			}
		} catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to delete marker.",Task.PLUGIN);
		}
	}

	/**
	 * Removes the inter-model error markers of two model resources related by a particular constraint
	 * @param constraint the constraint to be removed
	 * @throws EErrorAPI
	 * @throws EErrorUnsupported 
	 * @throws EErrorParser 
	 */
	public static void removeRelatedInterMarker(String constraintID) throws EErrorAPI, EErrorParser, EErrorUnsupported {
		try {
			EConstraint constraint = EConstraintManager.getInstance().getConstraintID(constraintID);
			for (String modelID : constraint.getModels()) {
				EModel model = MDEManager.getInstance().getModelID(modelID);
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(model.getURI());
				if(res.isAccessible())
					for (IMarker mk : res.findMarkers(EchoMarker.INTER_ERROR,false, 0))
						if (mk.getAttribute(EchoMarker.CONSTRAINT).equals(constraint.ID))
							mk.delete();
			
			}
		} catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to remove markers.",Task.PLUGIN);
		}
	}
		
	private static List<IResource> oppositeFromString(IResource src, IMarker marker) throws EErrorParser, EErrorUnsupported, EErrorAPI {
		String constraintID;
		try {
			constraintID = (String) marker.getAttribute(EchoMarker.CONSTRAINT);
		} catch (CoreException e) {
			throw new EErrorAPI(EErrorAPI.MARKER,"Failed to process marker.",Task.PLUGIN);
		}
		EConstraint c = EConstraintManager.getInstance().getConstraintID(constraintID);
		String srcID = MDEManager.getInstance().getModel(src.getFullPath().toString(),false).ID;
		List<IResource> res = new ArrayList<IResource>();
		for (String s : c.getModels())
			if(!s.equals(srcID))
				res.add(ResourcesPlugin.getWorkspace().getRoot().findMember(s));
		return res;
	}

}
