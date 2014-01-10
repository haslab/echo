package pt.uminho.haslab.echo.consistency.qvt;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.EModelParameter;
import pt.uminho.haslab.echo.consistency.ERelation;
import pt.uminho.haslab.echo.consistency.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QVTTransformation implements ETransformation {

	private static Map<RelationalTransformation,QVTTransformation> list = new HashMap<RelationalTransformation,QVTTransformation>();

	private List<EModelParameter> models = new ArrayList<EModelParameter>();
	private List<ERelation> relations = new ArrayList<ERelation>();
	private RelationalTransformation transformation;
	
	public QVTTransformation(org.eclipse.qvtd.pivot.qvtbase.Transformation transformation) throws ErrorParser {
		this.transformation = (RelationalTransformation) transformation;
		for (TypedModel mdl : transformation.getModelParameter()){
			models.add(new QVTModel(mdl));}
		

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
	
	public static QVTTransformation get(RelationalTransformation t) {
		return list.get(t);
	}
	
	

}
