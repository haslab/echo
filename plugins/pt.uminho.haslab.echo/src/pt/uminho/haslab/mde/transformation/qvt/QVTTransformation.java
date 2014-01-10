package pt.uminho.haslab.mde.transformation.qvt;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import pt.uminho.haslab.echo.ErrorParser;
<<<<<<< HEAD:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/mde/transformation/qvt/QVTTransformation.java
import pt.uminho.haslab.mde.transformation.EModelParameter;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;
=======
import pt.uminho.haslab.echo.consistency.EModelParameter;
import pt.uminho.haslab.echo.consistency.ERelation;
import pt.uminho.haslab.echo.consistency.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

>>>>>>> 960cb62ee476b59928466292cc8561fe497aa4fe:plugins/pt.uminho.haslab.echo/src/pt/uminho/haslab/echo/consistency/qvt/QVTTransformation.java

/**
 * An implementation of a model transformation in QVT-R
 * @author nmm
 */
public class QVTTransformation implements ETransformation {

	private static Map<RelationalTransformation,QVTTransformation> list = new HashMap<RelationalTransformation,QVTTransformation>();

	public static QVTTransformation get(RelationalTransformation t) {
		return list.get(t);
	}

	private List<EModelParameter> models = new ArrayList<EModelParameter>();
	private List<ERelation> relations = new ArrayList<ERelation>();
	private RelationalTransformation transformation;
	
	/** 
	 * processes a QVT-R transformation 
	 * @param transformation the original transformation
	 * @throws ErrorParser
	 */
	public QVTTransformation(org.eclipse.qvtd.pivot.qvtbase.Transformation transformation) throws ErrorParser {
		this.transformation = (RelationalTransformation) transformation;

		for (TypedModel mdl : transformation.getModelParameter())
			models.add(new QVTModel(mdl));
		
		for (Rule rule : transformation.getRule())
			relations.add(new QVTRelation(rule));

		list.put(this.transformation, this);
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
	
	@Override
	public String getIdentifier() {
		return null;
	}
	
	

}
