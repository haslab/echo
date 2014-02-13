package pt.uminho.haslab.mde.transformation.qvt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

/**
 * An implementation of a model transformation in QVT-R
 *
 * @author nmm
 * @version 0.4 13/02/2014
 */
public class EQVTTransformation extends ETransformation {

	private List<EModelParameter> models;
	private List<ERelation> relations;
	private RelationalTransformation transformation;

	/**
	 * processes a QVT-R transformation
	 * @param transformation the original transformation
	 * @throws ErrorParser
	 * @throws ErrorUnsupported
	 */
	public EQVTTransformation(org.eclipse.qvtd.pivot.qvtbase.Transformation transformation) throws ErrorUnsupported, ErrorParser {
		super(transformation.getName(),transformation);
	}

	@Override
	public RelationalTransformation getEObject() {
		return transformation;
	}

	@Override
	protected void process(EObject artifact) throws ErrorUnsupported {
		this.transformation = (RelationalTransformation) artifact;

		if (models == null) models = new ArrayList<EModelParameter>();
		
		for (TypedModel mdl : transformation.getModelParameter())
			models.add(new QVTModel(mdl));
		
		if (relations == null) relations = new ArrayList<ERelation>();

		for (Rule rule : transformation.getRule())
			relations.add(new QVTRelation(rule));
	}

	@Override
	public List<EModelParameter> getModels() {
		return models;
	}

	@Override
	public List<ERelation> getRelations() {
		return relations;
	}

	@Override
	public String getName() {
		return transformation.getName();
	}

}
