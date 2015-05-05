package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An embedding of an EMF QVT-R model transformation in Echo.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EQVTTransformation extends ETransformation {

	/** the processed EMF transformation */
	private RelationalTransformation transformation;
	/** the model parameters of this transformation */
	private Map<String,EQVTModelParameter> modelParams;
	/** the containing relations of this transformation */
	private List<EQVTRelation> relations;
	/** the domain dependencies of this transformation */
	private List<EDependency> dependencies;

	/**
	 * Processes an EMF QVT-R transformation.
	 * @param transformation the original EMF transformation
	 * @throws EErrorParser
	 * @throws EErrorUnsupported
	 */
	public EQVTTransformation(org.eclipse.qvtd.pivot.qvtbase.Transformation transformation) throws EErrorUnsupported, EErrorParser {
		super(transformation.getName(),transformation);
	}

	/** {@inheritDoc} */
	@Override
	public RelationalTransformation getEObject() {
		return transformation;
	}
	
	/** {@inheritDoc} */
	@Override
	protected void process(EObject artifact) throws EErrorUnsupported {
		this.transformation = (RelationalTransformation) artifact;

		// required because it may be called from the parent constructor
		if (modelParams == null) modelParams = new HashMap<String,EQVTModelParameter>();
		if (relations == null) relations = new ArrayList<EQVTRelation>();
		
		for (TypedModel mdl : transformation.getModelParameter())
			modelParams.put(mdl.getName(),new EQVTModelParameter(mdl));
	
		for (Rule rule : transformation.getRule())
			relations.add(new EQVTRelation(rule));
	}

	/** {@inheritDoc} */
	@Override
	public List<EQVTModelParameter> getModelParams() {
		return new ArrayList<EQVTModelParameter>(modelParams.values());
	}

	/** {@inheritDoc} */
	@Override
	public List<EQVTRelation> getRelations() {
		return relations;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return transformation.getName();
	}

	/** {@inheritDoc} */
	@Override
	public EQVTModelParameter getModelParameter(String paramName) {
		return modelParams.get(paramName);
	}

	/** {@inheritDoc} */
	@Override
	public List<EDependency> getDependencies() {
		return dependencies;
	}

}
